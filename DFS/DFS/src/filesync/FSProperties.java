package filesync;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import java.io.*;
import java.awt.*;
import javax.swing.*;

import GUI.DE_FileSync;

/** Properties aus Initialisierung laden
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FSProperties {
	/** momentan genutzt für Ausgaben zu INI-Standort und -Auswahl, erstmal auf true belassen*/
	private boolean debug = true;
	
	private Properties p;
	private String initName = null;
	private Component parent;
	private String iniFilename=null;
	private final String DIR = "DFSync";
	private String workingDir;

	public static final int NO_INI=-1; 
	public static final int UNKNOWN=0; 
	public static final int INI_IN_YAR=1; // nach Such-Prio
	public static final int INI_IN_HOME=2; 
	public static final int INI_IN_DIR=3; 
	private int mode = UNKNOWN;
	private Date lastFinishedSync;
	DateFormat df;
	private boolean excludeHiddenLinuxFiles = false;
	private boolean recordOnlyNewerFiles = false; // recordOnlyFilesModifiedSinceLastSync
	private boolean autoCorrect = false; // correct self sync problems by generating more bak-files 

	/** this will create Properties and load the ini-File as well*/
	public FSProperties (Component parentWindow, String ini) {
		initName = ini;
		parent = parentWindow;
		p = new Properties();
		df = DateFormat.getDateTimeInstance();
		loadIni();
	}

	// public setters & readers of my private Properties
	public void setSourceFolder(String folder) { p.setProperty("src", folder); saveIni();}
	public String getSourceFolder() { return(p.getProperty("src")); }
	public void setDestFolder(String folder) { p.setProperty("dest", folder); saveIni();}
	public String getDestFolder() { return(p.getProperty("dest")); }
	public void clearLastFinishedSync () { 
		p.remove( "lastFinishedSync" ); saveIni();}
	public void setLastFinishedSync (long time) { 
		p.setProperty( "lastFinishedSync", df.format(new Date(time)) ); saveIni();}
	public String getLastFinishedSyncDate() { 
		return (lastFinishedSync != null)? df.format(lastFinishedSync) : "kein letztes Datum hinterlegt."; }
	public long getLastFinishedSync() { 
		return (lastFinishedSync != null)? lastFinishedSync.getTime() : 0L; }
	public boolean excludeHiddenLinuxFiles() { return excludeHiddenLinuxFiles; }
	public boolean recordOnlyNewerFiles() { return recordOnlyNewerFiles; }
	public boolean autoCorrect() {return autoCorrect;}

	public void setProperty (String key, String value) { p.setProperty(key, value); saveIni();}
	public String getProperty (String key) { 
		if (p.getProperty(key) == null) {
			System.out.println("INFO: ini-Wert: " + key +"  nicht gefunden.");
			return "";
		} else return p.getProperty(key);
	}

	
	// load settings
	private void loadIni() {
		try {
			iniFilename = getIniFilename();
			if (debug) ((DE_FileSync) parent).writeLog("\n Arbeite mit: " + iniFilename);
			File f = new File (iniFilename);
			if (f.exists()) {
				p.load(new FileInputStream(iniFilename));
			} else {
				f.createNewFile();
			}
			
			// Defaults
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(parent, "Settings file not found.", "Read error", JOptionPane.WARNING_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(parent, "Could not read settings.", "Read error", JOptionPane.WARNING_MESSAGE);
		} catch (NullPointerException ex) { // wird erreicht, wenn ohne ini gearbeitet wird
		}
		
		try {
			lastFinishedSync = df.parse(p.getProperty("lastFinishedSync"));
		} catch (ParseException pe) { System.out.println("ERROR: ini-Wert: lastFinishedSync nicht auswertbar");
		} catch (NullPointerException pe) { System.out.println("INFO: ini-Wert: lastFinishedSync nicht gefunden.");
		}

		try {
			if (p.getProperty("excludeHiddenLinuxFiles").equals("ja"))
				excludeHiddenLinuxFiles = true;
		} catch (NullPointerException pe) { 
			System.out.println("INFO: ini-Wert: excludeHiddenLinuxFiles nicht gefunden. Verwende Standard: nein.");
			p.setProperty("excludeHiddenLinuxFiles", "nein");
			saveIni();
		}

		try {
			if (p.getProperty("recordOnlyFilesModifiedSinceLastSync").equals("ja"))
				recordOnlyNewerFiles = true;
		} catch (NullPointerException pe) { 
			System.out.println("INFO: ini-Wert: recordOnlyFilesModifiedSinceLastSync nicht gefunden. " + 				"Verwende Standard: nein.");
			p.setProperty("recordOnlyFilesModifiedSinceLastSync", "nein");
			saveIni();
		}

		try {
			if (p.getProperty("autoCorrect").equals("ja"))
				autoCorrect = true;
		} catch (NullPointerException pe) { 
			System.out.println("INFO: ini-Wert: autoCorrect nicht gefunden. Verwende Standard: nein.");
			p.setProperty("autoCorrect", "nein");
			saveIni();
		}

	}
	
	// Get the user specific ini filename based on INI_FILE
	private String getIniFilename() {
		
		// zur Ermittlung INI_IN_YAR
		String classPath = getClass().getResource("FSProperties.class").getPath();
		int protPos = classPath.indexOf("file:");
		if (protPos >= 0) classPath = classPath.substring(protPos+5);
			// sollte wenn TRUE immer null sein, z.B. file:/...
		int jarSignPos = classPath.lastIndexOf('!');
		if (jarSignPos > 0) classPath = classPath.substring(0, jarSignPos); //  Ende bei z.B. jar (Name mit "!")
		int dirnameEndsHere = classPath.lastIndexOf(File.separatorChar);
		if (dirnameEndsHere > 0) classPath = classPath.substring(0, dirnameEndsHere); // davon dirName
		if (debug) ((DE_FileSync) parent).writeLog("\n classPath = " + classPath);
		File jarDir = new File (classPath + File.separator + initName);
		if ( (protPos >= 0) && (jarSignPos > 0) && (jarDir.exists()) ) { // arbeiten wir wirklich mit einem jar?
			mode = INI_IN_YAR;
			workingDir = classPath;
			return (classPath + File.separator + initName);
		}
		
		String dirName = System.getProperty("user.dir");
		if (debug) ((DE_FileSync) parent).writeLog("\n dirName = " + dirName);
		File fileInDir = new File(dirName + File.separator + initName);
		if ( fileInDir.exists()) {
			mode=INI_IN_DIR;
			workingDir = dirName;
			return(dirName + File.separator + initName);
		}

		String homeName = System.getProperty("user.home") + File.separator + DIR;
		if (debug) ((DE_FileSync) parent).writeLog("\n homeName = " + homeName);
		File dirHome = new File(homeName); // lediglich Verz.-Test nicht Datei-Test auf "kaba.ini"
		if ( dirHome.exists()) {
			mode=INI_IN_HOME;
			workingDir = homeName;
			return(homeName + File.separator + initName);
		} else {
			int yes = JOptionPane.showConfirmDialog( parent,
				"Initialisierungs-Datei nicht gefunden.\n" +				"Hierin werden Einstellungen wie die " +				"Verzeichnisnamen zwischengespeichert.\n" + "Datei erzeugen?",
				"Datei DFS.ini nicht gefunden", JOptionPane.YES_NO_OPTION);
			if (yes == 0) {
				dirHome.mkdirs();
				mode=INI_IN_HOME;
				workingDir = homeName;
				return(homeName + File.separator + initName);
			} else {
				mode = NO_INI;
				workingDir = null;
				return null;
			}
		}
	}
	
	// save settings
	public void saveIni() {
		try {
			// Window size and position
			Rectangle r = parent.getBounds();
			if (! r.isEmpty()) {
				p.setProperty("WindowX", (new Integer(r.x)).toString());
				p.setProperty("WindowY", (new Integer(r.y)).toString());
				p.setProperty("WindowW", (new Integer(r.width)).toString());
				p.setProperty("WindowH", (new Integer(r.height)).toString());
			}
			
			// Save settings
			if ( isSaveable () )	p.store(new FileOutputStream(iniFilename), "FileSync - Settings");
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(parent, "Settings file not found.", "Save error", JOptionPane.WARNING_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(parent, "Could not save settings.", "Save error", JOptionPane.WARNING_MESSAGE);
		}
	}

	/** mögliche Mode (Standorte) der Properties*/
/*	public int getMode() {
		return mode;
	}*/
	
	/**
	 * @return
	 */
	private boolean isSaveable() {
		return (mode > UNKNOWN) ? true : false;
	}

	public String getIniDir() {
		return workingDir;
	}

}
