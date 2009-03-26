/*
 * $Id: AbstractJNIExecuteOnTask.java,v 1.4 2009-03-26 01:36:21 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.types.Commandline;

import static iprotium.util.ant.taskdefs.JNILibPropertyTask.BUNDLE;

/**
 * Ant Task to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.4 $
 */
public abstract class AbstractJNIExecuteOnTask extends ExecuteOn {
    private static final FileDirBoth FILE = new FileDirBoth();

    static {
        FILE.setValue(FileDirBoth.FILE);
    }

    protected static String SPACE = " ";

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

        if (cmdl.size() == 0) {
            String command = getProject().replaceProperties(command());

            log(command);

            for (String argument : Commandline.translateCommandline(command)) {
                if (argument.equals("SRCFILE")) {
                    createSrcfile();
                } else if (argument.equals("TARGETFILE")) {
                    createTargetfile();
                } else {
                    if (cmdl.getExecutable() == null) {
                        setExecutable(argument);
                    } else {
                        createArg().setValue(argument);
                    }
                }
            }
        }

        super.execute();
    }

    protected String getBundleString(String name) {
        return BUNDLE.getString(getOS(), getArch(), name);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
