/*
 * $Id: AbstractFilenameFilter.java,v 1.2 2009-11-21 21:42:30 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Abstract {@link FilenameFilter} base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFilenameFilter implements FilenameFilter {

    /**
     * Sole constructor.
     */
    protected AbstractFilenameFilter() { }

    /**
     * Method to wrap this {@link FilenameFilter} in a {@link FileFilter}
     * implementation.
     *
     * @return  The {@link FileFilter} implementation.
     */
    public FileFilter asFileFilter() { return new FileFilterImpl(this); }

    /**
     * Method to list the matching names with this {@link FilenameFilter}.
     *
     * @param   parents         The parent {@link File}s to list.
     *
     * @return  An array of matching names ({@link String}s).
     *
     * @see File#list(FilenameFilter)
     */
    public String[] list(File... parents) {
        HashSet<String> set = new HashSet<String>();

        for (File parent : parents) {
            if (parent != null) {
                String[] names = parent.list(this);

                if (names != null) {
                    set.addAll(Arrays.asList(names));
                }
            }
        }

        return set.toArray(new String[] { });
    }

    public abstract boolean accept(File parent, String name);

    private class FileFilterImpl extends AbstractFileFilter {
        private final FilenameFilter filter;

        public FileFilterImpl(FilenameFilter filter) {
            super();

            this.filter = filter;
        }

        @Override
        public boolean accept(File file) {
            return filter.accept(file.getParentFile(), file.getName());
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */