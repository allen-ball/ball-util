package ball.activation;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import javax.activation.DataSource;

/**
 * {@link DataSource} default method implementations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public interface DataSourceDefaultMethods extends DataSource {

    /**
     * {@link.rfc 2045} {@value #CONTENT_TYPE}
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * {@link.rfc 2045} {@value #APPLICATION_OCTET_STREAM}
     */
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";

    /**
     * {@link.rfc 3023} {@value #APPLICATION_XML}
     */
    public static final String APPLICATION_XML = "application/xml";

    /**
     * {@link.rfc 2045} {@value #TEXT_PLAIN}
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * {@link.rfc 2045} {@value #TEXT_HTML}
     */
    public static final String TEXT_HTML = "text/html";

    default void setName(String name) {
        throw new UnsupportedOperationException();
    }

    default void setContentType(String type) {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    @Override
    default InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    @Override
    default OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Method to clear the {@link DataSource} and discard any input on any
     * open {@link #getOutputStream()}.
     *
     * @throws  UnsupportedOperationException
     *                          If {@link #getOutputStream()} throws
     *                          {@link UnsupportedOperationException}.
     */
    default void clear() {
        try {
            getOutputStream().close();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * Method to get the number of bytes stored in {@link.this}
     * {@link DataSource}.  Default implementation returns {@code -1}.
     *
     * @return  The number of bytes stored in {@link.this}
     *          {@link DataSource}; {@code -1} if the count is unknown.
     */
    default long length() { return -1; }

    /**
     * Method to "wrap" the {@link InputStream} returned by
     * {@link #getInputStream()}into {@link InputStream} instances.
     *
     * @param   types           The {@link InputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link InputStream}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    default InputStream getInputStream(Class<?>... types) throws IOException {
        return wrap(getInputStream(), types);
    }

    /**
     * Method to "wrap" the {@link OutputStream} returned by
     * {@link #getOutputStream()}into {@link OutputStream} instances.
     *
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    default OutputStream getOutputStream(Class<?>... types) throws IOException {
        return wrap(getOutputStream(), types);
    }

    /**
     * Method to "wrap" an {@link InputStream} into {@link InputStream}
     * instances.
     *
     * @param   in              The {@link InputStream}.
     * @param   types           The {@link InputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link InputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    default InputStream wrap(InputStream in, Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                in =
                    type.asSubclass(InputStream.class)
                    .getConstructor(InputStream.class)
                    .newInstance(in);
            }
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }

        return in;
    }

    /**
     * Method to "wrap" an {@link OutputStream} into {@link OutputStream}
     * instances.
     *
     * @param   out             The {@link OutputStream}.
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    default OutputStream wrap(OutputStream out, Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                out =
                    type.asSubclass(OutputStream.class)
                    .getConstructor(OutputStream.class)
                    .newInstance(out);
            }
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }

        return out;
    }
}
