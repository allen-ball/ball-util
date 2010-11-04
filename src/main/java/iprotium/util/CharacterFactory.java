/*
 * $Id: CharacterFactory.java,v 1.1 2010-11-04 02:40:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * {@link Character} {@link Factory} implementation.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
