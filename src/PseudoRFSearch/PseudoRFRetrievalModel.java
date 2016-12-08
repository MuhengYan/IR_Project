package PseudoRFSearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import Search.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	final double COLLECTION_NUMBER = 142065539;
	HashMap<Integer,HashMap<String,Double>> TermDocScore=new HashMap<Integer,HashMap<String,Double>>();
	HashMap<Integer,HashMap<String,Integer>> docQuery = new HashMap<Integer,HashMap<String,Integer>>();
	List<Document> feedbackDocuments=new ArrayList<Document>();
	
	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
	    HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);
		HashMap<String,Double> queryScores = new HashMap<String,Double>();
		String[] queryContent = aQuery.GetQueryContent().trim().split(" ");
		
		double scores=1;
		double docScore=0;
		//calculate the scores 
		for(int j=0;j<TopK;j++){
			scores=1;
			for(int i=0;i<queryContent.length;i++){
			  if(TokenRFScore.get(queryContent[i])!=0){
				int docId=Integer.parseInt(feedbackDocuments.get(j).docid());  
			    double docFrequence = TermDocScore.get(docId).get(queryContent[i]);
			    docScore=alpha*docFrequence+(1-alpha)*TokenRFScore.get(queryContent[i]);
				scores=scores*docScore;
			  }
			  else continue;	}
			  queryScores.put(feedbackDocuments.get(j).docid(), scores);
		}
	
		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();
		//sort
		List<Map.Entry<String,Double>> infoIds = new ArrayList<Map.Entry<String,Double>>(queryScores.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {  
		     public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {  
		       if(o2.getValue() > o1.getValue()) return 1;
		          else return -1;
		          }
		       }); 		
        
    	for(int i=0;i<TopN;i++){
			Document doc=new Document(infoIds.get(i).getKey(),ixreader.getDocno(Integer.parseInt(infoIds.get(i).getKey())),infoIds.get(i).getValue());
			results.add(doc);
		}
		return results;
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
		feedbackDocuments=retrieveQuery(aQuery,TopK);
		String[] queryContent = aQuery.GetQueryContent().trim().split(" ");
		int feedbackLength = 0;
		for(int j=0;j<TopK;j++){
			feedbackLength+=ixreader.docLength(Integer.parseInt(feedbackDocuments.get(j).docid()));
		}
		double u=2000;
		for(int i=0;i<queryContent.length;i++){
			int tf=0;
			for(int j=0;j<TopK;j++){
				int docFrequency=0;
				int docId=Integer.parseInt(feedbackDocuments.get(j).docid());
			    HashMap<String,Integer> count=new HashMap<String,Integer>();
			    count=docQuery.get(docId); 
				if(count.containsKey(queryContent[i]))
					docFrequency=count.get(queryContent[i]);
				else docFrequency=0;
				tf=tf+docFrequency;	
			}
			double score=(tf+u*ixreader.CollectionFreq(queryContent[i])/COLLECTION_NUMBER)/(feedbackLength+u);
			
			TokenRFScore.put(queryContent[i], score);
		}
		
		return TokenRFScore;
	}
	
	public HashMap<Integer,HashMap<String,Integer>> documnetQuery(Query aQuery) throws IOException{
		String[] queryContent = aQuery.GetQueryContent().trim().split(" ");
		for(int j=0;j<queryContent.length;j++){
			String a = queryContent[j];
			int[][] documentLists;
			if(ixreader.CollectionFreq(a)!=0){
				documentLists = ixreader.getPostingList(a);
			}
			else{
				continue;	
			} 
			for(int i=0;i<documentLists.length;i++){
				HashMap<String,Integer> newQuery = new HashMap<String,Integer>();
				if(docQuery.containsKey(documentLists[i][0])){
					newQuery = docQuery.get(documentLists[i][0]);
					newQuery.put(a, documentLists[i][1]);
				//	docQuery.put(documentLists[i][0], newQuery);
				}else{
					newQuery.put(a, documentLists[i][1]);
					docQuery.put(documentLists[i][0], newQuery);
				}
			}
		}
		return docQuery;
	}
	
	public List<Document> retrieveQuery( Query aQuery, int TopN) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		//docid docno docscores
		
		docQuery=documnetQuery(aQuery);
		List<Document> queryResult = new ArrayList<Document>() ;
		HashMap<Integer,Double> queryScores = new HashMap<Integer,Double>();
		
		double u=2000;
		String[] queryContent = aQuery.GetQueryContent().trim().split(" ");
		double scores=1;
		
		//calculate the scores for each document
		for(Entry<Integer, HashMap<String, Integer>> entry : docQuery.entrySet()){
			scores=1;
			double docScore=0;
			double docLength = ixreader.docLength(entry.getKey());
			Map<String, Integer> count = entry.getValue();
			for(int i=0;i<queryContent.length;i++){
			  if(ixreader.CollectionFreq(queryContent[i])!=0){
				if(count.containsKey(queryContent[i])){
					docScore=(count.get(queryContent[i])+u*ixreader.CollectionFreq(queryContent[i])/COLLECTION_NUMBER)/(docLength+u);
					
				}else{
					docScore=(0+u*ixreader.CollectionFreq(queryContent[i])/COLLECTION_NUMBER)/(docLength+u);
				}
				if(TermDocScore.containsKey(entry.getKey())){
					HashMap<String,Double> a=TermDocScore.get(entry.getKey());
					a.put(queryContent[i], docScore);
				//	TermDocScore.put(entry.getKey(), a);
				}else{
					HashMap<String,Double> a=new HashMap<String,Double>();
					a.put(queryContent[i], docScore);
					TermDocScore.put(entry.getKey(), a);
				}
				scores=scores*docScore;
				}
			  else continue;	
			}
			queryScores.put(entry.getKey(), scores);
		}
		
		//sort
		List<Map.Entry<Integer,Double>> infoIds = new ArrayList<Map.Entry<Integer,Double>>(queryScores.entrySet()); 
		Collections.sort(infoIds, new Comparator<Map.Entry<Integer, Double>>() {  
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {  
            	if(o2.getValue() > o1.getValue()) return 1;
            	else return -1;
                 
            }
        }); 
	
	
		//add to queryResult
		for(int i=0;i<TopN;i++){
			Document doc=new Document(infoIds.get(i).getKey().toString(),ixreader.getDocno(infoIds.get(i).getKey()),infoIds.get(i).getValue());
	    	queryResult.add(doc);
		}
		
	    return queryResult;
	}
	
	
}