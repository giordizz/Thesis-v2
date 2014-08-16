package it.giordizz.Thesis.DataUtils.Miscellaneous;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


/**
 * 
 * @author giordano
 *
 * Crea il JSON contenente per ogni query del set specificato l'insieme delle rispettive entity.
 */
public class Gen_specificset_tagged_json {



	
	Vector<String> set= new Vector<String> ();;
	JSONArray json;
	String setname;

	public Gen_specificset_tagged_json(String s) {
		setname=s;
		
		
	}
	public void getStatus() throws IOException{
	
		
		json= (JSONArray)JSONValue.parse(new InputStreamReader(new FileInputStream("../data/tagged911.JSON")));
		
		
		BufferedReader reader2 = new BufferedReader(new FileReader("../dataset/"+ setname +"_set"));
		String line;
		while( (line=reader2.readLine())!=null) {
			
			set.add(line);
			
		}

		reader2.close();
		
		
		
		
		go();
		System.err.println("************** Status loaded! **************");
	}

	


	
	
	
	public void go(){
		System.err.println("************** Start tagging! **************");
		Iterator i =json.iterator();
		int count=0;
		String f = "[";
		while( i.hasNext()){
			JSONObject q = (JSONObject) i.next();
			if ( set.contains(q.get("query").toString())) {
					f+=q.toJSONString() + ",";
					count++;
			}
			

		}
		
		  
	System.out.println(f.subSequence(0, f.length()-1) + "]");


		System.err.println("computed #" + count++);	
	}
	
	public static void main (String args[]) throws IOException{

			
		
			Gen_specificset_tagged_json r = new Gen_specificset_tagged_json(args[0]);
			r.getStatus();

	}
}


