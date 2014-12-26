/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Set;

import static ball.util.StringUtil.NIL;

/**
 * Abstract Property base class.
 *
 * @param       <T>             The underlying type of this
 *                              {@link Property}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class Property<T> implements Comparable<Property<?>> {
    private final String name;
    private final boolean required;
    private final T value;

    /**
     * Sole constructor.
     *
     * @param   name            The name of the property.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
     * @param   value           The default value of the Property.
     *
     * @throws  NullPointerException
     *                          If the name parameter is {@code null}.
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
     * Method to get the name of this {@link Property}.
     *
     * @return  The name of the {@link Property}.
     */
    public String getName() { return name; }

    /**
     * Method to determine if the {@link Property} is required (should be
     * set).
     *
     * @return  {@code true} if the {@link Property} is required;
     *          {@code false} otherwise.
     */
    public boolean isRequired() { return required; }

    /**
     * Method to get the default value of this {@link Property}.
     *
     * @return  The default value of this {@link Property}.
     */
    public T getDefaultValue() { return value; }

    /**
     * Method to get the default value for this {@link Property} as a
     * {@link String}.
     *
     * @return  The default value of this {@link Property} as a
     *          {@link String}.
     */
    public String getDefaultValueAsString() {
        return toString(getDefaultValue());
    }

    /**
     * Method to determine if this {@link Property} is set.
     *
     * @param   properties      The {@link Properties} to be searched.
     *
     * @return  {@code true} if this {@link Property} is set;
     *          {@code false} otherwise.
     */
    public boolean isSet(Properties properties) {
        return properties.getProperty(getName()) != null;
    }

    /**
     * Method to get the {@link Property} {@link String} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     *
     * @return  The value of the {@link Property} (may be {@code null}).
     *
     * @throws  MissingResourceException
     *                          If the property is required and the property
     *                          is not set.
     */
    public String getProperty(Properties properties) {
        return getProperty(properties, isRequired());
    }

    /**
     * Method to get the {@link Property} {@link String} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
     *
     * @return  The value of the {@link Property} (may be {@code null}).
     *
     * @throws  MissingResourceException
     *                          If the {@link Property} is required and the
     *                          {@link Property} is not set.
     */
    public String getProperty(Properties properties, boolean required) {
        T value = getValue(properties, required);

        return (value != null) ? toString(value) : null;
    }

    /**
     * Method to set the {@link Property} {@link String} value.
     *
     * @param   properties      The {@link Properties} where the
     *                          {@link Property} is to be set.
     * @param   value           The value to set the {@link Property} to.
     *
     */
    public void setProperty(Properties properties, String value) {
        properties.setProperty(getName(), value);
    }

    /**
     * Method to get the {@link Property} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     *
     * @return  The value of the {@link Property} (may be {@code null}).
     *
     * @throws  MissingResourceException
     *                          If the {@link Property} is required and the
     *                          {@link Property} is not set.
     */
    public T getValue(Properties properties) {
        return getValue(properties, isRequired());
    }

    /**
     * Method to get the {@link Property} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
     *
     * @return  The value of the {@link Property} (may be {@code null}).
     *
     * @throws  MissingResourceException
     *                          If the {@link Property} is required and the
     *                          {@link Property} is not set.
     */
    public T getValue(Properties properties, boolean required) {
        return getValue(properties, required, getDefaultValue());
    }

    /**
     * Method to get the {@link Property} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     * @param   value           The value returned if the {@link Property}
     *                          is not set.
     *
     * @return  The value of the {@link Property} (may be {@code null}).
     *
     * @throws  MissingResourceException
     *                          If the {@link Property} is required and the
     *                          {@link Property} is not set.
     */
    public T getValue(Properties properties, T value) {
        return getValue(properties, isRequired(), value);
    }

    private T getValue(Properties properties, boolean required, T value) {
        String property = properties.getProperty(getName());

        if (required && property == null) {
            throw new MissingPropertyException(this);
        }

        return (property != null) ? fromString(property) : value;
    }

    /**
     * Method to set the {@link Property} value.
     *
     * @param   properties      The {@link Properties} to be searched for
     *                          the value.
     * @param   value           The new value of the {@link Property}.
     */
    public void setValue(Properties properties, T value) {
        setProperty(properties, toString(value));
    }

    /**
     * Method to convert a {@link String} representation to the
     * {@link Property}'s underlying {@link Object}.
     *
     * @param   string          The {@link String} representation of the
     *                          {@link Object}.
     *
     * @return  The {@link Object} cconverted from its {@link String}
     *          representation.
     */
    protected abstract T fromString(String string);

    /**
     * Method to convert the {@link Property}'s underlying {@link Object} to
     * its {@link String} representation.  This method should be overridden
     * by subclasses.
     *
     * @param   value           The {@link Object} to be converted.
     *
     * @return  The {@link String} representation.
     */
    protected String toString(T value) {
        return (value != null) ? value.toString() : null;
    }

    @Override
    public String toString() { return getName(); }

    public int compareTo(Property<?> that) {
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
     * Static method to return all static public {@link Property} fields
     * defined within a {@link Class}.
     *
     * @param   types           The {@link Class} instances to analyze.
     *
     * @return  The {@link Set} of {@link Property} objects defined in
     *          {@link Property} and {@link Iterable} fields.
     */
    public static Set<Property<?>> getStaticPropertyFields(Class<?>... types) {
        LinkedHashSet<Property<?>> set = new LinkedHashSet<>();

        for (Class<?> type : types) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    if (isPublic(field) && isStatic(field)) {
                        Object object = field.get(null);

                        if (object instanceof Property) {
                            set.add((Property) object);
                        } else if (object instanceof Iterable) {
                            for (Object element : (Iterable) object) {
                                if (element instanceof Property) {
                                    set.add((Property) element);
                                }
                            }
                        }
                    }
                } catch (IllegalAccessException exception) {
                }
            }
        }

        return Collections.unmodifiableSet(set);
    }

    private static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    private static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }

    /**
     * Convenience method to construct a qualified name based on a prefix.
     *
     * @param   object          The {@link Object} ({@link Class},
     *                          {@link Package}, or {@link CharSequence})
     *                          that represents the prefix of the
     *                          {@link Property} name.
     * @param   name            The name of the {@link Property}.
     *
     * @return  The qualified name of the {@link Property}.
     */
    protected static String getName(Object object, String name) {
        return getPrefix(object) + name;
    }

    private static String getPrefix(Object object) {
        String prefix = null;

        if (object instanceof Class) {
            prefix = ((Class) object).getName();
        } else if (object instanceof Package) {
            prefix = ((Package) object).getName();
        } else if (object instanceof CharSequence) {
            prefix = object.toString();
        } else if (object != null) {
            throw new ClassCastException(object.getClass().getName());
        }

        if (prefix != null && prefix.length() > 0) {
            if (! prefix.endsWith(".")) {
                prefix += ".";
            }
        }

        return (prefix != null) ? prefix : NIL;
    }
}
