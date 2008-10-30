/*
 * $Id: AbstractMatchingTask.java,v 1.2 2008-10-30 07:40:17 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * Abstract base class for Ant MatchingTask implementations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractMatchingTask extends MatchingTask {
    private File basedir = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    protected File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null) {
            setBasedir(getProject().resolveFile("."));
        }
    }

    protected Set<File> getMatchingFileSet() {
        Set<File> set = new TreeSet<File>();
        File base = getBasedir();

        if (base != null) {
            for (String path : getDirectoryScanner(base).getIncludedFiles()) {
                set.add(new File(base, path));
            }
        }

        return set;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
