/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.activation.ReaderWriterDataSource;
import ball.xml.HTML;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
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

import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Abstract {@link com.sun.tools.doclets.Taglet} base class.
 *
 * <p>Note: {@link #getName()} implementation requires the subclass is
 * annotated with {@link TagletName}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractTaglet implements AnnotatedTaglet {
    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";

    private static final String NO = "no";
    private static final String YES = "yes";

    protected static final Document DOCUMENT;
    protected static final Transformer TRANSFORMER;

    static {
        try {
            DOCUMENT =
                HTML.init(DocumentBuilderFactory.newInstance()
                          .newDocumentBuilder()
                          .newDocument());

            TRANSFORMER = TransformerFactory.newInstance().newTransformer();
            TRANSFORMER.setOutputProperty(OMIT_XML_DECLARATION, YES);
            TRANSFORMER.setOutputProperty(INDENT, YES);
            TRANSFORMER.setOutputProperty(INDENT_AMOUNT, String.valueOf(2));
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
    protected Configuration configuration = null;

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

    @Override
    public String getName() {
        String name = AnnotatedTaglet.super.getName();

        if (name == null) {
            name = getClass().getSimpleName().toLowerCase();
        }

        return name;
    }

    @Override public boolean isInlineTag() { return isInlineTag; }
    @Override public boolean inPackage() { return inPackage; }
    @Override public boolean inOverview() { return inOverview; }
    @Override public boolean inField() { return inField; }
    @Override public boolean inConstructor() { return inConstructor; }
    @Override public boolean inMethod() { return inMethod; }
    @Override public boolean inType() { return inType; }

    public Document document() { return DOCUMENT; }
    public Transformer transformer() { return TRANSFORMER; }

    @Override
    public String toString(Tag[] tags) { throw new IllegalStateException(); }

    @Override
    public String toString(Tag tag) { throw new IllegalStateException(); }

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
        return ((context != null)
                    ? (isEmpty(name)
                           ? context
                           : context.findClass(name))
                    : null);
    }

    /**
     * Method to attempt to get a link to a javadoc document describing the
     * argument {@link Class}.  {@link #configuration} should be
     * non-{@code null} to allow external links to be calculated.
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
        String brackets = EMPTY;

        while (type.isArray()) {
            brackets = "[]" + brackets;
            type = type.getComponentType();
        }

        Object link = type.getCanonicalName() + brackets;
        ClassDoc target = getClassDoc(context, type.getCanonicalName());

        if (target != null) {
            link = getClassDocLink(context, target.name() + brackets, target);
        }

        return link;
    }

    /**
     * Method to create a link to a {@link ClassDoc}.
     * {@link #configuration} should be non-{@code null} to allow
     * external links to be calculated.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   value           The value of the created link.
     * @param   target          The target {@link ClassDoc}.
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element} if the link
     *          could be calculated; the {@code value} otherwise.
     */
    protected Object getClassDocLink(Doc context,
                                     Object value, ClassDoc target) {
        URI href = getHref(getContainingClassDoc(context), target);

        return (href != null) ? HTML.a(document(), href, value) : value;
    }

    private URI getHref(ClassDoc context, ClassDoc target) {
        URI href = null;

        if (target.isIncluded()) {
            String path = EMPTY;
            String[] names = context.qualifiedName().split("[.]");

            for (int i = 0, n = names.length - 1; i < n; i += 1) {
                path += "../";
            }

            path += "./";

            if (! isEmpty(target.containingPackage().name())) {
                path +=
                    target.containingPackage().name().replaceAll("[.]", "/")
                    + "/";
            }

            path += target.name() + ".html";

            href = URI.create(path).normalize();
        } else {
            if (configuration != null) {
                String path =
                    configuration.extern
                    .getExternalLink(target.containingPackage().name(),
                                     null, target.name() + ".html")
                    .toString();

                if (path != null) {
                    href = URI.create(path);
                }
            }
        }

        return href;
    }

    /**
     * Method to attempt to get a link to a javadoc document describing the
     * argument {@link Enum} constant.  {@link #configuration} should
     * be non-{@code null} to allow external links to be calculated.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   constant        The target {@link Enum} constant.
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element} if the link
     *          could be calculated; a {@link String} containing the
     *          {@link Enum} name otherwise.
     */
    protected Object getEnumDocLink(Doc context, Enum<?> constant) {
        Object link = constant.name();
        ClassDoc target =
            getClassDoc(context,
                        constant.getDeclaringClass().getCanonicalName());

        if (target != null) {
            URI href =
                getHref(getContainingClassDoc(context), target)
                .resolve("#" + constant.name());

            link = HTML.a(document(), href, constant.name());
        }

        return link;
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

    /**
     * Method to get the corresponding {@link Class} for a {@link ClassDoc}.
     *
     * @param   doc             The {@link ClassDoc} (may be {@code null}).
     *
     * @return  The corresponding {@link Class}.
     *
     * @throws  ClassNotFoundException
     *                          If the {@link Class} is not found.
     */
    protected Class<?> getClassFor(ClassDoc doc) throws ClassNotFoundException {
        String name = null;

        if (doc != null) {
            if (doc.containingClass() != null) {
                name =
                    doc.containingClass().qualifiedName()
                    + "$" + doc.simpleTypeName();
            } else {
                name = doc.qualifiedName();
            }
        }

        return (name != null) ? Class.forName(name) : null;
    }

    /**
     * Method to get the {@link String} representation of a {@link Node}
     * (suitable for output).
     *
     * @param   node            The {@link Node}.
     *
     * @return  The {@link String} representation.
     */
    protected String toString(Node node) {
        ReaderWriterDataSource ds = new ReaderWriterDataSource(null, null);

        try (Writer out = ds.getWriter()) {
            transformer()
                .transform(new DOMSource(node), new StreamResult(out));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        return ds.toString();
    }

    @Override
    public String toString() { return super.toString(); }
}
