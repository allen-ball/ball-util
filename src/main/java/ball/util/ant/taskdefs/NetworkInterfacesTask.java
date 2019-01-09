/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke {@link NetworkInterface#getNetworkInterfaces()}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("network-interfaces")
@NoArgsConstructor @ToString
public class NetworkInterfacesTask extends AbstractClasspathTask {
    @Override
    public void execute() throws BuildException {
        super.execute();

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
}
