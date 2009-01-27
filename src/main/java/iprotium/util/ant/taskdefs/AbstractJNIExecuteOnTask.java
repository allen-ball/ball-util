/*
 * $Id: AbstractJNIExecuteOnTask.java,v 1.2 2009-01-27 22:00:19 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.types.Commandline;

/**
 * Ant Task to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractJNIExecuteOnTask extends ExecuteOn {
    private static final FileDirBoth FILE = new FileDirBoth();

    static {
        FILE.setValue(FileDirBoth.FILE);
    }

    private final ResourceBundle bundle;
    private String format = null;

    /**
     * Sole constructor.
     *
     * @param   bundle          The Bundle containing the default settings.
     */
    protected AbstractJNIExecuteOnTask(ResourceBundle bundle) {
        super();

        this.bundle = bundle;

        setFailonerror(true);
        setType(FILE);

        setFormat(getBundleString("format"));
    }

    protected String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    @Override
    public void init() throws BuildException {
        super.init();

        if (cmdl.size() == 0) {
            for (String argument :
                     Commandline.translateCommandline(getFormat())) {
                if (argument.equals("SRCFILE")) {
                    createSrcfile();
                } else if (argument.equals("TARGETFILE")) {
                    createTargetfile();
                } else {
                    if (cmdl.getExecutable() == null) {
                        setExecutable(replaceProperties(argument));
                    } else {
                        createArg().setValue(replaceProperties(argument));
                    }
                }
            }
        }
    }

    protected String getBundleString(String key) {
        return bundle.getString(key);
    }

    private String replaceProperties(String string) {
        return getProject().replaceProperties(string);
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/11/18 07:36:44  ball
 * Interim check-in.
 *
 */
