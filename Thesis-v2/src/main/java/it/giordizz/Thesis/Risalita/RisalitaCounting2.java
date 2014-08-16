package it.giordizz.Thesis.Risalita;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class RisalitaCounting2 {
	
	
	public class SPair {
		Integer id=0;
		Float value=0f;
		
		public SPair(Integer id, Float value) {
			this.id=id;
			this.value=value;
		}

		public SPair() {
			// TODO Auto-generated constructor stub
		}
		
		
	}
	
	
	
	
	
	
	Map<Integer,Vector<Integer>> articles = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<Integer>> categories = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Vector<SPair>> jaccardIndexes = new HashMap<Integer, Vector<SPair>>();
//	Map<Integer,Vector<Integer>> jaccardIndexes = new HashMap<Integer, Vector<Integer>>();
	Map<Integer,Integer> redirects = new HashMap<Integer, Integer>();
	
	int maxHeight;
	HashSet<Integer> main_topic;
	JSONArray json;
	Vector<Integer> intermediate_categories= new Vector<Integer> ();
	HashSet<String> queryNotTagged = new HashSet<String>();
	String setName;
	PrintWriter output;
//	int topK;
	Vector<Integer> allCategories = new Vector<Integer>();
	int currCatIdx = 0;

	RisalitaCounting2(int height,String setName) throws FileNotFoundException, UnsupportedEncodingException{
		maxHeight = height;
		this.setName = setName;
//		this.topK = topK;
		
		Integer[] aux= new Integer[]{694871,4892515,694861,3103170,693800,751381,693555,1004110,1784082,8017451,691928,690747,692348,696603,691008,695027,42958664,691182,693708,696648};
		main_topic = new HashSet<Integer>(Arrays.asList(aux));
	}
	
	public void getStatus(int topK) throws IOException{
		
		GZIPInputStream	gzip1=null, gzip2=null, gzip3=null;
		
		gzip1 = new GZIPInputStream(new FileInputStream("../dbpedia/wiki-article-categories-links-sorted.gz"));
		gzip2 = new GZIPInputStream(new FileInputStream("../dbpedia/wiki-categories-only-links-sorted.gz"));
		gzip3 = new GZIPInputStream(new FileInputStream("../dbpedia/jaccardFileWithVals.txt.gz")); //TODO
		
		
		
		BufferedReader br1 = new BufferedReader(new InputStreamReader(gzip1));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		BufferedReader br3 = new BufferedReader(new InputStreamReader(gzip3));


		String line="";
		while((line=br1.readLine())!=null) {
			final String[] s=line.split("\t");
			Vector<Integer> cur = articles.get(Integer.parseInt(s[0]));
			if (cur==null) 
				articles.put(Integer.parseInt(s[0]),new Vector<Integer>(){{add(Integer.parseInt(s[1]));}});
			 else {
				cur.add(Integer.parseInt(s[1]));
//				articles.put(Integer.parseInt(s[0]),cur);

			}
		}
		br1.close();

		while((line=br2.readLine())!=null) {
			final String[] s=line.split("\t");
			Vector<Integer> cur = categories.get(Integer.parseInt(s[0]));
			if (cur==null) 
				categories.put(Integer.parseInt(s[0]),new Vector<Integer>(){{add(Integer.parseInt(s[1]));}});
			else {
				cur.add(Integer.parseInt(s[1]));
//				categories.put(Integer.parseInt(s[0]),cur);
			}
		}	
		br2.close();
		

//		if (topK>0) 
//			while((line=br3.readLine())!=null) {
//				Integer id = Integer.parseInt(line);
//				final String[] s=br3.readLine().split("\t");
//	
//					
//				Vector<Integer> aux = new Vector<Integer>();
//				
//				for (int topI=0; topI < Math.min(s.length,topK) ; topI++ ){
//					
//				}
//					final String[] ss= s[topI].split(",");
//					aux.add(Integer.parseInt(s[topI]));
//				jaccardIndexes.put(id, aux);
//	
//			}			
//		br3.close();
//		
		if (topK>0) 
			while((line=br3.readLine())!=null) {
				Integer id = Integer.parseInt(line);
				final String[] s=br3.readLine().split("\t");
	
					
				Vector<SPair> aux = new Vector<SPair>();
				
				for (int topI=0; topI < Math.min(s.length,topK) ; topI++ ){
					
				
					final String[] ss= s[topI].split(",");
					aux.add(new SPair(Integer.parseInt(ss[0]),Float.parseFloat(ss[1])));
					
				}
				jaccardIndexes.put(id, aux);
	
			}			
		br3.close();
		
		
		json= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("../data/"+setName+"_set_tagged.JSON")));
		
		
		BufferedReader br4 = new BufferedReader(new FileReader("../Download/My_redirect_ID_2"));
		while( (line=br4.readLine())!=null) {
			final String[] s=line.split("\t");
		     redirects.put(Integer.parseInt(s[0]),Integer.parseInt(s[1]));
		   
		}
		
		br4.close();
		
		
		//TODO
//		BufferedReader br5 = new BufferedReader(new FileReader("../data/AllCategoriesSorted.txt"));
		BufferedReader br5 = new BufferedReader(new FileReader("../data/CatIntermadiateID"));
		while( (line=br5.readLine())!=null) 
		     intermediate_categories.add(Integer.parseInt(line));
		     			
		br5.close();
		
		BufferedReader br6 = new BufferedReader(new FileReader("query_not_tagged.txt"));
		while( (line=br6.readLine())!=null) 
		     queryNotTagged.add(line);
		     			
		br6.close();
		BufferedReader br7 = new BufferedReader(new FileReader("../data/AllCategoriesSorted.txt"));
		while( (line=br7.readLine())!=null) {
			if (!intermediate_categories.contains(Integer.parseInt(line)))
		     allCategories.add(Integer.parseInt(line));
			if (allCategories.size() == 20 )
				break;
		}
		     			
		br7.close();
		System.err.println("************** Status loaded! **************");
	}
	
	
	
	
	public Vector<Integer> checkVisited(Integer cat, HashSet<Integer> visited){
		Vector<Integer> aux=new  Vector<Integer>();
		
		
		try{
//			aux.addAll(this.categories.get(cat));	
			for (Integer c : this.categories.get(cat)) {
				if (!visited.contains(c))
					aux.add(c);
			}
	} catch(Exception e) {
		
	}
	
			
			
			return aux;
		
		
	}
	
	
	
	public HashSet<Integer> climb(HashSet<Integer> categories, HashSet<Integer> result,HashSet<Integer> visited,int height){
		if (categories.isEmpty()|| height > maxHeight )
				return result;

		HashSet<Integer> toExplore= new HashSet<Integer>();;

		
	
		for(Integer cat: categories) {

			if (!main_topic.contains(cat))
				toExplore.addAll(checkVisited(cat,visited));
			
			if (intermediate_categories.contains(cat)) {
//				TODO
//				if (!result.contains(cat))
					result.add(cat);

			}
				
		}
		
//		visited.addAll(categories);
		visited.addAll(toExplore);
//		System.out.println("da esploreare: \n" + toExplore);
//		System.out.println("curr results: \n" + result);
		
		return climb(toExplore,result,visited,height+1);
		
		
		
	}	
	
	public Vector<Integer> getCategoriesByTags(Vector<Integer> tags){
		Vector<Integer> cats= new Vector<Integer>();
		
		for (Integer tag: tags) {

			try {
				cats.addAll(articles.get(tag.toString()));
			} catch (Exception e) {
				try {
					cats.addAll(articles.get(redirects.get(tag.toString())));
				} catch (Exception e1) {

				}
				
			}
		}
		
		return cats;
				
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
	
	
	
	
	public HashSet<Integer> tag(String query,Integer tag){
		
		HashSet<Integer> aux= getCategoriesByTag(tag);

		return climb(aux,new HashSet<Integer>(),new HashSet<Integer>(aux),1);
		
	}	
	
	
//	private void printResult(HashMap<String, Integer> result, float numOfEntity) {
//		String aux="";
//		boolean first=true;
//		for (String speccat: intermediate_categories){
//			Integer count =result.get(speccat);
//			if (!first)
//					aux+=",";
//			else
//				first=false;
//			if (count==null)
//					aux+="0";
//			else {
//				float norm = (float) count / 10;
//				aux+=norm;
//			}
//				
//		}
//		
//		System.out.println(aux);
//		
//	}
	
	private void printResult(HashMap<Integer, Integer> result) {
		String aux="";
		boolean first=true;
		for (Integer speccat: intermediate_categories){
			Integer ii = result.get(speccat);
			if (!first)
					aux+=",";
			else
				first=false;
			if (ii==null)
					aux+="0";
			else
				aux+=ii;
		}
		
		System.out.println(aux);
		
	}

	
	private void writeResultOnFile(HashMap<Integer, Integer> result) {
	
		boolean first=true;
		for (Integer speccat: intermediate_categories){
			Integer ii = result.get(speccat);
			if (!first)
					output.append(",");
			else
				first=false;
			if (ii==null)
				output.append("0");
			else
				output.print(ii);
		}
		output.println();
		//System.out.println(aux);
		
	}
	private void writeResultOnFile(HashMap<Integer, Integer> result1, HashMap<Integer, Integer> result2) {
		
		boolean first=true;
		for (Integer speccat: intermediate_categories){
			Integer ii = result1.get(speccat);
			if (!first)
					output.append(",");
			else
				first=false;
			if (ii==null)
				output.append("0");
			else
				output.print(ii);
		}
		for (Integer speccat: intermediate_categories){
			Integer ii = result2.get(speccat);
			if (!first)
					output.append(",");
			else
				first=false;
			if (ii==null)
				output.append("0");
			else
				output.print(ii);
		}
		output.println();
		//System.out.println(aux);
		
	}
	public void tagAll(int index, int topK) throws FileNotFoundException, UnsupportedEncodingException{
		
		String ss = "../Thesis/data/test5_" + (index+1) +"/results_"+ setName  +".txt";
		output = new PrintWriter( ss , "UTF-8");
//		output = new PrintWriter("../Thesis/data/results_"+ setName  +".txt"  , "UTF-8");
		System.err.println("************** Start tagging! **************");
		Iterator<JSONObject> i =json.iterator();
		int count=1;
		
		if (intermediate_categories.size() == 124) {
			intermediate_categories.remove(allCategories.get(currCatIdx++));

			intermediate_categories.add(allCategories.get(currCatIdx));
		} else {
			intermediate_categories.add(allCategories.get(currCatIdx));
		}
		
		if (intermediate_categories.size()!=124) {
			System.err.println("errore numero fearures");
			System.exit(1);
		}
			
		
		
		HashMap<Integer,Integer> result1 = new HashMap<Integer,Integer>(); 
		HashMap<Integer,Integer> result2 = new HashMap<Integer,Integer>(); 
		
		while( i.hasNext()){
			JSONObject q = i.next();
			String query = (String) q.get("query");
			
				
			if (queryNotTagged.contains(query))
				continue;
			
			
			ArrayList<Integer> entities = new ArrayList<Integer>();
				
			for (Long id : (ArrayList<Long>) q.get("tags"))
				entities.add(id.intValue());
			
//			System.err.println("prima " + entities.size());
//			if (count==1) {
//				System.out.println(query);
//				System.out.println(entities);
//				for (Integer id : entities){
//					System.out.println(this.getCategoriesByTag(id));
//				}
////				System.exit(0);
//			}
			ArrayList<Integer> topKs = getTopKSimilarEntity2(entities, topK);
			
//			entities.addAll(topKs);  --> metodo con fusione topk #feature=123
//			System.err.println("dopo " + entities.size());
			
//			int numOfEntity = entities.size();

			result1.clear();
			result2.clear();
//			for (int i1=0;i1<numOfEntity;i1++){ 
//			this.maxHeight=8; 
			for (Integer id : entities){

				/*
				 * itero sui resultati
				 */
//				System.out.println("********************** ENTITY");
				 for (Integer cat: tag(query, id)){
						Integer curr_count =result1.get(cat);
						if (curr_count == null)
							result1.put(cat,1);
						else
							result1.put(cat, curr_count+1);
							
						
				 }
			}
			
//			this.maxHeight=3; //TODO
			
			for (Integer id : topKs){

				/*
				 * itero sui resultati
				 */
				 for (Integer cat: tag(query, id)){
						Integer curr_count =result2.get(cat);
						if (curr_count == null)
							result2.put(cat,1);
						else
							result2.put(cat, curr_count+1);
							
				 }
			}
			
			
			//System.err.println("stampo i risultati");

//			writeResultOnFile(result);
			writeResultOnFile(result1, result2);
			
//			if (count==1) {
//				System.exit(1);
//			}
			
			System.err.println("Tagged #" + count++);
		}
	
		
		output.close();

		System.err.println(ss);
	}
	


//	private ArrayList<Integer> getTopKSimilarEntity(ArrayList<Integer> entities) {
////		int numOfEntity = entities.size();
////		for (int indexEntity = 0; indexEntity < numOfEntity ; indexEntity++ )
//
//		
//		
//		ArrayList<Integer> aux= new ArrayList<Integer>(); 
//		for (Integer id : entities)
//			try {
////				System.err.println("ok");
////				for (Integer topI :)
//				aux.addAll(jaccardIndexes.get(id));
////				entities.addAll( jaccardIndexes.get( ((Long) entities.get(indexEntity)).intValue() ));
//				
//			} catch (Exception e) {
////				e.printStackTrace();
////				System.err.println("errore "+ id );
//			}
//			
//
//		
//		return aux;
//	}
//	
	
	
	private ArrayList<Integer> getTopKSimilarEntity2(ArrayList<Integer> entities, int topK) {
//		int numOfEntity = entities.size();
//		for (int indexEntity = 0; indexEntity < numOfEntity ; indexEntity++ )

//		(Vector<Integer>)[] aux1 = new (Vector<Integer>)[entities.size())];
		
		ArrayList<List<SPair>> aux = new ArrayList<List<SPair>>();
		
		
		int i=0;
		int  err = 0;
		for (Integer id : entities) {
			try {
//				System.err.println("ok");
//				for (Integer topI :)
				Vector<SPair> l = (jaccardIndexes.get(id));
				int toIdx = Math.min(l.size(), topK);
				aux.add(l.subList(0, toIdx)); //TODO .subList(0, topK)
//				System.out.println(aux.get(i).size());
//				entities.addAll( jaccardIndexes.get( ((Long) entities.get(indexEntity)).intValue() ));
				
			} catch (Exception e) {
//				e.printStackTrace();
				err++;
//				e.printStackTrace();
//				System.err.println("errore "+ id );
			}
//			
//			if (aux[i]!=null)
//				System.out.println(aux[i].size());
			i++;

		}
		if (aux.size()==0)
			return  new ArrayList<Integer>();
			
//		if (err== entities.size())
//			System.err.println("SONO CAZZI");
		int[] indexes = new int[aux.size()];
		
//		System.out.println(err);
		ArrayList<SPair> ris= new ArrayList<SPair>(topK); 	
		for (int ii=0 ; ii< topK; ii++	) {
			ris.add(new SPair());
		}
		
		
			for (int j=0;j< topK; j++){
				int maxIndex=0;
	//			ris.set(j, aux[0].elementAt(indexes[0]++));
				for (int jj=0; jj< aux.size(); jj++)
						if (aux.get(jj)==null || indexes[jj] >= aux.get(jj).size())
							continue;
						else if (ris.get(j).value < aux.get(jj).get(indexes[jj]).value) {
								ris.set(j,aux.get(jj).get(indexes[jj]));
								maxIndex=jj;
							}
				indexes[maxIndex]++;
			}
		
//		System.out.println(ris.size());
		
		ArrayList<Integer> risID = new ArrayList<Integer>(topK); 
		for (int k=0; k< ris.size();k++)
			risID.add(ris.get(k).id);
//		System.out.println(risID.get(0));
//		System.out.println(risID.toString());
		return risID;
	}
	public static void main (String args[]) throws IOException{

//		ArrayList<Integer>  sasd = new ArrayList<Integer>();
//		sasd.add(0);
//		sasd.add(1);
//		List<Integer> p =  (sasd).subList(0, 1);
//		class SPair {
//			Integer id=0;
//			Float value=0f;
//			
//			public SPair(Integer id, Float value) {
//				this.id=id;
//				this.value=value;
//			}
//
//			public SPair() {
//				// TODO Auto-generated constructor stub
//			}
//		}
//		Vector<Vector<SPair>> aux = new Vector<Vector<SPair>>(2);
//		int[] indexes = new int[2];
//		
//		int i=0;
//		
//		aux.add(new Vector<SPair>());		
//		aux.get(0).add(new SPair(3,0.88f));
//		aux.add(new Vector<SPair>());
//		aux.get(1).add(new SPair(4,0.98f));
//		aux.get(1).add(new SPair(5,0.68f)); 	
//				
//		ArrayList<SPair> ris= new ArrayList<SPair>(2); 	
//		ris.add(new SPair());
//		ris.add(new SPair());
//		
//		
//		for (int j=0;j< 2; j++){
//			int maxIndex=0;
////			ris.set(j, aux[0].elementAt(indexes[0]++));
//			for (int jj=0; jj< aux.size(); jj++)
//					if (aux.get(jj)!=null)
//						if (ris.get(j).value < aux.get(jj).elementAt(indexes[jj]).value) {
//							ris.set(j,aux.get(jj).elementAt(indexes[jj]));
//							maxIndex=jj;
//						}
//			indexes[maxIndex]++;
//		}
//		
		
		
			if (args.length==2) {
				
				String set=args[0];

				System.err.println("----------------------> "+ set);
				RisalitaCounting2 r = new RisalitaCounting2(Integer.parseInt(args[1]),set);
//				r.getStatus(0);
				r.getStatus(21);
				
//				for  (int topK=1;topK<22; topK+=2){
					
					
					
					
					
//					r.reset(Integer.parseInt(args[1]));
//					r.tagAll(1);
				for (int  i= 0 ; i< 20; i++){
					r.tagAll(i,21);
					
					System.err.println(" ----------------------- iter = " + i + "  ----------------------- ");
				}
					
//					System.err.println(" ----------------------- topk = " + topK + "  ----------------------- ");
//				}
				
				System.exit(0);
			}
	
				
			
//			if (args.length!=3) {
//				System.err.println("Error: set type, height or topK not specified");
//				System.exit(1);;
//			}
//			String set=args[0];
//
//			System.err.println("----------------------> "+ set);
//			
//			RisalitaCounting r = new RisalitaCounting(Integer.parseInt(args[1]),set,Integer.parseInt(args[2]));
//			
//			r.getStatus(Integer.parseInt(args[2]));
//
//			r.tagAll();
//			
//			JSONArray json = (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("C:\\Users\\giordano\\Desktop\\Python\\JSONs\\validation_set_tagged.JSON")));
//			JSONObject ar = (JSONObject) json.get(2);
//			ArrayList<Integer> v = (ArrayList<Integer>) ar.get("tags");
//			System.out.println(v);
	
	}

	private void reset(int height) {
		this.maxHeight= height;
		
	}
}
