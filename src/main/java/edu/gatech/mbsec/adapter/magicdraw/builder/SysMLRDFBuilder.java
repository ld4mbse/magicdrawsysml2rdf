package edu.gatech.mbsec.adapter.magicdraw.builder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import static edu.gatech.mbsec.adapter.magicdraw.builder.MagicDrawManager.descriptor;
import static edu.gatech.mbsec.adapter.magicdraw.builder.MagicDrawManager.projectId;
import edu.gatech.mbsec.adapter.magicdraw.parser.SysMLModel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The SysML to RDF builder.
 * @author rherrera
 */
public class SysMLRDFBuilder {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(SysMLRDFBuilder.class.getName());
    /**
     * Global vocabulary type.
     */
    private static final String GLOBAL_OWL_TYPE = "sysml-rdfvocabulary";
    /**
     * Logs an entire exception's stack trace.
     * @param ex the source exception.
     */
    private void logStackTrace(Exception ex) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(bos);
        ex.printStackTrace(printer);
        printer.flush();
        LOG.log(Level.SEVERE, "Could not build model.\n{0}", bos.toString());
    }

    private void buildModel(SysMLModel sysml, Model output, ModelDescriptor desc) {
        Property property;
        Resource resource;
        com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model src = sysml.getModel();
        String modelName = src.getName();
        if (modelName == null) modelName = "Data";
        resource = desc.resource("model", sysml.getName() + modelName, output);
        property = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        resource.addLiteral(property, modelName);
    }
    /**
     * Builds an RDF model from an {@link SysMLModel SysML model}.
     * @param sysml the abstraction of the mdzip file.
     * @param descriptor the building descriptor.
     * @return the corresponding model.
     */
    public Model build(SysMLModel sysml, ModelDescriptor descriptor) {
        Model model = ModelFactory.createDefaultModel();
        buildModel(sysml, model, descriptor);
        /*try {
            String fileName = source.getPath();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            fileName = fileName.replaceAll(".mdzip", "");
            //loadSysMLProject(fileName, file.getPath());*/
            return model;
        /*} catch(Exception ex) {
            logStackTrace(ex);
            throw ex;
        }*/
    }

}