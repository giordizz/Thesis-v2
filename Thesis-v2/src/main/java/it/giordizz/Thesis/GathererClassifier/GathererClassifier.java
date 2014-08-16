package it.giordizz.Thesis.GathererClassifier;

import it.acubelab.batframework.utils.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 
 * @author giordano
 * 
 * 
 * Raccoglie gli esempi, genera il modello e effettua la valutazione; 
 * è possibile aggiungere categorie al set di categorie intermedie.
 *
 */
public class GathererClassifier {

	public static void main(String[] args) throws Exception {

		
		PrintWriter writer = new PrintWriter("entity_features_test.txt", "UTF-8");
		
		
		
		Vector<Float> weights = new Vector<Float>();
		BufferedReader r1 = new BufferedReader(new FileReader("data/weights_newnew8top21.txt"));
		String weight;

		while ((weight = r1 .readLine()) != null) {
			weights.add(Float.parseFloat(weight));

		}
		r1.close();
		
		String[] cats = new String[67];

		BufferedReader reader = new BufferedReader(new FileReader("data/CatTarget.txt"));
		String line;

		int c = 0;
		while ((line = reader.readLine()) != null) {	
			cats[c++]=line;
		}
		reader.close();

		RisalitaCounting r = new RisalitaCounting();
		
		r.getStatus();
		
		
		ArrayList<Integer> toAdd = new ArrayList<Integer> ();
		
		
// Test per valutare la'aggiunta delle categorie "BESTS"		
//		BufferedReader reader2 = new BufferedReader(new FileReader("data/bests.txt"));
//		while ((line = reader2.readLine()) != null) {	
//			String[] s = line.split("\t");
//			toAdd.add(Integer.parseInt(s[1]));
//		}
//		reader2.close();
//		toAdd.add(708984);
		
		
// Test combinazioni entity normali e entity Snippets		
//		int test[][] = new int[7][3];
//		test[0][0]= 8;
//		test[0][1]= 21;
//		test[0][2]= -1;
//		
//		test[1][0]= 8;
//		test[1][1]= 5;
//		test[1][2]= -1;
//		
//		test[2][0]= 8;
//		test[2][1]= 21;
//		test[2][2]= 10;
//		
//		
//		test[3][0]= 3;
//		test[3][1]= 21;
//		test[3][2]= -1;
//		
//		test[4][0]= 3;
//		test[4][1]= 5;
//		test[4][2]= -1;
//		
//		test[5][0]= 3;
//		test[5][1]= 21;
//		test[5][2]= 10;
//		
//		test[6][0]= 8;
//		test[6][1]= 3;
//		test[6][2]= -1;		
		
		
		int test[][] = new int[1][3];
		test[0][0]= 3;
		test[0][1]= 5;
		test[0][2]= -1;
		

		
		for (int I=0; I< test.length ; I++){
			System.err.println("\ntest numero " + I + "\n");
		
			SVMClassifier s = new SVMClassifier(r.getMaxNumOfFtr());
			
			
			
			s.init();
			r.tagAll2(s,0,test[I][0],test[I][1],test[I][2]);
			r.tagAll2(s,1,test[I][0],test[I][1],test[I][2]);
			s.scaleProblems();
			
			// set normale
			float avgF1 = 0.f;
			float numOfTargetCategory = 0.f; 
			for (String categoryName : cats) {			
				try {
					s.setLabels(categoryName);
					
					Pair<Float, Float> res = s.test(weights.elementAt((int) numOfTargetCategory));
					
					numOfTargetCategory+=1.f;
					avgF1+=res.second;
					System.err.println("**** Category number " + numOfTargetCategory + " computed ****");
				} catch (Exception e) {
					e.printStackTrace();
					writer.println(e.getMessage());
				}
			}			
			
			writer.println(">>>>>>>>>>>>>>>>>>>>>> altezza:"+test[I][0]+", topK:"+test[I][1]+", firstN"+test[I][2]+" Avarage F1 :"  + avgF1 / numOfTargetCategory + "  <<<<<<<<<<<<<<<<<<<<<<<");
		}
		
		
//		
//		for (Integer catToAdd: toAdd){
//		
//			r.addCategoryToIntermediateSet(catToAdd);
//
//			s = new SVMClassifier(r.getMaxNumOfFtr());
//			
//	
//			
//			s.init();
//			r.tagAll(s,0);
//			r.tagAll(s,1);
//			s.scaleProblems();
//			
//			
//			avgF1 = 0.f;
//			numOfTargetCategory = 0.f; 
//			for (String categoryName : cats) {			
//				try {
//					//Pair<Float, Float> res = new SVMClassifier(line,mode).test();
//					s.setLabels(categoryName);
//					
//					Pair<Float, Float> res = s.test(weights.elementAt((int) numOfTargetCategory));
//					
//					numOfTargetCategory+=1.f;
//					avgF1+=res.second;
//	//						writer.printf("%s:\n\tW+: %f\n\tF1: %f\n", categoryName, res.first, res.second);
//					System.err.println("**** Category number " + numOfTargetCategory + " computed ****");
//				} catch (Exception e) {
//					e.printStackTrace();
//					writer.println(e.getMessage());
//				}
//			}			
//			
//			writer.println(">>>>>>>>>>>>>>>>>>>>>> Avarage F1 adding " + catToAdd + " :"  + avgF1 / numOfTargetCategory + "  <<<<<<<<<<<<<<<<<<<<<<<");
//		}
		
		writer.close();
		
	}
	
}
	


