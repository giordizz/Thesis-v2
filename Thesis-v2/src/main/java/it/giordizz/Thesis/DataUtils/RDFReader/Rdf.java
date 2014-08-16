package it.giordizz.Thesis.DataUtils.RDFReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;


public class Rdf {
	
	
	public static void  main(String[] argd) throws IOException {
	 Model model = ModelFactory.createDefaultModel();

	try {
		model.read(new FileInputStream("enwiki-article-categories.ttl"),null,"TTL");
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	StmtIterator iter;
	Statement stmt;

	iter = model.listStatements();
	while (iter.hasNext())
	    {
	    stmt = iter.next();
	    String[] aux1= (stmt.getSubject().toString()).split("/");
	   String[] aux2= (stmt.getObject().toString()).split("/");
	    System.out.println(aux1[aux1.length-1] +"\t" + aux2[aux2.length-1]) ;
	    
	}
}
	
}
