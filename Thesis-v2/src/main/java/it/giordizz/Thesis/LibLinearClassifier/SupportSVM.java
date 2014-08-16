package it.giordizz.Thesis.LibLinearClassifier;

import de.bwaldvogel.liblinear.Linear;
import de.bwaldvogel.liblinear.Model;
import de.bwaldvogel.liblinear.Parameter;
import de.bwaldvogel.liblinear.Problem;
import de.bwaldvogel.liblinear.SolverType;



public class SupportSVM {
	public static Parameter getParameters(double wPos,
			double wNeg, double C) {
		Parameter param = new Parameter(SolverType.L1R_LR,C,0.001);
		param.setWeights(new double[] { wPos, wNeg }, new int[] { 1, -1 });
		param.setC(C);
		return param;
	}

	public static Model trainModel(double wPos, double wNeg, Problem trainProblem, double C) {
		Parameter param = getParameters(wPos, wNeg, C);

//		String error_msg = Linear.check_parameter(trainProblem, param);
//
//		if (error_msg != null) {
//			System.err.print("ERROR: " + error_msg + "\n");
//			System.exit(1);
//		}
		
		return Linear.train(trainProblem, param);
		
	}
	

	
}
