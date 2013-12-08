/*
 * $Id$
 *
 * Copyright 2008 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import iprotium.io.IOUtil;
import iprotium.util.jni.POSIX;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to link a file to a new target.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("link")
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

    protected boolean isRelative() { return relative; }
    public void setRelative(boolean relative) { this.relative = relative; }

    protected boolean isSymbolic() { return symbolic; }
    public void setSymbolic(boolean symbolic) { this.symbolic = symbolic; }

    @Override
    protected void doFileOperations() throws BuildException {
        if (fileCopyMap.size() > 0) {
            log("Linking " + fileCopyMap.size()
                + " file" + ((fileCopyMap.size()) == 1 ? "" : "s")
                + " to " + destDir.getAbsolutePath());

            for (Object key : fileCopyMap.keySet()) {
                File from = new File(key.toString());

                for (String value : fileCopyMap.get(key)) {
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
        if (! from.equals(to)) {
            IOUtil.mkdirs(to.getParentFile());

            if (isSymbolic()) {
                if (isRelative()) {
                    from = new Path(from).relativizeTo(to).toFile();
                }

                if (! POSIX.symlink(from, to)) {
                    throw new IOException("symlink");
                }
            } else {
                if (! POSIX.link(from, to)) {
                    throw new IOException("link");
                }
            }
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }

    private class Path extends ArrayList<String> {
        private static final long serialVersionUID = 1240653866390577062L;

        private final String name;

        public Path(File file) {
            file = file.getAbsoluteFile();

            while (file != null) {
                add(0, file.getName());

                file = file.getParentFile();
            }

            this.name = remove(size() - 1);
        }

        public String getName() { return name; }

        public Path relativizeTo(File that) {
            return relativizeTo(new Path(that));
        }

        private Path relativizeTo(List<String> that) {
            while ((! this.isEmpty()) && (! that.isEmpty())) {
                if (this.get(0).equals(that.get(0))) {
                    this.remove(0);
                    that.remove(0);
                } else {
                    break;
                }
            }

            for (String string : that) {
                this.add(0, "..");
            }

            return this;
        }

        public File toFile() {
            File parent = null;

            for (String string : this) {
                parent = new File(parent, string);
            }

            return new File(parent, getName());
        }
    }
}
