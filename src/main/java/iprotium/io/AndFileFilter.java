/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Logical "and" {@link java.io.FileFilter} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class AndFileFilter extends AbstractFileFilter {
    private final List<FileFilter> list = new ArrayList<FileFilter>();

    /**
     * @param   collection      The {@link Collection} of
     *                          {@link FileFilter}s.
     */
    public AndFileFilter(Collection<FileFilter> collection) {
        super();

        list.addAll(collection);
    }

    /**
     * @param   filters         The {@link FileFilter}s.
     */
    public AndFileFilter(FileFilter... filters) {
        this(Arrays.asList(filters));
    }

    /**
     * Method to get the underlying {@link FileFilter} {@link List}.
     *
     * @return  The underlying {@link FileFilter} {@link List}.
     */
    public List<FileFilter> list() { return list; }

    @Override
    public boolean accept(File file) {
        boolean accepted = true;

        for (FileFilter filter : list()) {
            accepted &= filter.accept(file);

            if (! accepted) {
                break;
            }
        }

        return accepted;
    }
}
