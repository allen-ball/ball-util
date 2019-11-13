/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.lang;

import ball.util.Parser;
import java.util.regex.Pattern;

/**
 * Provides Java literals as an {@link Enum} type.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public enum Literal implements Parser.Lexeme {
    FALSE, TRUE, NULL;

    private final Pattern pattern;

    private Literal() {
        pattern = Pattern.compile("\\b" + Pattern.quote(lexeme()) + "\\B");
    }

    /**
     * Method to get the lexeme associated with {@link.this}
     * {@link Literal}.
     *
     * @return  The lexeme (literal).
     */
    public String lexeme() { return name().toLowerCase(); }

    @Override
    public Pattern pattern() { return pattern; }
}
