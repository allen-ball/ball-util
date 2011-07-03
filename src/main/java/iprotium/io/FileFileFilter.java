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
 * @see File#isFile()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class FileFileFilter extends AbstractFileFilter {

    /**
     * Public static instance of {@link FileFileFilter}.
     */
    public static final FileFileFilter INSTANCE = new FileFileFilter();

    /**
     * Sole constructor.
     */
    public FileFileFilter() { super(); }

    @Override
    public boolean accept(File file) { return file.isFile(); }
}
