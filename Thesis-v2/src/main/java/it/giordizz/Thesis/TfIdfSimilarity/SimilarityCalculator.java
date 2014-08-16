package it.giordizz.Thesis.TfIdfSimilarity;





import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

/**
 * 
 * @author giordano
 *
 * É incaricato di recupare i valori di tf-idf necessari per calcolare la cosine similiraty e di calcolarla.
 * Permette anche la tokenizzazione degli snippets.
 *
 */

public class SimilarityCalculator {
	
	private IndexSearcher searcher;
	private IndexReader reader;
	private Analyzer analyzer;
	private DefaultSimilarity similarity;
	private int numOfDocs;
	private String typeOfText;
	 
	 
	 
	public void openIndex(String path) throws IOException {

		this.typeOfText = "abstract";
		
		
		File indexDir = new File(path);
	    reader = DirectoryReader.open(FSDirectory.open(indexDir));
	    searcher = new IndexSearcher(reader);
	    
	    BufferedReader br = new BufferedReader(new FileReader("data/stopwords.txt"));
		
		CharArraySet stopWords = new CharArraySet(Version.LUCENE_4_9,173,true);

		String line;
		while ((line = br.readLine()) != null) 
			stopWords.add(line);
		br.close();
		
	    analyzer = new EnglishAnalyzer(Version.LUCENE_4_9,stopWords);
	    
	    
	    similarity = new DefaultSimilarity();
	    numOfDocs=reader.numDocs();
	    
	    System.err.println("num of docs " + numOfDocs );

        
	}
	public float getCosineSimilarity(Map<String, Float> tdIdfWeights, String title) throws IOException  {
		
		Query query = new TermQuery(new Term("title", title));
    	
		TopDocs hitsS =searcher.search(query, 1);		
		if (hitsS.scoreDocs.length==0)  {
//			System.err.println("document " + title + " not found");
			return -1f;
		}	
//		System.err.println("-> document " + title + " found");

		Map<String, Float> tdIdfWeightsCurrDoc =getTfIdfWeights(reader, hitsS.scoreDocs[0].doc);
		if (tdIdfWeightsCurrDoc==null)
				return -1f;
		HashSet<String> termsSet = new HashSet<String>();
		termsSet.addAll(tdIdfWeights.keySet());
		termsSet.addAll(tdIdfWeightsCurrDoc.keySet());
		
		
		
		RealVector v1 = toRealVector(tdIdfWeights, termsSet);
		RealVector v2 = toRealVector(tdIdfWeightsCurrDoc, termsSet);
		

		
        return (float) ((v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm()));
		
	}
	
	
	

	
   private Map<String, Float> getTfIdfWeights(IndexReader reader, int docId)
            throws IOException {
	   
        Terms vector = reader.getTermVector(docId, typeOfText);
  
        if (vector == null)
        	return null;
        
        TermsEnum termsEnum = null;
        termsEnum = vector.iterator(termsEnum);
       
        Map<String, Float> weights = new HashMap<String, Float>();        
        BytesRef text = null;
        while ((text = termsEnum.next()) != null) {
            String term = text.utf8ToString();
                       
            float freq = termsEnum.totalTermFreq();
            float idf =similarity.idf(reader.docFreq(new Term(typeOfText,term)),numOfDocs);
            weights.put(term, freq*idf);

        }
        return weights;
    }
   
   Map<String, Float> getTfIdfWeights(String textQuery) throws IOException{
		   
			
			
	   Map<String, Float> tfIdfWeights = new HashMap<String, Float>();

	   TokenStream tokens = analyzer.tokenStream(null, textQuery);
	   tokens.reset();
       try {
            while(tokens.incrementToken()) {

            	String curtTerm = tokens.getAttribute(CharTermAttribute.class).toString();
                Float value = tfIdfWeights.get(curtTerm);
                if (value==null)
                	tfIdfWeights.put(curtTerm, 1f);
                else	                	
                	tfIdfWeights.put(curtTerm, value+1f);
            }
       }
       catch(IOException e) {
       }
       
       tokens.close();
        
       for ( Entry<String, Float> term: tfIdfWeights.entrySet())
    	   term.setValue(term.getValue() * similarity.idf(reader.docFreq(new Term(typeOfText,term.getKey())), numOfDocs));
        
        
        
       return tfIdfWeights;
	        	
   }
		   
 
    private RealVector toRealVector(Map<String, Float> map, HashSet<String> termsSet) {
        RealVector vector = new ArrayRealVector(termsSet.size());
        int i = 0;
        for (String term : termsSet) {
            float value = map.containsKey(term) ? map.get(term) : 0;
            vector.setEntry(i++, value);
        }
        return (RealVector) vector.mapDivide(vector.getL1Norm());
    }
	    
    public void printAllDocumentsTitles() throws IOException {
    	System.err.println("printings docs titles");
    	for (int docID = 0; docID < numOfDocs ; docID++ ){
    		reader.document(docID);
    		System.err.println(reader.document(docID).getFields().get(0).stringValue());
    		System.err.println(reader.getTermVector(docID, typeOfText));
    		
//    		break;
    	}
    		
    }

	
}
