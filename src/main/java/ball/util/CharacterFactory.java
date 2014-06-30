/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

/**
 * {@link Character} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class CharacterFactory extends Factory<Character> {
    private static final long serialVersionUID = -3275175257378762737L;

    private static final CharacterFactory DEFAULT = new CharacterFactory();

    /**
     * {@link CharacterFactory} factory method.
     */
    public static CharacterFactory getDefault() { return DEFAULT; }

    /**
     * Sole constructor.
     *
     * @see #getDefault()
     */
    protected CharacterFactory() { super(Character.class); }
}
