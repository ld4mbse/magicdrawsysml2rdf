package edu.gatech.mbsec.adapter.magicdraw.builder;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Specifies {@link Model} creation properties.
 * @author rherrera
 */
public class ModelDescriptor {
    /**
     * The general ID property for all resources. The value of this constant
     * really means that a digest process must be performed over the resource
     * content to get its id.
     * @see #getIdProperty(java.lang.String)
     */
    public static final String GENERAL_ID_PROPERTY = "#MD5";

    private final String type;
    private final String basePath;
    private final String resourcesPath;
    private final String vocabularyPath;
    private final Map<String, String> vocabPrefixes;
    private final Map<String, String> typesIDproperties;
    private final MetaInformation meta;
    /**
     * Creates an instance with all required properties.
     * @param meta the meta-information to be added.
     * @param path the base path to build resource and vocabulary URIs.
     * @param type the inner type if this descriptor as a resource.
     * @param resources the resources path part.
     * @param vocabulary the vocabulary path part.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, String path, String type,
            String resources, String vocabulary) {
        this.type = Objects.requireNonNull(type);
        this.meta = Objects.requireNonNull(meta);
        this.vocabularyPath = Objects.requireNonNull(vocabulary);
        this.resourcesPath = Objects.requireNonNull(resources);
        this.basePath = Objects.requireNonNull(path);
        this.typesIDproperties = new HashMap<>();
        this.vocabPrefixes = new HashMap<>();
    }
    /**
     * Creates an instance with a default {@code vocab/} vocabulary path.
     * @param meta the meta-information to be added.
     * @param path the base path to build resource and vocabulary URIs.
     * @param type the inner type if this descriptor as a resource.
     * @param resources the resources path part.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, String path, String type,
            String resources) {
        this(meta, path, type, resources, "vocab/");
    }
    /**
     * Creates an instance with a default {@code vocab/} vocabulary path and a
     * default {@code rest/} services path.
     * @param meta the meta-information to be added.
     * @param type the inner type if this descriptor as a resource.
     * @param path the base path to build resource and vocabulary URIs.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, String path, String type) {
        this(meta, path, type, "rest/");
    }
    /**
     * Creates an instance with a default {@code vocab/} vocabulary path, a
     * default {@code rest/} services path and a default {@code model} type.
     * @param meta the meta-information to be added.
     * @param path the base path to build resource and vocabulary URIs.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, String path) {
        this(meta, path, "model");
    }
    /**
     * Creates an instance with default values. The base path is
     * set to {@code http://localhost:8080/}, the vocabulary path part to
     * {@code vocab/}, the services path part to {@code rest/} and type is set to
     * {@code model}.
     * @param meta the meta-information to be added.
     */
    public ModelDescriptor(MetaInformation meta) {
        this(meta, "http://localhost:8080/");
    }
    /**
     * Creates an instance from an example URL and a vocabulary path.
     * @param meta the meta-information to be added.
     * @param url example URL.
     * @param vocabulary the vocabulary path
     * @throws NullPointerException if {@code url} or {@code vocabulary} are
     * {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, URL url, String vocabulary) {
        StringBuilder sb = new StringBuilder(url.getProtocol());
        String services = "rest/", paths[] = url.getPath().split("/");
        String type = "model";
        sb.append("://");
        sb.append(url.getAuthority());
        sb.append('/');
        if (paths.length > 1) {
            sb.append(paths[1]);
            sb.append('/');
        }
        if (paths.length > 2) {
            services = paths[2] + "/";
        }
        if (paths.length > 3) {
            type = paths[3];
        }
        this.meta = Objects.requireNonNull(meta);
        this.vocabularyPath = Objects.requireNonNull(vocabulary);
        this.resourcesPath = services;
        this.basePath = sb.toString();
        this.typesIDproperties = new HashMap<>();
        this.vocabPrefixes = new HashMap<>();
        this.type = type;
    }
    /**
     * Creates an instance with a default {@code vocab/} vocabulary path and an
     * example URL.
     * @param meta the meta-information to be added.
     * @param url example URL.
     * @throws NullPointerException if {@code url} is {@code null}.
     */
    public ModelDescriptor(MetaInformation meta, URL url) {
        this(meta, url, "vocab/");
    }
    /**
     * Gets the base path defined in this descriptor.
     * @return defined base path.
     */
    public String getBasePath() {
        return basePath;
    }
    /**
     * Gets the resources path part defined in this descriptor.
     * @return defined resources path part.
     */
    public String getResourcesPath() {
        return resourcesPath;
    }
    /**
     * Gets the vocabulary path part defined in this descriptor.
     * @return defined vocabulary path part.
     */
    public String getVocabularyPath() {
        return vocabularyPath;
    }
    /**
     * Gets the inner type of this instance as a reasource.
     * @return the inner type if this instance as a resource.
     */
    public String getType() {
        return type;
    }
    /**
     * Gets a read-only version of all vocabulary prefixes map. To register a
     * new vocabulary prefix mapping invoke the 
     * {@link #vocabulary(java.lang.String)} method.
     * @return a read-only version of all vocabulary prefixes map.
     */
    public Map<String, String> getVocabPrefixes() {
        return Collections.unmodifiableMap(vocabPrefixes);
    }
    /**
     * Gets the property name that, for a given resource type, it's its
     * identifier. By default, the {@link #GENERAL_ID_PROPERTY} value will be
     * returned for all types. To customize this behaviour, call the method
     * {@link #addTypeIdProperty(java.lang.String, java.lang.String)} to define
     * a real property name from where to extract the resource id for that type.
     * @param type the resource type.
     * @return the name of the property whose value is the id of the resource.
     */
    public String getIdProperty(String type) {
        String id = typesIDproperties.get(type);
        return id == null ? GENERAL_ID_PROPERTY : id;
    }
    /**
     * Registers a custom id property for a given type. By defualt, all types
     * will be expected to have an "id" property whose value identifies the
     * resource. By calling this method, a customized property name can be used
     * for a given resource type.
     * @param type the resource type.
     * @param idProperty the name of the id property.
     */
    public void addTypeIdProperty(String type, String idProperty) {
        typesIDproperties.put(type, idProperty);
    }
    /**
     * Clears all the custom id properties for all registered types.
     */
    public void clearTypeIdProperties() {
        typesIDproperties.clear();
    }
    /**
     * Builds a vocabulary namespace given a resource type.
     * @param type the resource type.
     * @return a vocabulary namespace.
     */
    public String vocabulary(String type) {
        String URI = basePath + vocabularyPath;
        if (type != null && !type.isEmpty()) {
            URI = URI + type + "#";
            vocabPrefixes.put(type, URI);
        } else {
            vocabPrefixes.put("vocab", URI);
        }
        return URI;
    }
    /**
     * Builds a resource URI given its type and id.
     * @param type the simple resource type name.
     * @param ID the resource id.
     * @return a resource URI.
     * @throws NullPointerException if {@code type} or {@code ID} are null.
     */
    public String resource(String type, String ID) {
        type = basePath + resourcesPath + Objects.requireNonNull(type);
        return type + "/" + Objects.requireNonNull(ID);
    }
    /**
     * Creates or gets a resource given its type and id over a {@link Model}.
     * @param type the simple resource type name.
     * @param ID the resource id.
     * @param model the underlaying resource's model.
     * @return a created o gotten resource.
     */
    public Resource resource(String type, String ID, Model model) {
        return model.createResource(resource(type, ID));
    }
    /**
     * Creates or gets the underlying resource of this instance.
     * @param model the underlaying resource's model.
     * @return a created o gotten resource related to this instance.
     */
    public Resource me(Model model) {
        return model.createResource(resource(type, meta.getID()));
    }
    /**
     * Builds a {@link Property} for a given resource type.
     * @param type the resource type.
     * @param name the name of the property.
     * @return the property.
     */
    public Property property(String type, String name) {
        String URI = vocabulary(type) + name;
        return ResourceFactory.createProperty(URI);
    }
    /**
     * Customizes a model withe the {@link MetaInformation}.
     * @param model the model to sign.
     */
    public void customize(Model model) {
        Resource me = me(model);
        Map<String, String> vocabularies = meta.getVocabularies();
        Map<String, String> properties = meta.getProperties();
        if (!properties.isEmpty()) {
            for(Map.Entry<String, String> property : properties.entrySet()) {
                me.addLiteral(model.createProperty(property.getKey()), property.getValue());
            }
            for(Map.Entry<String, String> vocabulary : vocabularies.entrySet()) {
                vocabPrefixes.put(vocabulary.getKey(), vocabulary.getValue());
            }
        }
    }
}