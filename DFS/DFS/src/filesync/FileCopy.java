package filesync;
import java.io.*;

/*
 * File: FileCopy.java
 * Created for Projekt: DaniBibo
 * Created on 07.11.2007 by Daniel Enke
 */

/**
 * Diese Klasse ist eine Applikation zum Kopieren einer Datei, die eine statische Methode copy()
 * definiert, die von anderen Programmen zum Kopieren verwendet werden kann.
 * @author Daniel Enke
 */
public class FileCopy {

	public static void main(String[] args) {
		if (args.length !=2)
			System.err.println("Aufruf: java FileCopy <Quelldatei> <Ziel>");
		else {
			try { copy (args[0], args[1]); }
			catch (IOException e) {System.err.println(e.getMessage());}
		}
	}
	
	public static void copy (String fromName, String toName) throws IOException {
		File from = new File (fromName);
		File to = new File (toName);
		
		// Sicherstellen, dass Quelle existiert und lesbar ist
		if (!from.exists()) abort ("FileCopy: Quelldatei existiert nicht: " + fromName);
		if (!from.isFile()) abort ("FileCopy: kann keine Verzeichnisse kopieren: " + fromName);
		if (!from.canRead()) abort ("FileCopy: Quelldatei ist nicht lesbar: " + fromName);
		
		// wenn Ziel ein Verzeichnis ist, dann verwenden wir den Namen der Quelle auch für das Ziel
		if (to.isDirectory()) to = new File (to, from.getName());
		
		// Wenn Ziel existiert, stellen wir sicher das Ziel schreibbar ist und fragen vor dem
		// Überschreiben nach. Ansonsten prüfen wir, ob das Verzeichnis existiert und schreibbar ist.
		if (to.exists()) {
			if (!to.canWrite()) abort("FileCopy: Zieldatei ist nicht beschreibbar: " + toName);
			
			// Nachfrage
			System.out.println ("Existierende Datei überschreiben: " + toName + " ? (J/N): ");
			System.out.flush();
			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
			String response = in.readLine();
			if ( (! response.equals("J")) && (! response.equals("j")) )
				abort ("FileCopy: Existierende Datei wurde nicht überschrieben: " + toName);
		} else {
			String parent = to.getParent();
			if (parent == null) parent = System.getProperty("user.dir");
			File pDir = new File (parent);
			if (! pDir.exists()) abort ("FileCopy: Zielverzeichnis existiert nicht: " + parent);
			if ( pDir.isFile()) abort ("FileCopy: Ziel ist kein Verzeichnis: " + parent);
			if (! pDir.canWrite()) abort ("FileCopy: Zielverzeichnis ist nicht beschreibbar: " + parent);
		}
		
		// Wenn wir hier angekommen sind, ist alles iO! Es geht los!
		FileInputStream f = null;
		FileOutputStream t = null;
		
		try {
			f = new FileInputStream (from);
			t = new FileOutputStream (to);
			byte[] buffer = new byte[4096];
			int bytesRead;
			
			while ( (bytesRead = f.read(buffer)) != -1 ) t.write(buffer, 0, bytesRead);
		}
		finally { // Streams immer zu schließen versuchen, auch wenn Ausnahmen ausgelöst werden
			if (f !=  null) try { f.close(); } catch (IOException e) {}
			if (t !=  null) try { t.close(); } catch (IOException e) {}
		}
		
	}


	private static void abort (String msg) throws IOException {
		throw new IOException (msg);
	}

}
