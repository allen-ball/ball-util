/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;

/**
 * File {@link java.io.FileFilter} implementation.
 *
 * @see File#isFile()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
