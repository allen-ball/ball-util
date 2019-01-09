/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.net.InetAddress;
import java.util.Arrays;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to invoke {@link InetAddress#getAllByName(String)}.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("inet-addresses-for")
@NoArgsConstructor @ToString
public class InetAddressesForTask extends AbstractClasspathTask {
    @NotNull @Getter @Setter
    private String string = null;

    @Override
    public void execute() throws BuildException {
        super.execute();

        try {
            log(Arrays.toString(InetAddress.getAllByName(getString())));
        } catch (BuildException exception) {
            throw exception;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new BuildException(throwable);
        }
    }
}
