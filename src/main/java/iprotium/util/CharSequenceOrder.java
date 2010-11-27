/*
 * $Id: CharSequenceOrder.java,v 1.2 2010-11-27 01:52:58 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

/**
 * Abstract {@link Order} base class for ordering {@link CharSequence}
 * objects.  This class is specifically designed to make comparison with
 * {@link java.util.Map} key-sets and {@link java.util.Set}s more convenient
 * but developers should still take care that only immutable {@link Object}s
 * are actually inserted into these sets.
 *
 * @see java.util.Map
 * @see java.util.Set
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public abstract class CharSequenceOrder extends Order<CharSequence> {

    /**
     * See {@link CharSequenceOrder.CaseSensitive}.
     */
    public static final CharSequenceOrder CASE_SENSITIVE = new CaseSensitive();

    /**
     * See {@link CharSequenceOrder.CaseInsensitive}.
     */
    public static final CharSequenceOrder CASE_INSENSITIVE =
        new CaseInsensitive();

    /**
     * Sole constructor.
     */
    public CharSequenceOrder() { super(); }

    @Override
    public int compare(CharSequence left, CharSequence right) {
        int difference = 0;

        if (difference == 0) {
            int n = Math.min(left.length(), right.length());

            for (int i = 0; i < n; i += 1) {
                difference = compare(left.charAt(i), right.charAt(i));

                if (difference != 0) {
                    break;
                }
            }
        }

        if (difference == 0) {
            difference = left.length() - right.length();
        }

        return difference;
    }

    /**
     * Character comparator.
     */
    protected abstract int compare(int left, int right);

    /**
     * Case sensitive order.
     */
    public static class CaseSensitive extends CharSequenceOrder {
        private static final long serialVersionUID = 7790334593633380123L;

        /**
         * Sole constructor.
         */
        public CaseSensitive() { super(); }

        @Override
        protected int compare(int left, int right) { return left - right; }
    }

    /**
     * Case insensitive order (see {@link String#CASE_INSENSITIVE_ORDER}).
     */
    public static class CaseInsensitive extends CharSequenceOrder {
        private static final long serialVersionUID = -8408416760429550565L;

        /**
         * Sole constructor.
         */
        public CaseInsensitive() { super(); }

        /**
         * @see Character#toUpperCase(int)
         */
        @Override
        protected int compare(int left, int right) {
            return Character.toUpperCase(left) - Character.toUpperCase(right);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
