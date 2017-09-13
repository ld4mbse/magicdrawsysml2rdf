package edu.gatech.mbsec.adapter.magicdraw.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Helper class for scanning the classes inside a package.
 * @author rherrera
 */
public class ClassScanner {
    /**
     * Extracts the classes names from a given package name.
     * @param pkg the fully-qualified name of the package to scan.
     * @return the set of classes names within the package if any.
     * @throws FileNotFoundException if package is not on classpath.
     * @throws IOException if some I/O exception occurs.
     */
    public static Set<String> getClassesNames(String pkg)
            throws FileNotFoundException, IOException {
        Enumeration<JarEntry> entries;
        Set<String> classes = new HashSet<>();
        String jar, name, vocabularyPackage = pkg.replaceAll("\\.", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL path = loader.getResource(vocabularyPackage);
        if (path == null) throw new FileNotFoundException(vocabularyPackage);
        jar = path.getFile();
        jar = jar.substring(jar.indexOf(":") + 1, jar.indexOf("!"));
        entries = new JarFile(jar).entries();
        while(entries.hasMoreElements()) {
            name = entries.nextElement().getName();
            if (name.startsWith(vocabularyPackage)) {
                if (name.endsWith(".class") && !name.contains("$")) {
                    name = name.substring(0, name.indexOf(".class"));
                    classes.add(name.replaceAll("/", "."));
                }
            }
        }
        return classes;
    }
    /**
     * Extracts the classes from a given package name.
     * @param pkg the fully-qualified name of the package to scan.
     * @return the set of classes within the package if any.
     * @throws FileNotFoundException if package is not on classpath.
     * @throws IOException if some I/O exception occurs.
     * @throws ClassNotFoundException if a named class does not exists.
     */
    public static Set<Class<?>> getClasses(String pkg)
            throws IOException, ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<>();
        Set<String> names = getClassesNames(pkg);
        for(String name : names) {
            classes.add(Class.forName(name));
        }
        return classes;
    }
}