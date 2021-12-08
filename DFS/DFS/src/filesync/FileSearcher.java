/*
 * 5.0 - 05.08.08:
 * 	+bugfix für Linux-Man-Pages Perl Bsp.: CGI::Apache.3pm.gz, auf Ziel-LW ohne ":", Ausgabe in log
 * 4.0 - 04.08.08:
 * 	+ timeToleranceInSeconds impl., da im alten Suse-Linux 8 Veränderungen von 1s mehrfach vorkommen
 * V3: bringt nichts, daher wieder rückgängig gemacht, aber Ausgaben bringen was
 * V2: 	Abkürzung Zeile 64 für Performance
 */
package filesync;

import java.io.*;
import java.util.Date;
import java.text.DateFormat;

import GUI.DE_FileSync;

/** hier wird die eigentliche Arbeit gemacht, Verzeichnisabgleiche, kopieren, versionieren
 * @author heida
 */
public class FileSearcher {

	int found;
	File srcFolder, destFolder;
	
	DE_FileSync p;
	DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
	DateFormat tf = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	boolean excludeHiddenLinuxFiles, fastSync, autoCorrect;
	final int timeToleranceInSeconds=2;
	long lastFinishedSync;

	/**
	 * 
	 * @param parent
	 * @param sourceFolder
	 * @param destinationFolder
	 * @param excludeHiddenLinuxFiles - beginning with a dot
	 */
	public FileSearcher(DE_FileSync parent, String sourceFolder, String destinationFolder, 
			boolean excludeHiddenLinuxFiles, boolean fastSync, long lastFinishedSyncTime,
			boolean autoCorrect) {
		this.found = 0;
		this.srcFolder = new File(sourceFolder);
		this.destFolder = new File(destinationFolder);
		this.p = parent;
		this.excludeHiddenLinuxFiles = excludeHiddenLinuxFiles;
		this.lastFinishedSync = lastFinishedSyncTime;
		this.fastSync = fastSync;
		this. autoCorrect = autoCorrect; //TODO impl. autoCorrect in sync
		// LESEZEICHEN
	}

	public int synchronize(){ //LESEZEICHEN debuggen autocorrect & 1. Ausgangsbdi. umformuliert
		if (! srcFolder.exists()) return found;

		String srcFiles[] = srcFolder.list();
		if (srcFiles == null) return found; // 080726 bugfix für NullPointerException in for-Prüfung hiernach
		for(int i=0; i < srcFiles.length; i++) {
			if (p.threadSearch == null) { return found; } // abort if thread was interrupted

			String fullFileName = srcFiles[i];
			FileNameFactory fname = new FileNameFactory (fullFileName);
			String srcFilename = srcFolder.getAbsolutePath() + File.separator + fullFileName;
			String destFilename = destFolder.getAbsolutePath() + File.separator + fullFileName;
			if (fullFileName.indexOf(':') >= 0) { // bugfix für Linux-Man-Pages Perl Bsp.: CGI::Apache.3pm.gz
				String fullDestFileName = fullFileName.replaceAll(":", "_COLON_");
				destFilename = destFolder.getAbsolutePath() + File.separator + fullDestFileName;
				// logLineBegin("Nächster Datei / Verzeichnis-Name im Backup verändert: " + destFilename);
				// rausgenommen, damit Ausgabe nicht leer im Raum steht!
			}
			File subSrcFile = new File(srcFilename);
			File subDestFile = new File(destFilename);
				
			if (excludeHiddenLinuxFiles)
				if (fullFileName.startsWith(".")) continue; // nothing to do with a hidden Linux File if asked

			if (subSrcFile.isDirectory()) { // Verz.-abgleich
				long srcDate = subSrcFile.lastModified(); // eingefügt für fastSync gleich wie Dateiarbeit
				if ( (! fastSync) || (srcDate > lastFinishedSync) ) {
					if (! subDestFile.exists()) {
						showStatus("Bearbeite Verzeichnis: " + srcFilename);
						logLineBegin(destFilename + "-> Verz. zu erstellen: ");
						if ( ! p.testOnly() )  {
							logLineAppend (" erstelle Verzeichnis... ");
							subDestFile.mkdirs();
							if (subDestFile.canRead()) logLineAppend("OK!");
							else writeError("NIO!!!");
						}
					}
				}
				// recursively search sub-folders
				FileSearcher fs = new FileSearcher(p, subSrcFile.getAbsolutePath(), subDestFile.getAbsolutePath(),
					excludeHiddenLinuxFiles, fastSync, lastFinishedSync, autoCorrect);
				int foundInSub = fs.synchronize();
				found += foundInSub;
				showStatus("Bearbeite Verzeichnis: " + srcFilename);
			} else { // Dateiarbeiten ab hier: kopieren
				//TODO reicht immmer noch nicht aus, er kopiert Daten unter
				// home/enke/dosemu/drives/d/... und geht damit durch einen symb. Link hindurch 
				if (! subSrcFile.isFile()) continue; // um Links zu finden (Suse8)
				if (subSrcFile.length() <= 0) continue; // keine Links bearbeiten, reicht noch nicht
				if (p.extensionsToExclude.contains(fname.getExtension())) continue;

				long srcDate = subSrcFile.lastModified();

				// Test für schnellen Sync, record only modified Files after last finished Sync
				if (fastSync)
					if(srcDate < lastFinishedSync) continue;

				if (! subDestFile.exists()) {	
					logLineBegin(srcFilename + "-> Datei zu kopieren: ");
					filecopy (srcFilename, destFilename, subDestFile, srcDate);
				} else { // versionieren
					long destDate = subDestFile.lastModified();
					// if (srcDate == destDate) continue; // Abkürzung für Performance
					// witzig, wird dadurch nicht merklich schneller
					Date sourceFileDate = new Date (srcDate); 
					Date destinationFileDate = new Date (destDate);

					/* damit Dateien in SuSe-Linux-8 incht mehrfach synced werden, hier sind 1s-Unterscheide zu beobachten
					nach Datums-Setzen mit setLastModified() */ 
					if (Math.abs(srcDate - destDate) <= timeToleranceInSeconds*1000) continue; 
					else	if (srcDate < destDate) {
						writeError("FEHLER - Quelldatei ÄLTER als Zieldatei:"+
							"\nQ: "+df.format(sourceFileDate)+" -> "+srcFilename+
							"\nZ: "+df.format(destinationFileDate)+" -> "+destFilename); // LESEZEICHEN
					} else if (srcDate > destDate) { // Aktion nur wenn Quelle neuer, sonst
						//besteht Gefahr eine neuer Datei mit einer älteren zu überschreiben!!!
						logLineBegin(srcFilename+"-> Datei zu versionieren: ");

						// Vorarbeit für versionieren
						// zum versionieren erst alte Zieldatei mit Versions-Nr. versehen 
						String versionedFileName = getVersionString(destFolder.getAbsolutePath(), 
							fname.getFileNameWithoutExtension(), fname.getExtension());
						File subVersionedFile = new File (versionedFileName);
						boolean check = true;
						if ( ! p.testOnly() ) {
							logLineAppend ("-> versioniere zu "+versionedFileName+" ... ");
							check = subDestFile.renameTo(subVersionedFile);
						} 
						if ( ! check ) writeError("Fehler beim Umbenennen der Datei: "
							+srcFilename+" in: "+versionedFileName);

						// dann Datei kopieren
						filecopy (srcFilename, destFilename, subDestFile, srcDate);
					}
				}
			}
		}
		return found;
	}
	
	private void filecopy(String srcFilename, String destFilename, File destFile, long sourceDate) {
		if ( ! p.testOnly() ) {
			logLineAppend ("kopiere ...");
			try {
				FileCopy.copy(srcFilename, destFilename);
			} catch (IOException e) {
				writeError("IOException beim kopieren von "+srcFilename+" nach "+destFilename+" !\n"+
					e.getMessage() );
			}
			destFile.setLastModified(sourceDate);
			if (destFile.canRead()) logLineAppend("OK!"); else writeError("NIO!!!");
			found++;
		}
	}

	private String getVersionString(String path, String name, String ext) {
		File f; String versionString="";
		int destVersion=0;
		do {
			destVersion++;
			versionString = (new Integer (destVersion)).toString();
			while (versionString.length() < 3) versionString = "0" + versionString;
			f = new File (path+File.separator+name+"_"+versionString+"."+ext);
		} while (f.exists());
		return f.getAbsolutePath();
	}

	private void logLineBegin (String message) {
		p.writeLog("\n" + tf.format(new Date(System.currentTimeMillis())) +"-> "+ message);
	}
	
	private void logLineAppend (String message) {
		p.writeLog(message);
	}

	private void writeError (String message) {
		p.writeError(message);
	}

	private void showStatus (String message) {
		p.showStatus (message);
	}

}
