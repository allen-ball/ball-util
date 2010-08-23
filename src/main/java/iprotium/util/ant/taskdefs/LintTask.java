/*
 * $Id: LintTask.java,v 1.4 2010-08-23 03:27:24 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.util.ant.taskdefs.lint.CloneableCheck;
import iprotium.util.ant.taskdefs.lint.SerializableCheck;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.apache.tools.ant.BuildException;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to provide additional compile-time
 * ("lint") checks.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public class LintTask extends AbstractClassFileTask {
    private final Collection<Check> checks =
        Arrays.<Check>asList(new CloneableCheck(this),
                             new SerializableCheck(this));
    private transient Map<File,Class> map = null;

    /**
     * Sole constructor.
     */
    public LintTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

        map = getMatchingClassFileMap();

        for (Map.Entry<File,Class> entry : map.entrySet()) {
            for (Check check : checks) {
                run(check, entry.getKey(), entry.getValue());
            }
        }
    }

    private void run(Check check, File file, Class<?> type) {
        check.check(file, type);

        for (Field member : type.getFields()) {
            check.check(file, type, member);
        }

        for (Constructor member : type.getConstructors()) {
            check.check(file, type, member);
        }

        for (Method member : type.getMethods()) {
            check.check(file, type, member);
        }

        for (Class member : type.getClasses()) {
            check.check(file, type, member);
        }
    }

    /**
     * Abstract base class for {@link LintTask} checks.
     *
     * @see iprotium.util.ant.taskdefs.lint
     */
    public static abstract class Check {

        /**
         * {@value #EQUALS}
         */
        protected static final String EQUALS = "=";

        /**
         * {@value #SEMICOLON}
         */
        protected static final String SEMICOLON = ";";

        /**
         * {@value #SPACE}
         */
        protected static final String SPACE = " ";

        /**
         * {@value #LB}
         */
        protected static final String LB = "{";

        /**
         * {@value #RB}
         */
        protected static final String RB = "}";

        /**
         * {@value #LP}
         */
        protected static final String LP = "(";

        /**
         * {@value #RP}
         */
        protected static final String RP = ")";

        private final LintTask task;

        /**
         * Sole constructor.
         *
         * @param task          The {@link LintTask} instance.
         */
        protected Check(LintTask task) {
            if (task != null) {
                this.task = task;
            } else {
                throw new NullPointerException("task");
            }
        }

        /**
         * Callback to check a {@link Class}.
         *
         * @param file          The {@link Class} {@link File}.
         * @param type          The {@link Class}.
         */
        public void check(File file, Class<?> type) { }

        /**
         * Callback to check a {@link Field}.
         *
         * @param file          The {@link Class} {@link File}.
         * @param type          The containing {@link Class}.
         * @param member        The {@link Field} member.
         */
        public void check(File file, Class<?> type, Field member) { }

        /**
         * Callback to check a {@link Constructor}.
         *
         * @param file          The {@link Class} {@link File}.
         * @param type          The containing {@link Class}.
         * @param member        The {@link Constructor} member.
         */
        public void check(File file, Class<?> type, Constructor member) { }

        /**
         * Callback to check a {@link Method}.
         *
         * @param file          The {@link Class} {@link File}.
         * @param type          The containing {@link Class}.
         * @param member        The {@link Method} member.
         */
        public void check(File file, Class<?> type, Method member) { }

        /**
         * Callback to check an inner {@link Class}.
         *
         * @param file          The {@link Class} {@link File}.
         * @param type          The containing {@link Class}.
         * @param member        The inner {@link Class} member.
         */
        public void check(File file, Class<?> type, Class<?> member) { }

        /**
         * Method to get the source Java {@link File} given a {@link Class}
         * {@link File}.
         *
         * @param file          The {@link Class} {@link File}.
         *
         * @return The corresponding source {@link File}.
         */
        protected File getJavaFile(File file) {
            return task.getJavaFile(task.map, file);
        }

        /**
         * See {@link LintTask#log(File,int,String)}.
         */
        protected void log(File file, int lineno, String message) {
            task.log(file, lineno, message);
        }

        /**
         * See {@link LintTask#log(String)}.
         */
        protected void log(String message) { task.log(message); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
