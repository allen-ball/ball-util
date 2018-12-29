/*
 * $Id$
 *
 * Copyright 2009 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.util.Locale;
import org.apache.tools.ant.BuildException;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * to list the available {@link Locale}s.
 *
 * {@bean.info}
 *
 * @see Locale#getAvailableLocales()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@AntTask("locale-list")
public class LocaleListTask extends AbstractClasspathTask {

    /**
     * Sole constructor.
     */
    public LocaleListTask() { super(); }

    @Override
    public void execute() throws BuildException {
        super.execute();

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
}
