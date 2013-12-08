/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import iprotium.activation.ReaderWriterDataSource;
import iprotium.io.IOUtil;
import java.io.Writer;
import java.net.URI;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import static iprotium.util.StringUtil.isNil;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

/**
 * Abstract {@link Taglet} base class.
 *
 * <p>Note: {@link #getName()} implementation requires the subclass is
 * annotated with {@link TagletName}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractTaglet implements Taglet {
    private final boolean isInlineTag;
    private final boolean inPackage;
    private final boolean inOverview;
    private final boolean inField;
    private final boolean inConstructor;
    private final boolean inMethod;
    private final boolean inType;
    private final Document document;

    /**
     * Sole constructor.
     *
     * @param   isInlineTag     See {@link #isInlineTag()}.
     * @param   inPackage       See {@link #inPackage()}.
     * @param   inOverview      See {@link #inOverview()}.
     * @param   inField         See {@link #inField()}.
     * @param   inConstructor   See {@link #inConstructor()}.
     * @param   inMethod        See {@link #inMethod()}.
     * @param   inType          See {@link #inType()}.
     */
    protected AbstractTaglet(boolean isInlineTag, boolean inPackage,
                             boolean inOverview, boolean inField,
                             boolean inConstructor, boolean inMethod,
                             boolean inType) {
        this.isInlineTag = isInlineTag;
        this.inPackage = inPackage;
        this.inOverview = inOverview;
        this.inField = inField;
        this.inConstructor = inConstructor;
        this.inMethod = inMethod;
        this.inType = inType;
    }

    {
        try {
            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            document = builder.newDocument();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    @Override
    public String getName() {
        String name = null;
        TagletName annotation = getClass().getAnnotation(TagletName.class);

        if (annotation != null) {
            name = annotation.value();
        }

        if (name == null) {
            name = getClass().getSimpleName().toLowerCase();
        }

        return name;
    }

    @Override
    public boolean isInlineTag() { return isInlineTag; }

    @Override
    public boolean inPackage() { return inPackage; }

    @Override
    public boolean inOverview() { return inOverview; }

    @Override
    public boolean inField() { return inField; }

    @Override
    public boolean inConstructor() { return inConstructor; }

    @Override
    public boolean inMethod() { return inMethod; }

    @Override
    public boolean inType() { return inType; }

    @Override
    public String toString(Tag[] tags) { return null; }

    @Override
    public String toString(Tag tag) { return null; }

    /**
     * Method to create an HTML "a" {@link Element}.
     *
     * @param   text            The {@link Element} text.
     * @param   href            The href {@link org.w3c.dom.Attribute}
     *                          value.
     *
     * @return  The HTML "a" {@link Element}.
     */
    protected Element a(String text, URI href) {
        Element a = document.createElement("a");

        a.setAttribute("href", href.toASCIIString());
        a.setTextContent((! isNil(text)) ? text : href.toString());

        return a;
    }

    /**
     * Method to convert a {@link Node} to a {@link String}.
     *
     * @param   node            The {@link Node}.
     *
     * @return  The {@link String} representation of the {@link Node}.
     */
    protected String toString(Node node) {
        ReaderWriterDataSource ds = new ReaderWriterDataSourceImpl();
        Writer writer = null;

        try {
            writer = ds.getWriter();

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();

            transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node),
                                  new StreamResult(writer));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            IOUtil.close(writer);
        }

        return ds.toString();
    }

    private class ReaderWriterDataSourceImpl extends ReaderWriterDataSource {
        public ReaderWriterDataSourceImpl() { super(null, APPLICATION_XML); }
    }
}
