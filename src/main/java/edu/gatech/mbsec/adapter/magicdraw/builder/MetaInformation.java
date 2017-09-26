package edu.gatech.mbsec.adapter.magicdraw.builder;

import edu.gatech.mbsec.adapter.magicdraw.util.Cypher;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Meta-information to be added on the resulting building {@link Model model}.
 * @author rherrera
 */
public class MetaInformation {
    /**
     * The meta-property name whose value will denote the ID of this instance.
     */
    public static final String ID_PROPERTY = "graph";
    /**
     * The meta-property name whose value will denote the type of this instance.
     */
    public static final String TYPE_PROPERTY = "type";
    /**
     * Gets an instance given a collection of meta-properties.
     * @param vocabs the related vocabulary namespaces and prefixes.
     * @param metas the meta-properties collection to be defined.
     * @return a corresponding instance to the given metaproperties.
     */
    public static MetaInformation getInstance(Properties vocabs, String... metas) {
        String tokens[], value, ns, id = null, type = null;
        Map<String, String> properties = new HashMap<>();
        Map<String, String> vocabularies = new HashMap<>();
        if (metas != null) {
            for (String meta : metas) {
                tokens = meta.split("=");
                if (tokens[0].contains(":")) {
                    value = tokens[1];
                    tokens = tokens[0].split(":");
                    ns = vocabs.getProperty(tokens[0]);
                    if (ns == null)
                        throw new IllegalArgumentException("unknown namespace for prefix: " + tokens[0]);
                    properties.put(ns + tokens[1], value);
                    vocabularies.put(tokens[0], ns);
                } else if (ID_PROPERTY.equals(tokens[0]))
                    id = tokens[1];
                else if (TYPE_PROPERTY.equals(tokens[0]))
                    type = tokens[1];
                else
                    throw new IllegalArgumentException("prefix-namespace bad formed: " + meta);
            }
        }
        return new MetaInformation(properties, vocabularies, id, type);
    }
    /**
     * The meta-properties that constitutes this meta-information.
     */
    private final Map<String, String> properties;
    /**
     * The vocabularies used with the properties.
     */
    private final Map<String, String> vocabularies;
    /**
     * The optional user-defined ID for this instance.
     */
    private final String id;
    /**
     * The optional user-defined type for this instance.
     */
    private final String type;
    /**
     * Determines whether the type was defined by the user or not.
     */
    private final boolean typeUserDefined;
    /**
     * Constructs a instance specifing meta-properties.
     * @param properties the meta-properties of this meta-information.
     * @param vocabularies the vocabularies used along with the properties.
     * @param id the id for this meta-information.
     * @param type the type for this meta-information.
     */
    private MetaInformation(Map<String, String> properties,
            Map<String, String> vocabularies, String id, String type) {
        this.vocabularies = vocabularies;
        this.properties = properties;
        if (id == null) {
            if (properties.isEmpty()) {
                this.id = String.valueOf(new Date().getTime());
            } else {
                try {
                    this.id = Cypher.md5(properties.toString());
                } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    throw new IllegalStateException("cannot encode the properties to get an id for the model", e);
                }
            }
        } else
            this.id = id;
        if (type == null) {
            this.type = "model";
            this.typeUserDefined = false;
        } else {
            this.type = type;
            this.typeUserDefined = true;
        }
    }
    /**
     * Gets the vocabularies used with the properties.
     * @return the vocabularies used with the properties.
     */
    public Map<String, String> getVocabularies() {
        return Collections.unmodifiableMap(vocabularies);
    }
    /**
     * Gets the meta-properties of this meta-information.
     * @return the meta-properties of this meta-information.
     */
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    /**
     * Gets a suitable ID for representing this meta-information.
     * @return a suitable ID for representing this meta-information.
     */
    public String getID() {
        return id;
    }
    /**
     * Gets the type of this meta-information.
     * @return the type of this meta-information.
     */
    public String getType() {
        return type;
    }
    /**
     * Determines whether the type was defined by the user or not.
     * @return {@code true} if the type of this instance was defined by the
     * user, {@code false} otherwise.
     */
    public boolean isTypeUserDefined() {
        return typeUserDefined;
    }
}