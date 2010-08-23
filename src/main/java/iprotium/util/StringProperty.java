/*
 * $Id: StringProperty.java,v 1.5 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Property} implementation to manage properties whose values are
 * {@link String}s.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class StringProperty extends Property<String> {

    /**
     * Construct a {@link Property} with the argument name.  The {@link
     * Property} is not required and has no default value.
     *
     * @param   name            The name of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name) { this(name, null); }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Class} name.  The {@link Property} is not required and has no
     * default value.
     *
     * @param   type            The {@link Class} whose name should be used
     *                          as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> type, String name) {
        this(getName(type, name));
    }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Package} name.  The {@link Property} is not required and has
     * no default value.
     *
     * @param   pkg             The {@link Package} whose name should be
     *                          used as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Package pkg, String name) {
        this(getName(pkg, name));
    }

    /**
     * Construct a {@link Property} with the argument name and default
     * value.  The {@link Property} is not required.
     *
     * @param   name            The name of the {@link Property}.
     * @param   value           The default value of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name, String value) {
        this(name, false, value);
    }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Class} name with a default value.  The {@link Property} is not
     * required.
     *
     * @param   type            The {@link Class} whose name should be used
     *                          as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     * @param   value           The default value of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> type, String name,
                          String value) {
        this(getName(type, name), value);
    }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Package} name with a default value.  The {@link Property} is
     * not required.
     *
     * @param   pkg             The {@link Package} whose name should be
     *                          used as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     * @param   value           The default value of the {@link Property}.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Package pkg, String name, String value) {
        this(getName(pkg, name), value);
    }

    /**
     * Construct a {@link Property} with the argument name that may require
     * a value.  The {@link Property} has no default value.
     *
     * @param   name            The name of the {@link Property}.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(String name, boolean required) {
        this(name, required, null);
    }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Class} name that may require a value.  The {@link Property}
     * has no default value.
     *
     * @param   type            The {@link Class} whose name should be used
     *                          as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
     *
     * @throws  NullPointerException
     *                          If the name parameter is null.
     */
    public StringProperty(Class<? extends Object> type, String name,
                          boolean required) {
        this(getName(type, name), required);
    }

    /**
     * Construct a {@link Property} with the argument name qualified by the
     * {@link Package} name that may require a value.  The {@link Property}
     * has no default value.
     *
     * @param   pkg             The {@link Package} whose name should be
     *                          used as the prefix for the name of the
     *                          {@link Property}.
     * @param   name            The name of the {@link Property}.
     * @param   required        {@code true} if the {@link Property} is
     *                          required; {@code false} otherwise.
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
