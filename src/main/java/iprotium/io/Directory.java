/*
 * $Id: Directory.java,v 1.3 2011-05-01 22:58:53 ball Exp $
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Directory {@link FileImpl} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class Directory extends FileImpl {
    private static final long serialVersionUID = -1519925282641191828L;

    /**
     * @see File#File(String)
     */
    public Directory(CharSequence pathname) { super(pathname); }

    /**
     * @see File#File(String,String)
     */
    public Directory(CharSequence parent, CharSequence child) {
        super(parent, child);
    }

    /**
     * @see File#File(File,String)
     */
    public Directory(File parent, CharSequence child) { super(parent, child); }

    /**
     * @see File#File(URI)
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

    /**
     * Method to get a child {@link Directory}.
     *
     * @param   names           The names that make up the subpath of the
     *                          child {@link Directory}.
     *
     * @return  A child {@link Directory} with a subpath represented by
     *          {@code names}.
     */
    public Directory getChildDirectory(Iterable<CharSequence> names) {
        return getChildDirectory(this, names);
    }

    /**
     * Method to get a child {@link Directory}.
     *
     * @param   names           The names that make up the subpath of the
     *                          child {@link Directory}.
     *
     * @return  A child {@link Directory} with a subpath represented by
     *          {@code names}.
     */
    public Directory getChildDirectory(CharSequence... names) {
        return getChildDirectory(this, names);
    }

    @Override
    public Directory getAbsoluteFile() {
        return new Directory(getAbsolutePath());
    }

    @Override
    public Directory getCanonicalFile() throws IOException {
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

    /**
     * @see #getChildDirectory(Iterable)
     */
    public static Directory getChildDirectory(File parent,
                                              Iterable<CharSequence> names) {
        Directory directory =
            (parent instanceof Directory)
                ? ((Directory) parent)
                : new Directory(parent.getPath());

        for (CharSequence name : names) {
            directory = new Directory(directory, name);
        }

        return directory;
    }

    /**
     * @see #getChildDirectory(CharSequence...)
     */
    public static Directory getChildDirectory(File parent,
                                              CharSequence... names) {
        return getChildDirectory(parent, Arrays.asList(names));
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
