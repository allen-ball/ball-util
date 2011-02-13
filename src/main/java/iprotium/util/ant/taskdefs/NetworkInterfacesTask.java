/*
 * $Id: NetworkInterfacesTask.java,v 1.1 2011-02-13 02:44:52 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to invoke
 * {@link NetworkInterface#getNetworkInterfaces()}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
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
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new BuildException(throwable);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
