/*
 * $Id: FileImpl.java,v 1.1 2010-07-28 05:01:27 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static iprotium.util.Order.NATURAL;

/**
 * {@link File} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class FileImpl extends File {
    private static final long serialVersionUID = 5658423095615741365L;

    private transient Directory parent = null;

    /**
     * @see File#File(String)
     */
    public FileImpl(String pathname) { super(pathname); }

    /**
     * @see File#File(String,String)
     */
    public FileImpl(String parent, String child) { super(parent, child); }

    /**
     * @see File#File(File,String)
     */
    public FileImpl(File parent, String child) {
        super(parent, child);

        if (parent instanceof Directory) {
            if (NATURAL.compare(super.getParent(), parent.getPath()) == 0) {
                this.parent = (Directory) parent;
            }
        }
    }

    /**
     * @see File#File(URI)
     */
    public FileImpl(URI uri) { super(uri); }

    @Override
    public Directory getParentFile() {
        synchronized (this) {
            if (parent == null) {
                String path = getParent();

                parent = (path != null) ?  new Directory(path) : null;
            }
        }

        return parent;
    }

    @Override
    public FileImpl getAbsoluteFile() {
        return new FileImpl(getAbsolutePath());
    }

    @Override
    public File getCanonicalFile() throws IOException {
        return new FileImpl(getCanonicalPath());
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
