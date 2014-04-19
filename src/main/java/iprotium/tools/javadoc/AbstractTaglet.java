/*
 * $Id$
 *
 * Copyright 2012 - 2014 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.activation.ReaderWriterDataSource;
import iprotium.io.IOUtil;
import iprotium.xml.HTML;
import java.io.Writer;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static iprotium.util.StringUtil.NIL;
import static iprotium.util.StringUtil.isNil;
import static javax.xml.transform.OutputKeys.INDENT;
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
    private static final String NO = "no";
    private static final String YES = "yes";

    private static final String DOLLAR = "$";

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
    private final Transformer transformer;
    private transient Configuration configuration = null;

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
        this.inPackage = isInlineTag | inPackage;
        this.inOverview = isInlineTag | inOverview;
        this.inField = isInlineTag | inField;
        this.inConstructor = isInlineTag | inConstructor;
        this.inMethod = isInlineTag | inMethod;
        this.inType = isInlineTag | inType;
    }

    {
        try {
            document =
                HTML.init(DocumentBuilderFactory.newInstance()
                          .newDocumentBuilder()
                          .newDocument());

            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(INDENT, YES);
            transformer.setOutputProperty(OMIT_XML_DECLARATION, YES);
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to set the {@link Configuration}.
     * See {@link TagletWriter#configuration()}.  Some methods'
     * functionality may depend on the availability of this value.
     *
     * @param   configuration           The {@link Configuration}.
     */
    protected void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
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
     * @param   iterable        The {@link Iterable} of {@link Object}s to
     *                          translate to output.
     *
     * @return  The {@link TagletOutput}.
     */
    protected TagletOutput output(TagletWriter writer, Iterable<?> iterable) {
        ReaderWriterDataSource ds = new ReaderWriterDataSource(null, null);
        Writer out = null;

        try {
            out = ds.getWriter();

            for (Object object : iterable) {
                if (object instanceof Node) {
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
        return output(writer, Arrays.asList(objects));
    }

    /**
     * Convenience method to get the containing {@link ClassDoc}.
     *
     * @param   doc             The {@link Doc}.
     *
     * @return  The containing {@link ClassDoc} or {@code null} if there is
     *          none.
     */
    protected ClassDoc getContainingClassDoc(Doc doc) {
        ClassDoc container = null;

        if (doc instanceof ClassDoc) {
            container = (ClassDoc) doc;
        } else if (doc instanceof ProgramElementDoc) {
            container =
                getContainingClassDoc(((ProgramElementDoc) doc)
                                      .containingClass());
        }

        return container;
    }

    /**
     * Convenience method to attempt to find a {@link ClassDoc}.
     *
     * @param   context         The context {@link Doc}.
     * @param   name            The name to qualify.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDoc(Doc context, String name) {
        return getClassDoc(getContainingClassDoc(context), name);
    }

    /**
     * Convenience method to attempt to find a {@link ClassDoc}.
     *
     * @param   context         The context {@link ClassDoc}.
     * @param   name            The name to qualify.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDoc(ClassDoc context, String name) {
        return (context != null) ? context.findClass(name) : null;
    }

    /**
     * Method to attempt to get a link to a javadoc document describing the
     * argument {@link Class}.  {@link #setConfiguration(Configuration)}
     * should be called first to allow external links to be calculated.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   type            The target {@link Class}.
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element} if the link
     *          could be calculated; a {@link String} containing the
     *          {@link Class} name otherwise.
     */
    protected Object getClassDocLink(Doc context, Class<?> type) {
        return getClassDocLink(context, type.getCanonicalName());
    }

    private Object getClassDocLink(Doc context, String name) {
        Object link = name;
        ClassDoc target = getClassDoc(context, name);

        if (target != null) {
            URI href = getHref(getContainingClassDoc(context), target);

            if (href != null) {
                link = HTML.a(document, href, target.name());
            }
        }

        return link;
    }

    private URI getHref(ClassDoc context, ClassDoc target) {
        URI href = null;

        if (target.isIncluded()) {
            String path = NIL;
            String[] names = context.qualifiedName().split("[.]");

            for (int i = 0, n = names.length - 1; i < n; i += 1) {
                path += "../";
            }

            path += "./";
            path += target.qualifiedName().replaceAll("[.]", "/") + ".html";

            href = URI.create(path).normalize();
        } else {
            if (configuration != null) {
                String path =
                    configuration.extern
                    .getExternalLink(target.containingPackage().name(),
                                     null, target.name() + ".html");

                if (path != null) {
                    href = URI.create(path);
                }
            }
        }

        return href;
    }

    /**
     * Convenience method to attempt to qualify a class name.
     *
     * @param   context         The context {@link Doc}.
     * @param   name            The name to qualify.
     *
     * @return  The qualified name if the argument name can be qualified;
     *          the argument name otherwise.
     */
    protected String getQualifiedName(Doc context, String name) {
        ClassDoc doc = getClassDoc(context, name);

        return (doc != null) ? doc.qualifiedName() : name;
    }

    protected Class<?> getClassFor(ClassDoc doc) throws ClassNotFoundException {
        String name = null;

        if (doc != null) {
            if (doc.containingClass() != null) {
                name =
                    doc.containingClass().qualifiedName()
                    + DOLLAR + doc.simpleTypeName();
            } else {
                name = doc.qualifiedName();
            }
        }

        return (name != null) ? Class.forName(name) : null;
    }

    @Override
    public String toString() { return super.toString(); }
}
