package ball.util.ant.taskdefs;
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
import ball.activation.ReaderWriterDataSource;
import java.io.File;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.ClasspathUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static javax.xml.xpath.XPathConstants.NODESET;

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to test {@link XPath}
 * expressions.
 *
 * {@ant.task}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@AntTask("xpath-evaluate")
@NoArgsConstructor @ToString
public class XPathEvaluateTask extends Task
                               implements AnnotatedAntTask,
                                          ClasspathDelegateAntTask,
                                          ConfigurableAntTask {
    private static final String NO = "no";
    private static final String YES = "yes";

    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";

    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @NotNull @Getter @Setter
    private File file = null;
    @NotNull @Getter @Setter
    private String expression = null;

    @Override
    public void init() throws BuildException {
        super.init();
        ClasspathDelegateAntTask.super.init();
        ConfigurableAntTask.super.init();
    }

    @Override
    public void execute() throws BuildException {
        super.execute();
        AnnotatedAntTask.super.execute();

        log(String.valueOf(getFile()));
        log(getExpression());

        try {
            Document document =
                DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(getFile());
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression expression = xpath.compile(getExpression());
            NodeList set = (NodeList) expression.evaluate(document, NODESET);
            Transformer transformer =
                TransformerFactory.newInstance().newTransformer();

            transformer.setOutputProperty(OMIT_XML_DECLARATION, YES);
            transformer.setOutputProperty(INDENT, YES);
            transformer.setOutputProperty(INDENT_AMOUNT, String.valueOf(2));

            for (int i = 0; i < set.getLength(); i += 1) {
                ReaderWriterDataSource ds =
                    new ReaderWriterDataSource(null, null);

                try (OutputStream out = ds.getOutputStream()) {
                    transformer.transform(new DOMSource(set.item(i)),
                                          new StreamResult(out));
                }

                log();
                log(ds.getBufferedReader().lines());
            }
        } catch (BuildException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
