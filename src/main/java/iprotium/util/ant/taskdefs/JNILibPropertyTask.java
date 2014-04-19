/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import org.apache.tools.ant.BuildException;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to get platform-specific JNI values.
 *
 * {@bean-info}
 *
 * @see Prefix
 * @see Suffix
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JNILibPropertyTask extends AbstractPropertyTask {
    private static final String DOT = ".";

    private static final String PREFIX;
    private static final String SUFFIX;

    static {
        String name = "LIBNAME";
        String[] tokens = System.mapLibraryName(name).split(name, 2);
        String prefix = (tokens.length > 0) ? tokens[0] : null;
        String suffix = (tokens.length > 1) ? tokens[1] : null;

        if (suffix != null) {
            while (suffix.startsWith(DOT)) {
                suffix = suffix.substring(DOT.length());
            }
        }

        PREFIX = prefix;
        SUFFIX = suffix;
    }

    /**
     * Sole constructor.
     */
    protected JNILibPropertyTask() { super(); }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to get platform-specific JNI
     * library prefix.
     *
     * {@bean-info}
     */
    @AntTask("jnilib-prefix")
    public static class Prefix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Prefix() { super(); }

        @Override
        protected String getPropertyValue() { return PREFIX; }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to get platform-specific JNI
     * library suffix.
     *
     * {@bean-info}
     */
    @AntTask("jnilib-suffix")
    public static class Suffix extends JNILibPropertyTask {

        /**
         * Sole constructor.
         */
        public Suffix() { super(); }

        @Override
        protected String getPropertyValue() { return SUFFIX; }
    }
}
