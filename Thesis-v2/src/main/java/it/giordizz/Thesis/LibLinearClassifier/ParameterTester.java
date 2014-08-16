package it.giordizz.Thesis.LibLinearClassifier;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Problem;

import it.acubelab.batframework.metrics.Metrics;
import it.acubelab.batframework.metrics.MetricsResultSet;

public class ParameterTester {


	public static MetricsResultSet computeMetrics(Model model,
			Problem testProblem) throws IOException {

		List<HashSet<Integer>> outputOrig = new Vector<>();
		List<HashSet<Integer>> goldStandardOrig = new Vector<>();
		
		HashSet<Integer> goldPairs = new HashSet<>();
		HashSet<Integer> resPairs = new HashSet<>();
		for (int j = 0; j < testProblem.l; j++) {
			Feature[] svmNode = testProblem.x[j];
			double gold = testProblem.y[j];
			double pred = Linear.predict(model, svmNode);
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