package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.HashMap;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2015
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private FileInputStream fis = null;
    private BufferedReader reader = null;      
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		fis = new FileInputStream(Path.DataTextDir);
        reader = new BufferedReader(new InputStreamReader(fis));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		Map<String, Object> result=new HashMap<String, Object>();
		String line="";
		String key="";
		String s="";
		StringBuilder content=new StringBuilder();
		//mark if there is a doc
		boolean hasdoc=false;
		if((line =reader.readLine()) != null)  hasdoc=true;
		//make sure to exam if we should continue read next line before read next line
		if(hasdoc){
			key=line.split(",")[0];
			line=line.split(",")[1];
			StringTokenizer slist=new StringTokenizer(line);
        	while(slist.hasMoreTokens()){
        		s=slist.nextToken();
        		//check if there is a doc and what the ID of the doc is
        		//store the key
        		content.append(s+" ");
        	}
        	    content.append(" ");	
		}
		if(hasdoc){
			//convert the content from string to char array then return it 
			result.put(key, content.toString().toCharArray());
			return result;
		}
		else{
			// if hasdoc is false, there are no more docs
			reader.close();
	        fis.close();
			return null;
		}
	}
	
}
