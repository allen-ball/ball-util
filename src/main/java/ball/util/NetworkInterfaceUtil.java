/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class NetworkInterfaceUtil {
    private NetworkInterfaceUtil() { }

    /**
     * Utility method to get all {@link InterfaceAddress}es as a
     * {@link List}.
     *
     * @return  The {@link List} of {@link InterfaceAddress}es.
     */
    public static List<InterfaceAddress> getInterfaceAddressList() throws SocketException {
        ArrayList<InterfaceAddress> list = new ArrayList<>();

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
        ArrayList<InetAddress> list = new ArrayList<>();

        for (InterfaceAddress address : getInterfaceAddressList()) {
            list.add(address.getAddress());
        }

        return list;
    }
}
