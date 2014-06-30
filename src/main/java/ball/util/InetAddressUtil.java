/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.net.InetAddress;

/**
 * {@link InetAddress} utility methods.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class InetAddressUtil {
    private InetAddressUtil() { }

    /**
     * IPv4 localhost 127.0.0.1
     */
    public static final InetAddress LOCALHOST;

    static {
        try {
            LOCALHOST =
                InetAddress.getByAddress("localhost", bytes(127, 0, 0, 1));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to convert the argument {@link Number}s to a {@code byte}
     * array.
     *
     * @param   numbers         The {@link Number}s representing the byte
     *                          values.
     *
     * @return  The {@code byte} array.
     */
    public static byte[] bytes(Number... numbers) {
        byte[] bytes = new byte[numbers.length];

        for (int i = 0, n = bytes.length; i < n; i += 1) {
            bytes[i] = numbers[i].byteValue();
        }

        return bytes;
    }
}
