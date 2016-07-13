/*
 * $Id$
 *
 * Copyright 2010 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class CharSequenceOrder extends Order<CharSequence> {
    private static final long serialVersionUID = 2370157830215855054L;

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
     *
     * @param   left            The "left" value to compare.
     * @param   right           The "right" value to compare.
     *
     * @return  A value less than zero if {@code left} is ordered before
     *          {@code right}, a value greater than zero if {@code left} is
     *          ordered after {@code right}, or zero if {@code left} is
     *          interchangeable with {@code right} in the order.
     */
    protected abstract int compare(int left, int right);

    /**
     * Case sensitive order.
     */
    public static class CaseSensitive extends CharSequenceOrder {
        private static final long serialVersionUID = -2015623274353216264L;

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
        private static final long serialVersionUID = 7893403667961310779L;

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
