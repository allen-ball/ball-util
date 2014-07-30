/*
 * $Id$
 *
 * Copyright 2011 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.net.InetAddress;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke {@link InetAddress#getAllByName(String)}.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("inet-addresses-for")
public class InetAddressesForTask extends Task {
    private String string = null;

    /**
     * Sole constructor.
     */
    public InetAddressesForTask() { super(); }

    @NotNull
    public String getString() { return string; }
    public void setString(String string) { this.string = string; }

    @Override
    public void execute() throws BuildException {
        try {
            log(Arrays.toString(InetAddress.getAllByName(getString())));
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
