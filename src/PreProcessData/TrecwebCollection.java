package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

/**
 * This is for INFSCI 2140 in 2015
 *
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private FileInputStream fis = null;
    private BufferedReader reader = null;  
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		fis = new FileInputStream(Path.DataWebDir);
        reader = new BufferedReader(new InputStreamReader(fis));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use it any more
		Map<String, Object> result=new HashMap<String, Object>();
		String line="";
		String key="";
		//use string builder to store the content so space in memory can be saved
		StringBuilder content=new StringBuilder();
		int l=0;
		boolean hasdoc=false;
		boolean startc=false;
		boolean stopall=false;
		//make sure to exam if we should continue read next line before read next line
        while(!stopall&&(line =reader.readLine()) != null){
        		l=line.length();
        		//check if there is a doc and what the ID is
        		//if there is a ID but the content is already being recorded
        		//return the reader to last line and break the loop to return the content
        		if(line.startsWith("<DOCNO>")){
        			if(startc){
        				reader.reset();
        				break;
        			}
        			//add the key
        			key=line.substring(7, l-8);
        			hasdoc=true;
        			continue;
        		}
        		//start record the content
        		else if(line.startsWith("</DOCHDR>")){
        			startc=true;
        		}
        		//stop record the content and stop reading file
        		else if(line.endsWith("</DOC>")){
        			startc=false;
        			stopall=true;
       			}
        		//delete the tags
        		else if(startc){
       				content.append(line+" ");
        		}
        		reader.mark(2);
        }
		if(hasdoc){
			//convert result string content to char array
			result.put(key, content.toString().replaceAll("<[^>]*>"," ").toCharArray());
			return result;
		}
		else{
			//there is no doc left, return null and stop the reader
			reader.close();
	        fis.close();
			return null;
		}
	}
	
}
