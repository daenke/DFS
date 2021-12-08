import java.io.*;

/**
 * Dies ist eine Klasse für beliebige Text-Manipulationen
 * die ich für Vokabel-Datei-Änderungen verwende
 * @author Dani
 **/
public class TextParser extends BufferedReader {
  
  public TextParser(Reader in) {
    super(in);
  }
    
  public final String readLine() throws IOException {
    String line;
    line = super.readLine();
    return line;
  }

  /**
   * Klasse mit main zum Aufruf
   **/
  public static class TestTextParser {
    public static void main(String args[]) {
      try {
        if (args.length != 2) 
          throw new IllegalArgumentException("Wrong number of arguments");
        TextParser in = new TextParser( new FileReader(args[0]) );
        FileWriter out = new FileWriter (args[1]);
        String lineIn;
        int rowNumber=0;
        while((lineIn = in.readLine()) != null) {
			String answer, answerWithComment, lineOut1, lineOut2;
			int lastWordStartsAt, secondWordStartsAt;
			
/*			if ( (rowNumber % 3) == 0) out.write(lineIn + "\n");
			rowNumber++;
*/
			// 1. Zeile
        	// out.write(lineIn + "\n");
        	answer = lineIn.substring(lineIn.indexOf("\t")+1);
        	answerWithComment = lineIn.substring(0, lineIn.indexOf("\t")+1) + answer.substring(0, answer.indexOf(", ")) 
        		+ "\t" + answer.substring(answer.indexOf(", ")+2);
			out.write(answerWithComment + "\n");
        	
        	// 2. Zeile
        	lastWordStartsAt = answer.lastIndexOf(", ") + 2;
        	lineOut1 = answer.substring(0, lastWordStartsAt) + "...\t" + answer.substring(lastWordStartsAt);
			out.write(lineOut1 + "\n");
			
			// 3.Zeile
			secondWordStartsAt = answer.indexOf(", ") + 2;
			lineOut2 = answer.substring(0, secondWordStartsAt) + "..., " + answer.substring(lastWordStartsAt) +
				"\t" + answer.substring(secondWordStartsAt, lastWordStartsAt-2);
			out.write(lineOut2 + "\n");
        } 
        in.close(); out.close();
      }
      catch (Exception e) { 
        System.err.println(e);
        System.out.println("Usage: java TextParser$TestTextParser inFile outFile");
      }
    }
  }
}
