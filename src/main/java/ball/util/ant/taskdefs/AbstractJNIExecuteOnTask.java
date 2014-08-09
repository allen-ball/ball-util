/*
 * $Id$
 *
 * Copyright 2008 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.text.TextTable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.table.TableModel;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.types.Commandline;

/**
 * {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to compile JNI shared objects.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractJNIExecuteOnTask extends ExecuteOn
                                               implements AnnotatedTask {
    private static final FileDirBoth FILE = new FileDirBoth();

    /**
     * {@link JNIResourceBundle} available to subclass implementations.
     */
    protected static final JNIResourceBundle BUNDLE;

    static {
        FILE.setValue(FileDirBoth.FILE);

        try {
            BUNDLE = new JNIResourceBundle();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * {@link #SRCFILE} = {@value #SRCFILE}
     */
    protected static final String SRCFILE = "SRCFILE";

    /**
     * {@link #TARGETFILE} = {@value #TARGETFILE}
     */
    protected static final String TARGETFILE = "TARGETFILE";

    /**
     * {@link #SPACE} = {@value #SPACE}
     */
    protected static final String SPACE = " ";

    /**
     * {@link #EQUALS} = {@value #EQUALS}
     */
    protected static final String EQUALS = "=";

    private String arch = null;
    private String os = null;
    private File destdir = null;

    /**
     * Sole constructor.
     */
    protected AbstractJNIExecuteOnTask() {
        super();

        setFailonerror(true);
        setType(FILE);
        setVerbose(true);
    }

    public String getArch() { return arch; }
    public void setArch(String arch) { this.arch = arch; }

    public String getOS() { return os; }
    public void setOS(String os) { this.os = os; }

    public File getDestdir() { return destdir; }
    public void setDestdir(File destdir) { this.destdir = destdir; }

    protected abstract String command();

    @Override
    public void init() throws BuildException {
        super.init();

        if (getOS() == null) {
            setOS(getProject().getProperty("os.name"));
        }

        if (getArch() == null) {
            setArch(getProject().getProperty("os.arch"));
        }
    }

    @Override
    public void execute() throws BuildException {
        validate();

        if (getDestdir() == null) {
            setDestdir(getProject().resolveFile("."));
        }

        String command = getProject().replaceProperties(command());

        log(command);

        for (String argument : Commandline.translateCommandline(command)) {
            if (argument.equals(SRCFILE)) {
                createSrcfile();
            } else if (argument.equals(TARGETFILE)) {
                createTargetfile();
            } else {
                if (cmdl.getExecutable() == null) {
                    setExecutable(argument);
                } else {
                    createArg().setValue(argument);
                }
            }
        }

        super.execute();
    }

    /**
     * Method to get the {@link String} specified by the {@code os} and
     * {@code arch} properties and the parameter.
     *
     * @param   name            The parameter name.
     *
     * @return  The bundle {@link String}.
     */
    protected String getBundleString(String name) {
        return BUNDLE.getString(getOS(), getArch(), name);
    }

    /**
     * See {@link #log(Iterable)}.
     */
    protected void log(TableModel model) { log(model, Project.MSG_INFO); }

    /**
     * See {@link #log(Iterable,int)}.
     */
    protected void log(TableModel model, int msgLevel) {
        log(new TextTable(model), Project.MSG_INFO);
    }

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterable<String> iterable) {
        log(iterable, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterable<String> iterable, int msgLevel) {
        for (String line : iterable) {
            log(line, msgLevel);
        }
    }

    /**
     * See {@link #log(String)}.
     */
    protected void log(Iterator<String> iterator) {
        log(iterator, Project.MSG_INFO);
    }

    /**
     * See {@link #log(String,int)}.
     */
    protected void log(Iterator<String> iterator, int msgLevel) {
        while (iterator.hasNext()) {
            log(iterator.next(), msgLevel);
        }
    }

    @Override
    public String getAntTaskName() { return DELEGATE.getAntTaskName(this); }

    @Override
    public void validate() throws BuildException { DELEGATE.validate(this); }

    @Override
    public String toString() { return getClass().getSimpleName(); }

    /**
     * Abstract base class for optional configurables.
     */
    protected static abstract class Optional {
        private String name = null;
        private String ifP = null;
        private String unless = null;

        /**
         * Sole constructor.
         *
         * @param       name            The definition name.
         *
         * @see #setName(String)
         */
        protected Optional(String name) { setName(name); }

        /**
         * Method to specify the name of this {@link Optional} configurable.
         *
         * @param       name    The name of this {@link Optional}
         *                      configurable.
         */
        public void setName(String name) { this.name = name; }
        public String getName() { return name; }

        /**
         * See {@link #setName(String)}.
         */
        public void addText(String name) {
            if (getName() != null) {
                name = getName() + name;
            }

            setName(name);
        }

        /**
         * Sets the "if" condition to test on execution.  If the named
         * property is set, the {@link Optional} configurable will be
         * included.
         *
         * @param       ifP     The property condition to test on
         *                      execution.  If the value is {@code null} no
         *                      "if" test will not be performed.
         */
        public void setIf(String ifP) { this.ifP = ifP; }
        public String getIf() { return ifP; }

        /**
         * Sets the "unless" condition to test on execution.  If the named
         * property is set, the {@link Optional} configurable will not be
         * included.
         *
         * @param       unless  The property condition to test on
         *                      execution.  If the value is {@code null} no
         *                      "unless" test will not be performed.
         */
        public void setUnless(String unless) { this.unless = unless; }
        public String getUnless() { return unless; }

        /**
         * Method to determine if the "if" and "unless" tests have been
         * satisfied.
         *
         * @param       project The {@link Project}.
         *
         * @return      {@code true} if the "if" and "unless" tests have
         *              beed satisfied; {@code false} otherwise.
         */
        public boolean isActive(Project project) {
            return ((getIf() == null || project.getProperty(getIf()) != null)
                    && (getUnless() == null
                        || project.getProperty(getUnless()) == null));
        }

        @Override
        public int hashCode() { return System.identityHashCode(getName()); }
    }
}
