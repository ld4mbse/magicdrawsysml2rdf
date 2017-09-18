package edu.gatech.mbsec.adapter.magicdraw.builder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
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
     * Buils the RDF equivalents for the MD Package objects.
     * @param sysml the MD model abstraction.
     * @param output the target RDF model.
     * @param desc the building descriptor.
     */
    private void buildPackages(SysMLModel sysml, Model output, ModelDescriptor desc) {
        Resource resource, pck;
        List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> packages;
        com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model model = sysml.getModel();
        Property pcks, name = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        String joker = model.getName();
        if (joker == null) joker = "Data";
        resource = desc.resource("model", sysml.getName() + joker, output);
        if ((packages = sysml.getPackages()) != null) {
            pcks = desc.property(GLOBAL_OWL_TYPE, "Model_package");
            resource.addLiteral(name, joker);
            LOG.log(Level.INFO, "--- Model {0}", joker);
            LOG.log(Level.INFO, "--- Packages");
            for(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package modelPackage : packages) {
                joker = sysml.standardName(modelPackage.getQualifiedName());
                pck = desc.resource("packages", joker, output);
                //Only first-level packages must be included as model properties.
                if (!joker.contains("::")) {
                    resource.addProperty(pcks, pck);
                }
                if (modelPackage.getName() != null) {
                    pck.addProperty(name, modelPackage.getName());
                }
                LOG.log(Level.INFO, "[+] <package> {0}", joker);
            }
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
        requirements = sysml.getStereotypes(Stereotypes.Requirement);
        if (requirements != null) {
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
    }

	private void buildStereoTypedProperties(SysMLModel sysml, Class block,
            Resource rscBlock, Model output, ModelDescriptor desc) {
        Object direction;
        Type propertyType;
        Classifier classifier;
        LiteralString literal;
        Association association;
        String joker, stereotype;
        Resource rscProperty, rscJoker;
        ValueSpecification valueSpecification;
        InstanceSpecification stereotypeInstance;
        Property type = desc.property(GLOBAL_OWL_TYPE, "Property_type");
        Property owner = desc.property(GLOBAL_OWL_TYPE, "OwnedElement_owner");
        Property partProperty = desc.property(GLOBAL_OWL_TYPE, "Block_partProperty");
        Property referenceProperty = desc.property(GLOBAL_OWL_TYPE, "Block_referenceProperty");
        Property valueProperty = desc.property(GLOBAL_OWL_TYPE, "Block_valueProperty");
        Property flowProperty = desc.property(GLOBAL_OWL_TYPE, "Block_flowProperty");
        Property elementName = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        Property lowerMult = desc.property(GLOBAL_OWL_TYPE, "MultiplicityElement_lower");
        Property upperMult = desc.property(GLOBAL_OWL_TYPE, "MultiplicityElement_upper");
        Property reference = desc.property(GLOBAL_OWL_TYPE, "ReferenceProperty_association");
        Property defaultValue = desc.property(GLOBAL_OWL_TYPE, "ValueProperty_defaultValue");
        Property directionProperty = desc.property(GLOBAL_OWL_TYPE, "FlowProperty_direction");
        List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property> properties = block.getOwnedAttribute();
        for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property property : properties) {
            stereotypeInstance = property.getAppliedStereotypeInstance();
			if (stereotypeInstance != null) {
                joker = sysml.standardName(property);
                classifier = stereotypeInstance.getClassifier().get(0);
                stereotype = classifier.getName();
                if (stereotype.contains("PartProperty")) {
                    rscProperty = desc.resource("partproperties", joker, output);
                    rscBlock.addProperty(partProperty, rscProperty);
                    LOG.log(Level.INFO, "\t\t[+] <PartProperty>: {0}", joker);
                    rscProperty.addProperty(owner, rscBlock);
                    LOG.log(Level.INFO, "\t\t\t\towner: {0}", rscBlock.getURI());
                } else if (stereotype.contains("ReferenceProperty")) {
                    rscProperty = desc.resource("referenceproperties", joker, output);
                    rscBlock.addProperty(referenceProperty, rscProperty);
                    LOG.log(Level.INFO, "\t\t[+] <ReferenceProperty>: {0}", joker);
                    association = property.getAssociation();
                    if (association != null) {
                        if (MDSysMLModelHandler.isSysMLElement(association, "Block"))
                            joker = "associationblocks";
                        else if (association instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association)
                            joker = "associations";
                        else
                            joker = "unknown";
                        rscJoker = desc.resource(joker, sysml.standardName(association), output);
                        rscProperty.addProperty(reference, rscJoker);
                        LOG.log(Level.INFO, "\t\t\t\treference: {0}", rscJoker.getURI());
                    }
                } else if (stereotype.contains("ValueProperty")) {
                    rscProperty = desc.resource("valueproperties", joker, output);
                    rscBlock.addProperty(valueProperty, rscProperty);
                    LOG.log(Level.INFO, "\t\t[+] <ValueProperty>: {0}", joker);
                    valueSpecification = property.getDefaultValue();
                    if (valueSpecification instanceof LiteralString) {
                        literal = (LiteralString) valueSpecification;
                        rscProperty.addProperty(defaultValue, literal.getValue());
                        LOG.log(Level.INFO, "\t\t\t\tdefault value: {0}", literal.getValue());
                    }
                } else if (stereotype.contains("FlowProperty")) {
                    rscProperty = desc.resource("flowproperties", joker, output);
                    rscBlock.addProperty(flowProperty, rscProperty);
                    LOG.log(Level.INFO, "\t\t[+] <FlowProperty>: {0}", joker);
                    direction = StereotypesHelper.getStereotypePropertyFirst(property, (Stereotype)classifier, "direction");
                    if (direction instanceof EnumerationLiteral) {
                        EnumerationLiteral enumLit = (EnumerationLiteral) direction;
                        rscProperty.addProperty(directionProperty, enumLit.getName());
                        LOG.log(Level.INFO, "\t\t\t\tdirection: {0}", enumLit.getName());
                    }
                } else {
                    continue;
                }
                rscProperty.addProperty(elementName, property.getName());
                LOG.log(Level.INFO, "\t\t\t\tname: {0}", property.getName());
                propertyType = property.getType();
                if (propertyType != null) {
                    if (MDSysMLModelHandler.isSysMLElement(propertyType, "Block"))
                        joker = "blocks";
                    else if (MDSysMLModelHandler.isSysMLElement(propertyType, "ValueType"))
                        joker = "valuetypes";
                    else
                        joker = "unknown";
                    joker = desc.resource(joker, sysml.standardName(propertyType));
                    rscProperty.addProperty(type, output.createResource(joker));
                    LOG.log(Level.INFO, "\t\t\t\ttype: {0}", joker);
                }
                rscProperty.addProperty(lowerMult, Integer.toString(property.getLower()));
                LOG.log(Level.INFO, "\t\t\t\tlower: {0}", property.getLower());
                rscProperty.addProperty(upperMult, Integer.toString(property.getUpper()));
                LOG.log(Level.INFO, "\t\t\t\tupper: {0}", property.getUpper());
            }
        }
	}

	private static void buildConnectors(SysMLModel sysml, Class block,
            Resource rscBlock, Model output, ModelDescriptor desc) {
        String joker;
        Type connectorType;
        ConnectableElement role;
        Resource connector, rscJoker, endConnector;
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property definingProp;
        com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property partPortProp;
        Property owner = desc.property(GLOBAL_OWL_TYPE, "OwnedElement_owner");
        Property elementName = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        Property connectorProperty = desc.property(GLOBAL_OWL_TYPE, "Block_connector");
        Property connectorTypeProperty = desc.property(GLOBAL_OWL_TYPE, "Connector_type");
        Property connectorEnd = desc.property(GLOBAL_OWL_TYPE, "Connector_end");
        Property endRole = desc.property(GLOBAL_OWL_TYPE, "ConnectorEnd_role");
        Property definingEnd = desc.property(GLOBAL_OWL_TYPE, "ConnectorEnd_definingEnd");
        Property partPort = desc.property(GLOBAL_OWL_TYPE, "ConnectorEnd_partWithPort");
   		for (Connector element : block.getOwnedConnector()) {
            joker = sysml.standardName(element);
            connector = desc.resource("connectors", joker, output);
            rscBlock.addProperty(connectorProperty, connector);
            LOG.log(Level.INFO, "\t\t[+] <Connector>: {0}", joker);
            if (!element.getName().isEmpty()) {
                connector.addProperty(elementName, element.getName());
                LOG.log(Level.INFO, "\t\t\t\tname: {0}", element.getName());
            }
            connectorType = element.getType();
            if (connectorType != null) {
                if (MDSysMLModelHandler.isSysMLElement(connectorType, "Block"))
                    joker = "associationblocks";
                else if (connectorType instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association)
                    joker = "associations";
                else
                    joker = "unknown";
                rscJoker = desc.resource(joker, sysml.standardName(connectorType), output);
                connector.addProperty(connectorTypeProperty, rscJoker);
                LOG.log(Level.INFO, "\t\t\t\ttype: {0}", rscJoker.getURI());
            }
            connector.addProperty(owner, rscBlock);
            LOG.log(Level.INFO, "\t\t\t\towner: {0}", rscBlock.getURI());
            for (ConnectorEnd end : element.getEnd()) {
                joker = sysml.standardName(end.getID());
                endConnector = desc.resource("connectorends", joker, output);
                connector.addProperty(connectorEnd, endConnector);
                LOG.log(Level.INFO, "\t\t\t\t[+] <Connector End>: {0}", endConnector.getURI());
                role = end.getRole();
                if (MDSysMLModelHandler.isSysMLElement(role, "PartProperty"))
                    joker = "partproperties";
                else if (MDSysMLModelHandler.isSysMLElement(role, "ProxyPort"))
                    joker = "proxyports";
                else if (MDSysMLModelHandler.isSysMLElement(role, "FullPort"))
                    joker = "fullports";
                else if (role instanceof Port)
                    joker = "ports";
                else
                    joker = "unknown";
                endConnector.addProperty(endRole, desc.resource(joker, sysml.standardName(role), output));
                LOG.log(Level.INFO, "\t\t\t\t\t\trole: {0}", joker);
                if ((definingProp = end.getDefiningEnd()) != null) {
                    rscJoker = desc.resource("partproperties", sysml.standardName(definingProp), output);
                    endConnector.addProperty(definingEnd, rscJoker);
                    LOG.log(Level.INFO, "\t\t\t\t\t\tdefiningEnd: {0}", rscJoker.getURI());
                }
                if ((partPortProp = end.getPartWithPort()) != null) {
                    rscJoker = desc.resource("partproperties", sysml.standardName(partPortProp), output);
                    endConnector.addProperty(partPort, rscJoker);
                    LOG.log(Level.INFO, "\t\t\t\t\t\tpartWithPort: {0}", rscJoker.getURI());
                }
            }
		}
	}

	private static void buildPorts(SysMLModel sysml, Class block,
            Resource rscBlock, Model output, ModelDescriptor desc) {
        String joker;
        Resource rscPort;
        Type propertyType;
        Property type = desc.property(GLOBAL_OWL_TYPE, "Property_type");
        Property owner = desc.property(GLOBAL_OWL_TYPE, "OwnedElement_owner");
        Property elementName = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
        Property lowerMult = desc.property(GLOBAL_OWL_TYPE, "MultiplicityElement_lower");
        Property upperMult = desc.property(GLOBAL_OWL_TYPE, "MultiplicityElement_upper");
        Property isService = desc.property(GLOBAL_OWL_TYPE, "Port_isService");
        Property isBehavior = desc.property(GLOBAL_OWL_TYPE, "Port_isBehavior");
        Property isConjugated = desc.property(GLOBAL_OWL_TYPE, "Port_isConjugated");
        Property proxyPort = desc.property(GLOBAL_OWL_TYPE, "Block_proxyPort");
        Property fullPort = desc.property(GLOBAL_OWL_TYPE, "Block_fullPort");
        Property genericPort = desc.property(GLOBAL_OWL_TYPE, "Block_port");
		for (Port port : block.getOwnedPort()) {
            joker = sysml.standardName(port);
			if (MDSysMLModelHandler.isSysMLElement(port, "ProxyPort")) {
                rscPort = desc.resource("proxyports", joker, output);
                rscBlock.addProperty(proxyPort, rscPort);
                LOG.log(Level.INFO, "\t\t[+] <ProxyPort>: {0}", rscPort.getURI());
			} else if (MDSysMLModelHandler.isSysMLElement(port, "FullPort")) {
                rscPort = desc.resource("fullports", joker, output);
                rscBlock.addProperty(fullPort, rscPort);
                LOG.log(Level.INFO, "\t\t[+] <FullPort>: {0}", rscPort.getURI());
			} else if (port instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port) {
                rscPort = desc.resource("ports", joker, output);
                rscBlock.addProperty(genericPort, rscPort);
                LOG.log(Level.INFO, "\t\t[+] <BlockPort>: {0}", rscPort.getURI());
			} else {
                continue;
            }
            rscPort.addLiteral(elementName, port.getName());
            LOG.log(Level.INFO, "\t\t\t\tname: {0}", port.getName());
            propertyType = port.getType();
            if (propertyType != null) {
                if (MDSysMLModelHandler.isSysMLElement(propertyType, "Block"))
                    joker = "blocks";
                else if (MDSysMLModelHandler.isSysMLElement(propertyType, "InterfaceBlock"))
                    joker = "interfaceblocks";
                else
                    joker = "unknown";
                joker = desc.resource(joker, sysml.standardName(propertyType));
                rscPort.addProperty(type, output.createResource(joker));
                LOG.log(Level.INFO, "\t\t\t\ttype: {0}", joker);
            }
            rscPort.addProperty(isService, String.valueOf(port.isService()));
            LOG.log(Level.INFO, "\t\t\t\tisService: {0}", port.isService());
            rscPort.addProperty(isBehavior, String.valueOf(port.isBehavior()));
            LOG.log(Level.INFO, "\t\t\t\tisBehavior: {0}", port.isBehavior());
            rscPort.addProperty(isConjugated, String.valueOf(port.isConjugated()));
            LOG.log(Level.INFO, "\t\t\t\tisConjugated: {0}", port.isConjugated());
            rscPort.addProperty(lowerMult, Integer.toString(port.getLower()));
            LOG.log(Level.INFO, "\t\t\t\tlower: {0}", port.getLower());
            rscPort.addProperty(upperMult, Integer.toString(port.getUpper()));
            LOG.log(Level.INFO, "\t\t\t\tupper: {0}", port.getUpper());
            rscPort.addProperty(owner, rscBlock);
            LOG.log(Level.INFO, "\t\t\t\towner: {0}", rscBlock.getURI());
		}
    }

	private void buildBlocks(SysMLModel sysml, Model output, ModelDescriptor desc) {
        String joker;
        Resource resource;
        Property elementName;
        List<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> blocks;
        blocks = sysml.getStereotypes(Stereotypes.Block);
        if (blocks != null) {
            elementName = desc.property(GLOBAL_OWL_TYPE, "NamedElement_name");
            for(com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class block : blocks) {
                joker = sysml.standardName(block.getQualifiedName());
                resource = desc.resource("blocks", joker, output);
                LOG.log(Level.INFO, "[+] <block> {0}", joker);
                if ((joker = block.getName()) != null) {
                    resource.addLiteral(elementName, joker);
                }
                buildStereoTypedProperties(sysml, block, resource, output, desc);
                buildConnectors(sysml, block, resource, output, desc);
                buildPorts(sysml, block, resource, output, desc);
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
            buildPackages(sysml, model, descriptor);
            buildRequirements(sysml, model, descriptor);
            buildBlocks(sysml, model, descriptor);
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