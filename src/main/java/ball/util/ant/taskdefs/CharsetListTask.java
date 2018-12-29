/*
 * $Id$
 *
 * Copyright 2009 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.nio.charset.Charset;
import org.apache.tools.ant.BuildException;

import static ball.util.StringUtil.NIL;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to list the available {@link Charset}s.
 *
 * {@bean.info}
 *
 * @see Charset#availableCharsets()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("charset-list")
public class CharsetListTask extends AbstractClasspathTask {

    /**
     * Sole constructor.
     */
    public CharsetListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

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
}
