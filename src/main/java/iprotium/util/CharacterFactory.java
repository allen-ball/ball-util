/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Character} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class CharacterFactory extends Factory<Character> {
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
