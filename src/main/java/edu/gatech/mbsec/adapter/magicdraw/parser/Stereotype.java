package edu.gatech.mbsec.adapter.magicdraw.parser;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

/**
 * All possible SysML Stereotypes
 * @author rherrera
 */
public enum Stereotype {
    /**
     * SysML System Stereotype.
     */
    System(true),
    /**
     * SysML Block Stereotype.
     */
    Block(true),
    /**
     * SysML Requirement Stereotype.
     */
    Requirement(true),
    /**
     * SysML InterfaceBlock Stereotype.
     */
    InterfaceBlock(true),
    /**
     * SysML Item Flow Stereotype.
     */
    ItemFlow(false),
    /**
     * SysML Value Type Stereotype.
     */
    ValueType(false);
    /**
     * Indicates if this stereotype is apliable to an MD {@link Class class}.
     */
    private final boolean classApplicable;
    /**
     * Constructs a constant specifying if it is class applicable.
     * @param classApplicable class applicable flag.
     */
    private Stereotype(boolean classApplicable) {
        this.classApplicable = classApplicable;
    }
    /**
     * Determines whether this a class applicable stereotype.
     * @return {@code true} if this is a class applicable stereotype,
     * {@code false} otherwise.
     */
    public boolean isClassApplicable() {
        return classApplicable;
    }
    /**
     * Determines whether an element has this steretype.
     * @param element the element to test.
     * @return {@code true} if the given element has this stereotype;
     * {@code false} otherwise.
     */
    public boolean isOnElement(Element element) {
        return StereotypesHelper.hasStereotype(element,	name());
    }

}