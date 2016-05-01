/*
 * $Id$
 *
 * Copyright 2009 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.JNILib;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to get platform-specific JNI values.
 *
 * {@bean-info}
 *
 * @see FileNameFor
 * @see Load
 * @see Prefix
 * @see Suffix
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class JNILibTask extends AbstractPropertyTask {
    private static final String DOT = ".";

    /**
     * Sole constructor.
     */
    protected JNILibTask() { super(); }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to get platform-specific JNI
     * library file name.
     *
     * {@bean-info}
     */
    @AntTask("jnilib-file-name-for")
    public static class FileNameFor extends JNILibTask {
        private String libname = null;

        /**
         * Sole constructor.
         */
        public FileNameFor() { super(); }

        @NotNull
        public String getLibname() { return libname; }
        public void setLibname(String libname) { this.libname = libname; }

        @Override
        protected String getPropertyValue() throws Throwable {
            return (((JNILib.PREFIX != null) ? JNILib.PREFIX : NIL)
                    + getLibname()
                    + ((JNILib.SUFFIX != null) ? (DOT + JNILib.SUFFIX) : NIL));
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to load JNI library (and set the
     * property if successful).
     *
     * {@bean-info}
     */
    @AntTask("jnilib-load")
    public static class Load extends FileNameFor {

        /**
         * Sole constructor.
         */
        public Load() { super(); }

        @Override
        protected String getPropertyValue() throws Throwable {
            System.loadLibrary(getLibname());

            return super.getPropertyValue();
        }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to get platform-specific JNI
     * library prefix.
     *
     * {@bean-info}
     */
    @AntTask("jnilib-prefix")
    public static class Prefix extends JNILibTask {

        /**
         * Sole constructor.
         */
        public Prefix() { super(); }

        @Override
        protected String getPropertyValue() { return JNILib.PREFIX; }
    }

    /**
     * {@link.uri http://ant.apache.org/ Ant}
     * {@link org.apache.tools.ant.Task} to get platform-specific JNI
     * library suffix.
     *
     * {@bean-info}
     */
    @AntTask("jnilib-suffix")
    public static class Suffix extends JNILibTask {

        /**
         * Sole constructor.
         */
        public Suffix() { super(); }

        @Override
        protected String getPropertyValue() { return JNILib.SUFFIX; }
    }
}
