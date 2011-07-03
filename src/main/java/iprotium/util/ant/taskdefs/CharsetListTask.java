/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.nio.charset.Charset;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a> {@link Task} to list the
 * available {@link Charset}s.
 *
 * @see Charset#availableCharsets()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class CharsetListTask extends Task {

    /**
     * Sole constructor.
     */
    public CharsetListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            log("");

            for (String key : Charset.availableCharsets().keySet()) {
                log(key);
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
