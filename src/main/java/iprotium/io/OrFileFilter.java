/*
 * $Id: OrFileFilter.java,v 1.1 2009-11-16 03:48:25 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Logical "or" {@link java.io.FileFilter} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class OrFileFilter extends AbstractFileFilter {
    private final List<FileFilter> list = new ArrayList<FileFilter>();

    /**
     * @param   collection      The {@link Collection} of
     *                          {@link FileFilter}s.
     */
    public OrFileFilter(Collection<FileFilter> collection) {
        super();

        list.addAll(collection);
    }

    /**
     * @param   filters         The {@link FileFilter}s.
     */
    public OrFileFilter(FileFilter... filters) {
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
        boolean accepted = list().isEmpty();

        for (FileFilter filter : list()) {
            accepted |= filter.accept(file);

            if (accepted) {
                break;
            }
        }

        return accepted;
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
