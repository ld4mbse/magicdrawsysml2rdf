package edu.gatech.mbsec.adapter.magicdraw.util;

import edu.gatech.mbsec.adapter.magicdraw.editor.AnnotationEditor;
import edu.gatech.mbsec.adapter.magicdraw.editor.AnnotationMemberEditor;
import edu.gatech.mbsec.adapter.magicdraw.editor.ArrayMemberEditor;
import edu.gatech.mbsec.adapter.magicdraw.editor.StringMemberEditor;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;

/**
 * Customize all OSLC vocabulary URIs in a given resource package.
 * @author rherrera
 */
public class OSLCVocabularyCustomizer {
    /**
     * Logger of this class.
     */
    private static final Logger LOG = Logger.getLogger(OSLCVocabularyCustomizer.class.getName());
    /**
     * {@code @OslcNamespace} value editor.
     */
    private final StringMemberEditor resourceNamespaceValueEditor;
    /**
     * {@code @OslcNamespace} finder and editor.
     */
    private final AnnotationEditor resourceNamespaceEditor;
    /**
     * {@code @OslcPropertyDefinition} value editor.
     */
    private final StringMemberEditor propertyValueEditor;
    /**
     * {@code @OslcPropertyDefinition} finder and editor.
     */
    private final AnnotationEditor propertyEditor;
    /**
     * {@code @OslcSchema} value editor.
     */
    private final ArrayMemberEditor schemaValueEditor;
    /**
     * {@code @OslcSchema} finder and editor.
     */
    private final AnnotationEditor schemaEditor;
    /**
     * If set, a method will be removed from class if its name matches
     * this regex.
     */
    private final String removeMethodsNamePattern;
    /**
     * Tracks super classes to prevent being attempted to be compiled twice or more.
     */
    private final Set<String> superClasses;

    public OSLCVocabularyCustomizer(String vocabulary, String removeMethodsNamePattern) {
        // @OslcNamespaceDefinition editors
        StringMemberEditor namespaceValueEditor = new StringMemberEditor(vocabulary, ".*localhost.*" , "http(s)?://localhost(:\\d+)?(/.*)*#");
        AnnotationMemberEditor namespaceEditor = new AnnotationMemberEditor("org.eclipse.lyo.oslc4j.core.annotation.OslcNamespaceDefinition", namespaceValueEditor, "namespaceURI");
        // @OslcNamespace editors
        resourceNamespaceValueEditor = new StringMemberEditor(vocabulary);
        resourceNamespaceEditor = new AnnotationEditor("org.eclipse.lyo.oslc4j.core.annotation.OslcNamespace");
        // @OslcPropertyDefinition editors
        propertyValueEditor = new StringMemberEditor(vocabulary, ".*localhost.*" , "http(s)?://localhost(:\\d+)?(/.*)*[#/]");
        propertyEditor = new AnnotationEditor("org.eclipse.lyo.oslc4j.core.annotation.OslcPropertyDefinition");
        // @OslcSchema editors
        schemaValueEditor = new ArrayMemberEditor(namespaceEditor);
        schemaEditor = new AnnotationEditor("org.eclipse.lyo.oslc4j.core.annotation.OslcSchema");
        this.removeMethodsNamePattern = removeMethodsNamePattern;
        superClasses = new HashSet<>();
    }
    /**
     * Customizes a resource class with its property methods.
     * @param resourceClass the resource meta class.
     * @return {@code true} if customization was done; {@code false} otherwise.
     * @throws NotFoundException if a named method is not found.
     */
    private boolean cuztomize(CtClass resourceClass) throws NotFoundException {
        boolean cuztomized;
        AttributeInfo info;
        CtClass parentClass;
        ClassFile clazz = resourceClass.getClassFile();
        CtMethod methods[] = resourceClass.getDeclaredMethods();
        if (clazz.getName().endsWith("package-info")) {
            info = clazz.getAttribute(AnnotationsAttribute.visibleTag);
            cuztomized = schemaEditor.edit(info, schemaValueEditor);
            LOG.log(Level.FINER, "[{0}] {1}", new Object[]{cuztomized ? '+' : '-', resourceClass.getName()});
        } else if (superClasses.contains(resourceClass.getName())) {
            cuztomized = false;
            LOG.log(Level.FINER, "[-] {0} : already compiled", resourceClass.getName());
        } else {
            parentClass = resourceClass.getSuperclass();
            info = clazz.getAttribute(AnnotationsAttribute.visibleTag);
            cuztomized = resourceNamespaceEditor.edit(info, resourceNamespaceValueEditor);
            for(CtMethod method : methods) {
                if (removeMethodsNamePattern != null && method.getName().matches(removeMethodsNamePattern)) {
                    resourceClass.removeMethod(method);
                    cuztomized = true;
                } else {
                    info = method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
                    if (propertyEditor.edit(info, propertyValueEditor)) {
                        cuztomized = true;
                    }
                }
            }
            if (!parentClass.getName().equals(Object.class.getName()))
                superClasses.add(parentClass.getName());
            LOG.log(Level.FINER, "[{0}] {1}", new Object[]{cuztomized ? '+' : '-', resourceClass.getName()});
        }
        return cuztomized;
    }
    /**
     * Customizes all resource classes, on a given package, that contains
     * properties whose vocabulary URIs contains the value returned by the
     * method {@link #getPropertyURI()} to have a new vocabulary URI prefix.
     * @param pkg the package name to search OSLC resource classes.
     * @throws IOException if some I/O exception occurs.
     * @throws NotFoundException if a named class is not found.
     * @throws CannotCompileException if a modified class cannot be compiled.
     */
    public void customize(String pkg) throws IOException, NotFoundException,
            CannotCompileException {
        CtClass resourceClass;
        ClassPool pool = ClassPool.getDefault();
        Set<String> resourcesNames = ClassScanner.getClassesNames(pkg);
        for (String resourceName : resourcesNames) {
            resourceClass = pool.get(resourceName);
            if (cuztomize(resourceClass)) {
                resourceClass.toClass();
            }
        }
    }

}