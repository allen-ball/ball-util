/*
 * $Id: CharsetListTask.java,v 1.1 2009-09-04 16:56:31 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.nio.charset.Charset;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant {@link Task} to list the available {@link Charset}s.
 *
 * @see Charset#availableCharsets()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
