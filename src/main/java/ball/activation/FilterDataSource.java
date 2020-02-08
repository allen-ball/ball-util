package ball.activation;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.activation.DataSource;

/**
 * Abstract {@link DataSource} base class that wraps another
 * {@link DataSource}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class FilterDataSource extends AbstractDataSource {
    private final DataSource ds;

    /**
     * @param   ds              The filtered {@link DataSource}.
     */
    @ConstructorProperties({ "dataSource" })
    protected FilterDataSource(DataSource ds) {
        super();

        this.ds = Objects.requireNonNull(ds);
    }

    /**
     * Private no-argument constructor (for JAXB annotated subclasses).
     */
    private FilterDataSource() { this(new ByteArrayDataSource(null, null)); }

    /**
     * Method to get the filtered {@link DataSource}.
     *
     * @return  The filtered {@link DataSource}.
     */
    protected DataSource getDataSource() { return ds; }

    @Override
    public String getName() { return getDataSource().getName(); }

    @Override
    public void setName(String name) {
        try {
            DataSource ds = getDataSource();

            ds.getClass()
                .getMethod("setName", String.class)
                .invoke(ds, name);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public String getContentType() { return getDataSource().getContentType(); }

    @Override
    public void setContentType(String type) {
        try {
            DataSource ds = getDataSource();

            ds.getClass()
                .getMethod("setContentType", String.class)
                .invoke(ds, type);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getDataSource().getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getDataSource().getOutputStream();
    }
}
