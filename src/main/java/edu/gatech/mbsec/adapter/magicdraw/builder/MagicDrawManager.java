    /*********************************************************************************************
 * Copyright (c) 2014 Model-Based Systems Engineering Center, Georgia Institute of Technology.
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *  
 *  The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *  and the Eclipse Distribution License is available at
 *  http://www.eclipse.org/org/documents/edl-v10.php.
 *  
 *  Contributors:
 *  
 *	   Axel Reichwein (axel.reichwein@koneksys.com)		- initial implementation 
 *	   Sebastian Herzig (sebastian.herzig@me.gatech.edu) - support for loading multiple MagicDraw models at the same time      
 *******************************************************************************************/

package edu.gatech.mbsec.adapter.magicdraw.builder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLAssociationBlock;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLBlock;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLBlockDiagram;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLConnector;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLConnectorEnd;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLFlowProperty;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLFullPort;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLInterfaceBlock;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLInternalBlockDiagram;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLItemFlow;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLModel;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLPackage;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLPartProperty;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLPort;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLProxyPort;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLReferenceProperty;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLRequirement;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLValueProperty;
import edu.gatech.mbsec.adapter.magicdraw.resources.SysMLValueType;

import org.eclipse.lyo.oslc4j.core.model.Link;
import org.eclipse.lyo.oslc4j.provider.jena.JenaModelHelper;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory;
import com.nomagic.magicdraw.core.project.ProjectsManager;
import com.nomagic.magicdraw.export.image.ImageExporter;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.sysml.util.SysMLConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.EnumerationLiteral;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.InstanceSpecification;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.LiteralString;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Namespace;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;

import com.nomagic.runtime.ApplicationExitedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Axel Reichwein (axel.reichwein@koneksys.com)
 */
public class MagicDrawManager {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(MagicDrawManager.class.getName());

    public static ModelDescriptor descriptor;

	static Collection<String> predefinedMagicDrawSysMLPackageNames = new HashSet<String>();

	public static Collection<Class> mdSysmlRequirements = new ArrayList<Class>();
	public static Map<String, Collection<Class>> projectIdMDSysmlRequirementsMap = new HashMap<String, Collection<Class>>();

	public static Collection<Class> mdSysmlBlocks = new ArrayList<Class>(); // list
																			// of
																			// MagicDraw
																			// SysML
																			// blocks
																			// for
																			// a
																			// specific
																			// project
	// list gets overwritten for each new project - this is ok when loading each
	// individual model separately - each creation web service in the past would
	// first reload the model and the list would be ok
	// now, models are loaded at launch of adapter, and at refresh times. each
	// creation web service no longer reload the model
	// when trying to retrieve MagicDraw SysML blocks of a specific project,
	// this list only provides the blocks of the last loaded model
	// what is needed is a list of mdSysmlBlocks for each MagicDraw model,
	// populated when loading a specific model, and used in the creation web
	// services
	public static Map<String, Collection<Class>> projectIdMDSysmlBlocksMap = new HashMap<String, Collection<Class>>();

	static Collection<Class> mdSysmlInterfaceBlocks = new ArrayList<Class>();
	public static Map<String, Collection<Class>> projectIdMDSysmlInterfaceBlocksMap = new HashMap<String, Collection<Class>>();

	static Collection<InformationFlow> mdSysmlItemFlows = new ArrayList<InformationFlow>();
	public static Map<String, Collection<InformationFlow>> projectIdMDSysmlItemFlowsMap = new HashMap<String, Collection<InformationFlow>>();

	public static Collection<DataType> mdSysmlValueTypes = new ArrayList<DataType>();
	public static Map<String, Collection<DataType>> projectIdMDSysmlValueTypesMap = new HashMap<String, Collection<DataType>>();

	public static Collection<Property> mdSysmlPartProperties = new ArrayList<Property>();
	public static Map<String, Collection<Property>> projectIdMDSysmlPartPropertiesMap = new HashMap<String, Collection<Property>>();

	public static Collection<Connector> mdSysmlConnectors = new ArrayList<Connector>();
	public static Map<String, Collection<Connector>> projectIdMDSysmlConnectorsMap = new HashMap<String, Collection<Connector>>();

	public static Collection<Port> mdSysmlPorts = new ArrayList<Port>();
	public static Map<String, Collection<Port>> projectIdMDSysmlPortsMap = new HashMap<String, Collection<Port>>();

	public static Collection<Property> mdSysmlValueProperties = new ArrayList<Property>();
	public static Map<String, Collection<Property>> projectIdMDSysmlValuePropertiesMap = new HashMap<String, Collection<Property>>();

	public static Collection<Property> mdSysmlFlowProperties = new ArrayList<Property>();
	public static Map<String, Collection<Property>> projectIdMDSysmlFlowPropertiesMap = new HashMap<String, Collection<Property>>();

	public static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> mdSysmlPackages = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>();
	public static Map<String, Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>> projectIdMDSysmlPackagesMap = new HashMap<String, Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>>();

	static Collection<DiagramPresentationElement> mdSysmlBlockDiagrams = new ArrayList<DiagramPresentationElement>();
	static Collection<DiagramPresentationElement> mdSysmlInternalBlockDiagrams = new ArrayList<DiagramPresentationElement>();
	static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass> mdSysmlAssociationBlocks = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass>();

	static Collection<SysMLRequirement> oslcSysmlRequirements = new ArrayList<SysMLRequirement>();
	static Collection<SysMLBlock> oslcSysmlBlocks = new ArrayList<SysMLBlock>();

	static Map<String, Class> idMdSysmlRequirementMap = new HashMap<String, Class>();
	static Map<String, SysMLRequirement> idOslcSysmlRequirementMap = new HashMap<String, SysMLRequirement>();

	// static Map<String, Class> qNameMdSysmlBlockMap = new HashMap<String,
	// Class>();
	static Map<String, SysMLBlock> qNameOslcSysmlBlockMap = new HashMap<String, SysMLBlock>();

	static Map<String, SysMLPartProperty> qNameOslcSysmlPartPropertyMap = new HashMap<String, SysMLPartProperty>();
	static Map<String, SysMLReferenceProperty> qNameOslcSysmlReferencePropertyMap = new HashMap<String, SysMLReferenceProperty>();

	static Map<String, SysMLModel> oslcSysmlModelMap = new HashMap<String, SysMLModel>();
	static Map<String, SysMLPackage> qNameOslcSysmlPackageMap = new HashMap<String, SysMLPackage>();

	static Map<String, SysMLAssociationBlock> qNameOslcSysmlAssociationBlockMap = new HashMap<String, SysMLAssociationBlock>();
	static Map<String, SysMLConnector> qNameOslcSysmlConnectorMap = new HashMap<String, SysMLConnector>();
	static Map<String, SysMLConnectorEnd> qNameOslcSysmlConnectorEndMap = new HashMap<String, SysMLConnectorEnd>();
	static Map<String, SysMLPort> qNameOslcSysmlPortMap = new HashMap<String, SysMLPort>();
	static Map<String, SysMLProxyPort> qNameOslcSysmlProxyPortMap = new HashMap<String, SysMLProxyPort>();
	static Map<String, SysMLFullPort> qNameOslcSysmlFullPortMap = new HashMap<String, SysMLFullPort>();

	static Map<String, SysMLInterfaceBlock> qNameOslcSysmlInterfaceBlockMap = new HashMap<String, SysMLInterfaceBlock>();
	static Map<String, SysMLFlowProperty> qNameOslcSysmlFlowPropertyMap = new HashMap<String, SysMLFlowProperty>();

	static Map<String, SysMLItemFlow> qNameOslcSysmlItemFlowMap = new HashMap<String, SysMLItemFlow>();
	static Map<String, SysMLValueProperty> qNameOslcSysmlValuePropertyMap = new HashMap<String, SysMLValueProperty>();
	static Map<String, SysMLValueType> qNameOslcSysmlValueTypeMap = new HashMap<String, SysMLValueType>();

	static Map<String, SysMLBlockDiagram> qNameOslcSysmlBlockDiagramMap = new HashMap<String, SysMLBlockDiagram>();
	static Map<String, SysMLInternalBlockDiagram> qNameOslcSysmlInternalBlockDiagramMap = new HashMap<String, SysMLInternalBlockDiagram>();

	static String projectId;

	public static Application magicdrawApplication;
	public static Object applicationClassInstance;
	public static java.lang.Class<?> applicationClass;
	static Model model;
	public static Project project;
	public static ProjectsManager projectsManager;
	public static Map<String, Project> loadedProjects = new HashMap<String, Project>();

	static String magicDrawFileName;

	

	/**
	 * This method retrieves SysML elements from a specific MagicDraw project
	 * (mdzip file).
	 * 
	 * This method is invoked by most or all web services of the OSLC MagicDraw
	 * adapter.
	 * 
	 * @param projectId
	 *            the name of the MagicDraw mdzip file (not the name of the
	 *            SysML model contained in the mdzip file!)
	 * 
	 */
	public static void loadSysMLProject(String projectId, String magicDrawModelPath) throws ApplicationExitedException, MDModelLibException, URISyntaxException, IOException {
		initializeCollections();
		MagicDrawManager.projectId = projectId;
		magicDrawFileName = projectId;
		if (project == null) {
            magicdrawApplication = Application.getInstance();
            magicdrawApplication.start(false, true, false, new String[0], null);
		}
		projectsManager = magicdrawApplication.getProjectsManager();
        final File sysmlfile;
            sysmlfile = new File(magicDrawModelPath);
        // final File sysmlfile = new File(magicdrawModelsDirectory +
        // projectId + ".mdzip");
        ProjectDescriptor projectDescriptor = ProjectDescriptorsFactory.createProjectDescriptor(sysmlfile.toURI());
        projectsManager.loadProject(projectDescriptor, true);
        if (!SessionManager.getInstance().isSessionCreated()) {
            SessionManager.getInstance().createSession("MagicDraw OSLC Session for projectId" + projectId);
        }
        project = projectsManager.getActiveProject();
        loadedProjects.put(projectId, project);
        
		// List of packages not to load
		predefinedMagicDrawSysMLPackageNames.add("SysML");
		predefinedMagicDrawSysMLPackageNames.add("Matrix Templates Profile");
		predefinedMagicDrawSysMLPackageNames.add("UML Standard Profile");
		predefinedMagicDrawSysMLPackageNames.add("QUDV Library");
		predefinedMagicDrawSysMLPackageNames.add("PrimitiveValueTypes");
		predefinedMagicDrawSysMLPackageNames.add("MD Customization for SysML");
        
        // mapping MagicDraw SysML model
        model = mapSysMLModel(project);

        // collecting all MagicDraw SysML blocks and requirements
        mdSysmlBlocks = getAllSysMLBlocks(model);
        Collection<Class> blocks = new ArrayList<Class>();
        blocks.addAll(mdSysmlBlocks);
        projectIdMDSysmlBlocksMap.put(projectId, blocks);

        mdSysmlRequirements = getAllSysMLRequirements(model);
        Collection<Class> reqs = new ArrayList<Class>();
        reqs.addAll(mdSysmlRequirements);
        projectIdMDSysmlRequirementsMap.put(projectId, reqs);

        mdSysmlPackages = getAllSysMLPackages(model);
        Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> packages = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>();
        packages.addAll(mdSysmlPackages);
        projectIdMDSysmlPackagesMap.put(projectId, packages);

        mdSysmlAssociationBlocks = getAllSysMLAssociationBlocks(model);

        mdSysmlInterfaceBlocks = getAllSysMLInterfaceBlocks(model);
        Collection<Class> infblocks = new ArrayList<Class>();
        infblocks.addAll(mdSysmlInterfaceBlocks);
        projectIdMDSysmlInterfaceBlocksMap.put(projectId, infblocks);

        mdSysmlItemFlows = getAllSysMLItemFlows(model);
        Collection<InformationFlow> itemFlows = new ArrayList<InformationFlow>();
        itemFlows.addAll(mdSysmlItemFlows);
        projectIdMDSysmlItemFlowsMap.put(projectId, itemFlows);

        predefinedMagicDrawSysMLPackageNames.remove("QUDV Library");

        mdSysmlValueTypes = getAllSysMLValueTypes(model);
        Collection<DataType> valuetypes = new ArrayList<DataType>();
        valuetypes.addAll(mdSysmlValueTypes);
        projectIdMDSysmlValueTypesMap.put(projectId, valuetypes);

        predefinedMagicDrawSysMLPackageNames.add("QUDV Library");

        getAllSysMLDiagrams();

        // closing MagicDraw
        // magicdrawApplication.exit();
        //

        // mapping MagicDraw SysML packages into OSLC packages
        mapSysMLPackages();

        // mapping MagicDraw SysML requirements into OSLC requirements
        mapSysMLRequirements();

        // mapping MagicDraw SysML blocks into OSLC blocks
        mapSysMLBlocks();
        initializeMap();

        // mapping MagicDraw SysML interface blocks into OSLC interface
        // blocks
        mapSysMLInterfaceBlocks();

        // mapping MagicDraw SysML association blocks into OSLC association
        // blocks
        mapSysMLAssociationBlocks();

        // mapping MagicDraw SysML value types into OSLC value types
        mapSysMLValueTypes();

        // mapping MagicDraw SysML item flows into OSLC item flows
        mapSysMLItemFlows();

        // mapping MagicDraw SysML package relationships
        mapSysMLPackageRelationships();

        // mapping MagicDraw SysML requirements relationships
        mapSysMLRequirementRelationships();

        // mapping MagicDraw SysML block relationships
        mapSysMLBlockRelationships();

        // map SysML block diagrams
        //mapSysMLBlockDiagrams();

        // map SysML internal block diagrams

	}

	private static void initializeMap() {
		Collection<Property> parts = new ArrayList<Property>();
		parts.addAll(mdSysmlPartProperties);
		projectIdMDSysmlPartPropertiesMap.put(projectId, parts);

		Collection<Property> values = new ArrayList<Property>();
		values.addAll(mdSysmlValueProperties);
		projectIdMDSysmlValuePropertiesMap.put(projectId, values);

		Collection<Property> flows = new ArrayList<Property>();
		flows.addAll(mdSysmlFlowProperties);
		projectIdMDSysmlFlowPropertiesMap.put(projectId, flows);

		Collection<Connector> connectors = new ArrayList<Connector>();
		connectors.addAll(mdSysmlConnectors);
		projectIdMDSysmlConnectorsMap.put(projectId, connectors);

		Collection<Port> ports = new ArrayList<Port>();
		ports.addAll(mdSysmlPorts);
		projectIdMDSysmlPortsMap.put(projectId, ports);

	}

	private static void initializeCollections() {

		magicDrawFileName = null;
		// model = null;

		mdSysmlBlocks.clear();
		mdSysmlRequirements.clear();
		mdSysmlPackages.clear();
		mdSysmlAssociationBlocks.clear();
		mdSysmlInterfaceBlocks.clear();
		mdSysmlItemFlows.clear();
		mdSysmlValueTypes.clear();
		mdSysmlBlockDiagrams.clear();
		mdSysmlInternalBlockDiagrams.clear();
		mdSysmlPartProperties.clear();
		mdSysmlConnectors.clear();
		mdSysmlPorts.clear();
		mdSysmlValueProperties.clear();
		mdSysmlFlowProperties.clear();

		oslcSysmlRequirements.clear();
		oslcSysmlBlocks.clear();

		idMdSysmlRequirementMap.clear();
//		idOslcSysmlRequirementMap.clear();

		oslcSysmlModelMap.clear();
	}

	private static void mapSysMLBlockDiagrams() throws IOException {
		for (DiagramPresentationElement diagramPresentationElement : mdSysmlBlockDiagrams) {
			String diagramName = diagramPresentationElement.getDiagram().getName();
			// BaseElement baseElement =
			// diagramPresentationElement.getObjectParent();
			// Element owner =
			// diagramPresentationElement.getDiagram().getOwner();

			if (!diagramPresentationElement.isLoaded()) {
				diagramPresentationElement.ensureLoaded();
				System.out.println("diagram not loaded");
			}
			if (!diagramPresentationElement.isLoaded()) {
				System.out.println("diagram still not loaded");
				diagramPresentationElement.open();
			}
			Namespace namespace = diagramPresentationElement.getDiagram().getOwnerOfDiagram();
			String filePathName = "src/main/webapp/images/sysml block diagrams/" + diagramName + ".png";
			// String filePathName =
			// "C:/Users/Axel/git/oslc4jmagicdraw/oslc4jmagicdraw/src/main/webapp/images/sysml
			// block diagrams/"
			// + diagramName + ".png";
			File diagramFile = new File(filePathName);
			// File diagramFile = new File(mDestinationDir,
			// diagram.getHumanName() + diagram.getID() + ".png");
            ImageExporter.export(diagramPresentationElement, ImageExporter.PNG, diagramFile);
		}

	}

	private static void getAllSysMLDiagrams() throws URISyntaxException, IOException {
		for (DiagramPresentationElement diagramPresentationElement : magicdrawApplication.getProject().getDiagrams()) {
			String diagramType = diagramPresentationElement.getDiagramType().getType();
			String diagramName = diagramPresentationElement.getDiagram().getName();
			String qfOwner = getQualifiedNameOrID(diagramPresentationElement.getDiagram().getOwner());
			String diagramID = qfOwner + "::" + diagramName.replaceAll("\\n", "-").replaceAll(" ", "_");
			if (diagramPresentationElement.isLoaded()) {
				System.out.println("test");
			}
			if (diagramType.equals("SysML Block Definition Diagram")) {
				mdSysmlBlockDiagrams.add(diagramPresentationElement);

				SysMLBlockDiagram sysMLBlockDiagram;
                sysMLBlockDiagram = new SysMLBlockDiagram();
                qNameOslcSysmlBlockDiagramMap.put(magicDrawFileName + "/blockdiagrams/" + diagramID,
                        sysMLBlockDiagram);
                sysMLBlockDiagram.setAbout(URI.create(descriptor.resource("blockdiagrams", projectId + diagramID)));
                sysMLBlockDiagram.setName(diagramName.replaceAll(" ", "_"));

			} else if (diagramType.equals("SysML Internal Block Diagram")) {
				mdSysmlInternalBlockDiagrams.add(diagramPresentationElement);

				SysMLInternalBlockDiagram sysMLInternalBlockDiagram;
                sysMLInternalBlockDiagram = new SysMLInternalBlockDiagram();
                qNameOslcSysmlInternalBlockDiagramMap.put(magicDrawFileName + "/internalblockdiagrams/" + diagramID,
                        sysMLInternalBlockDiagram);
                sysMLInternalBlockDiagram.setAbout(URI.create(descriptor.resource("internalblockdiagrams", projectId + diagramID)));
                sysMLInternalBlockDiagram.setName(diagramName.replaceAll(" ", "_"));
			}

			String filePathName = "src/main/webapp/images/sysml diagrams/" + diagramName + ".png";
			File diagramFile = new File(filePathName);
            //TODO ask axel what is this meant to
    		//ImageExporter.export(diagramPresentationElement, ImageExporter.PNG, diagramFile);
		}

	}

	private static void mapSysMLValueTypes() throws URISyntaxException {
		for (DataType mdSysMLValueType : mdSysmlValueTypes) {
			String qName = mdSysMLValueType.getQualifiedName();
			SysMLValueType sysMLValueType = new SysMLValueType();
            qNameOslcSysmlValueTypeMap.put(
                    magicDrawFileName + "/valuetypes/" + qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
                    sysMLValueType);

            // name attribute
            String name = mdSysMLValueType.getName();

            // URI
            if (name != null) {
                sysMLValueType.setName(name);
                LOG.info("SysML Block with Name: " + sysMLValueType.getName());
                sysMLValueType.setAbout(URI.create(descriptor.resource("valuetypes", projectId + getQualifiedNameOrID(mdSysMLValueType))));
            }

            // unit attribute
            String humanName = mdSysMLValueType.getHumanName();
            Element unit = (Element) StereotypesHelper.getStereotypePropertyFirst(mdSysMLValueType,
                    StereotypesHelper.getFirstVisibleStereotype(mdSysMLValueType), "unit");

            Stereotype valueTypeStereotype = StereotypesHelper.getStereotype(Application.getInstance().getProject(),
                    SysMLConstants.VALUE_TYPE_STEREOTYPE, SysMLConstants.SYSML_PROFILE);
            if (valueTypeStereotype != null) {
                unit = ((InstanceSpecification) StereotypesHelper.getStereotypePropertyFirst(mdSysMLValueType,
                        valueTypeStereotype, SysMLConstants.VALUE_TYPE_UNIT_TAG));
            }

            if (unit != null) {
                sysMLValueType.setUnit(URI.create(descriptor.resource("units", projectId + getQualifiedNameOrID(unit))));
            }

            // quantity kind attribute
            Element quantityKind = (Element) StereotypesHelper.getStereotypePropertyFirst(mdSysMLValueType,
                    StereotypesHelper.getFirstVisibleStereotype(mdSysMLValueType), "quantityKind");
            if (quantityKind != null) {
                sysMLValueType.setUnit(URI.create(descriptor.resource("quantitykinds",projectId	+ getQualifiedNameOrID(quantityKind))));
            }
		}

	}

	private static Collection<DataType> getAllSysMLValueTypes(
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package packageableElement) throws MDModelLibException {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType> sysmlValueTypes = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType>();
		if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement;
			for (PackageableElement nestedPackageableElement : package_.getPackagedElement()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedPackageableElement, "ValueType")) {
					DataType magicDrawSysMLValueType = (DataType) nestedPackageableElement;
					sysmlValueTypes.add(magicDrawSysMLValueType);
				} else
					if (nestedPackageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
					com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) nestedPackageableElement;
					if (!predefinedMagicDrawSysMLPackageNames.contains(nestedPackage.getName())) {
						sysmlValueTypes.addAll(getAllSysMLValueTypes(nestedPackage));
					}
				}
			}
		}

		return sysmlValueTypes;
	}

	private static void mapSysMLItemFlows() throws MDModelLibException, URISyntaxException {
		for (com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow mdSysMLItemFlow : mdSysmlItemFlows) {
			String itemFlowID = mdSysMLItemFlow.getID();
			// qNameMdSysmlAssociationBlockMap.put(
			// qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
			// mdSysMLAssociationBlock);
			SysMLItemFlow sysMLItemFlow = new SysMLItemFlow();
            qNameOslcSysmlItemFlowMap.put(getQualifiedNameOrID(mdSysMLItemFlow), sysMLItemFlow);
            sysMLItemFlow.setAbout(URI.create(descriptor.resource("itemflows", projectId + getQualifiedNameOrID(mdSysMLItemFlow))));

            // information source
            NamedElement informationSource = (NamedElement) mdSysMLItemFlow.getInformationSource().toArray()[0];
            URI linkedInformationSourceURI = null;
            if (MDSysMLModelHandler.isSysMLElement(informationSource, "PartProperty")) {
                linkedInformationSourceURI = new URI(descriptor.resource("partproperties", projectId + getQualifiedNameOrID(informationSource)));
            } else if (MDSysMLModelHandler.isSysMLElement(informationSource, "ProxyPort")) {
                linkedInformationSourceURI = new URI(descriptor.resource("proxyports", projectId + getQualifiedNameOrID(informationSource)));
            } else if (MDSysMLModelHandler.isSysMLElement(informationSource, "FullPort")) {
                linkedInformationSourceURI = new URI(descriptor.resource("fullports", projectId	+ getQualifiedNameOrID(informationSource)));
            } else
                if (informationSource instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port) {
                linkedInformationSourceURI = new URI(descriptor.resource("ports", projectId+ getQualifiedNameOrID(informationSource)));
            }
            sysMLItemFlow.setInformationSource(linkedInformationSourceURI);

            // information target
            NamedElement informationTarget = (NamedElement) mdSysMLItemFlow.getInformationTarget().toArray()[0];
            URI linkedInformationTargetURI = null;
            if (MDSysMLModelHandler.isSysMLElement(informationTarget, "PartProperty")) {
                linkedInformationTargetURI = new URI(descriptor.resource("partproperties", projectId + getQualifiedNameOrID(informationTarget)));

            } else if (MDSysMLModelHandler.isSysMLElement(informationTarget, "ProxyPort")) {
                linkedInformationTargetURI = new URI(descriptor.resource("proxyports", projectId + getQualifiedNameOrID(informationTarget)));
            } else if (MDSysMLModelHandler.isSysMLElement(informationTarget, "FullPort")) {
                linkedInformationTargetURI = new URI(descriptor.resource("fullports", projectId + getQualifiedNameOrID(informationTarget)));
            } else
                if (informationTarget instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port) {
                linkedInformationTargetURI = new URI(descriptor.resource("ports", projectId	+ getQualifiedNameOrID(informationTarget)));
            }
            sysMLItemFlow.setInformationTarget(linkedInformationTargetURI);

            // realizingConnector
            if (mdSysMLItemFlow.getRealizingConnector().size() > 0) {
                Connector connector = (Connector) mdSysMLItemFlow.getRealizingConnector().toArray()[0];
                URI realizingConnectorURI = new URI(descriptor.resource("connectors", projectId + getQualifiedNameOrID(connector)));
                sysMLItemFlow.setRealizingConnector(realizingConnectorURI);
            }

            // itemProperty
            Property itemProperty = (Property) StereotypesHelper.getStereotypePropertyFirst(mdSysMLItemFlow,
                    StereotypesHelper.getFirstVisibleStereotype(mdSysMLItemFlow), "itemProperty");
            URI itemPropertyURI = null;
            if (itemProperty != null) {
                if (MDSysMLModelHandler.isSysMLElement(itemProperty, "FlowProperty")) {
                    itemPropertyURI = new URI(descriptor.resource("flowproperties", projectId + getQualifiedNameOrID(itemProperty)));
                }
                sysMLItemFlow.setItemProperty(itemPropertyURI);
            }

		}

	}

	private static void mapSysMLAssociationBlocks() throws URISyntaxException {
		for (com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass mdSysMLAssociationBlock : mdSysmlAssociationBlocks) {
			String qName = mdSysMLAssociationBlock.getQualifiedName();
			// qNameMdSysmlAssociationBlockMap.put(
			// qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
			// mdSysMLAssociationBlock);
			SysMLAssociationBlock sysMLAssociationBlock = new SysMLAssociationBlock();
            qNameOslcSysmlAssociationBlockMap.put(
                    magicDrawFileName + "/associationblocks/" + qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
                    sysMLAssociationBlock);

            // SysML association block Name attribute
            String name = mdSysMLAssociationBlock.getName();
            if (name != null) {
                sysMLAssociationBlock.setName(name);
                LOG.info("SysML Block with Name: " + sysMLAssociationBlock.getName());
                sysMLAssociationBlock.setAbout(URI.create(descriptor.resource("associationblocks", projectId + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

            // SysML association block memberEnd attribute
            Association mdSysMAssociation = (Association) mdSysMLAssociationBlock;
            Link[] linksArray = new Link[2];

            int linksArrayIndex = 0;
            for (Property memberEnd : mdSysMAssociation.getMemberEnd()) {
                URI linkedElementURI = null;
                linkedElementURI = new URI(descriptor.resource("referenceproperties", projectId	+ memberEnd.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_")));
                Link link = new Link(linkedElementURI);
                linksArray[linksArrayIndex] = link;
                linksArrayIndex++;
            }
            if (linksArrayIndex > 0) {
                sysMLAssociationBlock.setMemberEnds(linksArray);
            }
		}

	}

	private static void mapSysMLInterfaceBlocks() throws MDModelLibException, URISyntaxException {
		for (Class mdSysMLBlock : mdSysmlInterfaceBlocks) {
			String qName = mdSysMLBlock.getQualifiedName();
			// qNameMdSysmlInterfaceBlockMap.put(
			// qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
			// mdSysMLBlock);
			SysMLInterfaceBlock sysMLInterfaceBlock = new SysMLInterfaceBlock();
            qNameOslcSysmlInterfaceBlockMap.put(
                    magicDrawFileName + "/interfaceblocks/" + qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
                    sysMLInterfaceBlock);

            // SysML Block Name attribute
            String name = mdSysMLBlock.getName();
            if (name != null) {
                sysMLInterfaceBlock.setName(name);
                LOG.info("SysML Interface Block with Name: " + sysMLInterfaceBlock.getName());
                sysMLInterfaceBlock.setAbout(URI.create(descriptor.resource("interfaceblocks", projectId + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

            // SysML Block Flow Properties
            mapSysMLFlowProperties(mdSysMLBlock, sysMLInterfaceBlock);

            // SysML Proxy Ports
            mapSysMLProxyPorts(mdSysMLBlock, sysMLInterfaceBlock);
		}

	}

	private static void mapSysMLFlowProperties(Class mdSysMLBlock, SysMLInterfaceBlock sysMLInterfaceBlock) throws URISyntaxException {
		Link[] flowPropertiesLinksArray = getLinkedStereotypedSysMLElements(mdSysMLBlock.getOwnedAttribute(),
				"FlowProperty", descriptor.resource("flowproperties", projectId));

		if (flowPropertiesLinksArray != null) {
			sysMLInterfaceBlock.setFlowProperties(flowPropertiesLinksArray);

			LOG.info(" " + sysMLInterfaceBlock.getName());
			LOG.info("\tblock reference properties: " + flowPropertiesLinksArray.length);
			LOG.info(" " + sysMLInterfaceBlock.getName());
			LOG.info("\tblock reference properties: ");
			for (Link link : flowPropertiesLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Property property : mdSysMLBlock.getOwnedAttribute()) {

			if (property.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = property.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().contains("FlowProperty")) {
					SysMLFlowProperty sysmlFlowProperty = new SysMLFlowProperty();
                    qNameOslcSysmlFlowPropertyMap.put(
                            magicDrawFileName + "/flowproperties/"
                                    + property.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"),
                            sysmlFlowProperty);

                    // referenceProperty name
                    sysmlFlowProperty.setName(property.getName());

                    String qName = property.getQualifiedName();
                    sysmlFlowProperty.setAbout(URI.create(descriptor.resource("flowproperties", projectId + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // referenceProperty type
                    if (property.getType() != null) {
                        sysmlFlowProperty
                                .setType(new URI(descriptor.resource("blocks", projectId + property
                                        .getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    }

                    // referenceProperty multiplicity
                    // String lowerMultiplicity = Integer.toString(property
                    // .getLower());
                    // String upperMultiplicity = Integer.toString(property
                    // .getUpper());
                    // sysmlFlowProperty.setLower(lowerMultiplicity);
                    // sysmlFlowProperty.setUpper(upperMultiplicity);

                    // direction
                    Object directionObject = StereotypesHelper.getStereotypePropertyFirst(property,
                            (Stereotype) property.getAppliedStereotypeInstance().getClassifier().get(0),
                            "direction");
                    if (directionObject instanceof EnumerationLiteral) {
                        EnumerationLiteral enumLit = (EnumerationLiteral) directionObject;
                        String enumLitName = enumLit.getName();
                        if (enumLitName.equals("in")) {
                            // sysmlFlowProperty
                            // .setDirection(SysMLFlowDirection.IN);
                            sysmlFlowProperty.setDirection("in");
                        } else if (enumLitName.equals("out")) {
                            // sysmlFlowProperty
                            // .setDirection(SysMLFlowDirection.OUT);
                            sysmlFlowProperty.setDirection("out");
                        }

                    }

				}
			}
		}

	}

	private static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass> getAllSysMLAssociationBlocks(
			Model model) {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass> sysmlAssociationBlocks = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass>();
		java.lang.Class[] classArray = new java.lang.Class[1];
		classArray[0] = com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass.class;
		Collection<? extends Element> elementsOfType = ModelHelper.getElementsOfType(model, classArray, true, true);
		sysmlAssociationBlocks = (Collection<AssociationClass>) elementsOfType;
		LOG.info("**** Elements of type AssociationClass ****");
		for (Element element : elementsOfType) {
			LOG.info("" + element.getHumanName());
		}
		return sysmlAssociationBlocks;
	}

	// static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>
	// getAllSysMLAssociationBlocks(
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement
	// packageableElement) throws MDModelLibException {
	// Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>
	// sysmlBlocks = new
	// ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>();
	// if (packageableElement instanceof
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ =
	// (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package)
	// packageableElement;
	// for (PackageableElement nestedPackageableElement : package_
	// .getPackagedElement()) {
	// if (MDSysMLModelHandler.isSysMLElement(nestedPackageableElement,
	// "AssociationBlock")) {
	// Class magicDrawSysMLBlock = (Class) nestedPackageableElement;
	// sysmlBlocks.add(magicDrawSysMLBlock);
	// sysmlBlocks
	// .addAll(getAllSysMLAssociationBlocks(magicDrawSysMLBlock));
	// } else if (nestedPackageableElement instanceof
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage =
	// (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package)
	// nestedPackageableElement;
	// if (!predefinedMagicDrawSysMLPackageNames
	// .contains(nestedPackage.getName())) {
	// sysmlBlocks
	// .addAll(getAllSysMLAssociationBlocks(nestedPackage));
	// }
	// }
	// }
	// } else if (packageableElement instanceof
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) {
	// com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class class_ =
	// (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
	// packageableElement;
	// for (Classifier nestedClassifier : class_.getNestedClassifier()) {
	// if (MDSysMLModelHandler.isSysMLElement(nestedClassifier,
	// "AssociationBlock")) {
	// sysmlBlocks
	// .add((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class)
	// nestedClassifier);
	// }
	// sysmlBlocks.addAll(getAllSysMLAssociationBlocks(nestedClassifier));
	// }
	// }
	// return sysmlBlocks;
	// }

	private static Collection<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow> getAllSysMLItemFlows(
			Model model) throws MDModelLibException {
		Collection<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow> sysmlInformationFlows = new ArrayList<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow>();
		java.lang.Class[] classArray = new java.lang.Class[1];
		classArray[0] = com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow.class;
		Collection<? extends Element> elementsOfType = ModelHelper.getElementsOfType(model, classArray, true, true);
		sysmlInformationFlows = (Collection<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow>) elementsOfType;
		Collection<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow> itemFlows = new ArrayList<com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdinformationflows.InformationFlow>();
		for (InformationFlow sysmlInformationFlow : sysmlInformationFlows) {
			if (MDSysMLModelHandler.isSysMLElement(sysmlInformationFlow, "ItemFlow")) {
				itemFlows.add(sysmlInformationFlow);
			}
		}

		// LOG.info("**** Elements of type InformationFlow ****");
		// for (Element element : elementsOfType) {
		// LOG.info("" + element.getHumanName());
		// }
		return itemFlows;
	}

	private static void mapSysMLPackageRelationships() throws URISyntaxException {
		for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package mdSysMLPackage : mdSysmlPackages) {

			SysMLPackage sysMLPackage = qNameOslcSysmlPackageMap.get(magicDrawFileName + "/packages/"
					+ mdSysMLPackage.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"));

			// get nested blocks
			Link[] packageBlocksLinksArray = getLinkedStereotypedSysMLElements(mdSysMLPackage.getOwnedType(), "Block",
					descriptor.resource("blocks", projectId));
			if (packageBlocksLinksArray != null) {
				sysMLPackage.setBlocks(packageBlocksLinksArray);
				LOG.info(" " + sysMLPackage.getName());
				LOG.info("\tpackageBlocks: " + packageBlocksLinksArray.length);
				LOG.info(" " + sysMLPackage.getName());
				LOG.info("\tpackageBlocks: ");
				for (Link link : packageBlocksLinksArray) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// get nested requirements
			Link[] packageRequirementsLinksArray = getLinkedStereotypedSysMLElements(mdSysMLPackage.getOwnedType(),
					"Requirement", descriptor.resource("requirements", projectId));
			if (packageRequirementsLinksArray != null) {
				sysMLPackage.setRequirements(packageRequirementsLinksArray);
				LOG.info(" " + sysMLPackage.getName());
				LOG.info("\tpackageRequirements: " + packageRequirementsLinksArray.length);
				LOG.info(" " + sysMLPackage.getName());
				LOG.info("\tpackageRequirements: ");
				for (Link link : packageRequirementsLinksArray) {
					LOG.info("\t\t " + link.getValue());
				}
			}
		}

	}

	private static void mapSysMLPackages() throws URISyntaxException {
		for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package mdSysMLPackage : mdSysmlPackages) {
			String qName = mdSysMLPackage.getQualifiedName();
			// qNameMdSysmlPackageMap
			// .put(qName.replaceAll("\\n", "-"), mdSysMLPackage);
			SysMLPackage sysMLPackage = new SysMLPackage();
            qNameOslcSysmlPackageMap.put(
                    magicDrawFileName + "/packages/" + qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
                    sysMLPackage);

            // SysML Package Name attribute
            String name = mdSysMLPackage.getName();
            if (name != null) {
                sysMLPackage.setName(name);
                LOG.info("SysML Package with Name: " + sysMLPackage.getName());
                sysMLPackage.setAbout(URI.create(descriptor.resource("packages", projectId
                        + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }
		}

	}

	private static Model mapSysMLModel(Project project) throws URISyntaxException {
		Model model = project.getModel();
		SysMLModel sysMLModel = new SysMLModel();
        if (model.getName() == null) {
            sysMLModel.setName("Data");
        } else {
            sysMLModel.setName(model.getName());
        }

        sysMLModel.setAbout(URI.create(descriptor.resource("model", projectId + model.getName())));

        oslcSysmlModelMap.put(model.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"), sysMLModel);

        Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> modelPackages = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>();
        for (PackageableElement packageableElement : model.getPackagedElement()) {
            if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
                if (!predefinedMagicDrawSysMLPackageNames.contains(packageableElement.getName())) {
                    modelPackages.add((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement);
                }

            }
        }

        if (modelPackages.size() > 0) {
            Link[] linksArray = new Link[modelPackages.size()];

            int linksArrayIndex = 0;
            for (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package1 : modelPackages) {
                if (!predefinedMagicDrawSysMLPackageNames.contains(package1.getName())) {
                    URI linkedElementURI = null;
                    linkedElementURI = new URI(descriptor.resource("packages", projectId
                            + package1.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_")));
                    Link link = new Link(linkedElementURI);
                    linksArray[linksArrayIndex] = link;
                    linksArrayIndex++;
                }
            }

            sysMLModel.setPackages(linksArray);
            LOG.info(" " + sysMLModel.getName());
            LOG.info("\tmodel packages: " + linksArray.length);
            LOG.info(" " + sysMLModel.getName());
            LOG.info("\tmodel packages: ");
            for (Link link2 : linksArray) {
                LOG.info("\t\t " + link2.getValue());
            }
        }

        // map SysML packages
        // mapSysMLPackages(modelPackages);

		return model;

	}

	private static void mapSysMLBlockRelationships() throws MDModelLibException, URISyntaxException {
		for (Class mdSysmlBlock : mdSysmlBlocks) {
			SysMLBlock sysmlBlock = qNameOslcSysmlBlockMap.get(magicDrawFileName + "/blocks/"
					+ mdSysmlBlock.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"));

			// SysML Block generalization
			Collection<Classifier> inheritedClassifiers = mdSysmlBlock.getGeneral();
			Link[] inheritedBlocksLinkArray = new Link[inheritedClassifiers.size()];

			int inheritedBlocksLinkArrayIndex = 0;
			LOG.info(" " + sysmlBlock.getName());
			LOG.info("\tinheritedBlocks: " + inheritedBlocksLinkArray.length);
			for (Classifier inheritedclassifier : inheritedClassifiers) {
				String qNameInheritedclassifier = inheritedclassifier.getQualifiedName();
				SysMLBlock inheritedBlock = qNameOslcSysmlBlockMap.get(magicDrawFileName + "/blocks/"
						+ qNameInheritedclassifier.replaceAll("\\n", "-").replaceAll(" ", "_"));

				URI inheritedBlockURI = new URI(descriptor.resource("blocks", projectId
                        + qNameInheritedclassifier.replaceAll("\\n", "-").replaceAll(" ", "_")));
                Link inheritedBlockLink = new Link(inheritedBlockURI);
                inheritedBlocksLinkArray[inheritedBlocksLinkArrayIndex] = inheritedBlockLink;
                inheritedBlocksLinkArrayIndex++;
			}

			if (inheritedBlocksLinkArray.length > 0) {
				sysmlBlock.setInheritedBlocks(inheritedBlocksLinkArray);
				LOG.info(" " + sysmlBlock.getName());
				LOG.info("\tinheritedBlocks: ");
				for (Link link : inheritedBlocksLinkArray) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// SysML Block nesting
			Collection<Classifier> nestedClassifiers = mdSysmlBlock.getNestedClassifier();
			Link[] nestedBlocksLinkArray = new Link[nestedClassifiers.size()];
			int nestedBlocksLinkArrayIndex = 0;
			LOG.info(" " + sysmlBlock.getName());
			LOG.info("\tnestedBlocks: " + nestedBlocksLinkArray.length);
			for (Classifier nestedClassifier : nestedClassifiers) {
				String qNameNestedclassifier = nestedClassifier.getQualifiedName();
				SysMLBlock nestedBlock = qNameOslcSysmlBlockMap.get(magicDrawFileName + "/blocks/"
						+ qNameNestedclassifier.replaceAll("\\n", "-").replaceAll(" ", "_"));
				URI nestedBlockURI = new URI(
                        descriptor.resource("blocks", projectId + nestedBlock.getName()));
                Link nestedBlockLink = new Link(nestedBlockURI);
                nestedBlocksLinkArray[nestedBlocksLinkArrayIndex] = nestedBlockLink;
                nestedBlocksLinkArrayIndex++;
			}
			if (nestedBlocksLinkArray.length > 0) {
				sysmlBlock.setNestedBlocks(nestedBlocksLinkArray);
				LOG.info(" " + sysmlBlock.getName());
				LOG.info("\tnestedBlocks: ");
				for (Link link : nestedBlocksLinkArray) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// satisfies relationships (Block satisfies Requirement)
			Link[] satisfiesLinks = getDirectedLinksOfSysMLElement(true, mdSysmlBlock, "Satisfy");
			if (satisfiesLinks != null) {
				sysmlBlock.setSatisfies(satisfiesLinks);
				LOG.info(" " + sysmlBlock.getName());
				LOG.info("\tSatisfies: ");
				for (Link link : satisfiesLinks) {
					LOG.info("\t\t " + link.getValue());
				}
			}

		}

	}

	private static void mapSysMLRequirementRelationships() throws MDModelLibException, URISyntaxException {
		for (Class mdSysMLRequirement : mdSysmlRequirements) {

			// String sourceReqQualifiedName = mdSysMLRequirement
			// .getQualifiedName().replaceAll("\\n", "-")
			// .replaceAll(" ", "_");
			// SysMLRequirement sysMLRequirement = idOslcSysmlRequirementMap
			// .get(sourceReqQualifiedName);

			String id = (String) StereotypesHelper.getStereotypePropertyFirst(mdSysMLRequirement,
					StereotypesHelper.getFirstVisibleStereotype(mdSysMLRequirement), "Id");
			SysMLRequirement sysMLRequirement = idOslcSysmlRequirementMap.get(projectId + "/requirements/" + id);

			// subRequirements
			Collection<Class> subRequirements = new ArrayList<Class>();
			for (Classifier nestedClassifier : mdSysMLRequirement.getNestedClassifier()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedClassifier, "Requirement")) {
					subRequirements.add((Class) nestedClassifier);
				}
			}
			if (subRequirements.size() > 0) {
				Link[] subRequirementsLinksArray = new Link[subRequirements.size()];
				int linksArrayIndex = 0;
				for (NamedElement namedElement : subRequirements) {
                    URI linkedElementURI = null;

                    if (namedElement instanceof Class) {
                        Class mdSysMLClass = (Class) namedElement;
                        String linkedRequirementID = (String) StereotypesHelper.getStereotypePropertyFirst(
                                mdSysMLClass, StereotypesHelper.getFirstVisibleStereotype(mdSysMLRequirement),
                                "Id");
                        if (linkedRequirementID != null) {
                            linkedElementURI = new URI(descriptor.resource("requirements", projectId
                                    + linkedRequirementID));
                            Link link = new Link(linkedElementURI);
                            subRequirementsLinksArray[linksArrayIndex] = link;
                            linksArrayIndex++;
                        }
                    }

				}
				sysMLRequirement.setSubRequirements(subRequirementsLinksArray);
			}

			// master relationship
			URI masterURI = getDirectedLinkSysMLElement(true, mdSysMLRequirement, "Copy");
			if (masterURI != null) {
				sysMLRequirement.setMaster(masterURI);
			}

			// derivedFrom relationships (Requirement derivedFrom Requirements)
			Link[] derivedFromLinks = getDirectedLinksOfSysMLElement(true, mdSysMLRequirement, "DeriveReqt");
			if (derivedFromLinks != null) {
				sysMLRequirement.setDerivedFromElements(derivedFromLinks);
				LOG.info(" " + sysMLRequirement.getIdentifier());
				LOG.info("\tDerivedFrom: ");
				for (Link link : derivedFromLinks) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// derived relationships (Requirement has derived Requirements)
			Link[] derivedLinks = getDirectedLinksOfSysMLElement(false, mdSysMLRequirement, "DeriveReqt");
			if (derivedLinks != null) {
				sysMLRequirement.setDerivedElements(derivedLinks);
				LOG.info(" " + sysMLRequirement.getIdentifier());
				LOG.info("\tDerived: ");
				for (Link link : derivedLinks) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// satisfiedBy relationships (Requirement satisfied By X)
			Link[] satisfiedByLinks = getDirectedLinksOfSysMLElement(false, mdSysMLRequirement, "Satisfy");
			if (satisfiedByLinks != null) {
				sysMLRequirement.setSatisfiedBy(satisfiedByLinks);
				LOG.info(" " + sysMLRequirement.getIdentifier());
				LOG.info("\tSatisfiedBy: ");
				for (Link link : satisfiedByLinks) {
					LOG.info("\t\t " + link.getValue());
				}
			}

			// refinedBy relationships (Requirement refined By X)
			Link[] refinedByLinks = getDirectedLinksOfSysMLElement(false, mdSysMLRequirement, "Refine");
			if (refinedByLinks != null) {
				sysMLRequirement.setElaboratedBy(refinedByLinks);
				LOG.info(" " + sysMLRequirement.getIdentifier());
				LOG.info("\tRefinedBy: ");
				for (Link link : refinedByLinks) {
					LOG.info("\t\t " + link.getValue());
				}
			}

		}
	}

	private static void mapSysMLBlocks() throws MDModelLibException, URISyntaxException {

		for (Class mdSysMLBlock : mdSysmlBlocks) {
			String qName = mdSysMLBlock.getQualifiedName();
			// qNameMdSysmlBlockMap.put(
			// qName.replaceAll("\\n", "-").replaceAll(" ", "_"),
			// mdSysMLBlock);
			SysMLBlock sysMLBlock = new SysMLBlock();
            qNameOslcSysmlBlockMap.put(
                    magicDrawFileName + "/blocks/" + qName.replaceAll("\\n", "-").replaceAll(" ", "_"), sysMLBlock);

            // SysML Block Name attribute
            String name = mdSysMLBlock.getName();
            if (name != null) {
                sysMLBlock.setName(name);
                LOG.info("SysML Block with Name: " + sysMLBlock.getName());
                sysMLBlock.setAbout(URI.create(descriptor.resource("blocks", projectId
                        + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

            // SysML Block Parts
            mapSysMLPartProperties(mdSysMLBlock, sysMLBlock);

            // SysML Block References
            mapSysMLReferenceProperties(mdSysMLBlock, sysMLBlock);

            // SysML Block Value Properties
            mapSysMLValueProperties(mdSysMLBlock, sysMLBlock);

            // SysML Block Flow Properties
            mapSysMLFlowProperties(mdSysMLBlock, sysMLBlock);

            // SysML Block Connectors
            mapSysMLConnectors(mdSysMLBlock, sysMLBlock);

            // SysML Block Ports
            mapSysMLPorts(mdSysMLBlock, sysMLBlock);

		}
	}

	private static void mapSysMLValueProperties(Class mdSysMLBlock, SysMLBlock sysMLBlock) throws MDModelLibException, URISyntaxException {
		Link[] valuePropertiesLinksArray = getLinkedStereotypedSysMLElements(mdSysMLBlock.getOwnedAttribute(),
				"ValueProperty", descriptor.resource("valueproperties", projectId));

		if (valuePropertiesLinksArray != null) {
			sysMLBlock.setValueProperties(valuePropertiesLinksArray);

			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: " + valuePropertiesLinksArray.length);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: ");
			for (Link link : valuePropertiesLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Property property : mdSysMLBlock.getOwnedAttribute()) {

			if (property.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = property.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().contains("ValueProperty")) {
					SysMLValueProperty sysmlValueProperty = new SysMLValueProperty();
                    qNameOslcSysmlValuePropertyMap.put(
                            magicDrawFileName + "/valueproperties/" + getQualifiedNameOrID(property),
                            sysmlValueProperty);
                    mdSysmlValueProperties.add(property);

                    // valueProperty name
                    sysmlValueProperty.setName(property.getName());
                    sysmlValueProperty.setAbout(URI.create(descriptor.resource("valueproperties", projectId + getQualifiedNameOrID(property))));

                    // valueProperty type
                    if (property.getType() != null) {
                        if (MDSysMLModelHandler.isSysMLElement(property.getType(), "Block")) {
                            sysmlValueProperty.setType(new URI(descriptor.resource("blocks", projectId
                                    + getQualifiedNameOrID(property.getType()))));
                        } else if (MDSysMLModelHandler.isSysMLElement(property.getType(), "ValueType")) {
                            sysmlValueProperty.setType(new URI(descriptor.resource("valuetypes", projectId + getQualifiedNameOrID(property.getType()))));
                        }
                    }

                    // referenceProperty multiplicity
                    String lowerMultiplicity = Integer.toString(property.getLower());
                    String upperMultiplicity = Integer.toString(property.getUpper());
                    sysmlValueProperty.setLower(lowerMultiplicity);
                    sysmlValueProperty.setUpper(upperMultiplicity);

                    // defaultValue
                    ValueSpecification valueSpecification = property.getDefaultValue();
                    if (valueSpecification instanceof LiteralString) {
                        LiteralString literalString = (LiteralString) valueSpecification;
                        sysmlValueProperty.setDefaultValue(literalString.getValue());
                    }

				}
			}
		}

	}

	private static void mapSysMLPorts(Class mdSysMLBlock, SysMLBlock sysMLBlock) throws MDModelLibException, URISyntaxException {
		ArrayList<Port> proxyPortsList = new ArrayList<Port>();
		ArrayList<Port> fullPortsList = new ArrayList<Port>();
		ArrayList<Port> portsList = new ArrayList<Port>();

		for (Port port : mdSysMLBlock.getOwnedPort()) {
			if (MDSysMLModelHandler.isSysMLElement(port, "ProxyPort")) {
				proxyPortsList.add(port);
			} else if (MDSysMLModelHandler.isSysMLElement(port, "FullPort")) {
				fullPortsList.add(port);
			}
			// else if (MDSysMLModelHandler.isSysMLElement(port, "FlowPort")){
			// flowPortsList.add(port);
			// }
			else if (port instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port) {
				// standard port
				portsList.add(port);
			}
		}

		Link[] proxyPortsLinksArray;
		Link[] fullPortsLinksArray;
		Link[] portsLinksArray;

		if (proxyPortsList.size() > 0) {
			proxyPortsLinksArray = new Link[proxyPortsList.size()];
			String proxyPortBaseURI = descriptor.resource("proxyports", projectId);
			int proxyPortsLinksArrayIndex = 0;
			for (Port port : proxyPortsList) {
				URI linkedElementURI = new URI(proxyPortBaseURI + getQualifiedNameOrID(port));
                Link link = new Link(linkedElementURI);
                proxyPortsLinksArray[proxyPortsLinksArrayIndex] = link;
                proxyPortsLinksArrayIndex++;

                SysMLProxyPort sysMLProxyPort = new SysMLProxyPort();
                qNameOslcSysmlProxyPortMap.put(magicDrawFileName + "/proxyports/" + getQualifiedNameOrID(port),
                        sysMLProxyPort);

                // port name
                sysMLProxyPort.setName(port.getName());

                // port URI
                String qName = port.getQualifiedName();
                sysMLProxyPort.setAbout(URI.create(
                        descriptor.resource("proxyports", projectId + getQualifiedNameOrID(port))));

                // port type
                if (port.getType() != null) {
                    if (MDSysMLModelHandler.isSysMLElement(port.getType(), "Block")) {
                        sysMLProxyPort.setType(new URI(descriptor.resource("blocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    } else if (MDSysMLModelHandler.isSysMLElement(port.getType(), "InterfaceBlock")) {
                        sysMLProxyPort.setType(new URI(descriptor.resource("interfaceblocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    }
                }

                // isService
                sysMLProxyPort.setIsService(port.isService());

                // isBehavior
                sysMLProxyPort.setIsBehavior(port.isBehavior());

                // isConjugated
                sysMLProxyPort.setIsConjugated(port.isConjugated());

                // port multiplicity
                String lowerMultiplicity = Integer.toString(port.getLower());
                String upperMultiplicity = Integer.toString(port.getUpper());
                sysMLProxyPort.setLower(lowerMultiplicity);
                sysMLProxyPort.setUpper(upperMultiplicity);

			}
			sysMLBlock.setProxyPorts(proxyPortsLinksArray);
		}

		if (fullPortsList.size() > 0) {
			fullPortsLinksArray = new Link[fullPortsList.size()];
			String fullPortBaseURI = descriptor.resource("fullports", projectId);
			int fullPortsLinksArrayIndex = 0;
			for (Port port : fullPortsList) {
				URI linkedElementURI = new URI(fullPortBaseURI + getQualifiedNameOrID(port));
                Link link = new Link(linkedElementURI);
                fullPortsLinksArray[fullPortsLinksArrayIndex] = link;
                fullPortsLinksArrayIndex++;

                SysMLFullPort sysMLFullPort = new SysMLFullPort();
                qNameOslcSysmlFullPortMap.put(magicDrawFileName + "/fullports/" + getQualifiedNameOrID(port),
                        sysMLFullPort);

                // port name
                sysMLFullPort.setName(port.getName());

                // port URI
                sysMLFullPort.setAbout(URI.create(
                        descriptor.resource("fullports", projectId + getQualifiedNameOrID(port))));

                // port type
                if (port.getType() != null) {
                    if (MDSysMLModelHandler.isSysMLElement(port.getType(), "Block")) {
                        sysMLFullPort.setType(new URI(descriptor.resource("blocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    } else if (MDSysMLModelHandler.isSysMLElement(port.getType(), "InterfaceBlock")) {
                        sysMLFullPort.setType(new URI(descriptor.resource("interfaceblocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    }
                }

                // isService
                sysMLFullPort.setIsService(port.isService());

                // isBehavior
                sysMLFullPort.setIsBehavior(port.isBehavior());

                // isConjugated
                sysMLFullPort.setIsConjugated(port.isConjugated());

                // port multiplicity
                String lowerMultiplicity = Integer.toString(port.getLower());
                String upperMultiplicity = Integer.toString(port.getUpper());
                sysMLFullPort.setLower(lowerMultiplicity);
                sysMLFullPort.setUpper(upperMultiplicity);

			}
			sysMLBlock.setFullPorts(fullPortsLinksArray);
		}

		if (portsList.size() > 0) {
			portsLinksArray = new Link[portsList.size()];
			String fullPortBaseURI = descriptor.resource("ports", projectId);
			int portsLinksArrayIndex = 0;
			for (Port port : portsList) {
				URI linkedElementURI = new URI(fullPortBaseURI + getQualifiedNameOrID(port));
                Link link = new Link(linkedElementURI);
                portsLinksArray[portsLinksArrayIndex] = link;
                portsLinksArrayIndex++;

                SysMLPort sysMLPort = new SysMLPort();
                qNameOslcSysmlPortMap.put(magicDrawFileName + "/ports/" + getQualifiedNameOrID(port), sysMLPort);
                mdSysmlPorts.add(port);

                // port name
                sysMLPort.setName(port.getName());

                // port URI
                String qName = port.getQualifiedName();
                sysMLPort.setAbout(URI
                        .create(descriptor.resource("ports", projectId + getQualifiedNameOrID(port))));

                // port type
                if (port.getType() != null) {
                    if (MDSysMLModelHandler.isSysMLElement(port.getType(), "Block")) {
                        sysMLPort.setType(new URI(descriptor.resource("blocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    } else if (MDSysMLModelHandler.isSysMLElement(port.getType(), "InterfaceBlock")) {
                        sysMLPort.setType(new URI(descriptor.resource("interfaceblocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    }
                }

                // port owner
                if (port.getOwner() != null) {
                    NamedElement portOwnerNamedElement = (NamedElement) port.getOwner();
                    sysMLPort.setOwner(
                            new URI(descriptor.resource("blocks", projectId + portOwnerNamedElement
                                    .getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                }

                // isService
                sysMLPort.setIsService(port.isService());

                // isBehavior
                sysMLPort.setIsBehavior(port.isBehavior());

                // isConjugated
                sysMLPort.setIsConjugated(port.isConjugated());

                // port multiplicity
                String lowerMultiplicity = Integer.toString(port.getLower());
                String upperMultiplicity = Integer.toString(port.getUpper());
                sysMLPort.setLower(lowerMultiplicity);
                sysMLPort.setUpper(upperMultiplicity);


			}
			sysMLBlock.setPorts(portsLinksArray);
		}

	}

	private static void mapSysMLProxyPorts(Class mdSysMLBlock, SysMLInterfaceBlock sysMLInterfaceBlock)
			throws MDModelLibException, URISyntaxException {
		ArrayList<Port> proxyPortsList = new ArrayList<Port>();

		for (Port port : mdSysMLBlock.getOwnedPort()) {
			if (MDSysMLModelHandler.isSysMLElement(port, "ProxyPort")) {
				proxyPortsList.add(port);
			} else {
				// flow port
			}
		}

		Link[] proxyPortsLinksArray;

		if (proxyPortsList.size() > 0) {
			proxyPortsLinksArray = new Link[proxyPortsList.size()];
			String proxyPortBaseURI = descriptor.resource("proxyports", projectId);
			int proxyPortsLinksArrayIndex = 0;
			for (Port port : proxyPortsList) {
				URI linkedElementURI = new URI(proxyPortBaseURI + getQualifiedNameOrID(port));
                Link link = new Link(linkedElementURI);
                proxyPortsLinksArray[proxyPortsLinksArrayIndex] = link;
                proxyPortsLinksArrayIndex++;

                SysMLProxyPort sysMLProxyPort = new SysMLProxyPort();
                qNameOslcSysmlProxyPortMap.put(magicDrawFileName + "/proxyports/" + getQualifiedNameOrID(port),
                        sysMLProxyPort);

                // port name
                sysMLProxyPort.setName(port.getName());

                // port URI
                sysMLProxyPort.setAbout(URI.create(
                        descriptor.resource("proxyports", projectId + getQualifiedNameOrID(port))));

                // port type
                if (port.getType() != null) {
                    if (MDSysMLModelHandler.isSysMLElement(port.getType(), "Block")) {
                        sysMLProxyPort.setType(new URI(descriptor.resource("blocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    } else if (MDSysMLModelHandler.isSysMLElement(port.getType(), "InterfaceBlock")) {
                        sysMLProxyPort.setType(new URI(descriptor.resource("interfaceblocks", projectId
                                + port.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                    }
                }

                // isService
                sysMLProxyPort.setIsService(port.isService());

                // isBehavior
                sysMLProxyPort.setIsBehavior(port.isBehavior());

                // isConjugated
                sysMLProxyPort.setIsConjugated(port.isConjugated());

			}
			sysMLInterfaceBlock.setProxyPorts(proxyPortsLinksArray);
		}
	}

	private static void mapSysMLConnectors(Class mdSysMLBlock, SysMLBlock sysMLBlock) throws MDModelLibException, URISyntaxException {
		Link[] connectorsLinksArray = getLinkedSysMLElements(mdSysMLBlock.getOwnedConnector(),
				descriptor.resource("connectors", projectId));

		if (connectorsLinksArray != null) {
			sysMLBlock.setConnectors(connectorsLinksArray);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock connectors: " + connectorsLinksArray.length);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock connectors: ");
			for (Link link : connectorsLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Connector connector : mdSysMLBlock.getOwnedConnector()) {
			SysMLConnector sysMLConnector = new SysMLConnector();

            qNameOslcSysmlConnectorMap.put(magicDrawFileName + "/connectors/" + getQualifiedNameOrID(connector),
                    sysMLConnector);
            mdSysmlConnectors.add(connector);
            if (!connector.getName().equals("")) {
                // connector name
                sysMLConnector.setName(connector.getName());
            }
            sysMLConnector.setAbout(URI.create(
                    descriptor.resource("connectors", projectId + getQualifiedNameOrID(connector))));

            // connector ends
            Link[] connectorsEndsLinksArray = getLinkedSysMLElements(connector.getEnd(),
                    descriptor.resource("connectorends", projectId));
            sysMLConnector.setEnds(connectorsEndsLinksArray);

            // connector type
            if (connector.getType() != null) {

                // connector type is an association block
                if (MDSysMLModelHandler.isSysMLElement(connector.getType(), "Block")) {
                    sysMLConnector.setType(new URI(descriptor.resource("associationblocks", projectId
                            + connector.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                } else if (connector
                        .getType() instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association) {
                    sysMLConnector.setType(new URI(descriptor.resource("associations", projectId
                            + connector.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                }
            }

            // connector owner
            if (connector.getOwner() != null) {
                if (connector.getOwner() instanceof NamedElement) {
                    NamedElement namedElement = (NamedElement) connector.getOwner();
                    sysMLConnector.setOwner(new URI(descriptor.resource("blocks", projectId
                            + namedElement.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
                }
            }

            // map connector ends
            mapSysMLConnectorEnds(connector, sysMLConnector);

		}
	}

	private static void mapSysMLConnectorEnds(Connector connector, SysMLConnector sysMLConnector)
			throws MDModelLibException, URISyntaxException {

		for (ConnectorEnd connectorEnd : connector.getEnd()) {

            SysMLConnectorEnd sysMLConnectorEnd = new SysMLConnectorEnd();
            sysMLConnectorEnd.setAbout(
                    URI.create(descriptor.resource("connectorends", projectId + connectorEnd.getID())));
            qNameOslcSysmlConnectorEndMap.put(magicDrawFileName + "/connectorends/" + connectorEnd.getID(),
                    sysMLConnectorEnd);

            ConnectableElement role = connectorEnd.getRole();

            Property definingEnd = connectorEnd.getDefiningEnd();
            Property partWithPort = connectorEnd.getPartWithPort();

            // role
            if (MDSysMLModelHandler.isSysMLElement(role, "PartProperty")) {
                sysMLConnectorEnd.setRole(new URI(descriptor.resource("partproperties", projectId
                        + connectorEnd.getRole().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            } else if (MDSysMLModelHandler.isSysMLElement(role, "ProxyPort")) {
                sysMLConnectorEnd.setRole(new URI(descriptor.resource("proxyports", projectId
                        + connectorEnd.getRole().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            } else if (MDSysMLModelHandler.isSysMLElement(role, "FullPort")) {
                sysMLConnectorEnd.setRole(new URI(descriptor.resource("fullports", projectId
                        + connectorEnd.getRole().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            } else if (role instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port) {
                sysMLConnectorEnd.setRole(new URI(descriptor.resource("ports", projectId
                        + connectorEnd.getRole().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

            // definingEnd
            if (definingEnd != null) {
                sysMLConnectorEnd.setDefiningEnd(
                        new URI(descriptor.resource("partproperties", projectId + connectorEnd
                                .getDefiningEnd().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

            // partWithPort
            if (partWithPort != null) {
                sysMLConnectorEnd.setPartWithPort(
                        new URI(descriptor.resource("partproperties", projectId + connectorEnd
                                .getPartWithPort().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));
            }

		}

	}

	private static void mapSysMLReferenceProperties(Class mdSysmlBlock, SysMLBlock sysMLBlock)
			throws MDModelLibException, URISyntaxException {

		Link[] blockReferencesLinksArray = getLinkedStereotypedSysMLElements(mdSysmlBlock.getOwnedAttribute(),
				"ReferenceProperty", descriptor.resource("referenceproperties", projectId));

		if (blockReferencesLinksArray != null) {
			sysMLBlock.setReferenceProperties(blockReferencesLinksArray);

			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: " + blockReferencesLinksArray.length);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: ");
			for (Link link : blockReferencesLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Property property : mdSysmlBlock.getOwnedAttribute()) {

			if (property.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = property.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().contains("ReferenceProperty")) {
					SysMLReferenceProperty sysmlReferenceProperty = new SysMLReferenceProperty();
                    qNameOslcSysmlReferencePropertyMap.put(
                            magicDrawFileName + "/referenceproperties/"
                                    + property.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"),
                            sysmlReferenceProperty);

                    // referenceProperty name
                    sysmlReferenceProperty.setName(property.getName());

                    String qName = property.getQualifiedName();
                    sysmlReferenceProperty.setAbout(URI.create(descriptor.resource("referenceproperties", projectId + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // referenceProperty type
                    sysmlReferenceProperty.setType(new URI(descriptor.resource("blocks", projectId
                            + property.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // referenceProperty multiplicity
                    String lowerMultiplicity = Integer.toString(property.getLower());
                    String upperMultiplicity = Integer.toString(property.getUpper());
                    sysmlReferenceProperty.setLower(lowerMultiplicity);
                    sysmlReferenceProperty.setUpper(upperMultiplicity);

                    // referenceProperty association attribute
                    if (property.getAssociation() != null) {
                        Association mdSysMAssociation = property.getAssociation();
                        URI linkedElementURI = null;
                        String baseURI = descriptor.resource("unknown", projectId);
                        // check if property has an associationBlock as
                        // association

                        if (MDSysMLModelHandler.isSysMLElement(property.getAssociation(), "Block")) {
                            // if (!mdSysmlAssociationBlocks
                            // .contains((com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass)
                            // property.getAssociation())) {
                            // mdSysmlAssociationBlocks.add((com.nomagic.uml2.ext.magicdraw.classes.mdassociationclasses.AssociationClass)
                            // property
                            // .getAssociation());
                            // }
                            baseURI = descriptor.resource("associationblocks", projectId);
                        } else if (property
                                .getAssociation() instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Association) {
                            baseURI = descriptor.resource("associations", projectId);
                        }
                        linkedElementURI = new URI(baseURI
                                + mdSysMAssociation.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"));
                        sysmlReferenceProperty.setAssociation(linkedElementURI);
                    }

				}
			}
		}

	}

	private static void mapSysMLFlowProperties(Class mdSysmlBlock, SysMLBlock sysMLBlock) throws URISyntaxException {

		Link[] flowPropertiesLinksArray = getLinkedStereotypedSysMLElements(mdSysmlBlock.getOwnedAttribute(),
				"FlowProperty", descriptor.resource("flowproperties", projectId));

		if (flowPropertiesLinksArray != null) {
			sysMLBlock.setFlowProperties(flowPropertiesLinksArray);

			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: " + flowPropertiesLinksArray.length);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblock reference properties: ");
			for (Link link : flowPropertiesLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Property property : mdSysmlBlock.getOwnedAttribute()) {

			if (property.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = property.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().contains("FlowProperty")) {
					SysMLFlowProperty sysmlFlowProperty = new SysMLFlowProperty();
                    qNameOslcSysmlFlowPropertyMap.put(
                            magicDrawFileName + "/flowproperties/"
                                    + property.getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"),
                            sysmlFlowProperty);

                    // referenceProperty name
                    sysmlFlowProperty.setName(property.getName());

                    String qName = property.getQualifiedName();
                    sysmlFlowProperty.setAbout(URI.create(descriptor.resource("flowproperties", projectId + qName.replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // referenceProperty type
                    sysmlFlowProperty.setType(new URI(descriptor.resource("blocks", projectId
                            + property.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // referenceProperty multiplicity
                    String lowerMultiplicity = Integer.toString(property.getLower());
                    String upperMultiplicity = Integer.toString(property.getUpper());
                    sysmlFlowProperty.setLower(lowerMultiplicity);
                    sysmlFlowProperty.setUpper(upperMultiplicity);

                    // direction
                    Object directionObject = StereotypesHelper.getStereotypePropertyFirst(property,
                            (Stereotype) property.getAppliedStereotypeInstance().getClassifier().get(0),
                            "direction");
                    if (directionObject instanceof EnumerationLiteral) {
                        EnumerationLiteral enumLit = (EnumerationLiteral) directionObject;
                        String enumLitName = enumLit.getName();
                        if (enumLitName.equals("in")) {
                            // sysmlFlowProperty
                            // .setDirection(SysMLFlowDirection.IN);
                            sysmlFlowProperty.setDirection("in");
                        } else if (enumLitName.equals("out")) {
                            // sysmlFlowProperty
                            // .setDirection(SysMLFlowDirection.OUT);
                            sysmlFlowProperty.setDirection("out");
                        }

                    }

				}
			}
		}

	}

	private static Link[] getLinkedSysMLElements(Collection<? extends Element> elementCollection,
			String linkedElementBaseURI) throws URISyntaxException {

		// counting the number of links
		int linksCount = elementCollection.size();

		// creating linksArray
		Link[] linksArray = null;
		if (linksCount > 0) {
			linksArray = new Link[linksCount];
		}

		// populating linksArray
		int linksArrayIndex = 0;
		for (Element element : elementCollection) {
            URI linkedElementURI = null;
            linkedElementURI = new URI(linkedElementBaseURI + getQualifiedNameOrID(element));
            Link link = new Link(linkedElementURI);
            linksArray[linksArrayIndex] = link;
            linksArrayIndex++;
		}
		return linksArray;
	}

	private static Link[] getLinkedStereotypedSysMLElements(Collection<? extends NamedElement> namedElementCollection,
			String stereotypeName, String linkedElementBaseURI) throws URISyntaxException {

		// counting the number of links
		int linksCount = 0;
		for (NamedElement namedElement : namedElementCollection) {
			if (namedElement.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = namedElement.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().equals(stereotypeName)) {
					linksCount++;
				}
			}
		}

		// creating linksArray
		Link[] linksArray = null;
		if (linksCount > 0) {
			linksArray = new Link[linksCount];
		}

		// populating linksArray
		int linksArrayIndex = 0;
		for (NamedElement namedElement : namedElementCollection) {
			if (namedElement.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = namedElement.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().equals(stereotypeName)) {
                    URI linkedElementURI = null;
                    if (stereotypeName.equals("Requirement") & namedElement instanceof Class) {
                        String linkedElementID = (String) StereotypesHelper.getStereotypePropertyFirst(namedElement,
                                StereotypesHelper.getFirstVisibleStereotype(namedElement), "Id");
                        linkedElementURI = new URI(linkedElementBaseURI + linkedElementID);
                    } else {
                        linkedElementURI = new URI(linkedElementBaseURI + getQualifiedNameOrID(namedElement));
                    }

                    Link link = new Link(linkedElementURI);
                    linksArray[linksArrayIndex] = link;
                    linksArrayIndex++;
				}
			}
		}
		return linksArray;

	}

	private static URI getDirectedLinkSysMLElement(boolean isElementSource, Element element, String relationshipType)
			throws MDModelLibException, URISyntaxException {

		Collection<DirectedRelationship> directedRelationships;
		if (isElementSource) {
			directedRelationships = element.get_directedRelationshipOfSource();
		} else {
			directedRelationships = element.get_directedRelationshipOfTarget();
		}

		// getting relationships of that specific type
		Collection<DirectedRelationship> relationshipsOfType = new ArrayList<DirectedRelationship>();
		for (DirectedRelationship directedRelationship : directedRelationships) {
            String directedRelationshipType = directedRelationship.getAppliedStereotypeInstance().getClassifier()
                    .get(0).getName();
            if (directedRelationshipType.equals(relationshipType)) {
                relationshipsOfType.add(directedRelationship);
            }
		}

		URI linkedElementURI = null;
		for (DirectedRelationship directedRelationship : relationshipsOfType) {
			Collection<Element> linkedElements;
			if (isElementSource) {
				linkedElements = directedRelationship.getTarget();
			} else {
				linkedElements = directedRelationship.getSource();
			}
			for (Element linkedElement : linkedElements) {
				if (linkedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
					NamedElement linkedNamedElement = (NamedElement) linkedElement;
					String linkedElementBaseURI = descriptor.resource("unknown", projectId);
					String linkedElementID = null;
					boolean isLinkedElementRequirement = false;
					if (MDSysMLModelHandler.isSysMLElement(linkedNamedElement, "Block")) {
						linkedElementBaseURI = descriptor.resource("blocks", projectId);
					} else if (MDSysMLModelHandler.isSysMLElement(linkedNamedElement, "Requirement")) {
						linkedElementID = (String) StereotypesHelper.getStereotypePropertyFirst(linkedNamedElement,
								StereotypesHelper.getFirstVisibleStereotype(linkedNamedElement), "Id");
						isLinkedElementRequirement = true;
						linkedElementBaseURI = descriptor.resource("requirements", projectId);
					} else if (linkedNamedElement instanceof com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase) {
						linkedElementBaseURI = descriptor.resource("usecases", projectId);
					}
					String linkedElementQName = null;
					if (!isLinkedElementRequirement) {
						linkedElementQName = linkedNamedElement.getQualifiedName().replaceAll("\\n", "-")
								.replaceAll(" ", "_");
					} else {
						linkedElementQName = linkedElementID;
					}

                    linkedElementURI = new URI(linkedElementBaseURI + linkedElementQName);
				}
			}
		}
		return linkedElementURI;
	}

	private static Link[] getDirectedLinksOfSysMLElement(boolean isElementSource, Element element,
			String relationshipType) throws MDModelLibException, URISyntaxException {

		Collection<DirectedRelationship> directedRelationships;
		if (isElementSource) {
			directedRelationships = element.get_directedRelationshipOfSource();
		} else {
			directedRelationships = element.get_directedRelationshipOfTarget();
		}

		// counting the number of relationships of that specific type
		Collection<DirectedRelationship> relationshipsOfType = new ArrayList<DirectedRelationship>();
		for (DirectedRelationship directedRelationship : directedRelationships) {
                try {
                    String directedRelationshipType = directedRelationship.getAppliedStereotypeInstance().getClassifier()
                            .get(0).getName();
                    if (directedRelationshipType.equals(relationshipType)) {
                        relationshipsOfType.add(directedRelationship);
                    }
                } catch(Exception ex) {
                
                }
            }

		// creating the links array
		Link[] linksArray = null;
		if (relationshipsOfType.size() > 0) {
			linksArray = new Link[relationshipsOfType.size()];
		}

		// populating the links array
		int linksArrayIndex = 0;
		for (DirectedRelationship directedRelationship : relationshipsOfType) {
			Collection<Element> linkedElements;
			if (isElementSource) {
				linkedElements = directedRelationship.getTarget();
			} else {
				linkedElements = directedRelationship.getSource();
			}
			for (Element linkedElement : linkedElements) {
				if (linkedElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement) {
					NamedElement linkedNamedElement = (NamedElement) linkedElement;
					String linkedElementBaseURI = descriptor.resource("unknown", projectId);
					String linkedElementID = null;
					boolean isLinkedElementRequirement = false;
					if (MDSysMLModelHandler.isSysMLElement(linkedNamedElement, "Block")) {
						linkedElementBaseURI = descriptor.resource("blocks", projectId);
					} else if (MDSysMLModelHandler.isSysMLElement(linkedNamedElement, "Requirement")) {
						linkedElementID = (String) StereotypesHelper.getStereotypePropertyFirst(linkedNamedElement,
								StereotypesHelper.getFirstVisibleStereotype(linkedNamedElement), "Id");
						isLinkedElementRequirement = true;
						linkedElementBaseURI = descriptor.resource("requirements", projectId);
					} else if (linkedNamedElement instanceof com.nomagic.uml2.ext.magicdraw.mdusecases.UseCase) {
						linkedElementBaseURI = descriptor.resource("usecases", projectId);
					}
					String linkedElementQName = null;
					if (!isLinkedElementRequirement) {
						linkedElementQName = linkedNamedElement.getQualifiedName().replaceAll("\\n", "-")
								.replaceAll(" ", "_");
					} else {
						linkedElementQName = linkedElementID;
					}
					URI linkedElementURI = new URI(linkedElementBaseURI + linkedElementQName);
                    Link link = new Link(linkedElementURI);
                    linksArray[linksArrayIndex] = link;
                    linksArrayIndex++;
				}
			}
		}
		return linksArray;
	}

	private static void mapSysMLPartProperties(Class mdSysmlBlock, SysMLBlock sysMLBlock) throws URISyntaxException {

		Link[] blockPartsLinksArray = getLinkedStereotypedSysMLElements(mdSysmlBlock.getOwnedAttribute(),
				"PartProperty", descriptor.resource("partproperties", projectId));

		if (blockPartsLinksArray != null) {
			sysMLBlock.setPartProperties(blockPartsLinksArray);

			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblockParts: " + blockPartsLinksArray.length);
			LOG.info(" " + sysMLBlock.getName());
			LOG.info("\tblockParts: ");
			for (Link link : blockPartsLinksArray) {
				LOG.info("\t\t " + link.getValue());
			}
		}

		for (Property property : mdSysmlBlock.getOwnedAttribute()) {

			if (property.getAppliedStereotypeInstance() != null) {
				InstanceSpecification stereotypeInstance = property.getAppliedStereotypeInstance();
				if (stereotypeInstance.getClassifier().get(0).getName().contains("PartProperty")) {
					SysMLPartProperty sysmlPartProperty = new SysMLPartProperty();
                    qNameOslcSysmlPartPropertyMap.put(
                            magicDrawFileName + "/partproperties/" + getQualifiedNameOrID(property),
                            sysmlPartProperty);
                    mdSysmlPartProperties.add(property);

                    // partProperty name
                    sysmlPartProperty.setName(property.getName());

                    String qName = property.getQualifiedName();
                    sysmlPartProperty.setAbout(URI.create(descriptor.resource("partproperties", projectId + getQualifiedNameOrID(property))));

                    // partProperty type
                    sysmlPartProperty.setType(new URI(descriptor.resource("blocks", projectId
                            + property.getType().getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // partProperty owner
                    NamedElement partPropertyOwnerNamedElement = (NamedElement) property.getOwner();
                    sysmlPartProperty.setOwner(new URI(
                            descriptor.resource("blocks", projectId + partPropertyOwnerNamedElement
                                    .getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_"))));

                    // partProperty multiplicity
                    String lowerMultiplicity = Integer.toString(property.getLower());
                    String upperMultiplicity = Integer.toString(property.getUpper());
                    sysmlPartProperty.setLower(lowerMultiplicity);
                    sysmlPartProperty.setUpper(upperMultiplicity);

				}
			}
		}

	}

	private static void mapSysMLRequirements() throws URISyntaxException {
		for (Class mdSysMLRequirement : mdSysmlRequirements) {
			String id = (String) StereotypesHelper.getStereotypePropertyFirst(mdSysMLRequirement,
					StereotypesHelper.getFirstVisibleStereotype(mdSysMLRequirement), "Id");
			if (id != null) {
				idMdSysmlRequirementMap.put(id, mdSysMLRequirement);
				// qNameMdSysmlRequirementMap.put(
				// mdSysMLRequirement.getQualifiedName()
				// .replaceAll("\\n", "-").replaceAll(" ", "_"),
				// mdSysMLRequirement);

				SysMLRequirement sysMLRequirement = new SysMLRequirement();

                // SysML Requirement id attribute
                sysMLRequirement.setIdentifier(id);
                idOslcSysmlRequirementMap.put(magicDrawFileName + "/requirements/" + id, sysMLRequirement);

                sysMLRequirement
                        .setAbout(URI.create(descriptor.resource("requirements", projectId + id)));

                // qNameOslcSysmlRequirementMap.put(mdSysMLRequirement
                // .getQualifiedName().replaceAll("\\n", "-")
                // .replaceAll(" ", "_"), sysMLRequirement);
                LOG.info("SysML Requirement with ID: " + sysMLRequirement.getIdentifier());

                // SysML Requirement Name attribute
                String name = mdSysMLRequirement.getName();
                if (name != null) {
                    sysMLRequirement.setTitle(name);
                    LOG.info("\tTitle: " + sysMLRequirement.getTitle());
                }

                // SysML Requirement Text attribute
                String text = (String) StereotypesHelper.getStereotypePropertyFirst(mdSysMLRequirement,
                        StereotypesHelper.getFirstVisibleStereotype(mdSysMLRequirement), "Text");
                if (text != null) {
                    sysMLRequirement.setDescription(text);
                    LOG.info("\tDescription: " + sysMLRequirement.getDescription());
                }

                // SysML Requirement hyperlink attribute
                Stereotype hyperlinkOwnerStereotype = StereotypesHelper
                        .getStereotype(Project.getProject(mdSysMLRequirement), "HyperlinkOwner");
                List textValues = StereotypesHelper.getStereotypePropertyValue(mdSysMLRequirement,
                        hyperlinkOwnerStereotype, "hyperlinkText");
                if (textValues.size() > 0) {
                    String hyperlinkText = (String) textValues.get(0);
                    sysMLRequirement.setHyperlink(hyperlinkText);
                }
                // for (int i = textValues.size() - 1; i >= 0; --i) {
                // String link = (String) textValues.get(i);
                // }

			}
		}

	}

	public static SysMLBlockDiagram getBlockDiagramByQualifiedName(String qualifiedName) throws URISyntaxException {
		SysMLBlockDiagram sysMLBlockDiagram = qNameOslcSysmlBlockDiagramMap.get(qualifiedName);
		return sysMLBlockDiagram;
	}

	public static List<SysMLBlockDiagram> getBlockDiagrams(String projectName) {
		List<SysMLBlockDiagram> sysMLBlocks = new ArrayList<SysMLBlockDiagram>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlBlockDiagramMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/blockdiagrams/")) {
				sysMLBlocks.add(qNameOslcSysmlBlockDiagramMap.get(qNameOslcSysmlElement));
			}
		}
		return sysMLBlocks;
	}

	public static SysMLInternalBlockDiagram getInternalBlockDiagramByQualifiedName(String qualifiedName)
			throws URISyntaxException {
		SysMLInternalBlockDiagram sysMLInternalBlockDiagram = qNameOslcSysmlInternalBlockDiagramMap.get(qualifiedName);
		return sysMLInternalBlockDiagram;
	}

	public static List<SysMLInternalBlockDiagram> getInternalBlockDiagrams(String projectName) {
		List<SysMLInternalBlockDiagram> sysMLBlocks = new ArrayList<SysMLInternalBlockDiagram>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlInternalBlockDiagramMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/internalblockdiagrams/")) {
				sysMLBlocks.add(qNameOslcSysmlInternalBlockDiagramMap.get(qNameOslcSysmlElement));
			}
		}
		return sysMLBlocks;
	}

	public static SysMLRequirement getRequirementByID(String qualifiedName) throws URISyntaxException {
		SysMLRequirement sysMLRequirement = idOslcSysmlRequirementMap.get(qualifiedName);

		return sysMLRequirement;
	}

	public static List<SysMLRequirement> getRequirements() {
		List<SysMLRequirement> sysMLRequirements = new ArrayList<SysMLRequirement>();
		for (String id : idOslcSysmlRequirementMap.keySet()) {
			sysMLRequirements.add(idOslcSysmlRequirementMap.get(id));
		}
		return sysMLRequirements;
	}

	public static edu.gatech.mbsec.adapter.magicdraw.resources.SysMLBlock getBlockByQualifiedName(String qualifiedName) {
		SysMLBlock sysMLBlock = qNameOslcSysmlBlockMap.get(qualifiedName);
		return sysMLBlock;
	}

	public static List<SysMLBlock> getBlocks(String projectName) {
		List<SysMLBlock> sysMLBlocks = new ArrayList<SysMLBlock>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlBlockMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/blocks/")) {
				sysMLBlocks.add(qNameOslcSysmlBlockMap.get(qNameOslcSysmlElement));
			}
		}
		return sysMLBlocks;
	}

	static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> getAllSysMLPackages(
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement packageableElement) {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package> sysmlPackages = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package>();

		if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement;
			if (!predefinedMagicDrawSysMLPackageNames.contains(package_.getName())) {
				if (!(packageableElement instanceof Model)) {
					sysmlPackages.add(package_);
				}
				// add nested packages
				for (PackageableElement nestedPackageableElement : package_.getPackagedElement()) {
					if (nestedPackageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
						com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) nestedPackageableElement;
						if (!predefinedMagicDrawSysMLPackageNames.contains(nestedPackage.getName())) {
							sysmlPackages.addAll(getAllSysMLPackages(nestedPackage));
						}
					}
				}
			}
		}

		return sysmlPackages;
	}

	static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> getAllSysMLBlocks(
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement packageableElement)
					throws MDModelLibException {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> sysmlBlocks = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>();
		if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement;
			for (PackageableElement nestedPackageableElement : package_.getPackagedElement()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedPackageableElement, "Block")
						| MDSysMLModelHandler.isSysMLElement(nestedPackageableElement, "System")) {
					Class magicDrawSysMLBlock = (Class) nestedPackageableElement;
					sysmlBlocks.add(magicDrawSysMLBlock);
					sysmlBlocks.addAll(getAllSysMLBlocks(magicDrawSysMLBlock));
				} else
					if (nestedPackageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
					com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) nestedPackageableElement;
					if (!predefinedMagicDrawSysMLPackageNames.contains(nestedPackage.getName())) {
						sysmlBlocks.addAll(getAllSysMLBlocks(nestedPackage));
					}
				}
			}
		} else if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class class_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) packageableElement;
			for (Classifier nestedClassifier : class_.getNestedClassifier()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedClassifier, "Block")
						| MDSysMLModelHandler.isSysMLElement(nestedClassifier, "System")) {
					sysmlBlocks.add((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) nestedClassifier);
				}
				sysmlBlocks.addAll(getAllSysMLBlocks(nestedClassifier));
			}
		}
		return sysmlBlocks;
	}

	static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> getAllSysMLInterfaceBlocks(
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement packageableElement)
					throws MDModelLibException {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> sysmlBlocks = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>();
		if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement;
			for (PackageableElement nestedPackageableElement : package_.getPackagedElement()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedPackageableElement, "InterfaceBlock")) {
					Class magicDrawSysMLBlock = (Class) nestedPackageableElement;
					sysmlBlocks.add(magicDrawSysMLBlock);
					sysmlBlocks.addAll(getAllSysMLInterfaceBlocks(magicDrawSysMLBlock));
				} else
					if (nestedPackageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
					com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) nestedPackageableElement;
					if (!predefinedMagicDrawSysMLPackageNames.contains(nestedPackage.getName())) {
						sysmlBlocks.addAll(getAllSysMLInterfaceBlocks(nestedPackage));
					}
				}
			}
		} else if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class class_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) packageableElement;
			for (Classifier nestedClassifier : class_.getNestedClassifier()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedClassifier, "InterfaceBlock")) {
					sysmlBlocks.add((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) nestedClassifier);
				}
				sysmlBlocks.addAll(getAllSysMLInterfaceBlocks(nestedClassifier));
			}
		}
		return sysmlBlocks;
	}

	static Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> getAllSysMLRequirements(
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.PackageableElement packageableElement)
					throws MDModelLibException {
		Collection<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class> sysmlRequirements = new ArrayList<com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class>();
		if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package package_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) packageableElement;
			for (PackageableElement nestedPackageableElement : package_.getPackagedElement()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedPackageableElement, "Requirement")) {
					com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class sysMLRequirement = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) nestedPackageableElement;
					sysmlRequirements.add(sysMLRequirement);
					sysmlRequirements.addAll(getAllSysMLRequirements(sysMLRequirement));
				} else
					if (nestedPackageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) {
					com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package nestedPackage = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package) nestedPackageableElement;
					if (!predefinedMagicDrawSysMLPackageNames.contains(nestedPackage.getName())) {
						sysmlRequirements.addAll(getAllSysMLRequirements(nestedPackage));
					}
				}
			}
		} else if (packageableElement instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) {
			com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class class_ = (com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) packageableElement;
			for (Classifier nestedClassifier : class_.getNestedClassifier()) {
				if (MDSysMLModelHandler.isSysMLElement(nestedClassifier, "Requirement")) {
					sysmlRequirements.add((com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class) nestedClassifier);
				}
				sysmlRequirements.addAll(getAllSysMLRequirements(nestedClassifier));
			}
		}
		return sysmlRequirements;
	}

	public static SysMLPartProperty getPartPropertyByQualifiedName(String propertyQualifiedName) {
		SysMLPartProperty sysMLPartProperty = qNameOslcSysmlPartPropertyMap.get(propertyQualifiedName);
		return sysMLPartProperty;
	}

	public static List<SysMLPartProperty> getPartProperties(String projectName) {
		List<SysMLPartProperty> elements = new ArrayList<SysMLPartProperty>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlPartPropertyMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/partproperties/")) {
				elements.add(qNameOslcSysmlPartPropertyMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLReferenceProperty getReferencePropertyByQualifiedName(String propertyQualifiedName) {
		SysMLReferenceProperty sysMLReferenceProperty = qNameOslcSysmlReferencePropertyMap.get(propertyQualifiedName);
		return sysMLReferenceProperty;
	}

	public static List<SysMLReferenceProperty> getReferenceProperties(String projectName) {
		List<SysMLReferenceProperty> elements = new ArrayList<SysMLReferenceProperty>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlReferencePropertyMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/referenceproperties/")) {
				elements.add(qNameOslcSysmlReferencePropertyMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLValueProperty getValuePropertyByQualifiedName(String propertyQualifiedName) {
		SysMLValueProperty sysMLValueProperty = qNameOslcSysmlValuePropertyMap.get(propertyQualifiedName);
		return sysMLValueProperty;
	}

	public static List<SysMLValueProperty> getValueProperties(String projectName) {
		List<SysMLValueProperty> elements = new ArrayList<SysMLValueProperty>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlValuePropertyMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/valueproperties/")) {
				elements.add(qNameOslcSysmlValuePropertyMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLValueType getValueTypeByQualifiedName(String propertyQualifiedName) {
		SysMLValueType sysMLValueType = qNameOslcSysmlValueTypeMap.get(propertyQualifiedName);
		return sysMLValueType;
	}

	public static List<SysMLValueType> getValueTypes(String projectName) {
		List<SysMLValueType> elements = new ArrayList<SysMLValueType>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlValueTypeMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/valuetypes/")) {
				elements.add(qNameOslcSysmlValueTypeMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLFlowProperty getFlowPropertyByQualifiedName(String propertyQualifiedName) {
		SysMLFlowProperty sysMLFlowProperty = qNameOslcSysmlFlowPropertyMap.get(propertyQualifiedName);
		return sysMLFlowProperty;
	}

	public static List<SysMLFlowProperty> getFlowProperties(String projectName) {
		List<SysMLFlowProperty> elements = new ArrayList<SysMLFlowProperty>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlFlowPropertyMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/flowproperties/")) {
				elements.add(qNameOslcSysmlFlowPropertyMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLInterfaceBlock getInterfaceBlockByQualifiedName(String propertyQualifiedName) {
		SysMLInterfaceBlock sysMLInterfaceBlock = qNameOslcSysmlInterfaceBlockMap.get(propertyQualifiedName);
		return sysMLInterfaceBlock;
	}

	public static List<SysMLInterfaceBlock> getInterfaceBlocks(String projectName) {
		List<SysMLInterfaceBlock> elements = new ArrayList<SysMLInterfaceBlock>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlInterfaceBlockMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/interfaceblocks/")) {
				elements.add(qNameOslcSysmlInterfaceBlockMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLItemFlow getItemFlowByQualifiedName(String propertyQualifiedName) {
		SysMLItemFlow sysMLItemFlow = qNameOslcSysmlItemFlowMap.get(propertyQualifiedName);
		return sysMLItemFlow;
	}

	public static List<SysMLItemFlow> getItemFlows() {
		List<SysMLItemFlow> sysMLItemFlows = new ArrayList<SysMLItemFlow>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlItemFlowMap.keySet()) {
			sysMLItemFlows.add(qNameOslcSysmlItemFlowMap.get(qNameOslcSysmlElement));
		}
		return sysMLItemFlows;
	}

	public static SysMLPort getPortByQualifiedName(String propertyQualifiedName) {
		SysMLPort sysMLPort = qNameOslcSysmlPortMap.get(propertyQualifiedName);
		return sysMLPort;
	}

	public static List<SysMLPort> getPorts(String projectName) {
		List<SysMLPort> elements = new ArrayList<SysMLPort>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlPortMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/ports/")) {
				elements.add(qNameOslcSysmlPortMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLProxyPort getProxyPortByQualifiedName(String propertyQualifiedName) {
		SysMLProxyPort sysMLProxyPort = qNameOslcSysmlProxyPortMap.get(propertyQualifiedName);
		return sysMLProxyPort;
	}

	public static List<SysMLProxyPort> getProxyPorts(String projectName) {
		List<SysMLProxyPort> elements = new ArrayList<SysMLProxyPort>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlProxyPortMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/proxyports/")) {
				elements.add(qNameOslcSysmlProxyPortMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLFullPort getFullPortByQualifiedName(String propertyQualifiedName) {
		SysMLFullPort sysMLFullPort = qNameOslcSysmlFullPortMap.get(propertyQualifiedName);
		return sysMLFullPort;
	}

	public static List<SysMLFullPort> getFullPorts(String projectName) {
		List<SysMLFullPort> elements = new ArrayList<SysMLFullPort>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlFullPortMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/fullports/")) {
				elements.add(qNameOslcSysmlFullPortMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLConnector getConnectorByQualifiedName(String propertyQualifiedName) {
		SysMLConnector sysMLConnector = qNameOslcSysmlConnectorMap.get(propertyQualifiedName);
		return sysMLConnector;
	}

	public static List<SysMLConnector> getConnectors(String projectName) {
		List<SysMLConnector> elements = new ArrayList<SysMLConnector>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlConnectorMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/connectors/")) {
				elements.add(qNameOslcSysmlConnectorMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLConnectorEnd getConnectorEndByQualifiedName(String propertyQualifiedName) {
		// if(qNameOslcSysmlConnectorEndMap.keySet().size() > 0){
		// String key = (String)
		// qNameOslcSysmlConnectorEndMap.keySet().toArray()[0];
		// if(key.startsWith("/oslc4jmagicdraw/services/")){
		// propertyQualifiedName = "/oslc4jmagicdraw/services/" + projectId +
		// "/connectorends/" + propertyQualifiedName;
		// }
		// }
		SysMLConnectorEnd sysMLConnectorEnd = qNameOslcSysmlConnectorEndMap.get(propertyQualifiedName);
		return sysMLConnectorEnd;
	}

	public static List<SysMLConnectorEnd> getConnectorEnds(String projectName) {
		List<SysMLConnectorEnd> elements = new ArrayList<SysMLConnectorEnd>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlConnectorEndMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/connectorends/")) {
				elements.add(qNameOslcSysmlConnectorEndMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLModel getModelByName(String modelName) {
		SysMLModel sysMLModel = oslcSysmlModelMap.get(modelName);
		return sysMLModel;
	}

	public static List<SysMLModel> getModels() {
		List<SysMLModel> sysMLModels = new ArrayList<SysMLModel>();
		for (String id : oslcSysmlModelMap.keySet()) {
			sysMLModels.add(oslcSysmlModelMap.get(id));
		}
		return sysMLModels;
	}

	public static SysMLPackage getPackageByQualifiedName(String qualifiedName) {
		SysMLPackage sysMLPackage = qNameOslcSysmlPackageMap.get(qualifiedName);
		return sysMLPackage;
	}

	public static List<SysMLPackage> getPackages(String projectName) {
		List<SysMLPackage> elements = new ArrayList<SysMLPackage>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlPackageMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/packages/")) {
				elements.add(qNameOslcSysmlPackageMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static SysMLAssociationBlock getAssociationBlockByQualifiedName(String blockQualifiedName) {
		SysMLAssociationBlock sysMLAssociationBlock = qNameOslcSysmlAssociationBlockMap.get(blockQualifiedName);
		return sysMLAssociationBlock;
	}

	public static List<SysMLAssociationBlock> getAssociationBlocks(String projectName) {
		List<SysMLAssociationBlock> elements = new ArrayList<SysMLAssociationBlock>();
		for (String qNameOslcSysmlElement : qNameOslcSysmlAssociationBlockMap.keySet()) {
			if (qNameOslcSysmlElement.startsWith(projectName + "/associationblocks/")) {
				elements.add(qNameOslcSysmlAssociationBlockMap.get(qNameOslcSysmlElement));
			}
		}
		return elements;
	}

	public static String getQualifiedNameOrID(Element element) {
		String qfOrID = null;
		if (element instanceof NamedElement) {
			NamedElement namedElement = (NamedElement) element;
			if (namedElement.getName().equals("")) {
				qfOrID = element.getID();
			} else {
				qfOrID = ((NamedElement) element).getQualifiedName().replaceAll("\\n", "-").replaceAll(" ", "_");
			}
		} else {
			qfOrID = element.getID();
		}
		return qfOrID;
	}

	public static String getQualifiedNameFromURI(URI uri) {
		String uriString = uri.getRawPath();
		uriString = uriString.replace("/oslc4jmagicdraw/services/", "");
		String[] uriStrings = uriString.split("/");
		String qualifiedName = uriStrings[2];
		// qualifiedName = qualifiedName.replaceAll("\\n", "-").replaceAll(" ",
		// "_");
		return qualifiedName;
	}

	public static String getNameFromQualifiedName(String qualifiedName) {
		String[] elementQNameParts = qualifiedName.split("::");
		int newElementQNamePartsSize = elementQNameParts.length;
		String elementName = elementQNameParts[newElementQNamePartsSize - 1];
		return elementName;
	}

	public static URI getURIFromQualifiedName(String typeAndQualifiedName) {
		String[] typeAndQualifiedNameStrings = typeAndQualifiedName.split("_");
		String type = typeAndQualifiedNameStrings[0];
		String qualifiedName = typeAndQualifiedNameStrings[typeAndQualifiedNameStrings.length - 1];

		// URI cannot contain empty characters
		qualifiedName = qualifiedName.replaceAll("\\n", "-").replaceAll(" ", "_");

		String elementType = null;
		if (type.equals("MODEL")) {
			elementType = "model";
		} else if (type.equals("PACKAGE")) {
			elementType = "packages";
		} else if (type.equals("BLOCK")) {
			elementType = "blocks";
		} else if (type.equals("REQUIREMENT")) {
			elementType = "requirements";
		} else if (type.equals("PART")) {
			elementType = "parts";
		} else if (type.equals("PORT")) {
			elementType = "ports";
		}

		URI elementURI = URI.create(descriptor.resource(elementType, projectId + qualifiedName));

		return elementURI;
	}

	public static void loadSysMLProjects(String filePath) throws ApplicationExitedException, MDModelLibException, URISyntaxException, IOException {
		if (projectsManager != null) {
			while (!projectsManager.getProjects().isEmpty()) {
				projectsManager.closeProject();
			}
		}
		loadedProjects.clear();
        initializeMapsAcrossAllProjects();
        File file = new File(filePath);
        if (file.getPath().endsWith("mdzip")) {
            String fileName = file.getPath();
            if (fileName.contains(File.separator)) {
                fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
            }
            fileName = fileName.replaceAll(".mdzip", "");
            loadSysMLProject(fileName, file.getPath());
        }
        LOG.log(Level.INFO, "Data read from {0} and converted into RDF resources at {1}", new Object[]{file, new Date().toString()});
	}

	protected static void initializeMapsAcrossAllProjects() {
		predefinedMagicDrawSysMLPackageNames.clear();

		qNameOslcSysmlBlockMap.clear();
		qNameOslcSysmlPartPropertyMap.clear();
		qNameOslcSysmlReferencePropertyMap.clear();
		qNameOslcSysmlPackageMap.clear();
		qNameOslcSysmlAssociationBlockMap.clear();
		qNameOslcSysmlConnectorMap.clear();
		qNameOslcSysmlConnectorEndMap.clear();
		qNameOslcSysmlPortMap.clear();
		qNameOslcSysmlProxyPortMap.clear();
		qNameOslcSysmlFullPortMap.clear();
		qNameOslcSysmlInterfaceBlockMap.clear();
		qNameOslcSysmlFlowPropertyMap.clear();
		qNameOslcSysmlItemFlowMap.clear();
		qNameOslcSysmlValuePropertyMap.clear();
		qNameOslcSysmlValueTypeMap.clear();
		qNameOslcSysmlBlockDiagramMap.clear();
		qNameOslcSysmlInternalBlockDiagramMap.clear();

		projectIdMDSysmlRequirementsMap.clear();
		projectIdMDSysmlBlocksMap.clear();
		projectIdMDSysmlInterfaceBlocksMap.clear();
		projectIdMDSysmlItemFlowsMap.clear();
		projectIdMDSysmlPackagesMap.clear();
		projectIdMDSysmlValueTypesMap.clear();
		projectIdMDSysmlPartPropertiesMap.clear();
		projectIdMDSysmlValuePropertiesMap.clear();
		projectIdMDSysmlFlowPropertiesMap.clear();
		projectIdMDSysmlPortsMap.clear();
		projectIdMDSysmlConnectorsMap.clear();

	}

	public static void makeSysMLProjectActive(String projectId2) {
		projectsManager.setActiveProject(loadedProjects.get(projectId2));
		project = projectsManager.getActiveProject();

	}

	public static com.hp.hpl.jena.rdf.model.Model getModel() throws Exception {
        com.hp.hpl.jena.rdf.model.Model jenaModel;
		ArrayList<Object> objectList = new ArrayList<>();
		objectList.addAll(qNameOslcSysmlBlockMap.values());
		objectList.addAll(qNameOslcSysmlPartPropertyMap.values());
		objectList.addAll(qNameOslcSysmlReferencePropertyMap.values());
		objectList.addAll(oslcSysmlModelMap.values());
		objectList.addAll(qNameOslcSysmlPackageMap.values());
		objectList.addAll(qNameOslcSysmlAssociationBlockMap.values());
		objectList.addAll(qNameOslcSysmlConnectorMap.values());
		objectList.addAll(qNameOslcSysmlConnectorEndMap.values());
		objectList.addAll(qNameOslcSysmlPortMap.values());
		objectList.addAll(qNameOslcSysmlProxyPortMap.values());
		objectList.addAll(qNameOslcSysmlFullPortMap.values());
		objectList.addAll(qNameOslcSysmlInterfaceBlockMap.values());
		objectList.addAll(qNameOslcSysmlFlowPropertyMap.values());
		objectList.addAll(qNameOslcSysmlItemFlowMap.values());
		objectList.addAll(qNameOslcSysmlValuePropertyMap.values());
		objectList.addAll(qNameOslcSysmlValueTypeMap.values());
		objectList.addAll(qNameOslcSysmlBlockDiagramMap.values());
		objectList.addAll(qNameOslcSysmlInternalBlockDiagramMap.values());
		LOG.info("Before building model");
		System.out.println("Before building model");
        jenaModel = JenaModelHelper.createJenaModel(objectList.toArray());
		LOG.info("After building model");
		System.out.println("After building model");
        LOG.log(Level.INFO, "RDF model gotten with {0} statements", jenaModel.size());
        return jenaModel;
	}
}
