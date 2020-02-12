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
import java.io.PrintWriter;

/**
 * {@link Throwable} {@link ReaderWriterDataSource}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
