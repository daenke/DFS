/*
 * Created on 23.08.2008
 * History for Danis FileSync:
 * 0.1: erste Version
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package filesync;

import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * @author heida
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileExtensionFilter {
	@SuppressWarnings("rawtypes")
	HashSet extensionSet = new HashSet ();
	String _ext ="";

	@SuppressWarnings("unchecked")
	public FileExtensionFilter (String extensions) {
		if (extensions != null) _ext = extensions;
		StringTokenizer st = new StringTokenizer (_ext, ";");
		while (st.hasMoreElements()) {
			String buff = (String) st.nextElement();
			if (buff.startsWith(".")) buff = buff.substring(1); // 1. Zeichen löschen
			extensionSet.add(buff);
		} 
	}

	public boolean contains (String extensionToLookFor) {
		String buff = extensionToLookFor;
		if (buff.startsWith(".")) buff = buff.substring(1); // 1. Zeichen löschen
		return extensionSet.contains(buff); 
	}

	public String getFilteredExtensions() {
		return extensionSet.toString();
	}
}
