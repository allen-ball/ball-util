/*
 * $Id: FileFileFilter.java,v 1.1 2009-11-15 04:07:48 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;

/**
 * File {@link java.io.FileFilter} implementation.
 *
 * @see File#isFile()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
