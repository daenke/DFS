/*
* History for Danis FileSync:
* 	Version 3 07.07.08:
* 		+ wenn Nutzer nicht mit ini arbeitet auch nicht nach log-File fragen
 * 2:	+ log-Datei an derselben Stelle wie ini anlegen
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package logging;

import java.io.*;
import javax.swing.*;

import GUI.DE_FileSync;

/**
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileLogger {

	private DE_FileSync parent;
	String loggerFilename; // Wunsch-Log-Filename aus Konstruktor
	String log = null; // berechneter LOG-Filename (null wenn ohne LOG gearbeitet wird)
	PrintWriter logFile;
	static final boolean APPEND=true;

	public FileLogger(DE_FileSync parentWindow, String filename) {
		
		loggerFilename = filename;
		parent = parentWindow;
		getLogFile();	
	}
	
	private void getLogFile() {
		
		try {
			log = getLogFilePositionAsIniFilePosition(); // wenn user mit ini-File arbeitet nicht mehr nach log fragen
			if (log != null) // wenn user nicht mit files arbeitet auch nicht nach log fragen
				logFile = new PrintWriter(new BufferedWriter(new FileWriter( log, APPEND )));
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(parent, "Could not open log-File.", "Read error", JOptionPane.WARNING_MESSAGE);
		}
	}

	private String getLogFilePositionAsIniFilePosition() {
		return parent.getIniPath() + File.separator + loggerFilename;
	}

	public void write(String s){
		if (log != null) logFile.write(s);
	}

	public void flush(){
		if (log != null) logFile.flush();
	}

	public void close(){
		if (log != null) logFile.close();
	}
	
}
