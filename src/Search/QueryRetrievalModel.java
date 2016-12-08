package Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	final double COLLECTION_NUMBER = 142065539;
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		//docid docno docscores
		List<Document> queryResult = new ArrayList<Document>() ;
		HashMap<Integer,Double> queryScores = new HashMap<Integer,Double>();
		HashMap<Integer,HashMap<String,Integer>> docQuery = new HashMap<Integer,HashMap<String,Integer>>();
		double u=2000;
		String[] queryContent = aQuery.GetQueryContent().trim().split(" ");
		double scores=1;
		//search for all relevant documents
		for(int j=0;j<queryContent.length;j++){
			String a = queryContent[j];
			int[][] documentLists;
			if(indexReader.CollectionFreq(a)!=0){
				documentLists = indexReader.getPostingList(a);
			}
			else{
				continue;	
			} 
			for(int i=0;i<documentLists.length;i++){
				HashMap<String,Integer> newQuery = new HashMap<String,Integer>();
				if(docQuery.containsKey(documentLists[i][0])){
					newQuery = docQuery.get(documentLists[i][0]);
					newQuery.put(a, documentLists[i][1]);
					docQuery.put(documentLists[i][0], newQuery);
				}else{
					newQuery.put(a, documentLists[i][1]);
					docQuery.put(documentLists[i][0], newQuery);
				}
			}
		}
		//calculate the scores for each document
		for(Entry<Integer, HashMap<String, Integer>> entry : docQuery.entrySet()){
			scores=1;
			double docScore=0;
			double docLength = indexReader.docLength(entry.getKey());
			Map<String, Integer> count = entry.getValue();
			for(int i=0;i<queryContent.length;i++){
			  if(indexReader.CollectionFreq(queryContent[i])!=0){
				if(count.containsKey(queryContent[i])){
					docScore=(count.get(queryContent[i])+u*indexReader.CollectionFreq(queryContent[i])/COLLECTION_NUMBER)/(docLength+u);
					
				}else{
					docScore=(0+u*indexReader.CollectionFreq(queryContent[i])/COLLECTION_NUMBER)/(docLength+u);
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
			Document doc=new Document(infoIds.get(i).getKey().toString(),indexReader.getDocno(infoIds.get(i).getKey()),infoIds.get(i).getValue());
	    	queryResult.add(doc);
		}
	    return queryResult;
	}
 	
}