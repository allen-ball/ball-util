/*
 * $Id$
 *
 * Copyright 2010 - 2015 Allen D. Ball.  All rights reserved.
 */
package ball.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Directory {@link FileImpl} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class Directory extends FileImpl {
    private static final long serialVersionUID = 3183474028935755479L;

    /**
     * See {@link System#getProperty(String)} and {@code java.home}.
     */
    public static final Directory JAVA_HOME =
        new Directory(System.getProperty("java.home"));

    /**
     * See {@link System#getProperty(String)} and {@code java.io.tmpdir}.
     */
    public static final Directory TMPDIR =
        new Directory(System.getProperty("java.io.tmpdir"));

    /**
     * See {@link System#getProperty(String)} and {@code user.dir}.
     */
    public static final Directory USER_DIR =
        new Directory(System.getProperty("user.dir"));

    /**
     * See {@link System#getProperty(String)} and {@code user.home}.
     */
    public static final Directory USER_HOME =
        new Directory(System.getProperty("user.home"));

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

    private Directory(File file) { this(file.getAbsolutePath()); }

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
     * Static method to return the argument {@link File} as a
     * {@link Directory}.  A new {@link Directory} instance is created only
     * if the argument {@link File} is not an instance of {@link Directory}.
     *
     * @param   file    The {@link File}.
     *
     * @return  The argument {@link File} as a {@link Directory}.
     */
    public static Directory asDirectory(File file) {
        Directory directory = null;

        if (file != null) {
            if (file instanceof Directory) {
                directory = (Directory) file;
            } else {
                directory = new Directory(file);
            }
        }

        return directory;
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
