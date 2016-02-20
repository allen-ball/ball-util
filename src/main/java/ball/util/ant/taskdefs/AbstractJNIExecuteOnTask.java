/*
 * $Id$
 *
 * Copyright 2008 - 2016 Allen D. Ball.  All rights reserved.
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
}
