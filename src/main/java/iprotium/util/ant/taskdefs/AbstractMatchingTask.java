/*
 * $Id: AbstractMatchingTask.java,v 1.5 2009-10-25 20:52:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * Abstract base class for Ant {@link MatchingTask} implementations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public abstract class AbstractMatchingTask extends MatchingTask {
    private File basedir = null;
    private File file = null;

    /**
     * Sole constructor.
     */
    protected AbstractMatchingTask() { super(); }

    protected File getBasedir() { return basedir; }
    public void setBasedir(File basedir) { this.basedir = basedir; }

    protected File getFile() { return file; }
    public void setFile(File file) { this.file = file; }

    @Override
    public void execute() throws BuildException {
        if (getBasedir() == null && getFile() == null) {
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

        File file = getFile();

        if (file != null) {
            set.add(file);
        }

        return set;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
