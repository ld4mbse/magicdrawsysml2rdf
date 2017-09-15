package edu.gatech.mbsec.adapter.magicdraw.builder;

import edu.gatech.mbsec.adapter.magicdraw.util.ClassScanner;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concentrate all vocabularies known by Apache Jena.
 * @author rherrera
 */
public class Vocabularies {
    /**
     * Logger of this class.
     */
    private static final Logger LOG;
    /**
     * Registered namespaces by prefix.
     */
    private final static Properties PREFIXES;
    /**
     * Extracts the prefixes-ns mapping from the jena vocabulary package.
     * @return the apache jena well known vocabulary namespaces.
     */
    @SuppressWarnings("UseSpecificCatch")
    static Properties getPrefixes() throws IOException, ClassNotFoundException {
        Set<Class<?>> classes;
        String prefix, namespace;
        Properties prefixes = new Properties();
        classes = ClassScanner.getClasses("com.hp.hpl.jena.vocabulary");
        for(Class<?> vocab : classes) {
            try {
                prefix = vocab.getSimpleName().toLowerCase();
                namespace = (String)vocab.getMethod("getURI").invoke(null);
                prefixes.put(prefix, namespace);
                LOG.log(Level.FINER, "[+] {0} <{1}>", new Object[]{prefix, namespace});
            } catch (Exception ex) {
                LOG.log(Level.FINER, "[-] \"{0}\" -> {1}", new Object[]{vocab.getName(), ex.toString()});
            }
        }
        return prefixes;
    }
    /**
     * Initializer block.
     */
    static {
        LOG = Logger.getLogger(Vocabularies.class.getName());
        try {
            PREFIXES = getPrefixes();
        } catch (IOException | ClassNotFoundException ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    /**
     * Retrives the known prefixes-namespaces map.
     * @return the known prefixes-namespaces map.
     */
    public static Map<String, String> getKnownPrefixes() {
        Map<String, String> prefixes = (Map<String, String>)(Object)PREFIXES;
        return Collections.unmodifiableMap(prefixes);
    }
    /**
     * Resolves a namespace given a well known prefix.
     * @param prefix the well known prefix.
     * @return {@code null} if there is not a namespace for the given prefix;
     * its namespace otherwise.
     */
    public static String getNamespace(String prefix) {
        return PREFIXES.getProperty(prefix);
    }
    /**
     * Gets the registered namespaces by prefix with the possibility of
     * overwrite or extend them with custom ones. Custom mappings are
     * prefix-namespace separated by and equals sign.
     * @param customs optional custom namespaces by prefix.
     * @return the merge of the registered namespaces with {@code customs}.
     */
    public static Properties asProperties(String[] customs) {
        Properties properties;
        String namespace, tokens[];
        if (customs == null || customs.length == 0) {
            properties = new Properties();
            properties.putAll(PREFIXES);
        } else {
            properties = new Properties(PREFIXES);
            for(String custom : customs) {
                tokens = custom.split("=");
                if (tokens.length == 2) {
                    namespace = tokens[1];
                    if (!namespace.endsWith("/") && !namespace.endsWith("#")) {
                        namespace = namespace + "#";
                    }
                    properties.setProperty(tokens[0], namespace);
                    LOG.log(Level.FINER, "[+] {0} <{1}>", new Object[]{tokens[0], namespace});
                } else {
                    LOG.log(Level.FINER, "[-] {0}", custom);
                }
            }
        }
        return properties;
    }

}