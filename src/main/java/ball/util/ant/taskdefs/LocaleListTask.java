/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to list the available
 * {@link Locale}s.
 *
 * {@bean-info}
 *
 * @see Locale#getAvailableLocales()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("locale-list")
public class LocaleListTask extends Task {

    /**
     * Sole constructor.
     */
    public LocaleListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        try {
            for (Locale locale : Locale.getAvailableLocales()) {
                log(String.valueOf(locale));
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
