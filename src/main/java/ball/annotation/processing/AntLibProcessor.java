package ball.annotation.processing;
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
import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.AntLib;
import ball.util.ant.taskdefs.AntTask;
import ball.xml.FluentDocument;
import ball.xml.FluentDocumentBuilderFactory;
import ball.xml.XalanConstants;
import java.io.OutputStream;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Task;

import static java.lang.reflect.Modifier.isAbstract;
import static javax.tools.StandardLocation.CLASS_OUTPUT;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

/**
 * Generates {@code antlib.xml} (at location(s) specified by {@link AntLib})
 * at the end of annotation processing.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ AntLib.class, AntTask.class })
@NoArgsConstructor @ToString
public class AntLibProcessor extends AnnotatedProcessor
                             implements ClassFileProcessor, XalanConstants {
    private static final String ANTLIB_XML = "antlib.xml";

    private static final Transformer TRANSFORMER;

    static {
        try {
            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            TRANSFORMER.setOutputProperty(OMIT_XML_DECLARATION, NO);
            TRANSFORMER.setOutputProperty(INDENT, YES);
            TRANSFORMER.setOutputProperty(XALAN_INDENT_AMOUNT.toString(),
                                          String.valueOf(2));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public void process(Set<Class<?>> set,
                        JavaFileManager fm) throws Throwable {
        TreeSet<String> paths = new TreeSet<>();
        AntLibXML antlib = new AntLibXML();

        for (Class<?> type : set) {
            AntTask annotation = type.getAnnotation(AntTask.class);

            if (annotation != null) {
                if (Task.class.isAssignableFrom(type)) {
                    if (! isAbstract(type.getModifiers())) {
                        antlib.put(annotation.value(), type);
                    }
                }
            }

            AntLib lib = type.getAnnotation(AntLib.class);

            if (lib != null) {
                paths.add(type.getPackage().getName());
            }
        }

        for (String path : paths) {
            FileObject file =
                fm.getFileForOutput(CLASS_OUTPUT, path, ANTLIB_XML, null);

            try (OutputStream out = file.openOutputStream()) {
                antlib.writeTo(out);
            }
        }
    }

    @NoArgsConstructor
    private static class AntLibXML extends TreeMap<String,Class<?>> {
        private static final long serialVersionUID = -8903476717502118017L;

        public FluentDocument asDocument() throws Exception {
            FluentDocument d =
                FluentDocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();

            d.add(d.element("antlib",
                            entrySet().stream()
                            .map(t -> d.element("taskdef",
                                                d.attr("name", t.getKey()),
                                                d.attr("classname",
                                                       t.getValue().getName())))));

            return d;
        }

        public void writeTo(OutputStream out) throws Exception {
            TRANSFORMER.transform(new DOMSource(asDocument()),
                                  new StreamResult(out));
        }
    }
}
