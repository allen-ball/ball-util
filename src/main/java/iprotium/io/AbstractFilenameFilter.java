/*
 * $Id: AbstractFilenameFilter.java,v 1.1 2009-11-14 07:25:47 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Abstract {@link FilenameFilter} base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
