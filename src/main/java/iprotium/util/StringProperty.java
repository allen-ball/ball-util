/*
 * $Id: StringProperty.java,v 1.1 2008-10-26 20:54:48 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Property implementation to manage properties whose values are Strings.
 *
 * @see String
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class StringProperty extends Property<String> {

    /**
     * Construct a Property with the argument name.  The Property is not
     * required and has no default value.
     *
     * @param   name            The name of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name) { this(name, null); }

    /**
     * Construct a Property with the argument name qualified by the Class
     * name.  The Property is not required and has no default value.
     *
     * @param   cls             The Class whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> cls, String name) {
        this(getName(cls, name));
    }

    /**
     * Construct a Property with the argument name qualified by the Package
     * name.  The Property is not required and has no default value.
     *
     * @param   pkg             The Package whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Package pkg, String name) {
        this(getName(pkg, name));
    }

    /**
     * Construct a Property with the argument name and default value.  The
     * Property is not required.
     *
     * @param   name            The name of the Property.
     * @param   value           The default value of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name, String value) {
        this(name, false, value);
    }

    /**
     * Construct a Property with the argument name qualified by the Class
     * name with a default value.  The Property is not required.
     *
     * @param   cls             The Class whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     * @param   value           The default value of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> cls, String name,
                          String value) {
        this(getName(cls, name), value);
    }

    /**
     * Construct a Property with the argument name qualified by the Package
     * name with a default value.  The Property is not required.
     *
     * @param   pkg             The Package whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     * @param   value           The default value of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Package pkg, String name, String value) {
        this(getName(pkg, name), value);
    }

    /**
     * Construct a Property with the argument name that may require a value.
     * The Property has no default value.
     *
     * @param   name            The name of the Property.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name, boolean required) {
        this(name, required, null);
    }

    /**
     * Construct a Property with the argument name qualified by the Class
     * name that may require a value.  The Property has no default value.
     *
     * @param   cls             The Class whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> cls, String name,
                          boolean required) {
        this(getName(cls, name), required);
    }

    /**
     * Construct a Property with the argument name qualified by the Package
     * name that may require a value.  The Property has no default value.
     *
     * @param   pkg             The Package whose name should be used as the
     *                          prefix for the name of the Property.
     * @param   name            The name of the Property.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Package pkg, String name, boolean required) {
        this(getName(pkg, name), required);
    }

    private StringProperty(String name, boolean required, String value) {
        super(name, required, value);
    }

    @Override
    protected String fromString(String string) { return string; }
}
/*
 * $Log: not supported by cvs2svn $
 */
