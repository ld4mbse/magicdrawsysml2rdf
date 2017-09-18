package edu.gatech.mbsec.adapter.magicdraw.conversion;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import edu.gatech.mbsec.adapter.magicdraw.builder.ModelDescriptor;
import java.util.logging.Logger;

/**
 *
 * @author rherrera
 */
public class Vocabulary {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(Vocabulary.class.getName());


    private ResIterator getResourcesByType(Resource type, Model src) {
        return src.query(new SimpleSelector(null, RDF.type, type)).listSubjects();
    }

    public Resource getResourceType(String name, Model src, ModelDescriptor desc) {
        String blockType = desc.vocabulary(name);
        blockType = blockType.substring(0, blockType.length() - 1);
        return src.createResource(blockType);
    }

    public Model convert(Model src, ModelDescriptor desc) {
        String name;
        RDFNode parent;
        ResIterator blocks, properties;
        Resource block, property, resource;
        Model output = ModelFactory.createDefaultModel();
        Resource BLOCK_TYPE = getResourceType("Block", src, desc);
        Resource PART_PROPERTY_TYPE = getResourceType("PartProperty", src, desc);
        Resource VALUE_PROPERTY_TYPE = getResourceType("ValueProperty", src, desc);
        Property INHERITENCE_PROPERTY = desc.property("", "Block_inheritedBlock");
        Property NAME = desc.property("", "NamedElement_name");
        //Property properyFinder = desc.property("", "OwnedElement_owner");
        blocks = getResourcesByType(BLOCK_TYPE, src);
        while(blocks.hasNext()) {
            block = blocks.next();
            name = src.getProperty(block, NAME).getString();
            resource = desc.resource("block", name.replaceAll(" ", "_"), output);
            if (src.getProperty(block, INHERITENCE_PROPERTY) != null) {
                parent = src.getProperty(block, INHERITENCE_PROPERTY).getObject();
                output.add(resource, RDFS.subClassOf, parent);
            }
            output.add(resource, RDFS.label, name);
            output.add(resource, RDF.type, RDFS.Class);
            output.add(resource, RDFS.isDefinedBy, desc.vocabulary(null));
            /*
            elements = src.query(new SimpleSelector(null, properyFinder, block)).listSubjects();
            while(elements.hasNext()) {
                element = elements.next();
                
            }*/
        }

        properties = getResourcesByType(PART_PROPERTY_TYPE, src);
        while(properties.hasNext()) {
            property = properties.next();
            name = src.getProperty(property, NAME).getString();
            resource = desc.resource("property", name.replaceAll(" ", "_"), output);
            output.add(resource, RDFS.label, name);
            output.add(resource, RDF.type, RDF.Property);
            output.add(resource, RDFS.isDefinedBy, desc.vocabulary(null));
        }

        properties = getResourcesByType(VALUE_PROPERTY_TYPE, src);
        while(properties.hasNext()) {
            property = properties.next();
            name = src.getProperty(property, NAME).getString();
            resource = desc.resource("property", name.replaceAll(" ", "_"), output);
            output.add(resource, RDFS.label, name);
            output.add(resource, RDF.type, RDF.Property);
            output.add(resource, RDFS.isDefinedBy, desc.vocabulary(null));
        }
        return output;
    }

}