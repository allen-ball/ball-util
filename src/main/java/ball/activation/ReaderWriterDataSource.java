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
import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link javax.activation.DataSource} implementation that provides a
 * {@link BufferedReader} wrapping the {@link javax.activation.DataSource}
 * {@link java.io.InputStream} and a {@link PrintWriter} wrapping the
 * {@link javax.activation.DataSource} {@link java.io.OutputStream}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public class ReaderWriterDataSource extends FilterDataSource {
    protected static final Charset CHARSET = UTF_8;

    private final Charset charset;

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    @ConstructorProperties({ "name", "contentType" })
    public ReaderWriterDataSource(String name, String type) {
        this(name, type, null);
    }

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     * @param   charset         The {@link Charset} used to encode the
     *                          {@link java.io.OutputStream}.
     */
    @ConstructorProperties({ "name", "contentType", "charset" })
    public ReaderWriterDataSource(String name, String type, Charset charset) {
        this(name, type, charset, null);
    }

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     * @param   charset         The {@link Charset} used to encode the
     *                          {@link java.io.OutputStream}.
     * @param   content         The initial content {@link String}.
     */
    @ConstructorProperties({ "name", "contentType", "charset", EMPTY })
    public ReaderWriterDataSource(String name, String type, Charset charset, String content) {
        super(new ByteArrayDataSource(name, type));

        this.charset = (charset != null) ? charset : CHARSET;

        if (content != null) {
            try (Writer writer = getWriter()) {
                writer.write(content);
            } catch (IOException exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }
    }

    /**
     * Private no-argument constructor (for JAXB annotated subclasses).
     */
    private ReaderWriterDataSource() { this(null, null); }

    /**
     * Method to get the {@link Charset} used to create the
     * {@link BufferedReader} and {@link PrintWriter}.
     *
     * @return  The Charset.
     */
    public Charset getCharset() { return charset; }

    /**
     * Method to return a new {@link Reader} to read the underlying
     * {@link java.io.InputStream}.
     *
     * @see #getInputStream()
     *
     * @return  A {@link Reader} wrapping the
     *          {@link javax.activation.DataSource}
     *          {@link java.io.InputStream}.
     *
     * @throws  IOException     If an I/O exception occurs.
     */
    public Reader getReader() throws IOException {
        return new InputStreamReader(getInputStream(), getCharset());
    }

    /**
     * Method to return a new {@link Writer} to write to the underlying
     * {@link java.io.OutputStream}.
     *
     * @see #getOutputStream()
     *
     * @return  A {@link Writer} wrapping the
     *          {@link javax.activation.DataSource}
     *          {@link java.io.OutputStream}.
     *
     * @throws  IOException     If an I/O exception occurs.
     */
    public Writer getWriter() throws IOException {
        return new OutputStreamWriter(getOutputStream(), getCharset());
    }

    /**
     * Method to return a new {@link BufferedReader} to read the underlying
     * {@link java.io.InputStream}.
     *
     * @see #getInputStream()
     *
     * @return  A {@link BufferedReader} wrapping the
     *          {@link javax.activation.DataSource}
     *          {@link java.io.InputStream}.
     *
     * @throws  IOException     If an I/O exception occurs.
     */
    public BufferedReader getBufferedReader() throws IOException {
        return new BufferedReader(getReader());
    }

    /**
     * Method to return a new {@link PrintWriter} to write to the underlying
     * {@link java.io.OutputStream}.
     *
     * @see #getOutputStream()
     *
     * @return  A {@link PrintWriter} wrapping the
     *          {@link javax.activation.DataSource}
     *          {@link java.io.OutputStream}.
     *
     * @throws  IOException     If an I/O exception occurs.
     */
    public PrintWriter getPrintWriter() throws IOException {
        return new PrintWriter(getWriter(), true);
    }

    /**
     * Method to return a new {@link PrintStream} to write to the underlying
     * {@link java.io.OutputStream}.
     *
     * @see #getOutputStream()
     *
     * @return  A {@link PrintStream} wrapping the
     *          {@link javax.activation.DataSource}
     *          {@link java.io.OutputStream}.
     *
     * @throws  IOException     If an I/O exception occurs.
     */
    public PrintStream getPrintStream() throws IOException {
        return new PrintStream(getOutputStream(), true, getCharset().name());
    }

    /**
     * Method to write the contents of this
     * {@link javax.activation.DataSource} to a {@link PrintWriter}.
     *
     * @see #getBufferedReader()
     *
     * @param   writer          The target {@link PrintWriter}.
     *
     * @throws  IOException     If a problem is encountered opening or
     *                          reading the {@link BufferedReader} or
     *                          writing to the {@link PrintWriter}.
     */
    public void writeTo(PrintWriter writer) throws IOException {
        getBufferedReader().lines().forEach(t -> writer.println(t));
    }

    @Override
    public String toString() {
        String string = null;

        try (BufferedReader reader = getBufferedReader()) {
            string = reader.lines().collect(joining("\n"));
        } catch (IOException exception) {
            string = super.toString();
        }

        return string;
    }

    /**
     * Convenience method to get the name of a {@link Charset}.
     *
     * @param   charset         The {@link Charset}.
     *
     * @return  The name of the {@link Charset} if non-{@code null};
     *          {@code null} otherwise.
     *
     * @see Charset#name()
     */
    protected static String nameOf(Charset charset) {
        return (charset != null) ? charset.name() : null;
    }
}
