package it.giordizz.Thesis.AddingRemovingOneCategory;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_problem;

import it.acubelab.batframework.metrics.Metrics;
import it.acubelab.batframework.metrics.MetricsResultSet;

/**
 * 
 * @author giordano
 *
 * Confronta i valori della predizione con quelli del gold standard.
 *
 */
public class ParameterTester {


	public static MetricsResultSet computeMetrics(svm_model model,
			svm_problem testProblem) throws IOException {

		List<HashSet<Integer>> outputOrig = new Vector<>();
		List<HashSet<Integer>> goldStandardOrig = new Vector<>();
		
		HashSet<Integer> goldPairs = new HashSet<>();
		HashSet<Integer> resPairs = new HashSet<>();
		for (int j = 0; j < testProblem.l; j++) {
			svm_node[] svmNode = testProblem.x[j];
			double gold = testProblem.y[j];
			double pred = svm.svm_predict(model, svmNode);
			if (gold > 0.0)
				goldPairs.add(j);
			if (pred > 0.0)
				resPairs.add(j);
		}
		goldStandardOrig.add(goldPairs);
		outputOrig.add(resPairs);

		Metrics<Integer> metrics = new Metrics<>();
		MetricsResultSet results = metrics.getResult(outputOrig,
				goldStandardOrig, new IndexMatch());

		return results;
	}

}