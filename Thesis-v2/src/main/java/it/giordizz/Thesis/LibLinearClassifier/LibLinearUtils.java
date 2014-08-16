package it.giordizz.Thesis.LibLinearClassifier;
import it.acubelab.batframework.utils.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import de.bwaldvogel.liblinear.Feature;
import de.bwaldvogel.liblinear.Problem;


public class LibLinearUtils {
	//[-1,1]
	public static double scale(double value, double rangeMin, double rangeMax) {
		return rangeMax == rangeMin? 0.0 : (value - rangeMin) / (rangeMax - rangeMin) * 2f - 1;
	}
	
	//[0,1]
	public static double scale2(double value, double rangeMin, double rangeMax) {
		return rangeMax == rangeMin? 0.0 : (value - rangeMin) / (rangeMax - rangeMin) ;
	}



	public static Pair<double[], double[]> findRanges(Problem problem, int maxnftrs) {

		double[] rangeMins = new double[maxnftrs];
		double[] rangeMaxs = new double[maxnftrs];

		
		for ( Feature[] v : problem.x)
			for ( Feature n : v) {
				if (rangeMins[n.getIndex()-1] > n.getValue())
					rangeMins[n.getIndex()-1] = n.getValue();

				if (rangeMaxs[n.getIndex()-1] < n.getValue())
					rangeMaxs[n.getIndex()-1] =n.getValue();
			}
		
		

		return new Pair<>(rangeMins,rangeMaxs);
	}
	
	public static void scaleProblem(Problem problem, double[] rangeMins,
			double[] rangeMaxs) {
		for (int i = 0; i < problem.l; i++)
			scaleNode(problem.x[i], rangeMins, rangeMaxs);
	}

	public static void dumpRanges(double[] mins, double[] maxs, String filename)
			throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(filename));
		br.write("x\n-1 1\n");
		for (int i = 0; i < mins.length; i++)
			br.write(String.format("%d %f %f%n", i + 1, mins[i], maxs[i]));
		br.close();
	}

	public static void scaleNode(Feature[] ftrVect, double[] rangeMins,
			double[] rangeMaxs) {
		
		for (int i = 0; i < ftrVect.length; i++)
			ftrVect[i].setValue(scale2(ftrVect[i].getValue(), rangeMins[ftrVect[i].getIndex()-1],
					rangeMaxs[ftrVect[i].getIndex()-1]));

	}
}