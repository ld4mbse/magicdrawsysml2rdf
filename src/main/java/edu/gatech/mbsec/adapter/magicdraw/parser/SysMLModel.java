package edu.gatech.mbsec.adapter.magicdraw.parser;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Profile;
import static edu.gatech.mbsec.adapter.magicdraw.parser.MagicDrawApplication.SYSML_PROFILE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wraps a MagicDraw SysML model.
 * @author rherrera
 */
public class SysMLModel {
    /**
     * The name of this model.
     */
    private final String name;
    /**
     * The owner project.
     */
    private final Project project;
    /**
     * Reference to the SysML profile of this project.
     */
    private final Profile profile;
    /**
     * SysML packages.
     */
    private final List<Package> packages;
    /**
     * SysML stereotyped elements.
     */
    private final Map<Stereotypes, List<Class>> elements;
    /**
     * SysML associations;
     */
    private final List<AssociationClass> associations;
    /**
     * SysML information flows.
     */
    private final List<InformationFlow> informationFlows;
    /**
     * SysML data types.
     */
    private final List<DataType> dataTypes;
    /**
     * Constructs a default instance.
     * @param name the name of this model.
     * @param project the owner project.
     */
    public SysMLModel(String name, Project project) {
        this.profile = StereotypesHelper.getProfile(project, SYSML_PROFILE);
        this.project = project;
        this.name = name;
        packages = new ArrayList<>();
        elements = new HashMap<>();
        associations = new ArrayList<>();
        informationFlows = new ArrayList<>();
        dataTypes = new ArrayList<>();
    }
    /**
     * Gets the name of this model, normally the name of the source mdzip file.
     * @return the name of this model.
     */
    public String getName() {
        return name;
    }
    /**
     * Gets the owner project.
     * @return the owner project.
     */
    public Project getProject() {
        return project;
    }
    /**
     * Gets the SysML profile of the wrapped SysML project.
     * @return the SysML profile of the wrapped SysML project.
     */
    public Profile getProfile() {
        return profile;
    }
    /**
     * Gets the underlaying model.
     * @return the underlaying model.
     */
    public Model getModel() {
        return project.getModel();
    }
    /**
     * Adds a new package.
     * @param pkg the new package.
     */
    public void addPackage(Package pkg) {
        packages.add(pkg);
    }
    /**
     * Gets the SysML packages.
     * @return SysML packages.
     */
    public List<Package> getPackages() {
        if (packages == null) return null;
        return Collections.unmodifiableList(packages);
    }
    /**
     * Adds a steretyped element-
     * @param element the element to add.
     * @param stereotype the stereotype is has.
     */
    public void addElement(Class element, Stereotypes stereotype) {
        List<Class> stereotypes = elements.get(stereotype);
        if (stereotypes == null) {
            stereotypes = new ArrayList<>();
            elements.put(stereotype, stereotypes);
        }
        stereotypes.add(element);
    }
    /**
     * Gets the stereotyped elements.
     * @return the stereotyped elements.
     */
    public Map<Stereotypes, List<Class>> getElements() {
        return Collections.unmodifiableMap(elements);
    }
    /**
     * Gets all class elements with a given stereotype.
     * @param steretype the stereotype to match.
     * @return all class elements with a given stereotype.
     */
    public List<Class> getStereotypes(Stereotypes steretype) {
        List<Class> stereotypes = elements.get(steretype);
        return stereotypes == null ? null : Collections.unmodifiableList(stereotypes);
    }
    /**
     * Adds a new association.
     * @param association the new association.
     */
    public void addAssociation(AssociationClass association) {
        associations.add(association);
    }
    /**
     * Gets the associations.
     * @return the associations.
     */
    public List<AssociationClass> getAssociations() {
        if (associations == null) return null;
        return Collections.unmodifiableList(associations);
    }
    /**
     * Adds a new information flow.
     * @param flow the new information flow.
     */
    public void addInformationFlow(InformationFlow flow) {
        informationFlows.add(flow);
    }
    /**
     * Gets the information flows.
     * @return the information flows.
     */
    public List<InformationFlow> getInformationFlows() {
        if (informationFlows == null) return null;
        return Collections.unmodifiableList(informationFlows);
    }
    /**
     * Adds a new data type.
     * @param type the new data type.
     */
    public void addDataType(DataType type) {
        dataTypes.add(type);
    }
    /**
     * Gets the data types.
     * @return the data types.
     */
    public List<DataType> getDataTypes() {
        if (dataTypes == null) return null;
        return Collections.unmodifiableList(dataTypes);
    }
    /**
     * Standardize a name with this model name.
     * @param original the original name to standardize.
     * @return the input name standardized.
     */
    public String standardName(String original) {
        original = original.replaceAll("\\n", "-");
        original = original.replaceAll(" ", "_");
        return name + original;
    }
    /**
     * Standardize the name of an element with this model name.
     * @param original the original name to standardize.
     * @return the input name standardized.
     */
    public String standardName(Element original) {
        String stdName;
        NamedElement named;
        if (original instanceof NamedElement) {
            named = (NamedElement) original;
            stdName = named.getName();
            if (stdName == null || stdName.isEmpty())
                stdName = original.getID();
            else
                stdName = named.getQualifiedName();
        } else
            stdName = original.getID();
        return standardName(stdName);
    }

    @Override
    public String toString() {
        List<Class> stereotypes;
        Stereotypes[] stereoTypes = Stereotypes.values();
        StringBuilder sb = new StringBuilder();
        sb.append("Packages: ");
        sb.append(packages.size());
        for (Stereotypes stereotype : stereoTypes) {
            if (stereotype.isClassApplicable()) {
                stereotypes = elements.get(stereotype);
                sb.append("\n");
                sb.append(stereotype.name());
                sb.append(": ");
                sb.append(stereotypes == null ? 0 : stereotypes.size());
            }
        }
        sb.append("\nAssociations: ");
        sb.append(associations.size());
        sb.append("\nInformation Flows: ");
        sb.append(informationFlows.size());
        sb.append("\nData Types: ");
        sb.append(dataTypes.size());
        return sb.toString();
    }
}