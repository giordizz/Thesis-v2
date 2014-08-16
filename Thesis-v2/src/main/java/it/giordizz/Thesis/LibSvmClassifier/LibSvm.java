package it.giordizz.Thesis.LibSvmClassifier;

import it.acubelab.batframework.utils.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


public class LibSvm {

	public static void main(String[] args) throws Exception {

		if (args.length!=2) {
			System.err.println("specify argument");
			System.exit(1);
		}
			
		
		
		
		PrintWriter writer = new PrintWriter(args[1]+ ".txt", "UTF-8");
		
		Vector<Float> weights = new Vector<Float>();
//		BufferedReader r = new BufferedReader(new FileReader("data/weights_new" + args[0] + "top21_2.txt")); //TODO
		BufferedReader r = new BufferedReader(new FileReader("data/weights_new" + args[0] + "top21.txt")); //TODO
		String weight;
//		int count=0;
		while ((weight = r .readLine()) != null) {
			weights.add(Float.parseFloat(weight));
//			System.err.println(++count);
		}
		r.close();
		
			//TODO
		for  (int topK=20;topK<21; topK+=1){ //TODO
			System.err.println("******************************** topK " + topK + "******************************** ");	
			writer.println("**************** topK " + topK + "****************");	
				
		SVMClassifier c = new SVMClassifier(args[1]);
//			SVMClassifier c = new SVMClassifier(0);
		c.gatherData();
		

			
		for (int pow = 0;pow >= -0; pow--) {
			System.err.println("**************** C = 10^" + pow + "****************");
//			System.err.println("**** fattore "+ pow + "****");
			BufferedReader reader = new BufferedReader(new FileReader("data/CatTarget.txt"));
			writer.println("**************** C = 10^" + pow + "****************");
			
			float avgF1 = 0.f;
			float numOfTargetCategory = 0.f; 
			String categoryName;
		//	int count=1;
			while ((categoryName = reader.readLine()) != null) {			
				try {
					//Pair<Float, Float> res = new SVMClassifier(line,mode).test();
					c.setLabels(categoryName);
					
					Pair<Float, Float> res = c.test(pow,weights.elementAt((int) numOfTargetCategory));
					
					numOfTargetCategory+=1.f;
					avgF1+=res.second;
					writer.printf("%s:\n\tW+: %f\n\tF1: %f\n", categoryName, res.first, res.second);
					System.err.println("**** Category number " + numOfTargetCategory + " computed ****");
				} catch (Exception e) {
					e.printStackTrace();
					writer.println(e.getMessage());
				}
			}			
			reader.close();
			writer.println(">>>>>>>>>>>>>>>>>>>>>> Avarage F1: " + avgF1 / numOfTargetCategory + "  <<<<<<<<<<<<<<<<<<<<<<<");
	//		totAvgF1+= avgF1 / numOfTargetCategory;
		}
		
		}
		
		writer.close();
}
}
	


