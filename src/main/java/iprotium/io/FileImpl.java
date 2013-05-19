/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;

import static iprotium.util.Order.NATURAL;

/**
 * {@link File} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class FileImpl extends File {
    private static final long serialVersionUID = -1392273539158430301L;

    /**
     * {@link #DOT} = {@value #DOT}
     */
    protected static final String DOT = ".";

    private transient Directory parent = null;

    /**
     * @see File#File(String)
     */
    public FileImpl(CharSequence pathname) { super(pathname.toString()); }

    /**
     * @see File#File(String,String)
     */
    public FileImpl(CharSequence parent, CharSequence child) {
        super(parent.toString(), child.toString());
    }

    /**
     * @see File#File(File,String)
     */
    public FileImpl(File parent, CharSequence child) {
        super(parent, child.toString());

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

    private FileImpl(File file) { this(file.getAbsolutePath()); }

    /**
     * Method to get the base-name (removing the last suffix) of
     * {@code this} {@link File}'s name.
     *
     * @return  The base-name of this {@link File}'s name.
     *
     * @see #getName()
     */
    public String getNameBase() { return getNameBase(this); }

    /**
     * Method to get the suffix of {@code this} {@link File}'s name.
     *
     * @return  The suffix of this {@link File}'s name ({@code null} if
     *          there is none).
     *
     * @see #getName()
     */
    public String getNameSuffix() { return getNameSuffix(this); }

    /**
     * Method to get a child {@link File}.
     *
     * @param   names           The names that make up the subpath of the
     *                          child {@link FileImpl}.
     *
     * @return  A child {@link File} with a subpath represented by
     *          {@code names}.
     */
    public FileImpl getChildFile(Iterable<CharSequence> names) {
        return getChildFile(this, names);
    }

    /**
     * Method to get a child {@link File}.
     *
     * @param   names           The names that make up the subpath of the
     *                          child {@link File}.
     *
     * @return  A child {@link FileImpl} with a subpath represented by
     *          {@code names}.
     */
    public FileImpl getChildFile(CharSequence... names) {
        return getChildFile(this, names);
    }

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
    public FileImpl getCanonicalFile() throws IOException {
        return new FileImpl(getCanonicalPath());
    }

    /**
     * Static method to return the argument {@link File} as a
     * {@link FileImpl}.  A new {@link FileImpl} instance is created only
     * if the argument {@link File} is not an instance of {@link FileImpl}.
     *
     * @param   file    The {@link File}.
     *
     * @return  The argument {@link File} as a {@link FileImpl}.
     */
    public static FileImpl asFileImpl(File file) {
        FileImpl impl = null;

        if (file != null) {
            if (file instanceof FileImpl) {
                impl = (FileImpl) file;
            } else {
                impl = new FileImpl(file);
            }
        }

        return impl;
    }

    /**
     * @see #getChildFile(Iterable)
     */
    public static FileImpl getChildFile(File parent,
                                        Iterable<CharSequence> names) {
        FileImpl file = Directory.getChildDirectory(parent);
        Iterator<CharSequence> iterator = names.iterator();

        while (iterator.hasNext()) {
            CharSequence name = iterator.next();

            if (iterator.hasNext()) {
                file = Directory.getChildFile(file, name);
            } else {
                file = new FileImpl(file, name);
            }
        }

        return file;
    }

    /**
     * @see #getChildFile(CharSequence...)
     */
    public static FileImpl getChildFile(File parent, CharSequence... names) {
        return getChildFile(parent, Arrays.asList(names));
    }

    /**
     * @see #getNameBase()
     */
    public static String getNameBase(File file) {
        return (file != null) ? getNameBase(file.getName()) : null;
    }

    /**
     * @see #getNameBase()
     */
    public static String getNameBase(CharSequence name) {
        String base = null;

        if (name != null) {
            String string = name.toString();
            int index = string.lastIndexOf(DOT);

            if (index > 0) {
                base = string.substring(0, index);
            } else {
                base = string;
            }
        }

        return base;
    }

    /**
     * @see #getNameSuffix()
     */
    public static String getNameSuffix(File file) {
        return (file != null) ? getNameSuffix(file.getName()) : null;
    }

    /**
     * @see #getNameSuffix()
     */
    public static String getNameSuffix(CharSequence name) {
        String suffix = null;

        if (name != null) {
            String string = name.toString();
            int index = string.lastIndexOf(DOT);

            if (index > 0) {
                suffix = string.substring(index + DOT.length());
            }
        }

        return suffix;
    }
}
