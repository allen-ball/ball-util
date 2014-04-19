/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke {@link NetworkInterface#getNetworkInterfaces()}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("network-interfaces")
public class NetworkInterfacesTask extends Task {

    /**
     * Sole constructor.
     */
    public NetworkInterfacesTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            List<NetworkInterface> list =
                Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface ni : list) {
                log(String.valueOf(ni));
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }

    @Override
    public String toString() { return getClass().getSimpleName(); }
}
