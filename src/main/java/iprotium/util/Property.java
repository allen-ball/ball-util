/*
 * $Id: Property.java,v 1.1 2008-10-26 20:54:48 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.MissingResourceException;
import java.util.Properties;

/**
 * Abstract Property base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public abstract class Property<T> implements Comparable<Property<?>> {
    private final String name;
    private final boolean required;
    private final T value;

    /**
     * Sole constructor.
     *
     * @param   name            The name of the property.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     * @param   value           The default value of the Property.
     *
     * @throws  NullPointerException
     *                          If the name or type parameter is null.
     */
    protected Property(String name, boolean required, T value) {
        if (name != null) {
            this.name = name;
        } else {
            throw new NullPointerException("name");
        }

        this.required = required;
        this.value = value;
    }

    /**
     * Method to get the name of the Property.
     *
     * @return  The name of the Property.
     */
    public String getName() { return name; }

    /**
     * Method to determine if the Property is required (should be set).
     *
     * @return  true if the Property is required; false otherwise.
     */
    public boolean isRequired() { return required; }

    /**
     * Method to get the default value for the Property.
     *
     * @return  The default value of the Property.
     */
    public T getDefaultValue() { return value; }

    /**
     * Method to determine if the Property is set.
     *
     * @param   properties      The Properties to be searched.
     *
     * @return  true if the Property is set; false otherwise.
     */
    public boolean isSet(Properties properties) {
        return properties.getProperty(getName()) != null;
    }

    /**
     * Method to get the Property string value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     *
     * @return  The value of the Property (may be null).
     *
     * @throws  MissingResourceException
     *                          If the property is required and the property
     *                          is not set.
     */
    public String getProperty(Properties properties) {
        return getProperty(properties, isRequired());
    }

    /**
     * Method to get the Property string value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     *
     * @return  The value of the Property (may be null).
     *
     * @throws  MissingResourceException
     *                          If the Property is required and the Property
     *                          is not set.
     */
    public String getProperty(Properties properties, boolean required) {
        T value = getValue(properties, required);

        return (value != null) ? toString(value) : null;
    }

    /**
     * Method to set the Property string value.
     *
     * @param   properties      The Properties where the Property is to be
     *                          set.
     * @param   value           The value to set the Property to.
     *
     */
    public void setProperty(Properties properties, String value) {
        properties.setProperty(getName(), value);
    }

    /**
     * Method to get the Property value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     *
     * @return  The value of the Property (may be null).
     *
     * @throws  MissingResourceException
     *                          If the Property is required and the Property
     *                          is not set.
     */
    public T getValue(Properties properties) {
        return getValue(properties, isRequired());
    }

    /**
     * Method to get the Property value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     * @param   required        true if the Property is required; false
     *                          otherwise.
     *
     * @return  The value of the Property (may be null).
     *
     * @throws  MissingResourceException
     *                          If the Property is required and the Property
     *                          is not set.
     */
    public T getValue(Properties properties, boolean required) {
        return getValue(properties, required, getDefaultValue());
    }

    /**
     * Method to get the Property value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     * @param   value           The value returned if the Property is not
     *                          set.
     *
     * @return  The value of the Property (may be null).
     *
     * @throws  MissingResourceException
     *                          If the property is required and the property
     *                          is not set.
     */
    public T getValue(Properties properties, T value) {
        return getValue(properties, isRequired(), value);
    }

    private T getValue(Properties properties, boolean required, T value) {
        String property = properties.getProperty(getName());

        if (required && property == null) {
            throw new MissingPropertyException();
        }

        return (property != null) ? fromString(property) : value;
    }

    /**
     * Method to set the Property value.
     *
     * @param   properties      The Properties to be searched for the
     *                          value.
     * @param   value           The new value of the Property.
     */
    public void setValue(Properties properties, T value) {
        setProperty(properties, toString(value));
    }

    /**
     * Abstract method to convert a string representation to the Property's
     * underlying object.  This method must be overridden by subclasses.
     *
     * @param   string          The String representation of the Object.
     *
     * @return  The Object cconverted from its String representation.
     */
    protected abstract T fromString(String string);

    /**
     * Method to convert the Property's underlying object to its String
     * representation.  This method should be overridden by subclasses.
     *
     * @param   value           The Object to be converted.
     *
     * @return  The String representation.
     */
    protected String toString(T value) {
        return (value != null) ? value.toString() : null;
    }

    @Override
    public String toString() { return getName(); }

    public int compareTo(Property that) {
        return this.getName().compareTo(that.getName());
    }

    @Override
    public boolean equals(Object that) {
        return ((that instanceof Property)
                    ? (this.compareTo((Property) that) == 0)
                    : super.equals(that));
    }

    @Override
    public int hashCode() { return getName().hashCode(); }

    /**
     * Convenience method to construct a qualified name based on a Class.
     *
     * @param   cls             The Class whose name should be used as a a
     *                          prefix.
     * @param   name            The name of the Property.
     *
     * @return  The qualified name of the Property.
     */
    protected static String getName(Class<? extends Object> cls, String name) {
        return getName((cls != null) ? cls.getName() : null, name);
    }

    /**
     * Convenience method to construct a qualified name based on a Package.
     *
     * @param   pkg             The Package whose name should be used as a a
     *                          prefix.
     * @param   name            The name of the Property.
     *
     * @return  The qualified name of the Property.
     */
    protected static String getName(Package pkg, String name) {
        return getName((pkg != null) ? pkg.getName() : null, name);
    }

    /**
     * Convenience method to construct a qualified name based on a prefix.
     *
     * @param   prefix          The prefix of the Property name.
     * @param   name            The name of the Property.
     *
     * @return  The qualified name of the Property.
     */
    protected static String getName(String prefix, String name) {
        return (prefix != null) ? (prefix + "." + name) : name;
    }

    /**
     * Exception thrown to indicate a required Property is not set.
     */
    public class MissingPropertyException extends MissingResourceException {
        private static final long serialVersionUID = 6351016292213085011L;

        /**
         * Sole constructor.
         */
        public MissingPropertyException() {
            super("Undefined property `" + getName() + "'",
                  Property.this.getClass().getName(), getName());
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
