package edu.gatech.mbsec.adapter.magicdraw.parser;

import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
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
     * The whole model.
     */
    private final Model model;
    /**
     * SysML packages.
     */
    private final List<Package> packages;
    /**
     * SysML stereotyped elements.
     */
    private final Map<Stereotype, List<Class>> stereotypes;
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
     * @param model the underlaying model.
     */
    public SysMLModel(String name, Model model) {
        this.name = name;
        this.model = model;
        packages = new ArrayList<>();
        stereotypes = new HashMap<>();
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
     * Gets the underlaying model.
     * @return the underlaying model.
     */
    public Model getModel() {
        return model;
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
        return Collections.unmodifiableList(packages);
    }
    /**
     * Adds a steretyped element-
     * @param element the element to add.
     * @param stereotype the stereotype is has.
     */
    public void addStereotype(Class element, Stereotype stereotype) {
        List<Class> elements = stereotypes.get(stereotype);
        if (elements == null) {
            elements = new ArrayList<>();
            stereotypes.put(stereotype, elements);
        }
        elements.add(element);
    }
    /**
     * Gets the stereotyped elements.
     * @return the stereotyped elements.
     */
    public Map<Stereotype, List<Class>> getStereotypes() {
        return Collections.unmodifiableMap(stereotypes);
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
        return Collections.unmodifiableList(dataTypes);
    }

    @Override
    public String toString() {
        List<Class> elements;
        Stereotype[] stereoTypes = Stereotype.values();
        StringBuilder sb = new StringBuilder();
        sb.append("Packages: ");
        sb.append(packages.size());
        for (Stereotype stereotype : stereoTypes) {
            if (stereotype.isClassApplicable()) {
                elements = stereotypes.get(stereotype);
                sb.append("\n");
                sb.append(stereotype.name());
                sb.append(": ");
                sb.append(elements == null ? 0 : elements.size());
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