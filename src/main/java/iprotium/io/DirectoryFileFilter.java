/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;

/**
 * File {@link java.io.FileFilter} implementation.
 *
 * @see File#isDirectory()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class DirectoryFileFilter extends AbstractFileFilter {

    /**
     * Public static instance of {@link DirectoryFileFilter}.
     */
    public static final DirectoryFileFilter INSTANCE =
        new DirectoryFileFilter();

    /**
     * Sole constructor.
     */
    public DirectoryFileFilter() { super(); }

    @Override
    public boolean accept(File file) { return file.isDirectory(); }
}
