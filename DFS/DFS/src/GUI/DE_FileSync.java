/*
 * Created on 25.05.2008
 * History for Danis FileSync:
 * 1.7 - xx.10.08:
 * 	- Autocorrect zum selbständigen Korrigieren von Fehlern in winXP bei DAI, geöffnete Dateien
 * 		werden fälschlich mit aktuellem Zeitstempel gesehen
 * 	- Links dürfen nicht verfolgt werden, Selbstrekursion bei /home/enke/dosemu/drives/d
 * 	- möglichst hinten einen kleinen Balken der proz. Fertgistellung anzeigt, vielleicht mit 
 * 		hinterlegen des letzten found Wertes in INI + Zeitabschätzung über hinterlegte Dauer
 * 1.6 - 23.08.08:
 * 	+ FileExtensions ausschließbar über ini (Bsp.: .class-Dateien)
 * 	+ neue Klassen FileExtensionFilter, FileNameFilter
 * 1.5 - 05.08.08:
 *		+ bei Verzeichn.-wechsel wird lastSync=null
 *		+ bugfix Fenster waren klein - in saveIni
 * 	+ FileSearcher V5: 
 * 		bugfix für Linux-Man-Peages Perl Bsp.: CGI::Apache.3pm.gz, auf Ziel-LW ohne ":", Ausgabe in log
 * 	+ ini automatisch akualisieren bei neuen Versionen (in Ausnahme),  ini sofort schreiben bei Änderung ...
 * 1.4 - 04.08.08:
 * 	- in Ver. 2 eine Menu-Struktur mit JTable wäre nett
 * 	+ Fast Sync Mode implementiert (Nur Dat./Vz. nach letztem Sync werden bearbeitet.)
 * 	+ bugfix für suse-Linux-8: Zeittolerierung von > 1s eingefügt für notToSync-Dateien 
 * 	+ bugfix NullPointerException bei HiddenfilesOverride in ini nicht gefunden mit Ausgaben
 * 1.3 - 26.07.08:
 * 	+ Last Finished Sync: in ini impl & am Beginn anzeigen
 * 	+ Exclude Hidden Linux Files: in ini impl & am Beginn anzeigen
 * 1.2 - 07.07.08:
 * 	-	Sound abspielen wenn fertig
 * 	+ Modifikation des ini-Ladens, dass Sync mit Icon startbar ist unter lin9, ini aus jarDir mit höchster Prio
 * 	+ Farbe bei neuem sync schwarz, wenn vorher rot (Fehler)
 * 
 * 1.0: erste Version
 * 	+ mehrere INIs (+logs) werden untersützt, Such-Prio siehe FSProperties
 * 	- Datenbank realisieren wenn s schneller gehen soll (mit long Date oder hashCode), problematisch bei 
 * 		vielen Dateien, große Datenmengen zu durchsuchen?
 * 	- mehrere Syncs-Jobs hintereinander ermöglichen mit JTable und Ausschlussverzeichnisse
 * 		z.B. .metadata
 * 1.1:
 * 	+ bugfix: Fensterdarstellung niO, Fenster ist leer, makeframe(visible) ganz am Schluss
 */
package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Date;
import java.text.DateFormat;

import javax.swing.*;
import logging.*;
import filesync.*;

/**
 * 
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class DE_FileSync extends CloseableJFrame implements Runnable {
	
	private final String VERSION_ID = "1.6";
	private final String TITLE = "Danis File Sync";
	
	JPanel panelMain = new JPanel(new BorderLayout());
	JToolBar toolbarMain = new JToolBar();
	
	JTextField textSourceLocation = new JTextField();
	JButton btnSourceFolder = new JButton("Quelle wählen ...");
	JTextField textDestLocation = new JTextField();
	JButton btnDestFolder = new JButton("Ziel wählen...");

	JButton btnSyncFiles = new JButton("Synchronisiere !");
	
	JTextArea log = new JTextArea ();
	JScrollPane logScroll = new JScrollPane(log,
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	JPanel logPanel = new JPanel(new BorderLayout());

	JLabel labelProgress = new JLabel("Bereit");
	
	public volatile Thread threadSearch = null;

	/** Quell- und Zielverzeichnis aus Textfeldern*/
	String source, destination;
	FSProperties fsProp;
	public static final String INI_FILENAME = "DFS.ini";
	FileLogger logFile;
	public static final String LOG_FILENAME= "DFS.log";
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	
	private boolean showOnly = true; JCheckBox check;
	private ErrorCounter errors;
	Date cancelTime;
	public FileExtensionFilter extensionsToExclude;
	
	public DE_FileSync() {

		// Main panel
		getContentPane().add(panelMain);
		setTitle(TITLE + " - Version: " + VERSION_ID);
		// setIconImage(new ImageIcon(getClass().getResource("/images/icon.gif")).getImage());
		
		// Labels
		JPanel locLabel = new JPanel(new GridLayout(0,1));
		locLabel.add(new JLabel("Quell-Verzeichnis:   ", JLabel.RIGHT));
		locLabel.add(new JLabel("Ziel-Verzeichnis:   ", JLabel.RIGHT));
		locLabel.add(new JLabel(""));
		locLabel.add(new JLabel(""));
		
		// Fields
		JPanel locText = new JPanel(new GridLayout(0,1));
		locText.add (textSourceLocation);
		locText.add(textDestLocation);
		locText.add(new JLabel(""));
		locText.add(new JLabel(""));

		// Buttons
		JPanel locButton = new JPanel(new GridLayout (0,1));
		btnSourceFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fsProp.setSourceFolder( chooseFolder(textSourceLocation) );
			}
		});
		locButton.add(btnSourceFolder);
		btnDestFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fsProp.setDestFolder( chooseFolder(textDestLocation) );
			}
		});
		locButton.add(btnDestFolder);
		JPanel checkPanel = new JPanel();
		check = new JCheckBox("Teste erstmal !", true);
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if ( check.isSelected()) showOnly = true; // aktivieren immer möglich
				else if (threadSearch==null) showOnly = false; // deaktiveren nur wenn sync inaktiv
				else check.setSelected(true);
			}
		});
		checkPanel.add (check);
		locButton.add(checkPanel);
		btnSyncFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				syncFiles();
			}
		});
		locButton.add (btnSyncFiles);
		
		// Upper panel
		JPanel actPanel = new JPanel(new BorderLayout());
		actPanel.add(locLabel, BorderLayout.WEST);
		actPanel.add(locText, BorderLayout.CENTER);
		actPanel.add(locButton, BorderLayout.EAST);
		actPanel.setBorder(BorderFactory.createTitledBorder("Eingaben"));
		
		// Add the whole thing to the main panel
		panelMain.add(actPanel, BorderLayout.NORTH);
		
		// Log panel
		logPanel.setBorder(BorderFactory.createTitledBorder("Protokoll"));
		// log.setLineWrap(true); log.setWrapStyleWord(true); // Zeilen NICHT umbrechen!
		logPanel.add(logScroll, BorderLayout.CENTER);
		panelMain.add(logPanel, BorderLayout.CENTER);
		
		// panelMain.add(new DirectoryTreePanel(), BorderLayout.CENTER); // test
		
		// Progress
		labelProgress.setBorder(BorderFactory.createTitledBorder("Status"));
		panelMain.add(labelProgress, BorderLayout.SOUTH);
		
		// init Einstellungen
		fsProp = new FSProperties ( this, INI_FILENAME);
		extensionsToExclude = new FileExtensionFilter (fsProp.getProperty("excludedFileExtensions"));
		textSourceLocation.setText(fsProp.getSourceFolder());
		textDestLocation.setText(fsProp.getDestFolder());
		textSourceLocation.setCaretPosition(textSourceLocation.getText().length());
		textSourceLocation.requestFocus();
		try {
			int x = Integer.parseInt(fsProp.getProperty("WindowX"));
			int y = Integer.parseInt(fsProp.getProperty("WindowY"));
			int w = Integer.parseInt(fsProp.getProperty("WindowW"));
			int h = Integer.parseInt(fsProp.getProperty("WindowH"));
			setBounds(x, y, w, h);
		} catch (NumberFormatException ex) {
			setBounds(0, 0, 600, 450);
		}
		
		writeLog("\n");
		writeLog("\n Letzte Synchronisation war am: " + fsProp.getLastFinishedSyncDate());
		writeLog("\n Ohne Versteckte Linux Dateien / Verzeichnisse: " + 
			((fsProp.excludeHiddenLinuxFiles()) ? "ja" : "nein") );
		writeLog("\n Sichere nur Dateien nach letzter Synchronisation: " + 
			((fsProp.recordOnlyNewerFiles()) ? "ja" : "nein") );
		writeLog("\n Korrigieren Fehler beim Sync selbständig: " + 
			((fsProp.autoCorrect()) ? "ja" : "nein") );
		writeLog("\n Schließe Dateinamen mit folgenden Erweiterungsnamen aus: " + 
			extensionsToExclude.getFilteredExtensions() );

		// Make frame visible
		panelMain.validate();
		setVisible(true);

	}

	public static void main(String[] args) {
		// Set native look and feel
		// Get the native look and feel class name
		String nativeLF = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(nativeLF);
		} catch (InstantiationException e) {
		} catch (ClassNotFoundException e) {
		} catch (UnsupportedLookAndFeelException e) {
		} catch (IllegalAccessException e) {
		}
		
		new DE_FileSync();
	}

	private String chooseFolder(JTextField t) {
		StringBuffer loc = new StringBuffer(); String init;
		
		try {
			init = t.getText();
		} catch (NullPointerException e) {
			init = System.getProperty("user.home");
		}
		if (Util.chooseFolder(loc, init, panelMain, false)) {
			t.setText(loc.toString());
		}
		if (! loc.toString().equals(init)) fsProp.clearLastFinishedSync();
		return loc.toString();
	}
	
	
	// quit
	public void closeFrame() {
		fsProp.saveIni();
		System.exit(0);
	}
	
	public void start() {
		if (threadSearch == null) {
			cancelTime = null;
			threadSearch = new Thread(this);
			threadSearch.start();
		} else {
			try {
				cancelTime = new Date (System.currentTimeMillis());
				threadSearch.interrupt(); threadSearch = null;
			} catch (Exception ex) {}
		}
	}
	
	public void run() {
		if (threadSearch != null) {
			// Remove old results
			log.setText(""); log.setForeground(Color.BLACK);
			errors = new ErrorCounter();
			if (! testOnly()) logFile = new FileLogger (this, LOG_FILENAME);
			Date now = new Date (System.currentTimeMillis());
			writeLog("\n\n"+df.format(now)+" -> Synchronisiere: "+source+" mit "+
				destination +"\n");
			
			FileSearcher fs = new FileSearcher ( this, textSourceLocation.getText(), 
				textDestLocation.getText(), fsProp.excludeHiddenLinuxFiles(), 
				fsProp.recordOnlyNewerFiles(), fsProp.getLastFinishedSync(), fsProp.autoCorrect());
			int workedItems = fs.synchronize();
			
			// Done
			showStatus ("Fertig.");
			now = new Date (System.currentTimeMillis());
			if (cancelTime != null) {
				writeLog("\n"+df.format(cancelTime) + " -> Abbruch durch Nutzer!\n");
			}
			writeLog("\n"+df.format(now)+" -> Synchronisieren beendet. Anzahl Fehler = "+
				errors.get()+"\n");
			
			if (! testOnly()) {
				logFile.flush();  logFile.close();
				showStatus("Erledigt: " + workedItems + " Dateien in Ziel-Verzeichnissen angelegt.");
				if (cancelTime == null) fsProp.setLastFinishedSync(now.getTime());
			}
			
			// stop the thread
			threadSearch = null;
		}
	}
	
	public void writeError(String message) {
		errors.increment();
		log.setForeground(Color.RED);
		writeLog("\n***************************************************\n");
		writeLog(message);
		writeLog("\n***************************************************\n");
	}

	public void writeLog(String message) {
		if ( logFile != null ) logFile.write(message);
		log.append(message);
		// log.setCaretPosition(log.getText().length()-1);
	}

	public void showStatus (String message) {
		labelProgress.setText(message);
	}

	protected void syncFiles() {
		source = textSourceLocation.getText();
		destination = textDestLocation.getText();
		File s = new File(source);
		File d = new File(destination);
		if ( ( s.exists() && d.exists() ) || threadSearch != null) { // Quelle und Ziel prüfen vorab
			start(); // start new thread and begin searching. if a thread is already running, this will stop it.
		} else {
			JOptionPane.showMessageDialog(this, "Gewählte Verzeichnisse bitte erst anlegen.", 
				"Verzeichnis nicht gefunden", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public boolean testOnly() {
		return showOnly;
	}
	
	/** wo steht ini-Datei, Modes siehe Property-class*/
	public String getIniPath() {
		return fsProp.getIniDir();
	}
}
