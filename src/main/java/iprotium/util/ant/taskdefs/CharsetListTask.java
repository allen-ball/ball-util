/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import iprotium.annotation.AntTask;
import java.nio.charset.Charset;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import static iprotium.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to list the available
 * {@link Charset}s.
 *
 * {@bean-info}
 *
 * @see Charset#availableCharsets()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("charset-list")
public class CharsetListTask extends Task {

    /**
     * Sole constructor.
     */
    public CharsetListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            log(NIL);

            for (String key : Charset.availableCharsets().keySet()) {
                log(key);
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
