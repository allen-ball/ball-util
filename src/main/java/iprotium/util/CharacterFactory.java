/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Character} {@link Factory} implementation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class CharacterFactory extends Factory<Character> {
    private static final long serialVersionUID = -5999396646798789571L;

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
