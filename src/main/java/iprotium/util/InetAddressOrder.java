/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.util.ComparableUtil;
import java.net.InetAddress;

/**
 * Abstract {@link Order} base class for ordering {@link InetAddress} objects.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class InetAddressOrder extends Order.NonNull<InetAddress> {

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
        private static final long serialVersionUID = -4139066099040817571L;

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
