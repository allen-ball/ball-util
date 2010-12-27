/*
 * $Id: JNICCTask.java,v 1.7 2010-12-27 02:44:43 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private File include = null;
    private TreeMap<String,String> defineMap = new TreeMap<String,String>();

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super();

        setParallel(false);
    }

    protected File getInclude() { return include; }
    public void setInclude(File include) { this.include = include; }

    public void addConfiguredDefine(Define define) {
        defineMap.put(define.getName(), define.getValue());
    }

    @Override
    protected String command() {
        String string = getBundleString("cc");

        for (Map.Entry<String,String> entry : defineMap.entrySet()) {
            string += SPACE;
            string += getBundleString("cc-D");
            string += entry.getKey();

            String value = entry.getValue();

            if (value != null) {
                string += EQUALS;
                string += value;
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
    public static class Define {
        private String name = null;
        private String value = null;

        /**
         * Sole constructor.
         *
         * @param       name            The define name.
         *
         * @see #setName(String)
         */
        public Define(String name) { setName(name); }

        /**
         * No-argument constructor.
         */
        public Define() { this(null); }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
