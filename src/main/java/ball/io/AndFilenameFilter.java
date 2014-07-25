/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Logical "and" {@link java.io.FilenameFilter} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class AndFilenameFilter extends AbstractFilenameFilter {
    private final List<FilenameFilter> list = new ArrayList<FilenameFilter>();

    /**
     * @param   collection      The {@link Collection} of
     *                          {@link FilenameFilter}s.
     */
    public AndFilenameFilter(Collection<FilenameFilter> collection) {
        super();

        list.addAll(collection);
    }

    /**
     * @param   filters         The {@link FilenameFilter}s.
     */
    public AndFilenameFilter(FilenameFilter... filters) {
        this(Arrays.asList(filters));
    }

    /**
     * Method to get the underlying {@link FilenameFilter} {@link List}.
     *
     * @return  The underlying {@link FilenameFilter} {@link List}.
     */
    public List<FilenameFilter> list() { return list; }

    @Override
    public boolean accept(File parent, String name) {
        boolean accepted = true;

        for (FilenameFilter filter : list()) {
            accepted &= filter.accept(parent, name);

            if (! accepted) {
                break;
            }
        }

        return accepted;
    }
}