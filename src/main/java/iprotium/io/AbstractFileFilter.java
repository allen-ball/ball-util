/*
 * $Id: AbstractFileFilter.java,v 1.1 2009-11-14 07:25:47 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Abstract {@link FileFilter} base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
