/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.list;

/**
 * {@link NetworkInterface} utility methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class NetworkInterfaceUtil {

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

    private NetworkInterfaceUtil() { }

    /**
     * Utility method to get all {@link InterfaceAddress}es as a
     * {@link List}.
     *
     * @return  The {@link List} of {@link InterfaceAddress}es.
     */
    public static List<InterfaceAddress> getInterfaceAddressList() throws SocketException {
        ArrayList<InterfaceAddress> list = new ArrayList<InterfaceAddress>();

        for (NetworkInterface ni :
                 list(NetworkInterface.getNetworkInterfaces())) {
            list.addAll(ni.getInterfaceAddresses());
        }

        return list;
    }

    /**
     * Utility method to get all interface {@link InetAddress}es as a
     * {@link List}.
     *
     * @return  The {@link List} of {@link InetAddress}es.
     */
    public static List<InetAddress> getInterfaceInetAddressList() throws SocketException {
        ArrayList<InetAddress> list = new ArrayList<InetAddress>();

        for (InterfaceAddress address : getInterfaceAddressList()) {
            list.add(address.getAddress());
        }

        return list;
    }

    private static byte[] bytes(Number... numbers) {
        byte[] bytes = new byte[numbers.length];

        for (int i = 0, n = bytes.length; i < n; i += 1) {
            bytes[i] = numbers[i].byteValue();
        }

        return bytes;
    }
}
