/*
 * $Id: LocaleListTask.java,v 1.1 2009-09-04 16:56:31 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.ant.taskdefs;

import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant {@link Task} to list the available {@link Locale}s.
 *
 * @see Locale#getAvailableLocales()
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
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
/*
 * $Log: not supported by cvs2svn $
 */
