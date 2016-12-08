package PreProcessData;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

public class StopWordRemover {
	//you can add essential private methods or variables
	private FileInputStream fis = null;
    private BufferedReader reader = null;
    private Map<String, Object> m=new HashMap<String, Object>();
	public StopWordRemover( ) throws IOException {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		fis = new FileInputStream(Path.StopwordDir);
        reader = new BufferedReader(new InputStreamReader(fis));
        String line="";
        //read the stop word and store in map in advance
        while((line=reader.readLine())!=null){
        	m.put(line, "");
        }
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		//convert the word from char array to String
		String wording=new String(word);
		//check if the word is already in the map
		return m.containsKey(wording);
	}
}
