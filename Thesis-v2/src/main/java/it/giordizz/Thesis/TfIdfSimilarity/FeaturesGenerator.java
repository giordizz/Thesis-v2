package it.giordizz.Thesis.TfIdfSimilarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * 
 * @author giordano
 * 
 * Crea il file contenente le features; ogni feature rappresenta il valore di cosine similarity medio tra il vettore
 * tf-idf ottenuto a partire dalla concatenazione degli snippets associati ad una query e quelli dei documenti caratterizzanti
 * ognuna delle 121 categorie,
 *
 */
public class FeaturesGenerator {

	PrintWriter out ;
	
	
	private ArrayList<ArrayList<String>> getGroups() throws IOException {
		HashMap<Integer,String> pageIdsToTitle = new HashMap<Integer,String>();
		
		BufferedReader br1 = new BufferedReader(new FileReader("data/My_IDs"));
		String line;
		while( (line=br1.readLine())!=null) {
			String[] pair = line.split("\t");
			pageIdsToTitle.put(Integer.parseInt(pair[1]), pair[0]);
		}
		br1.close();
		
		BufferedReader br2 = new BufferedReader(new FileReader("data/articlesOfIntermediateCategories.txt"));
		
		ArrayList<ArrayList<String>>  groups = new ArrayList<ArrayList<String>> ();

		while( (line=br2.readLine())!=null) {
			String[] ids = line.split("\t");
			
			ArrayList<String>  group = new ArrayList<String>();
			for (String id: ids){
				group.add(pageIdsToTitle.get(Integer.parseInt(id)));

			}
			
			groups.add(group);
		}
		br2.close();
		
		return groups;
		
		
		
	}
	
	public void generateFeatures(String setName, String typeOfText) throws IOException{
		

		
		
		out = new PrintWriter("data/" + typeOfText + "_" + setName +".txt");
		
		
		SimilarityCalculator sc = new SimilarityCalculator();
		
		sc.openIndex("./indexDir");
		
		
		ArrayList<ArrayList<String>> groups = getGroups();
		
		
		ArrayList<JSONObject> queries= (ArrayList<JSONObject>)JSONValue.parse(new InputStreamReader(new FileInputStream("data/" + setName + "_similarity.JSON")));
		int countQuery = 0;
		
		System.err.println("read all bolds and snippet of "+ queries.size() + " queries");
		
		for ( JSONObject query: queries){
			
//			if (!set.contains(queryText)) 
//				continue;
		
			System.out.println("queries # "+ ++countQuery);
			String allText = "";
			
			for (Object q : query.entrySet()){
				System.out.println(((Map.Entry<String,JSONObject>)  q).getKey());
			 ArrayList<String> texts = (ArrayList<String>) ((JSONObject) ((Map.Entry<String,JSONObject>)  q).getValue()).get(typeOfText);
			 for (String text : texts)
				 allText+=" " + text;
			} 
			Map<String, Float> weightsOfQuery = sc.getTfIdfWeights(allText);
			 
			 
			float[] features = new float[groups.size()]; 
	
			for (int ftrIdx = 0; ftrIdx < groups.size(); ftrIdx++){
				 float notNull=0;
				
				 for (String title :groups.get(ftrIdx)) {
					 float res = sc.getCosineSimilarity(weightsOfQuery, title);
					 if (res!=-1f){
						 features[ftrIdx]+=res;
						 notNull++;
					 }
						
					 
				 }
				 features[ftrIdx] /= notNull;
			}
			 
			writeFeaturesVector(features);

		}
		
		out.close();
	}
	
	private void writeFeaturesVector(float[] features) {
		for (int ftrIdx = 0; ftrIdx < features.length ; ftrIdx++){
			if (ftrIdx!=0)
				out.print(",");
			
			out.print(features[ftrIdx]);
			
		}
		out.println();	
		
	}	
	
	
	public static void main(String[] args) throws IOException {

		FeaturesGenerator fg = new FeaturesGenerator();

		fg.generateFeatures(args[0], args[1]);

	}


}
