/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import java.io.File;
import java.util.LinkedHashSet;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@AntTask("jni-cc")
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private File include = null;
    private LinkedHashSet<Definition> defineSet =
        new LinkedHashSet<Definition>();

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super();

        setParallel(false);
    }

    protected File getInclude() { return include; }
    public void setInclude(File include) { this.include = include; }

    public void addConfiguredDefine(Definition definition) {
        defineSet.add(definition);
    }

    @Override
    protected String command() {
        String string = getBundleString("cc");

        for (Definition definition : defineSet) {
            if (definition.isActive(getProject())) {
                string += SPACE;
                string += getBundleString("cc-D");
                string += definition.getName();

                String value = definition.getValue();

                if (value != null) {
                    string += EQUALS;
                    string += value;
                }
            }
        }

        if (getInclude() != null) {
            string += SPACE;
            string += getBundleString("cc-I");
            string += getInclude().getAbsolutePath();
        }

        string += SPACE;
        string += getBundleString("cc-args");

        return string;
    }

    /**
     * {@link JNICCTask} CPP definition.
     */
    public static class Definition extends Optional {
        private String value = null;

        /**
         * Sole constructor.
         *
         * @param       name            The definition name.
         *
         * @see #setName(String)
         */
        public Definition(String name) { super(name); }

        /**
         * No-argument constructor.
         */
        public Definition() { this(null); }

        public void setValue(String value) { this.value = value; }
        public String getValue() { return value; }
    }
}
