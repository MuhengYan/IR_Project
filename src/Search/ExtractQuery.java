package Search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Classes.Path;
import Classes.Query;
import PreProcessData.StopWordRemover;
import PreProcessData.WordNormalizer;
import PreProcessData.WordTokenizer;


public class ExtractQuery {
	private BufferedReader queryBuffer;
    String context;
    Query aquery = new Query();
    StopWordRemover stopwordRemover = new StopWordRemover();
	WordNormalizer normalizer = new WordNormalizer();
    
	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
	    //NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		queryBuffer = new BufferedReader(new FileReader(new File(Path.TopicDir)));
	}
	
	public boolean hasNext() throws IOException
	{
		if(queryBuffer.readLine() != null)  return true;
	    else return false;
	}
	
	public Query next() throws IOException
	{   
		context = queryBuffer.readLine();
		while(context != null){
		  if(context.contains("<num>")){
			aquery.SetTopicId(context.trim().substring(14).trim());
		  }  
		  if(context.contains("<title>")){
			aquery.SetQueryContent(context.substring(8).trim());  
		  } 
		  if(context.contains("</top>"))  break; 
		  
		  context=queryBuffer.readLine();
		}
		WordTokenizer tokenizer = new WordTokenizer(aquery.GetQueryContent().toCharArray());
		char[] word = null;
		String results ="";
		while ((word = tokenizer.nextWord()) != null) {
			word = normalizer.lowercase(word);
			if (!stopwordRemover.isStopword(word))
				results=results+" "+normalizer.stem(word);
			
		}
		aquery.SetQueryContent(results);
		
		return aquery;
	}
}
