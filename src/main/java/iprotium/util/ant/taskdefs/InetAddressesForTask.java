/*
 * $Id$
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.net.InetAddress;
import java.util.Arrays;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a>
 * {@link org.apache.tools.ant.Task} to invoke
 * {@link InetAddress#getAllByName(String)}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class InetAddressesForTask extends Task {
    private String string = null;

    /**
     * Sole constructor.
     */
    public InetAddressesForTask() { super(); }

    protected String getString() { return string; }
    public void setString(String string) { this.string = string; }

    @Override
    public void execute() throws BuildException {
        if (getString() == null) {
            throw new BuildException("`string' attribute must be specified");
        }

        try {
            log(Arrays.toString(InetAddress.getAllByName(getString())));
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new BuildException(throwable);
        }
    }
}
