package it.giordizz.Thesis.DataUtils.Miscellaneous;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


/**
 * 
 * 
 * @author giordano
 *
 *
 * Crea il file contenente liste di pagine di wikipidia ordinate per ordine non decrescente
 * del valore dell'indice Jaccard rispetto ad ogni pagina di wikipedia. 
 */
public class createJaccardFile {
	PrintWriter writer;
	

	private class CompPair implements Comparable<CompPair> {
	        public int first;
	        public float second;
	
	        public CompPair(Integer first, Float second) {
	                this.first = first;
	                this.second = second;
	        }

			@Override
			public int compareTo(CompPair o) {
				return -Float.compare(second, o.second);
			}
	

	}
	private class SpecPair implements Comparable<SpecPair> {
        public int first;
        public Vector<CompPair> second;

        public SpecPair(Integer first, Vector<CompPair> second) {
                this.first = first;
                this.second = second;
        }

        @Override
        public boolean equals(Object sp) {
        	return first== ((SpecPair) sp).first;
        }
        
        @Override
		public int compareTo(SpecPair o) {
			return (int) (first - o.first);
		}

		

}
	
	
	
	HashMap<Integer,Vector<CompPair>> J = new HashMap<Integer,Vector<CompPair>>();
	TreeSet<SpecPair> B1 = new TreeSet<SpecPair>();
	
	SpecPair[] S= new SpecPair[50000000];

	SpecPair dummy = new SpecPair(0,null);
	

	
	public void binaryAdd(final Integer p1, final Integer p2, final float jaccardIndex) {
		SpecPair[] aux = (SpecPair[])B1.toArray();
		
		
		dummy.first =p1;
		
		int index = Arrays.binarySearch(aux, dummy);
		if (index==-1) {
		
			B1.add(new SpecPair(p1,new Vector<CompPair>(){{ add(new CompPair(p2, jaccardIndex)); }}));
		}
		else {
			Vector<CompPair> v = aux[index].second;
			v.add(new CompPair(p2, jaccardIndex));
		}
		dummy.first =p2;	
		index = Arrays.binarySearch(aux, dummy);
		if (index==-1) {
			
			B1.add(new SpecPair(p2,new Vector<CompPair>(){{ add(new CompPair(p1, jaccardIndex)); }} ));
		}
		else {
			Vector<CompPair> v = aux[index].second;
			v.add(new CompPair(p1, jaccardIndex));
		}

	}
	
	public void getData2() throws FileNotFoundException, IOException {
		
		System.out.println("acquiring data");
		GZIPInputStream	gzip2=null;
		
		gzip2 = new GZIPInputStream(new FileInputStream("../../fra/relationships-sorted.gz"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		
		//347839172
		int r = 0;
		String row;
		while ( (row = br2.readLine()) != null ){
			String[] vals = row.split("\t");
			final Integer p1 = Integer.parseInt(vals[0]);
			final Integer p2 = Integer.parseInt(vals[2]);
			int inters = Integer.parseInt(vals[3]);
			int union = Integer.parseInt(vals[4]);
			
			final float jaccard = (float) inters / (float) union;
			
			binaryAdd(p1,p2,jaccard);
			System.out.println("row "+ ++r);
		}
		System.out.println("-> data acquired");
	}
	
	public void getData3() throws FileNotFoundException, IOException {
		
		System.out.println("acquiring data");
		GZIPInputStream	gzip2=null;
		
		gzip2 = new GZIPInputStream(new FileInputStream("../../fra/relationships-sorted.gz"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		
		int r = 0;
		String row;
		StringTokenizer s ;
		while ( (row = br2.readLine()) != null ){
			s = new StringTokenizer(row, "\t");
			String[] vals = new String[6];
			
			int i=0;
			while (s.hasMoreTokens()) {
				vals[i++] = s.nextToken();
			}
			final Integer p1 = Integer.parseInt(vals[0]);
			final Integer p2 = Integer.parseInt(vals[2]);
			int inters = Integer.parseInt(vals[4]);
			int union = Integer.parseInt(vals[5]);
			
			final float jaccard = (float) inters / (float) union;
			
			if (S[p1]!=null)
				S[p1].second.add(new CompPair(p2,jaccard ));
			else
				S[p1] = new SpecPair(p1, new Vector<CompPair>() {{  add(new CompPair(p2,jaccard )); }});
			
			if (S[p2]!=null)
				S[p2].second.add(new CompPair(p1,jaccard ));
			else
				S[p2] = new SpecPair(p2, new Vector<CompPair>() {{  add(new CompPair(p1,jaccard )); }});			
			
			if (++r % 100000000 == 0)
				System.out.println("-> data " +r);
		}
		
		System.out.println("-> data acquired");
	}	
	
	public void do4() throws FileNotFoundException, IOException {
		writer = new PrintWriter("../data/jaccardFileWithVals.txt", "UTF-8");
		System.out.println("acquiring data");
		GZIPInputStream	gzip2=null;
		
		gzip2 = new GZIPInputStream(new FileInputStream("../../fra/relationships-sorted.gz"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		
		int r = 0;
		
		StringTokenizer s ;
		Integer currId= -1;
		
		Vector<CompPair> aux = new Vector<CompPair>();
		
		String row;
		while ( (row = br2.readLine()) != null ){
			s = new StringTokenizer(row, "\t");
			String[] vals = new String[6];
			
			
			for (int i=0;s.hasMoreTokens();i++) 
				vals[i] = s.nextToken();

			
			final Integer p1 = Integer.parseInt(vals[0]);
			final Integer p2 = Integer.parseInt(vals[2]);
			int inters = Integer.parseInt(vals[4]);
			int union = Integer.parseInt(vals[5]);
			
			
			final float jaccard = (float) inters / (float) union;
			
			
			if (!p1.equals(currId)) {
				
				writeCurr(currId,aux);			
				aux.clear();
				currId= p1;
				if (S[p1]!=null) {
					aux.addAll(S[p1].second);
				}
			}
			
				
			
//			if (S[p1]!=null)
				aux.add(new CompPair(p2,jaccard ));
//			else
//				S[p1] = new SpecPair(p1, new Vector<CompPair>() {{  add(new CompPair(p2,jaccard )); }});
			
			if (S[p2]!=null)
				S[p2].second.add(new CompPair(p1,jaccard ));
			else
				S[p2] = new SpecPair(p2, new Vector<CompPair>() {{  add(new CompPair(p1,jaccard )); }});			
			
			if (++r % 100000000 == 0)
				System.out.println("-> data " +r);
		}
		writeCurr(currId,aux);
		System.out.println("-> data acquired");
		
		writer.close();
	}
	
	
	
	private void writeCurr(Integer currId, Vector<CompPair> aux) {
		if (currId==-1)
				return;
		
		writer.println(currId);
		Collections.sort(aux);
		
		boolean first = true;
		for (CompPair c1 :	aux) {
			if (first){
					
				writer.print(c1.first+","+c1.second);
				first=false;
			}
			else
				writer.print("\t" + c1.first+","+c1.second);
		}
			
		writer.println();	
		writer.flush();
		
		if (S[currId]!=null)
			S[currId].second.clear();
	}

	public void getData() throws FileNotFoundException, IOException {
		
		System.out.println("acquiring data");
		GZIPInputStream	gzip2=null;
		
		gzip2 = new GZIPInputStream(new FileInputStream("data/prova.gz"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(gzip2));
		
		int r = 0;
		String row;
		while ( (row = br2.readLine()) != null ){
			String[] vals = row.split("\t");
			final Integer p1 = Integer.parseInt(vals[0]);
			final Integer p2 = Integer.parseInt(vals[2]);
			int inters = Integer.parseInt(vals[3]);
			int union = Integer.parseInt(vals[4]);
			final float jaccard = (float) inters / (float) union;
			
			Vector<CompPair> aux1 = J.get(p1);
			if (aux1==null)
				J.put(p1, new Vector<CompPair>() {{  add(new CompPair(p2,jaccard )); }});
			else {
				aux1.add(new CompPair(p2,jaccard ));
				J.put(p1,aux1);
			}
			
			Vector<CompPair> aux2 = J.get(p2);
			if (aux2==null)
				J.put(p2, new Vector<CompPair>() {{  add(new CompPair(p1,jaccard )); }});
			else {
				aux2.add(new CompPair(p1,jaccard ));
				J.put(p2,aux2);
			}
			
			System.out.println("row "+ ++r);
		}
		System.out.println("-> data acquired");
	}

	public void writeJaccardFile() throws FileNotFoundException, UnsupportedEncodingException{
		System.out.println("writing in file " +J.size()+"records ");
		PrintWriter writer = new PrintWriter("../data/jaccardFile.txt", "UTF-8");
		
		int c = 0;
		for (Entry<Integer, Vector<CompPair>> j :  J.entrySet()){
			writer.println(j.getKey());
//			Vector<CompPair> aux = 
			Collections.sort(j.getValue());
			
			boolean first = true;
			for (CompPair c1 :	j.getValue()) {
				if (first){
						
					writer.print(c1.first);
					first=false;
				}
				writer.print("\t" + c1.first);
			}
				
			writer.println();	
			writer.flush();
			System.out.println("record " + ++c); 
				
		}
			
		
		System.out.println("-> file written");
		writer.close();
	}
	
	
	public void writeJaccardFile3() throws FileNotFoundException, UnsupportedEncodingException{
		System.out.println("writing in file  ");
		PrintWriter writer = new PrintWriter("../data/jaccardFile.txt", "UTF-8");
		
		int c = 0;
		for (SpecPair j :  S){
			if (j==null)
					continue;
			writer.println(j.first);
			Collections.sort(j.second);
			
			boolean first = true;
			for (CompPair c1 :	j.second) {
				if (first){
						
					writer.print(c1.first);
					first=false;
				}
				else
					writer.print("\t" + c1.first);
			}
				
			writer.println();	
			writer.flush();
			System.out.println("record " + ++c); 
				
		}
			
		
		System.out.println("-> file written");
		writer.close();
	}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		createJaccardFile j = new createJaccardFile();
		j.do4();
	}

}
