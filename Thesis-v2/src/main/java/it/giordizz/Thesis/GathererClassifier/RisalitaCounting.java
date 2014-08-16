package it.giordizz.Thesis.GathererClassifier;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import libsvm.svm_node;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



public class RisalitaCounting {
	
	public class SPair {
		Integer id=0;
		Float value=0f;
		
		public SPair(Integer id, Float value) {
			this.id=id;
			this.value=value;
		}

		public SPair() {
		}
		
		
	}
	
	Map<Integer,Vector<Integer>> articles = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<Integer>> categories = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<SPair>> jaccardIndexes = new HashMap<Integer, Vector<SPair>>();	
	Map<Integer,Integer> redirects = new HashMap<Integer, Integer>();
	

	HashSet<Integer> main_topic;
	JSONArray jsonTraining;
	JSONArray jsonValidation;
	JSONArray jsonTraining1;
	JSONArray jsonValidation1;
	TreeSet<Integer> intermediate_categories = new TreeSet<Integer>();;
	HashSet<String> queryNotTagged = new HashSet<String>();
	String setName;

	RisalitaCounting(){

		Integer[] aux= new Integer[]{694871,4892515,694861,3103170,693800,751381,693555,1004110,1784082,8017451,691928,690747,692348,696603,691008,695027,42958664,691182,693708,696648};
		main_topic = new HashSet<Integer>(Arrays.asList(aux));
	}
	
	public void getStatus() throws IOException{
		
		GZIPInputStream	gzip1=null, gzip2=null, gzip3=null;
		
		gzip1 = new GZIPInputStream(new FileInputStream("../../dbpedia/wiki-article-categories-links-sorted.gz"));
		gzip2 = new GZIPInputStream(new FileInputStream("../../dbpedia/wiki-categories-only-links-sorted.gz"));
		gzip3 = new GZIPInputStream(new FileInputStream("../../dbpedia/jaccardFileWithVals.txt.gz"));
		
		
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(gzip1));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		BufferedReader br3 = new BufferedReader(new InputStreamReader(gzip3));


		String line="";
		while((line=br1.readLine())!=null) {
			final String[] s=line.split("\t");
			Vector<Integer> cur = articles.get(Integer.parseInt(s[0]));
			if (cur==null) 
				articles.put(Integer.parseInt(s[0]),new Vector<Integer>(){/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				{add(Integer.parseInt(s[1]));}});
			 else {
				cur.add(Integer.parseInt(s[1]));
				articles.put(Integer.parseInt(s[0]),cur);

			}
		}
		br1.close();

		while((line=br2.readLine())!=null) {
			final String[] s=line.split("\t");
			Vector<Integer> cur = categories.get(Integer.parseInt(s[0]));
			if (cur==null) 
				categories.put(Integer.parseInt(s[0]),new Vector<Integer>(){/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				{add(Integer.parseInt(s[1]));}});
			else {
				cur.add(Integer.parseInt(s[1]));
				categories.put(Integer.parseInt(s[0]),cur);
			}
		}	
		br2.close();
		


		
		while((line=br3.readLine())!=null) {
			Integer id = Integer.parseInt(line);
			final String[] s=br3.readLine().split("\t");

				
			Vector<SPair> aux = new Vector<SPair>();
			
			for (int topI=0; topI < Math.min(s.length,21) ; topI++ ){
				
			
				final String[] ss= s[topI].split(",");
				aux.add(new SPair(Integer.parseInt(ss[0]),Float.parseFloat(ss[1])));
				
			}
			jaccardIndexes.put(id, aux);

		}			
		br3.close();
		
		
		jsonTraining= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("data/training_set_tagged.JSON")));
		jsonValidation= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("data/validation_set_tagged.JSON")));
		

		
		jsonTraining1= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("data/training_entitiesSnippets.JSON")));
		jsonValidation1= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("data/validation_entitiesSnippets.JSON")));
		
		
		BufferedReader br4 = new BufferedReader(new FileReader("../../Download/My_redirect_ID_2"));
		while( (line=br4.readLine())!=null) {
			final String[] s=line.split("\t");
		     redirects.put(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
		   
		}
		
		br4.close();
		
		
		
//		BufferedReader br5 = new BufferedReader(new FileReader("../data/AllCategoriesSorted.txt"));
		BufferedReader br5 = new BufferedReader(new FileReader("data/new_intermediateCatSorted"));
		while( (line=br5.readLine())!=null) 
		     intermediate_categories.add(Integer.parseInt(line));
		     			
		br5.close();
		
		
		
		BufferedReader br6 = new BufferedReader(new FileReader("query_not_tagged.txt"));
		while( (line=br6.readLine())!=null) 
		     queryNotTagged.add(line);
		     			
		br6.close();
		
		System.err.println("************** Status loaded! **************");
	}
	

	
	public Vector<Integer> checkVisited(Integer cat, HashSet<Integer> visited){
		Vector<Integer> aux=new  Vector<Integer>();
		
		
		try{

			for (Integer c : this.categories.get(cat)) {
				if (!visited.contains(c))
					aux.add(c);
			}
	} catch(Exception e) {
		
	}
	
			
			
			return aux;
		
		
	}
	
	public HashSet<Integer> climb(HashSet<Integer> categories, HashSet<Integer> result,HashSet<Integer> visited,int height, int maxHeight){
		if (categories.isEmpty()|| height > maxHeight )
				return result;

		HashSet<Integer> toExplore= new HashSet<Integer>();;

		
	
		for(Integer cat: categories) {

			if (!main_topic.contains(cat))
				toExplore.addAll(checkVisited(cat,visited));
			
			if (intermediate_categories.contains(cat)) {
					result.add(cat);

			}
				
		}

		visited.addAll(toExplore);
	
		return climb(toExplore,result,visited,height+1,maxHeight);
		
		
		
	}	
	

	public HashSet<Integer> getCategoriesByTag(Integer tag){
		
		HashSet<Integer> cats= new HashSet<Integer>();

		try {
			cats.addAll(articles.get(tag));
		} catch (Exception e) {
			try {
				cats.addAll(articles.get(redirects.get(tag)));
			} catch (Exception e1) {

			}
			
		}
	
	return cats;
	
	
}
	
	public HashSet<Integer> tag(String query,Integer tag, int maxHeight){
		
		HashSet<Integer> aux= getCategoriesByTag(tag);

		return climb(aux,new HashSet<Integer>(),new HashSet<Integer>(aux),1, maxHeight);
		
	}	
	


	private void writeResultOnFile(HashMap<Integer, Integer> result1, HashMap<Integer, Integer> result2, int whichDataSet, SVMClassifier s) {
		
		
		

		Vector<svm_node> auxNodeVect = new Vector<svm_node>();
		
		int c=0;
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result1.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
//					
			}
		}
		
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result2.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
					
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
					
			}
		}
		
		
		
		svm_node[] nodeVect = new svm_node[auxNodeVect.size()];
		for (int i=0; i< nodeVect.length; i++)
			nodeVect[i] = auxNodeVect.elementAt(i);

		s.addExample(nodeVect, whichDataSet);
		
	}
//	public void tagAll(SVMClassifier s, int whichDataSet){
//		System.err.println("************** Start tagging! **************");
//		
//		
//		Iterator i = (whichDataSet==0) ? jsonTraining.iterator() : jsonValidation.iterator();
//		HashMap<Integer,Integer> result1 = new HashMap<Integer,Integer>(); 
//		HashMap<Integer,Integer> result2 = new HashMap<Integer,Integer>(); 
//		
//		
//		int count=1;
//		while( i.hasNext()){
//			JSONObject q = (JSONObject) i.next();
//			String query ="";// (String) q.get("query");
//			
////			if (queryNotTagged.contains(query))
////				continue;
//			
//			
//			ArrayList<Integer> entities = new ArrayList<Integer>();
//			int count1=0;
//			for (Object query1:q.entrySet())	{
//				count1++;
//				ArrayList<Long> ents = (ArrayList<Long>) ((Map.Entry) query1).getValue();
//				for (int firstI=0; firstI< firstN; firstI++)
//					entities.add(ents.get(firstI).intValue());
//			}
//			System.err.println("-------------> entities " + entities.size());
//			if(count1>1)
//				System.err.println("*************************** ERRRRRROOOOOOOOOREEEE");
//
//			
//			
////			for (Long id : (ArrayList<Long>) q.get("tags"))
////				entities.add(id.intValue());
////			System.err.println("prima " + entities.size());
//			
//			ArrayList<Integer> topKs = getTopKSimilarEntity2(entities);
//			
////			entities.addAll(topKs);  --> metodo con fusione topk #feature=123
////			System.err.println("dopo " + entities.size());
//			
////			int numOfEntity = entities.size();
//
//			result1.clear();
//			result2.clear();
////			for (int i1=0;i1<numOfEntity;i1++){ 
//				
//			for (Integer id : entities){
//
//				/*
//				 * itero sui resultati
//				 */
//				 for (Integer cat: tag(query, id)){
//						Integer curr_count =result1.get(cat);
//						if (curr_count == null)
//							result1.put(cat,1);
//						else
//							result1.put(cat, curr_count+1);
//							
//				 }
//			}
//			
//			
//			for (Integer id : topKs){
//
//				/*
//				 * itero sui resultati
//				 */
//				 for (Integer cat: tag(query, id)){
//						Integer curr_count =result2.get(cat);
//						if (curr_count == null)
//							result2.put(cat,1);
//						else
//							result2.put(cat, curr_count+1);
//							
//				 }
//			}
//			
//			
//			//System.err.println("stampo i risultati");
//
////			writeResultOnFile(result);
//			writeResultOnFile(result1, result2, whichDataSet, s);
//			
//			
//			
//			System.err.println("Tagged #" + count++);
//		}
//		
//		
//		
//		System.err.println("stop tagging");
////		System.err.println("results written on  -> ../Thesis/data/results_"+ setName  +".txt");
////		System.err.println("results written on  -> ../Thesis/data/new8top"+topK+"/results_"+ setName  +".txt");
//	}
	
	/**
	 * entitity standard + entity snippets
	 * 
	 * @param s
	 * @param whichDataSet
	 * @param maxHeight
	 * @param topK
	 * @param firstN
	 */
	public void tagAll2(SVMClassifier s, int whichDataSet, int maxHeight, int topK, int firstN){
		System.err.println("************** Start tagging! **************");
		
		
		JSONArray arr = (whichDataSet==0) ? jsonTraining : jsonValidation;
		JSONArray arr1 = (whichDataSet==0) ? jsonTraining1 : jsonValidation1;
		HashMap<Integer,Integer> result1 = new HashMap<Integer,Integer>(); 
		HashMap<Integer,Integer> result2 = new HashMap<Integer,Integer>(); 
		HashMap<Integer,Integer> result3 = new HashMap<Integer,Integer>(); 
		HashMap<Integer,Integer> result4 = new HashMap<Integer,Integer>(); 	
		
		int count=1;
		for (int idx=0; idx < arr.size() ; idx++ ){
			JSONObject q1 = (JSONObject) arr.get(idx);
			JSONObject q2 = (JSONObject) arr1.get(idx);
			String query =(String) q1.get("query");
			System.err.println(query);
			
//			if (queryNotTagged.contains(query))
//				continue;
			
			ArrayList<Integer> entities = new ArrayList<Integer>();
			
			for (Long id : (ArrayList<Long>) q1.get("tags"))
				entities.add(id.intValue());
			
			ArrayList<Integer> topKs = getTopKSimilarEntity2(entities,topK); 
			
			ArrayList<Integer> entities1 = new ArrayList<Integer>();
			int count1=0;
			for (Object query1:q2.entrySet())	{
				count1++;
				ArrayList<Long> ents = (ArrayList<Long>) ((Map.Entry) query1).getValue();
				
				HashSet<Integer> set = new HashSet<Integer>();
				for (Long ent:ents)
					set.add(ent.intValue());
				
				if (firstN==-1)
					firstN=set.size();
				else
					firstN=Math.min(firstN, set.size());
				
				if (firstN==0)
					System.err.println("ERRORE!");
					
			
				Iterator<Integer> it = set.iterator();
				for (int firstI=0; firstI< firstN; firstI++)
					entities1.add(it.next());
				
				
			}

			
			System.err.println("-------------> entities " + entities1.size());
			if(count1!=1)
				System.err.println("*************************** ERRRRRROOOOOOOOOREEEE");

			
			ArrayList<Integer> topKs1 = getTopKSimilarEntity2(entities1, topK);
			


			result1.clear();
			result2.clear();
			result3.clear();
			result4.clear();


			
			for (Integer id : entities){

				/*
				 * itero sui resultati
				 */
				 for (Integer cat: tag(query, id, maxHeight)){
						Integer curr_count =result1.get(cat);
						if (curr_count == null)
							result1.put(cat,1);
						else
							result1.put(cat, curr_count+1);
							
				 }
			}
			
			
			for (Integer id : topKs){

				/*
				 * itero sui resultati
				 */
				 for (Integer cat: tag(query, id, maxHeight)){
						Integer curr_count =result2.get(cat);
						if (curr_count == null)
							result2.put(cat,1);
						else
							result2.put(cat, curr_count+1);
							
				 }
			}
			
			
			for (Integer id : entities1){

				/*
				 * itero sui resultati
				 */
				 for (Integer cat: tag(query, id, maxHeight)){
						Integer curr_count =result3.get(cat);
						if (curr_count == null)
							result3.put(cat,1);
						else
							result3.put(cat, curr_count+1);
							
				 }
			}
			
			
			for (Integer id : topKs1){

				/*
				 * itero sui resultati
				 */
				 for (Integer cat: tag(query, id, maxHeight)){
						Integer curr_count =result4.get(cat);
						if (curr_count == null)
							result4.put(cat,1);
						else
							result4.put(cat, curr_count+1);
							
				 }
			}
			
			
		

			writeResultOnFile(result1, result2, result3, result4, whichDataSet, s);
			
			
			
			System.err.println("Tagged #" + count++);
		}
		
		
		
		System.err.println("stop tagging");

	}
	
	

	private void writeResultOnFile(HashMap<Integer, Integer> result1,
			HashMap<Integer, Integer> result2,
			HashMap<Integer, Integer> result3,
			HashMap<Integer, Integer> result4, int whichDataSet, SVMClassifier s) {
		
		Vector<svm_node> auxNodeVect = new Vector<svm_node>();
		
		int c=0;
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result1.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
			}
		}
		
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result2.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
					
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
					
			}
		}
		
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result3.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
					
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
					
			}
		}
		
		for (Iterator<Integer> it = intermediate_categories.iterator(); it.hasNext(); c++ ) {
			Integer ii = result4.get(it.next());
			if (ii!=null){
				
				svm_node auxNode = new svm_node();
				auxNode.index = c+1;
					
				auxNode.value = ii;
				auxNodeVect.add(auxNode);
					
			}
		}
		
		svm_node[] nodeVect = new svm_node[auxNodeVect.size()];
		for (int i=0; i< nodeVect.length; i++)
			nodeVect[i] = auxNodeVect.elementAt(i);

		s.addExample(nodeVect, whichDataSet);
		
	}

	private ArrayList<Integer> getTopKSimilarEntity2(ArrayList<Integer> entities, int topK) {

		ArrayList<List<SPair>> aux = new ArrayList<List<SPair>>();
		
		
		for (Integer id : entities) {
			try {
				Vector<SPair> l = (jaccardIndexes.get(id));
				int toIdx = Math.min(l.size(), topK);
				aux.add(l.subList(0, toIdx));

			} catch (Exception e) {
			}


		}
		if (aux.size()==0)
			return  new ArrayList<Integer>();

		int[] indexes = new int[aux.size()];
		

		ArrayList<SPair> ris= new ArrayList<SPair>(topK); 	
		for (int ii=0 ; ii< topK; ii++	) {
			ris.add(new SPair());
		}
		
		
			for (int j=0;j< topK; j++){
				int maxIndex=0;

				for (int jj=0; jj< aux.size(); jj++)
						if (aux.get(jj)==null || indexes[jj] >= aux.get(jj).size())
							continue;
						else if (ris.get(j).value < aux.get(jj).get(indexes[jj]).value) {
								ris.set(j,aux.get(jj).get(indexes[jj]));
								maxIndex=jj;
							}
				indexes[maxIndex]++;
			}
		
	
		ArrayList<Integer> risID = new ArrayList<Integer>(topK); 
		for (int k=0; k< ris.size();k++)
			risID.add(ris.get(k).id);

		return risID;
	}

	public void addCategoryToIntermediateSet(Integer catToAdd) {
		intermediate_categories.add(catToAdd);
		
	}

	public int getMaxNumOfFtr(){
		return intermediate_categories.size()*4;
		
	}
				
		
	



}
