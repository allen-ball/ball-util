/*
 * $Id: Directory.java,v 1.2 2010-08-23 03:43:54 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Directory {@link FileImpl} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class Directory extends FileImpl {
    private static final long serialVersionUID = 1378072016766367842L;

    /**
     * @see FileImpl#FileImpl(String)
     */
    public Directory(String pathname) { super(pathname); }

    /**
     * @see FileImpl#FileImpl(String,String)
     */
    public Directory(String parent, String child) { super(parent, child); }

    /**
     * @see FileImpl#FileImpl(File,String)
     */
    public Directory(File parent, String child) { super(parent, child); }

    /**
     * @see FileImpl#FileImpl(URI)
     */
    public Directory(URI uri) { super(uri); }

    {
        if (super.exists()) {
            if (! super.isDirectory()) {
                throw new IllegalArgumentException(this
                                                   + " is not a directory");
            }
        }
    }

    @Override
    public Directory getAbsoluteFile() {
        return new Directory(getAbsolutePath());
    }

    @Override
    public File getCanonicalFile() throws IOException {
        return new Directory(getCanonicalPath());
    }

    /**
     * @return  {@code true} always.
     */
    @Override
    public boolean isDirectory() { return true; }

    /**
     * @throws  IOException     Always.
     */
    @Override
    public boolean createNewFile() throws IOException {
        throw new IOException(this + " is a directory");
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
