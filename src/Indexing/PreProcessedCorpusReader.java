package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	private BufferedReader doc;
	
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		doc = new BufferedReader(new FileReader(new File(Path.ResultHM1+type)));		
	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		Map<String,String> docInfor=new HashMap<String,String>();
		String docContent = doc.readLine();
		if(docContent!=null){
	      docInfor.put("DOCNO", docContent);
		  docInfor.put("CONTENT",doc.readLine().trim());
		  return docInfor;
		}else{
		  doc.close();	
		  return null;	
		}	
	}

}
