/*
 * $Id: PatternFilenameFilter.java,v 1.1 2009-11-15 04:41:37 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.util.regex.Pattern;

/**
 * {@link Pattern} {@link java.io.FilenameFilter} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class PatternFilenameFilter extends AbstractFilenameFilter {
    private final Pattern pattern;

    /**
     * Sole constructor.
     *
     * @param   pattern         The {@link Pattern} specifying the
     *                          {@link File} names to match.
     */
    public PatternFilenameFilter(Pattern pattern) {
        super();

        if (pattern != null) {
            this.pattern = pattern;
        } else {
            throw new NullPointerException("pattern");
        }
    }

    @Override
    public boolean accept(File parent, String name) {
        return pattern.matcher(name).matches();
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
