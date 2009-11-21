/*
 * $Id: AbstractFileFilter.java,v 1.2 2009-11-21 21:42:30 ball Exp $
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
 * Abstract {@link FileFilter} base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class AbstractFileFilter implements FileFilter {

    /**
     * Sole constructor.
     */
    protected AbstractFileFilter() { }

    /**
     * Method to wrap this {@link FileFilter} in a {@link FilenameFilter}
     * implementation.
     *
     * @return  The {@link FilenameFilter} implementation.
     */
    public FilenameFilter asFilenameFilter() {
        return new FilenameFilterImpl(this);
    }

    /**
     * Method to list the matching {@link File}s with this
     * {@link FileFilter}.
     *
     * @param   parents         The parent {@link File}s to list.
     *
     * @return  An array of matching {@link File}s.
     *
     * @see File#listFiles(FileFilter)
     */
    public File[] list(File... parents) {
        HashSet<File> set = new HashSet<File>();

        for (File parent : parents) {
            if (parent != null) {
                File[] files = parent.listFiles(this);

                if (files != null) {
                    set.addAll(Arrays.asList(files));
                }
            }
        }

        return set.toArray(new File[] { });
    }

    public abstract boolean accept(File file);

    private class FilenameFilterImpl extends AbstractFilenameFilter {
        private final FileFilter filter;

        public FilenameFilterImpl(FileFilter filter) {
            super();

            this.filter = filter;
        }

        @Override
        public boolean accept(File parent, String name) {
            return filter.accept(new File(parent, name));
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
