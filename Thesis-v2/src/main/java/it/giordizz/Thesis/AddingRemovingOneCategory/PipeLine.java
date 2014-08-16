package it.giordizz.Thesis.AddingRemovingOneCategory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class PipeLine {


	public static void main(String[] args) throws Exception {
		System.out.println("thread "+Integer.parseInt(args[0]));
	
		PrintWriter writer = new PrintWriter("stats.txt", "UTF-8");

		
		int maxIter = Integer.parseInt(args[1]);
		
		int horizont = 8;
		System.out.println("# iter : " + maxIter + " horizont = " + horizont );
//		BufferedReader br5 = new BufferedReader(new FileReader("../data/CatIntermadiateID"));
		HashSet<Integer> intermediate_categories= new HashSet<Integer> ();
		BufferedReader br5 = new BufferedReader(new FileReader("data/CatIntermadiateID"));
		String line;
		while( (line=br5.readLine())!=null) 
		     intermediate_categories.add(Integer.parseInt(line));		     		
		br5.close();
		
		Vector<Integer> allCategories = new Vector<Integer>();
//		BufferedReader br7 = new BufferedReader(new FileReader("../data/AllCategoriesSorted.txt"));
		BufferedReader br7 = new BufferedReader(new FileReader("data/AllCategoriesSorted.txt"));
		
//		BufferedReader br7 = new BufferedReader(new FileReader("data/intermediateCatsSiblings.txt"));
		while( (line=br7.readLine())!=null)  {
			Integer catID = Integer.parseInt(line);
		
			if (!intermediate_categories.contains(catID))
				allCategories.add(Integer.parseInt(line));		
		}
		br7.close();
		
		intermediate_categories.clear();
		
		
		
		

		
		
		int topK = 21; 
		Container container = new Container(topK);		
		container.getStatus();

		
		Gatherer gatherer = new Gatherer();
		gatherer.gatherData();
		
		
		
		
		
		ExecutorService execServ = Executors
				.newFixedThreadPool(Integer.parseInt(args[0]));
		List<Future<Result>> futures = new Vector<>();
		

	
		int iterationIdx = 0;
		for (Integer categoryID : allCategories){
			futures.add(execServ.submit(new Worker(horizont, container, categoryID, gatherer )));

		}
		
//  Removing one cat per iteration test
//		for (int queryIdx = 0; queryIdx < 123 ; queryIdx++){
//			futures.add(execServ.submit(new Worker(queryIdx, container, gatherer)));
//		}
		
		for (Future<Result> future : futures) {
			Result r = future.get();
			writer.println(   r.avgF1 + "\t" + r.categoryID  );
			writer.flush();
		}
		

		writer.close();
		
			
		execServ.shutdown();
  
	}
	
}
	


