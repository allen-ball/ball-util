/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.activation.ReaderWriterDataSource;
import iprotium.io.IOUtil;
import java.io.Writer;
import java.net.URI;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
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

    /**
     * Helper method to implement {@link Taglet} static
     * {@code register(Map)} method.
     *
     * @param   type            The {@link AbstractTaglet} implementation
     *                          {@link Class}.
     * @param   map             The javadoc {@link Taglet} {@link Map}.
     */
    protected static void register(Class<? extends AbstractTaglet> type,
                                   Map<String,Taglet> map) {
        try {
            AbstractTaglet value = type.newInstance();
            String key = value.getName();

            if (map.containsKey(key)) {
                map.remove(key);
            }

            map.put(key, value);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private final boolean isInlineTag;
    private final boolean inPackage;
    private final boolean inOverview;
    private final boolean inField;
    private final boolean inConstructor;
    private final boolean inMethod;
    private final boolean inType;
    protected final Document document;

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
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        throw new IllegalArgumentException(tag.position().toString());
    }

    @Override
    public TagletOutput getTagletOutput(Doc doc,
                                        TagletWriter writer) throws IllegalArgumentException {
        throw new IllegalArgumentException(doc.position().toString());
    }

    /**
     * Method to produce {@link Taglet} output.
     *
     * See {@link #getTagletOutput(Tag,TagletWriter)} and
     * {@link #getTagletOutput(Doc,TagletWriter)}.
     *
     * @param   writer          The {@link TagletWriter}.
     * @param   objects         The {@link Object}s to translate to output.
     *
     * @return  The {@link TagletOutput}.
     */
    protected TagletOutput output(TagletWriter writer, Object... objects) {
        ReaderWriterDataSource ds = new ReaderWriterDataSourceImpl();
        Writer out = null;

        try {
            out = ds.getWriter();

            for (Object object : objects) {
                if (object instanceof Node) {
                    Transformer transformer =
                        TransformerFactory.newInstance().newTransformer();

                    transformer.setOutputProperty(OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource((Node) object),
                                          new StreamResult(out));
                } else {
                    out.write(String.valueOf(object));
                }
            }

            out.flush();
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        } finally {
            IOUtil.close(out);
        }

        TagletOutput output = writer.getOutputInstance();

        output.setOutput(ds.toString());

        return output;
    }

    private class ReaderWriterDataSourceImpl extends ReaderWriterDataSource {
        public ReaderWriterDataSourceImpl() { super(null, APPLICATION_XML); }
    }
}
