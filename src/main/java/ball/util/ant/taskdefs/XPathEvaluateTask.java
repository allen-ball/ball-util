package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import ball.xml.XalanConstants;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
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

/**
 * {@link.uri http://ant.apache.org/ Ant} {@link Task} to test
 * {@link javax.xml.xpath.XPath} expressions.
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
                                          ConfigurableAntTask, XalanConstants {
    @Getter @Setter @Accessors(chain = true, fluent = true)
    private ClasspathUtils.Delegate delegate = null;
    @NotNull @Getter @Setter
    private File file = null;
    @NotNull @Getter @Setter
    private String expression = null;
    @NotNull @Getter
    private QName qname = XPathConstants.STRING;
    @NotNull @Getter @Setter
    private int tab = 2;

    public void setQname(String name) throws IllegalArgumentException {
        try {
            qname =
                (QName)
                XPathConstants.class.getField(name.toUpperCase())
                .get(null);
        } catch (Exception exception) {
            qname = QName.valueOf(name);
        }
    }

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

        try {
            log(String.valueOf(getFile()));

            Document document =
                DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(getFile());

            log(getExpression());

            XPathExpression expression =
                XPathFactory.newInstance().newXPath()
                .compile(getExpression());
            QName qname = getQname();
            Object value = expression.evaluate(document, qname);

            if (XPathConstants.NODESET.equals(qname)) {
                Transformer transformer =
                    TransformerFactory.newInstance().newTransformer();

                transformer.setOutputProperty(OMIT_XML_DECLARATION, YES);
                transformer.setOutputProperty(INDENT, (tab > 0) ? YES : NO);
                transformer.setOutputProperty(XALAN_INDENT_AMOUNT.toString(),
                                              String.valueOf(tab));

                NodeList nodeset = (NodeList) value;

                for (int i = 0; i < nodeset.getLength(); i += 1) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    transformer.transform(new DOMSource(nodeset.item(i)),
                                          new StreamResult(out));
                    log(out.toString("UTF-8"));
                }
            } else {
                log(String.valueOf(value));
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
