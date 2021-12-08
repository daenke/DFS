/*
 * Created on 23.08.2008
 * History for Danis FileSync:
 * 0.1: erste Version
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package filesync;

/**
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileNameFactory {
	private String fileDescriptionName, extension;

	public FileNameFactory (String fileName) {
		
		int extensionBegin = fileName.lastIndexOf('.');
		if (extensionBegin > 0) {
			extension = fileName.substring(extensionBegin+1);
			fileDescriptionName = fileName.substring(0, extensionBegin);
		} else {
			extension = ""; fileDescriptionName = fileName;
		}
	}

	public String getFileNameWithoutExtension () {
		return fileDescriptionName;
	}

	public String getExtension () {
		return extension;
	}

}
