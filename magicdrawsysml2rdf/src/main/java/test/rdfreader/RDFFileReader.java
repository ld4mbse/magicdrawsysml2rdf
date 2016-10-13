package test.rdfreader;



import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;





public class RDFFileReader {

	public static void main(String[] args) {
		
		
		
		// create an empty RDF model
		Model model = ModelFactory.createDefaultModel();

		// use FileManager to read OSLC Resource Shape in RDF
//		String inputFileName = "file:C:/Users/Axel/git/EcoreMetamodel2OSLCSpecification/EcoreMetamodel2OSLCSpecification/Resource Shapes/Block.rdf";
		String inputFileName = "file:C:/apps/eclipse-ld4mbse-jdrepo/workspace/magicdrawsysml2rdf/generated.rdf";
		InputStream in = FileManager.get().open(inputFileName);
		if (in == null) {
			throw new IllegalArgumentException("File: " + inputFileName
					+ " not found");
		}

		// read the RDF/XML file
		model.read(in, null);

		for (String prefix : model.getNsPrefixMap().keySet()) {
			System.out.println(prefix);
		}
		
		// write it to standard out
//		model.removeNsPrefix("sysml");
		model.write(System.out, "RDF/XML");
//		model.write(System.out, "JSON-LD");
		// model.write(System.out);
		
//		RDFDataMgr.write(System.out, model, Lang.JSONLD);

	}

}
