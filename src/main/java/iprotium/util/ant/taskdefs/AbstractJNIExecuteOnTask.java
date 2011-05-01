/*
 * $Id: AbstractJNIExecuteOnTask.java,v 1.12 2011-05-01 23:02:11 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.types.Commandline;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.12 $
 */
public abstract class AbstractJNIExecuteOnTask extends ExecuteOn {
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

    protected String getArch() { return arch; }
    public void setArch(String arch) { this.arch = arch; }

    protected String getOS() { return os; }
    public void setOS(String os) { this.os = os; }

    protected File getDestdir() { return destdir; }
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

    protected String getBundleString(String name) {
        return BUNDLE.getString(getOS(), getArch(), name);
    }

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
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2010/12/27 20:34:36  ball
 * Support <define/> to specify CPP defines and <link/> to specify
 * link libraries.
 *
 */
