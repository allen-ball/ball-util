/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.io;

import java.io.File;
import java.util.regex.Pattern;

/**
 * {@link Pattern} {@link java.io.FilenameFilter} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class PatternFilenameFilter extends AbstractFilenameFilter {
    private final Pattern pattern;

    /**
     * Construct a {@link java.io.FilenameFilter} from a {@link Pattern}.
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

    /**
     * Construct a {@link java.io.FilenameFilter} from a regular expression
     * ({@link String}).
     *
     * @param   string          The {@link String} specifying the regular
     *                          expression that {@link File} names should
     *                          match.
     */
    public PatternFilenameFilter(String string) {
        this(Pattern.compile(string));
    }

    @Override
    public boolean accept(File parent, String name) {
        return pattern.matcher(name).matches();
    }
}
