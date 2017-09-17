package edu.gatech.mbsec.adapter.magicdraw.builder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import edu.gatech.mbsec.adapter.magicdraw.parser.Stereotypes;
import edu.gatech.mbsec.adapter.magicdraw.parser.SysMLModel;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
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
    //private static final String GLOBAL_OWL_TYPE = "sysml-rdfvocabulary";
    private static final String GLOBAL_OWL_TYPE = "sysml";
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
    /**
     * Buils the RDF equivalent for the MD Model object.
     * @param sysml the MD model abstraction.
     * @param output the target RDF model.
     * @param desc the building descriptor.
     */
    private void buildModel(SysMLModel sysml, Model output, ModelDescriptor desc) {
        Resource resource, pck;
        Property elementName, pcks;
        List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> packages;
        com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model src = sysml.getModel();
        String name = src.getName();
        packages = sysml.getPackages();
        if (name == null) name = "Data";
        resource = desc.resource("model", sysml.getName() + name, output);
        elementName = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        pcks = desc.property(GLOBAL_OWL_TYPE, "Model_package");
        resource.addLiteral(elementName, name);
        LOG.log(Level.INFO, "--- Model {0}", name);
        LOG.log(Level.INFO, "--- Packages");
        for(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package modelPackage : packages) {
            name = sysml.standardName(modelPackage.getQualifiedName());
            pck = desc.resource("packages", name, output);
            //Only first-level packages must be included as model properties.
            if (!name.contains("::")) {
                resource.addProperty(pcks, pck);
            }
            if (modelPackage.getName() != null) {
                pck.addProperty(elementName, modelPackage.getName());
            }
            LOG.log(Level.INFO, "[+] <package> {0}", name);
        }
    }
    /**
     * Gets a property from an SysML element in a given stereotype.
     * @param project the underlaying project.
     * @param element the property owner element.
     * @param stereotype the stereotype containing the property.
     * @param property the name of the property to extract.
     * @return the property value;
     */
    private Object getElementProperty(Project project, Element element, String stereotype, String property) {
        List<Object> values;
        Stereotype owner = StereotypesHelper.getStereotype(project, stereotype);
        values = StereotypesHelper.getStereotypePropertyValue(element, owner, property);
        return values.isEmpty() ? null : values.get(0);
    }
    /**
     * Gets a property from an SysML element in the first visible stereotype.
     * @param element the property owner element.
     * @param property the name of the property to extract.
     * @return the property value;
     */
    private Object getElementProperty(Element element, String property) {
        Stereotype stereotype = StereotypesHelper.getFirstVisibleStereotype(element);
        return StereotypesHelper.getStereotypePropertyFirst(element, stereotype, property);
    }
    /**
     * Buils the RDF equivalent for the MD Requirements objects.
     * @param sysml the MD model abstraction.
     * @param output the target RDF model.
     * @param desc the building descriptor.
     */
    private void buildRequirements(SysMLModel sysml, Model output, ModelDescriptor desc) {
        String joker;
        Resource resource;
        Property hyperlink;
        List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> requirements;
        requirements = sysml.getElements().get(Stereotypes.Requirement);
        hyperlink = desc.property(GLOBAL_OWL_TYPE, "Requirement_hyperlink");
        for(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class requirement : requirements) {
			joker = getElementProperty(requirement, "Id").toString();
            resource = desc.resource("requirements", sysml.standardName(joker), output);
            resource.addProperty(DCTerms.identifier, joker);
            LOG.log(Level.INFO, "[+] <requirement> {0}", joker);
            if ((joker = requirement.getName()) != null) {
                resource.addProperty(DCTerms.title, joker);
                LOG.log(Level.INFO, "\t\ttitle: {0}", joker);
            }
            joker = getElementProperty(requirement, "Text").toString();
            if (joker != null && !joker.isEmpty()) {
                resource.addProperty(DCTerms.description, joker);
                LOG.log(Level.INFO, "\t\tdescription: {0}", joker);
            }
            //joker = (String)getElementProperty(requirement, "hyperlinkText");
            joker = (String)getElementProperty(sysml.getProject(), requirement, "HyperlinkOwner", "hyperlinkText");
            if (joker != null) {
                resource.addProperty(hyperlink, joker);
                LOG.log(Level.INFO, "\t\thyperlink: {0}", joker);
            }
        }
    }
    /**
     * Builds an RDF model from an {@link SysMLModel SysML model}.
     * @param sysml the abstraction of the mdzip file.
     * @param descriptor the building descriptor.
     * @return the corresponding model.
     */
    public Model build(SysMLModel sysml, ModelDescriptor descriptor) {
        Model model = ModelFactory.createDefaultModel();
        try {
            buildModel(sysml, model, descriptor);
            buildRequirements(sysml, model, descriptor);
            for(Map.Entry<String, String> prefix : descriptor.getVocabPrefixes().entrySet()) {
                model.setNsPrefix(prefix.getKey(), prefix.getValue());
            }
            return model;
        } catch(Exception ex) {
            logStackTrace(ex);
            throw ex;
        }
    }

}