/*
 * $Id: LinkTask.java,v 1.1 2008-11-18 07:36:44 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.io.IOUtil;
import iprotium.util.jni.POSIX;
import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;

/**
 * Ant Task of link a file to a new target.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class LinkTask extends Copy {
    private boolean symbolic = false;
    private boolean relative = true;

    /**
     * Sole constructor.
     */
    public LinkTask() { super(); }

    @Override
    public void setFiltering(boolean filtering) {
        if (! filtering) {
            super.setFiltering(filtering);
        } else {
            throw new BuildException("`filtering' not supported.");
        }
    }

    protected boolean getRelative() { return relative; }
    public void setRelative(boolean relative) { this.relative = relative; }

    protected boolean getSymbolic() { return symbolic; }
    public void setSymbolic(boolean symbolic) { this.symbolic = symbolic; }

    @Override
    protected void doFileOperations() throws BuildException {
        if (fileCopyMap.size() > 0) {
            log("Linking " + fileCopyMap.size()
                + " file" + ((fileCopyMap.size()) == 1 ? "" : "s")
                + " to " + destDir.getAbsolutePath());

            for (Object key : fileCopyMap.keySet()) {
                File from = new File(key.toString());

                for (String value : ((String[]) fileCopyMap.get(key))) {
                    File to = new File(value);

                    if (! from.equals(to)) {
                        try {
                            log("Linking " + from + " to " + to, verbosity);

                            link(from, to);
                        } catch (IOException exception) {
                            String message =
                                "Failed to link " + from + " to " + to;

                            if (failonerror) {
                                throw new BuildException(message,
                                                         exception,
                                                         getLocation());
                            }

                            log(message, Project.MSG_ERR);
                        }
                    } else {
                        log("Skipping self-link of " + from, verbosity);
                    }
                }
            }
        }
    }

    private void link(File from, File to) throws IOException {
        from = from.getAbsoluteFile();
        to = to.getAbsoluteFile();

        IOUtil.mkdirs(to.getParentFile());

        if (! getSymbolic()) {
            if (! POSIX.link(from, to)) {
                throw new IOException("link");
            }
        } else {
            symlink(from, to);
        }
    }

    private void symlink(File from, File to) throws IOException {
        if (getRelative()) {
log(from.toURI().toString());
log(to.getParentFile().toURI().toString());
log(to.getParentFile().toURI().relativize(from.toURI()).toString());
            from =
                new File(to.getParentFile().toURI().relativize(from.toURI()));
        }

        if (! POSIX.symlink(from, to)) {
            throw new IOException("symlink");
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
