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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * {@link javax.activation.DataSource} backed by a temporary {@link File}
 * and based on {@link FileInputStream} and {@link FileOutputStream}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
public class TempFileDataSource extends AbstractDataSource {
    private final String prefix;
    private final String suffix;
    private final File parent;
    private volatile File file = null;

    /**
     * @param   prefix          The file name prefix.
     * @param   suffix          The file name suffix.
     * @param   parent          The parent {@link File}.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     *
     * @see File#createTempFile(String,String,File)
     */
    @ConstructorProperties({ EMPTY, EMPTY, EMPTY, "contentType" })
    public TempFileDataSource(String prefix, String suffix, File parent,
                              String type) {
        super();

        if (prefix != null) {
            this.prefix = prefix;
        } else {
            this.prefix = TempFileDataSource.class.getSimpleName();
        }

        this.suffix = suffix;
        this.parent = parent;

        if (type != null) {
            setContentType(type);
        }
    }

    /**
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    @ConstructorProperties({ "contentType" })
    public TempFileDataSource(String type) { this(null, null, null, type); }

    {
        try {
            clear();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public long length() { return file.length(); }

    @Override
    public FileInputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public FileOutputStream getOutputStream() throws IOException {
        FileOutputStream out = null;
        File old = file;

        if (old != null) {
            old.delete();
        }

        file = File.createTempFile(prefix, suffix, parent);
        out = new FileOutputStream(file);

        return out;
    }

    @Override
    protected void finalize() throws Throwable {
        if (file != null) {
            file.delete();
        }

        super.finalize();
    }
}
