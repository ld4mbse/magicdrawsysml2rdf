package edu.gatech.mbsec.adapter.magicdraw.parser;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.runtime.ApplicationExitedException;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The .mdzip files parser
 * @author rherrera
 */
public class MagicDrawParser {
    /**
     * Logger of this class.
     */
    private static final Logger LOG;
    /**
     * Packages to exclude from parsing.
     */
    private static final Set<String> EXCLUDED_PACKAGES;
    /**
     * Initialization block.
     */
    static {
        LOG = Logger.getLogger(MagicDrawParser.class.getName());
        EXCLUDED_PACKAGES = new HashSet<>();
		EXCLUDED_PACKAGES.add("SysML");
		EXCLUDED_PACKAGES.add("Matrix Templates Profile");
		EXCLUDED_PACKAGES.add("UML Standard Profile");
		EXCLUDED_PACKAGES.add("QUDV Library");
		EXCLUDED_PACKAGES.add("PrimitiveValueTypes");
		EXCLUDED_PACKAGES.add("MD Customization for SysML");
    }
    /**
     * The MD application.
     */
    private final MagicDrawApplication application;
    /**
     * Constructs an instance specifying the MagicDraw application.
     * @param application the MagicDraw application.
     */
    public MagicDrawParser(MagicDrawApplication application) {
        this.application = application;
    }
    /**
     * Logs an entire exception's stack trace.
     * @param ex the source exception.
     */
    private void logStackTrace(Exception ex) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(bos);
        ex.printStackTrace(printer);
        printer.flush();
        LOG.log(Level.SEVERE, "Could not parse mdzip.\n{0}", bos.toString());
    }
    /**
     * Gets the first matching stereotype on an element.
     * @param element the element to find its stereotype.
     * @param filter if {@code null} all stereotypes will be tested, if
     * {@code true} or {@code false}, only those stereotypes whose
     * {@link Stereotype#isClassApplicable()} return value match with the
     * {@code filter} will be tested.
     * @return the first matching stereotype on an element.
     */
    private Stereotype getStereotypeOnElement(Class element, Boolean filter) {
        Stereotype[] stereotypes = Stereotype.values();
        for(Stereotype stereotype : stereotypes) {
            if (stereotype.isOnElement(element)) {
                if (filter == null || stereotype.isClassApplicable() == filter)
                    return stereotype;
            }
        }
        return null;
    }
    /**
     * Gets all elements with a stereotype in a container element.
     * @param container the container element.
     * @param output the mdzip model abstraction.
     */
	private void traverse(PackageableElement container,
            SysMLModel output) {
        Package pkg;
		Class element;
        DataType dataType;
        Stereotype stereotype;
        String name = container.getName().replaceAll("\n", "-");
        Collection<PackageableElement> subelements;
        if (container instanceof Class) {
            element = (Class)container;
            stereotype = getStereotypeOnElement(element, true);
            if (stereotype == null)
                LOG.log(Level.INFO, "[-] {0}: no stereotype", name);
            else {
                output.addStereotype(element, stereotype);
                LOG.log(Level.INFO, "[+] {0} <{1}>",
                        new Object[]{name, stereotype.name()});
                for (Classifier classifier : element.getNestedClassifier()) {
                    traverse(classifier, output);
                }
            }
        } else if (container instanceof Package) {
            if (EXCLUDED_PACKAGES.contains(container.getName()))
                LOG.log(Level.INFO, "[-] {0}: excluded package", name);
            else {
                if (!((pkg = (Package) container) instanceof Model)) {
                    LOG.log(Level.INFO, "[+] {0}: <Package>", name);
                    output.addPackage(pkg);
                }
                subelements = pkg.getPackagedElement();
                for (PackageableElement subelement : subelements) {
                    traverse(subelement, output);
                }
            }
        } else if (container instanceof DataType) {
            dataType = (DataType) container;
            if (Stereotype.ValueType.isOnElement(dataType)) {
                output.addDataType(dataType);
                LOG.log(Level.INFO, "[+] {0} <DataType>", name);
            } else {
                LOG.log(Level.INFO, "[-] {0}: not a DataType", name);
            }
        }
	}
    /**
     * Gets the elements of a particular {@link Element element} type.
     * @param <T> the specific {@code Element} type.
     * @param model the underlaying model.
     * @param types the required types.
     * @return matching elements with the given type.
     */
	private static <T extends Element> Collection<T> getTypes(Model model,
            java.lang.Class<T>... types) {
		return (Collection<T>) ModelHelper.getElementsOfType(model, types, true, true);
	}
    /**
     * Instrospect the model for particular {@link Element element} types.
     * @param model the underlying model.
     * @param output 
     */
    private void introspect(Model model, SysMLModel output) {
        Collection<AssociationClass> associations = getTypes(model, AssociationClass.class);
        Collection<InformationFlow> informationFlows = getTypes(model, InformationFlow.class);
		for (AssociationClass association : associations) {
            output.addAssociation(association);
            LOG.log(Level.INFO, "[+] {0} <AssociationClass>", association.getHumanName());
		}
		for (InformationFlow flow : informationFlows) {
            if (Stereotype.ItemFlow.isOnElement(flow)) {
                output.addInformationFlow(flow);
                LOG.log(Level.INFO, "[+] {0} <InformationFlow>", flow.getHumanName());
            } else {
                LOG.log(Level.INFO, "[-] {0} : not an ItemFlow", flow.getHumanName());
            }
		}
    }
    /**
     * Parses an MD model into a {@link SysMLModel SysMLModel}.
     * @param model the MD project.
     * @param mdzip the original mdzip source file.
     * @return the mdzip model abstraction.
     */
    private SysMLModel parse(Model model, String mdzip) {
        SysMLModel output = new SysMLModel(mdzip, model);
        LOG.info("--- READING");
        traverse(model, output);
        LOG.info("--- INTROSPECTING");
        introspect(model, output);
        LOG.log(Level.INFO, "--- DONE\n{0}", output.toString());
        return output;
    }
    /**
     * Parses an mdzip file into a {@link SysMLModel}.
     * @param mdzip the input file to parse.
     * @return the mdzip model abstraction.
     * @throws ApplicationExitedException if the application ends unexpectedly.
     */
    public SysMLModel parse(String mdzip) throws ApplicationExitedException {
        Project project;
        int index = mdzip.lastIndexOf(File.separator) + 1;
        try {
            project = application.getProject(mdzip);
            mdzip = mdzip.substring(index, mdzip.indexOf(".mdzip"));
            return parse(project.getModel(), mdzip);
        } catch (ApplicationExitedException ex) {
            logStackTrace(ex);
            throw ex;
        }
    }

}