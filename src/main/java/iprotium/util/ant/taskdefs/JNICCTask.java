/*
 * $Id: JNICCTask.java,v 1.5 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.DirSet;

/**
 * Ant {@link org.apache.tools.ant.Task} to compile JNI shared objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class JNICCTask extends AbstractJNIExecuteOnTask {
    private File include = null;
    private List<DirSet> list = new ArrayList<DirSet>();

    /**
     * Sole constructor.
     */
    public JNICCTask() {
        super();

        setParallel(false);
    }

    protected File getInclude() { return include; }
    public void setInclude(File include) { this.include = include; }

    @Override
    protected String command() {
        String string = getBundleString("cc");

        if (getInclude() != null) {
            string += SPACE;
            string += getBundleString("cc-I");
            string += getInclude().getAbsolutePath();
        }

        string += SPACE;
        string += getBundleString("cc-args");

        return string;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
