/*
 * $Id$
 *
 * Copyright 2014 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import java.io.PrintWriter;

/**
 * {@link Throwable} {@link ReaderWriterDataSource}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ThrowableDataSource extends ReaderWriterDataSource {

    /**
     * Sole constructor.
     *
     * @param   throwable       The {@link Throwable}.
     */
    public ThrowableDataSource(Throwable throwable) {
        super(throwable.getClass().getSimpleName(), TEXT_PLAIN);

        try (PrintWriter out = getPrintWriter()) {
            throwable.printStackTrace(out);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }
}
