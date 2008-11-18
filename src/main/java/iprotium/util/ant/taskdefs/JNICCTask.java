/*
 * $Id: JNICCTask.java,v 1.1 2008-11-18 07:36:44 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.ResourceBundle;
import org.apache.tools.ant.BuildException;

/**
 * Ant Task to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private File include = null;

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super(ResourceBundle.getBundle(JNICCTask.class.getName()));
    }

    protected File getInclude() { return include; }
    public void setInclude(File include) { this.include = include; }

    @Override
    public void init() throws BuildException {
        super.init();

        if (getInclude() != null) {
            createArg().setValue("-I" + getInclude().getAbsolutePath());
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
