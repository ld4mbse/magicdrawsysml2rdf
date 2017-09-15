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
     * 
     * @param vocabs
     * @param metas
     * @return 
     */
    public static MetaInformation getInstance(Properties vocabs, String... metas) {
        String tokens[], value, ns, id = null;
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
                else
                    throw new IllegalArgumentException("prefix-namespace bad formed: " + meta);
            }
        }
        return new MetaInformation(properties, vocabularies, id);
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
    private String id;
    /**
     * Constructs a instance specifing meta-properties.
     * @param properties the meta-properties of this meta-information.
     * @param vocabularies the vocabularies used along with the properties.
     */
    private MetaInformation(Map<String, String> properties,
            Map<String, String> vocabularies, String id) {
        this.vocabularies = vocabularies;
        this.properties = properties;
        this.id = id;
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
        if (id == null) {
            if (properties.isEmpty()) {
                id = String.valueOf(new Date().getTime());
            } else {
                try {
                    id = Cypher.md5(properties.toString());
                } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    id = String.valueOf(new Date().getTime());
                }
            }
        }
        return id;
    }
}