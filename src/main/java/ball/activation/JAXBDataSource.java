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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * {@link ReaderWriterDataSource} implementation to provide
 * {@link Marshaller} marshalling and {@link Unmarshaller} unmarshalling
 * services.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
public class JAXBDataSource extends ReaderWriterDataSource {

    /**
     * Construct and marshal the argument {@link Object}.
     *
     * @param   object          The {@link Object} to marshal.
     */
    public JAXBDataSource(Object object) {
        this();

        try {
            marshal(object);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * No-argument constructor.
     */
    public JAXBDataSource() { super(null, APPLICATION_XML); }

    /**
     * Marshal the argument {@link Object}.
     *
     * @param   object          The {@link Object} to marshal.
     *
     * @throws  IOException     If an I/O exception occurs.
     * @throws  JAXBException   If a JAXB exception occurs.
     */
    public void marshal(Object object) throws IOException, JAXBException {
        marshal(JAXBContext.newInstance(object.getClass()), object);
    }

    /**
     * Marshal the argument {@link Object}.
     *
     * @param   context         The {@link JAXBContext}.
     * @param   object          The {@link Object} to marshal.
     *
     * @throws  IOException     If an I/O exception occurs.
     * @throws  JAXBException   If a JAXB exception occurs.
     */
    public void marshal(JAXBContext context, Object object) throws IOException, JAXBException {
        try (OutputStream out = getOutputStream()) {
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty("jaxb.encoding", getCharset().name());
            marshaller.setProperty("jaxb.formatted.output", true);
            marshaller.marshal(object, out);
        }
    }

    /**
     * Unmarshal the argument {@link Object}.
     *
     * @param   <T>             The target type.
     * @param   type            The target {@link Class}.
     *
     * @return  The unmarshalled {@link Object}.
     *
     * @throws  IOException     If an I/O exception occurs.
     * @throws  JAXBException   If a JAXB exception occurs.
     */
    public <T> T unmarshal(Class<? extends T> type) throws IOException, JAXBException {
        return unmarshal(JAXBContext.newInstance(type), type);
    }

    /**
     * Unmarshal the argument {@link Object}.
     *
     * @param   <T>             The target type.
     * @param   context         The {@link JAXBContext}.
     * @param   type            The target {@link Class}.
     *
     * @return  The unmarshalled {@link Object}.
     *
     * @throws  IOException     If an I/O exception occurs.
     * @throws  JAXBException   If a JAXB exception occurs.
     */
    public <T> T unmarshal(JAXBContext context, Class<? extends T> type) throws IOException, JAXBException {
        T object = null;

        try (InputStream in = getInputStream()) {
            Unmarshaller unmarshaller = context.createUnmarshaller();

            object = type.cast(unmarshaller.unmarshal(in));
        }

        return object;
    }
}
