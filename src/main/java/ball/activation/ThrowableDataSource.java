/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import ball.io.IOUtil;
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

        PrintWriter out = null;

        try {
            out = getPrintWriter();
            throwable.printStackTrace(out);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        } finally {
            IOUtil.close(out);
        }
    }
}
