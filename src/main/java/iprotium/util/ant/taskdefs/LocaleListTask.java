/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * <a href="http://ant.apache.org/">Ant</a> {@link Task} to list the
 * available {@link Locale}s.
 *
 * @see Locale#getAvailableLocales()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
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
        } catch (RuntimeException exception) {
            exception.printStackTrace();
            throw exception;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BuildException(exception);
        }
    }
}
