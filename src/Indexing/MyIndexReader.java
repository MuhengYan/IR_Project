package Indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	public Map<Integer,String> docId=new HashMap<Integer,String>();
	private BufferedReader indexFile;
	String path;
	String type;
	List<int[][]> docFreq;
	
	
	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		docFreq = new LinkedList<>();
		this.type = type;
		if(type.equals("trecweb"))  path=Path.IndexWebDir; 
		else path=Path.IndexTextDir;
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) throws IOException {
		BufferedReader docIdFile = new BufferedReader(new FileReader(new File(path +"docId.txt")));	 
		String content = docIdFile.readLine(); 
		int docNo = -1;
		while(content!=null){
		  String[] builder = content.split(" ");   
		  if(builder[1].equals(docno)){
			  docNo=Integer.parseInt(builder[0]);
		  }
		  content = docIdFile.readLine(); 
		}
		
		return docNo;
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) throws IOException {
		BufferedReader docIdFile = new BufferedReader(new FileReader(new File(path +"docId.txt")));	 
		String content = docIdFile.readLine(); 
		String docNo = null;
		while(content!=null){
		  String[] builder = content.split(" ");   
		  if(Integer.parseInt(builder[0]) == docid){
			  docNo=builder[1];
		  }
		  content = docIdFile.readLine(); 
		}
		return docNo;
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		//calculate the number in each term
		int size = docFreq.size();
		int[][] ans = new int[size][2];
		int index = 0;
		for(int[][] t: docFreq){
			ans[index][0]=t[0][0];
			ans[index][1]=t[0][1];
			index++;
		}
		return ans;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		//number of rows in the GetPostingList array
		int i = token.charAt(0);
		int j =  i % 5;
		if(type.equals("trecweb")){
			  indexFile = new BufferedReader(new FileReader(new File(Path.IndexWebDir+j+".txt")));	
		}else{
			  indexFile = new BufferedReader(new FileReader(new File(Path.IndexTextDir+j+".txt")));		
		}
	    String content = indexFile.readLine();
		while(content!=null){	
		  String[] builder = content.split(" ");   
		  if(token.equals(builder[0])){
		  int[][] temp = new int[1][2]; 
		  temp[0][0] = Integer.parseInt(builder[1]) ;
		  temp[0][1] = Integer.parseInt(builder[2]) ;
		  docFreq.add(temp);
		  }
		  content = indexFile.readLine();
		}
		return docFreq.size();
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		//sum of the GetPostingList array[i][1]
		BufferedReader termFreqFile =  new BufferedReader(new FileReader(new File(path +"termFile.txt")));	 
		String content = termFreqFile.readLine();
		int collectionFreq = 0;
		while(content!=null){
		  String[] builder = content.split(" ");   
		  if(token.equals(builder[0])){
			  collectionFreq = Integer.parseInt(builder[1]);
		  }
		  content = termFreqFile.readLine();
		}
		return collectionFreq;
	}
	
	public void Close() throws IOException {
	}
	
}