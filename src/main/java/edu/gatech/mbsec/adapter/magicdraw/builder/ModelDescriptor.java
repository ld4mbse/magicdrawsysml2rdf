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
    /**
     * Default base path prefix.
     */
    public static final String DEFAULT_BASE_PATH = "http://localhost:8080/";
    /**
     * Default resources path prefix.
     */
    public static final String DEFAULT_RESOURCES_PATH = "rest/";
    /**
     * Default vocabulary path prefix.
     */
    public static final String DEFAULT_VOCAB_PATH = "vocab";
    /**
     * Becomes an input string into a path.
     * @param token the input string.
     * @return the input string with a trailing forward slash.
     */
    static String path(String token) {
        if (token.startsWith("/")) token = token.substring(1);
        return token.endsWith("/") ? token : token + "/";
    }
    /**
     * Becomes an input string into a vocabulary path.
     * @param token the input string.
     * @return the input string with a trailing sign number.
     */
    public static String vocab(String token) {
        if (token.startsWith("/")) token = token.substring(1);
        return token.endsWith("#") || token.endsWith("/") ? token : token + "#";
    }

    private final String basePath;
    private final String resourcesPath;
    private final String vocabularyPath;
    private final String vocabBaseURI;
    private final String vocabPrefix;
    private final String resourcesBaseURI;
    private final Map<String, String> vocabPrefixes;
    private final Map<String, String> typesIDproperties;
    /**
     * Creates an instance with all required properties.
     * @param path the base path to build resource and vocabulary URIs.
     * @param resources the resources path part.
     * @param vocabulary the vocabulary path part.
     */
    public ModelDescriptor(String path, String resources, String vocabulary) {
        String[] vocab;
        if (vocabulary == null)
            vocab = new String[] { DEFAULT_VOCAB_PATH, DEFAULT_VOCAB_PATH };
        else if (vocabulary.contains(":")) {
            vocab = vocabulary.split(":");
            if (vocab.length != 2) {
                vocabulary = "Invalid -vocab argument syntax: " + vocabulary;
                throw new IllegalArgumentException(vocabulary);
            }
        } else
            vocab = new String[] { DEFAULT_VOCAB_PATH, vocabulary };
        this.vocabularyPath = vocab(vocab[1]);
        this.resourcesPath = resources == null ? DEFAULT_RESOURCES_PATH : path(resources);
        this.basePath = path == null ? DEFAULT_BASE_PATH : path(path);
        this.resourcesBaseURI = this.basePath + this.resourcesPath;
        this.vocabBaseURI = this.basePath + this.vocabularyPath;
        this.typesIDproperties = new HashMap<>();
        this.vocabPrefixes = new HashMap<>();
        this.vocabPrefix = vocab[0];
        this.vocabPrefixes.put(vocabPrefix, vocabBaseURI);
    }
    /**
     * Creates an instance with a {@link #DEFAULT_VOCAB_PATH}.
     * @param path the base path to build resource and vocabulary URIs.
     * @param resources the resources path part.
     */
    public ModelDescriptor(String path, String resources){
        this(path, resources, DEFAULT_VOCAB_PATH);
    }
    /**
     * Creates an instance with a {@link #DEFAULT_VOCAB_PATH} and a
     * {@link #DEFAULT_RESOURCES_PATH}.
     * @param path the base path to build resource and vocabulary URIs.
     */
    public ModelDescriptor(String path) {
        this(path, DEFAULT_RESOURCES_PATH);
    }
    /**
     * Creates an instance with default values. The base path is
     * set to {@link #DEFAULT_BASE_PATH}, the vocabulary path part to
     * {@link #DEFAULT_VOCAB_PATH} and the services path part to
     * {@link #DEFAULT_RESOURCES_PATH}.
     */
    public ModelDescriptor() {
        this(DEFAULT_BASE_PATH);
    }
    /**
     * Creates an instance from an example URL and a vocabulary path.
     * @param url example URL.
     * @param resources the resources path part.
     * @param vocabulary the vocabulary path part.
     */
    public ModelDescriptor(URL url, String resources, String vocabulary) {
        StringBuilder sb = new StringBuilder(url.getProtocol());
        String[] vocab, paths = url.getPath().split("/");
        if (vocabulary == null)
            vocab = new String[] { DEFAULT_VOCAB_PATH, DEFAULT_VOCAB_PATH };
        else if (vocabulary.contains(":")) {
            vocab = vocabulary.split(":");
            if (vocab.length != 2) {
                vocabulary = "Invalid -vocab argument syntax: " + vocabulary;
                throw new IllegalArgumentException(vocabulary);
            }
        } else
            vocab = new String[] { DEFAULT_VOCAB_PATH, vocabulary };
        sb.append("://");
        sb.append(url.getAuthority());
        sb.append('/');
        if (paths.length > 1) {
            sb.append(paths[1]);
            sb.append('/');
        }
        this.vocabularyPath = vocab(vocab[1]);
        this.resourcesPath = resources == null ? DEFAULT_RESOURCES_PATH : path(resources);
        this.basePath = sb.toString();
        this.resourcesBaseURI = this.basePath + this.resourcesPath;
        this.vocabBaseURI = this.basePath + this.vocabularyPath;
        this.typesIDproperties = new HashMap<>();
        this.vocabPrefixes = new HashMap<>();
        this.vocabPrefix = vocab[0];
        this.vocabPrefixes.put(vocabPrefix, vocabBaseURI);
    }
    /**
     * Creates an instance with a {@link #DEFAULT_VOCAB_PATH} and an
     * example URL.
     * @param url example URL.
     * @param resources the resources path part.
     */
    public ModelDescriptor(URL url, String resources) {
        this(url, resources, DEFAULT_VOCAB_PATH);
    }
    /**
     * Creates an instance with an example URL.
     * @param url example URL.
     */
    public ModelDescriptor(URL url) {
        this(url, DEFAULT_RESOURCES_PATH);
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
     * Gets the base URI for resources.
     * @return the base URI for resources.
     */
    public String getResourcesBaseURI() {
        return resourcesBaseURI;
    }
    /**
     * Gets the prefix for the {@link #getVocabBaseURI()}.
     * @return the prefix for the {@link #getVocabBaseURI()}.
     */
    public String getVocabPrefix() {
        return vocabPrefix;
    }
    /**
     * Gets the base URI for vocabulary resources.
     * @return the base URI for vocabulary resources.
     */
    public String getVocabBaseURI() {
        return vocabBaseURI;
    }
    /**
     * Gets the string "{@link #getVocabPrefix()}:{@link #getVocabBaseURI()}".
     * @return "{@link #getVocabPrefix()}:{@link #getVocabBaseURI()}".
     */
    public String getVocabDefinicion() {
        return vocabPrefix + ":" + vocabBaseURI;
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
     * Adds a custom vocabulary prefix.
     * @param prefix the vocabulary prefix.
     * @param uri the vocabulary URI.
     */
    void addVocabularyPrefix(String prefix, String uri) {
        vocabPrefixes.put(prefix, uri);
    }
    /**
     * Builds a resource URI given its type and id.
     * @param type the simple resource type name.
     * @param ID the resource id.
     * @return a resource URI.
     * @throws NullPointerException if {@code type} or {@code ID} are null.
     */
    public String resource(String type, String ID) {
        type = resourcesBaseURI + Objects.requireNonNull(type);
        return type + "/" + Objects.requireNonNull(ID);
    }
    /**
     * Creates or gets a resource given its type and id over a {@link Model}.
     * @param type the simple resource type name.
     * @param ID the resource id.
     * @param model the underlaying resource's model.
     * @param addTypeProperty whether to add or not an RDF.type property.
     * @return a created o gotten resource.
     */
    public Resource resource(String type, String ID, Model model,
            boolean addTypeProperty) {
        if (addTypeProperty) {
            Resource rscType = model.createResource(vocabBaseURI + type);
            return model.createResource(resource(type, ID), rscType);
        } else
            return model.createResource(resource(type, ID));
    }
    /**
     * Creates or gets a resource given its type and id over a {@link Model}.
     * This method is equivalent to {@code resource(type, ID, model, true)}.
     * @param type the simple resource type name.
     * @param ID the resource id.
     * @param model the underlaying resource's model.
     * @return a created o gotten resource.
     */
    public Resource resource(String type, String ID, Model model) {
        return resource(type, ID, model, true);
    }
    /**
     * Builds a {@link Property} for a given resource type.
     * @param name the name of the property.
     * @return the property.
     */
    public Property property(String name) {
        return ResourceFactory.createProperty(vocabBaseURI + name);
    }
}