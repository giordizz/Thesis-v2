package it.giordizz.Thesis.AddingRemovingOneCategory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Vector;

import libsvm.svm_node;
import libsvm.svm_problem;



/**
 * 
 * @author giordano
 * 
 * Si occupa di acquisire le features di base memorizzate su file e di generarne una copia su richiesta.
 *
 */
public class Gatherer {

	svm_problem trainProblem;
	svm_problem develProblem;
	
	int totNumOfFtrs=-1;


	
	/**
	 * 
	 * @author giordano
	 *
	 * Genera una copia dei problemi caricati inizialmente. I problemi in questione contengo le 123 features di partenza. 
	 *
	 */
	public class Problems {
		svm_problem trainProblem;
		svm_problem develProblem;
		int totNumOfFtrs;
		
		Problems(svm_problem trainProblem, svm_problem develProblem, int totNumOfFtrs, int index) {
			
			int length = 2;
			this.totNumOfFtrs = totNumOfFtrs;
			
			this.trainProblem = new svm_problem();
			this.trainProblem.l = trainProblem.l;
			this.trainProblem.y = new double[trainProblem.l];
			
			this.trainProblem.x = new svm_node[trainProblem.l][];
			
			for (int i=0; i < trainProblem.x.length ; i++){
				svm_node[] aux= trainProblem.x[i];
				ArrayList<svm_node> auxX = new ArrayList<svm_node>(aux.length - length);
				for (int ii=0; ii < aux.length - length ; ii++){
					svm_node nodeAux= new svm_node();				
					nodeAux.index =aux[ii].index;										
					nodeAux.value =aux[ii].value;
					if (aux[ii].index-1 != index) 
						auxX.add(nodeAux);
				}
				
				this.trainProblem.x[i] = new svm_node[auxX.size()];
				for (int j=0; j<auxX.size(); j++ )				
					this.trainProblem.x[i][j] = auxX.get(j);
			}
			

			
			this.develProblem = new svm_problem();
			this.develProblem.l = develProblem.l;
			this.develProblem.y = new double[develProblem.l];
			
			
			this.develProblem.x = new svm_node[develProblem.l][];
			
			for (int i=0; i < develProblem.x.length ; i++){
				svm_node[] aux= develProblem.x[i];
				ArrayList<svm_node> auxX = new ArrayList<svm_node>(aux.length - length);
				
				for (int ii=0; ii < aux.length - length; ii++){
					svm_node nodeAux= new svm_node();				
					nodeAux.index =aux[ii].index;										
					nodeAux.value =aux[ii].value;
					if (aux[ii].index-1 != index) 
						auxX.add(nodeAux);
				}

				this.develProblem.x[i] = new svm_node[auxX.size()];
				for (int j=0; j<auxX.size(); j++ )				
					this.develProblem.x[i][j] = auxX.get(j);
			}
			
		}
		
		
		
	

	Problems(svm_problem trainProblem, svm_problem develProblem, int totNumOfFtrs) {
		
		this.totNumOfFtrs = totNumOfFtrs;
		
		this.trainProblem = new svm_problem();
		this.trainProblem.l = trainProblem.l;
		this.trainProblem.y = new double[trainProblem.l];
		
		this.trainProblem.x = new svm_node[trainProblem.l][];
		
		for (int i=0; i < trainProblem.x.length ; i++){
			svm_node[] aux= trainProblem.x[i];
			this.trainProblem.x[i] = new svm_node[aux.length];
			for (int ii=0; ii < aux.length ; ii++){
				this.trainProblem.x[i][ii]= new svm_node();				
				this.trainProblem.x[i][ii].index =aux[ii].index;
				this.trainProblem.x[i][ii].value =aux[ii].value;
			}
		}
			
				
		
		
		
		
		this.develProblem = new svm_problem();
		this.develProblem.l = develProblem.l;
		this.develProblem.y = new double[develProblem.l];
		
		
		this.develProblem.x = new svm_node[develProblem.l][];
		
		for (int i=0; i < develProblem.x.length ; i++){
			svm_node[] aux= develProblem.x[i];
			this.develProblem.x[i] = new svm_node[aux.length];
			for (int ii=0; ii < aux.length; ii++){
				this.develProblem.x[i][ii]= new svm_node();				
				this.develProblem.x[i][ii].index =aux[ii].index;
				this.develProblem.x[i][ii].value =aux[ii].value;
			}
		}
		
	}
	
	
	
}

	
	/**
	 * setType: specifica se i dati da raccogliere fanno parte del training o
	 * del development set
	 */
	public enum setType {
		TRAINING, DEVELOPMENT
	}

	
	
	private void gatherUnlabeledData(setType T) throws Exception {

		String featuresFile = (T == setType.TRAINING) ? "data/results_training.txt" 
				: "data/results_validation.txt";



		
		BufferedReader reader = new BufferedReader(new FileReader(featuresFile));

		
		
		
		
		Vector<svm_node[]> auxVector =  new Vector<svm_node[]>();
		String lineOfFeatures;
		while  ( (lineOfFeatures = reader.readLine()) != null) {
			String[] features = lineOfFeatures.split(",");


			Vector<svm_node> auxNodeVect = new Vector<svm_node>();
			int ftrIdx = 0;
			for (; ftrIdx < features.length; ftrIdx++) {
				Double d = Double.parseDouble(features[ftrIdx]);
				if (d!=0.) {
					svm_node auxNode = new svm_node();
					auxNode.index = ftrIdx+1;
					auxNode.value = d;
					auxNodeVect.add(auxNode);
					}
					
			}

			if (totNumOfFtrs == -1)
				totNumOfFtrs = ftrIdx+2;
			
			svm_node[] nodeVect = new svm_node[auxNodeVect.size()+2];
			int i=0;
			for (; i< auxNodeVect.size(); i++)
				nodeVect[i] = auxNodeVect.elementAt(i);
			
			svm_node plus1Node = new svm_node();
			plus1Node.index = 247;
			plus1Node.value = 0;
			nodeVect[i++]= plus1Node;
			
			svm_node plus2Node = new svm_node();
			plus2Node.index = 248;
			plus2Node.value = 0;
			nodeVect[i]= plus2Node;
			
			auxVector.add(nodeVect);
	
		}

		
		svm_problem problem = new svm_problem();
		problem.l = auxVector.size();
		problem.x = new svm_node[problem.l][];
		
		
		for  ( int i=0; i < problem.l ; i++ ) 
			problem.x[i]=auxVector.elementAt(i);
		
		
		
		if (T == setType.TRAINING)			
			trainProblem=problem;
		else
			develProblem=problem;
		
		reader.close();

	}

	public void gatherData() throws Exception {
		System.err.println("start gathering training examples..");
		gatherUnlabeledData(setType.TRAINING);
		System.err.println("-> training examples gathered! <-");
		
		System.err.println("start gathering development examples..");
		gatherUnlabeledData(setType.DEVELOPMENT);		
		System.err.println("->  development examples gathered! <-");

	}
	
	public Problems getCopyOfProblems(int index){
		return new Problems(trainProblem, develProblem, totNumOfFtrs-4, index);
	}
	public Problems getCopyOfProblems(){
		return new Problems(trainProblem, develProblem, totNumOfFtrs);
	}
}
