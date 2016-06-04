/*
 * $Id$
 *
 * Copyright 2013 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.util.ComparableUtil;
import java.net.InetAddress;

/**
 * Abstract {@link Order} base class for ordering {@link InetAddress} objects.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class InetAddressOrder extends Order.NonNull<InetAddress> {
    private static final long serialVersionUID = 1748825185989187065L;

    /**
     * See {@link InetAddress#getAddress()}.
     */
    public static final InetAddressOrder ADDRESS = new Address();

    /**
     * See {@link InetAddress#getHostName()}.
     */
    public static final InetAddressOrder NAME = new Name();

    /**
     * Sole constructor.
     */
    protected InetAddressOrder() { super(); }

    private static class Address extends InetAddressOrder {
        private static final long serialVersionUID = 7472215382401084893L;

        /**
         * Sole constructor.
         */
        public Address() { super(); }

        @Override
        public int compare(InetAddress left, InetAddress right) {
            int difference = super.compare(left, right);

            if (difference == 0 && left != null && right != null) {
                difference = compare(left.getAddress(), right.getAddress());
            }

            return difference;
        }

        private int compare(byte[] left, byte[] right) {
            int difference = left.length - right.length;

            for (int i = 0; difference == 0 && i < left.length; i += 1) {
                difference = left[i] - right[i];

                if (difference != 0) {
                    break;
                }
            }

            return difference;
        }

        private byte[] getAddress(InetAddress address) {
            return (address != null) ? address.getAddress() : null;
        }
    }

    private static class Name extends Address {
        private static final long serialVersionUID = 8692612779720319815L;

        /**
         * Sole constructor.
         */
        public Name() { super(); }

        @Override
        public int compare(InetAddress left, InetAddress right) {
            int difference =
                ComparableUtil.compare(getHostName(left), getHostName(right));

            if (difference == 0) {
                difference = super.compare(left, right);
            }

            return difference;
        }

        private String getHostName(InetAddress address) {
            return (address != null) ? address.getHostName() : null;
        }
    }
}
