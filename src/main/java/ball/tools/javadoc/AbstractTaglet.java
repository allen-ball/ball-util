/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.xml.FluentDocument;
import ball.xml.FluentDocumentBuilder;
import ball.xml.FluentNode;
import ball.xml.HTMLTemplates;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.lang3.ArrayUtils;
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
public abstract class AbstractTaglet implements AnnotatedTaglet,
                                                HTMLTemplates {
    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";

    private static final String NO = "no";
    private static final String YES = "yes";

    private final boolean isInlineTag;
    private final boolean inPackage;
    private final boolean inOverview;
    private final boolean inField;
    private final boolean inConstructor;
    private final boolean inMethod;
    private final boolean inType;
    private final Transformer transformer;
    private final FluentDocument document;
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

        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OMIT_XML_DECLARATION, YES);
            transformer.setOutputProperty(INDENT, YES);
            transformer.setOutputProperty(INDENT_AMOUNT, String.valueOf(2));

            document =
                FluentDocumentBuilder
                .wrap(DocumentBuilderFactory.newInstance()
                      .newDocumentBuilder()
                      .newDocument());
            document
                .add(element("html",
                             element("head",
                                     element("meta",
                                             attribute("charset", "utf-8"))),
                             element("body")));
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
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

    @Override
    public Transformer transformer() { return transformer; }

    @Override
    public FluentDocument document() { return document; }

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
     * Method to get a "href" attribute {@link URI} to a
     * {@link ClassDoc target} from a {@link ClassDoc context}.
     * {@link #configuration} must be non-{@code null} to allow external
     * links to be calculated.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   target          The target {@link ClassDoc}.
     *
     * @return  The {@link URI} if the "href" could be calculated;
     *          {@code null} otherwise.
     */
    protected URI href(ClassDoc context, ClassDoc target) {
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
     * Method to get a "href" attribute {@link URI} to a
     * {@link Enum} constant from a {@link ClassDoc context}.
     * {@link #configuration} must be non-{@code null} to allow external
     * links to be calculated.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   constant        The target {@link Enum} constant.
     *
     * @return  The {@link URI} if the "href" could be calculated;
     *          {@code null} otherwise.
     */
    protected URI href(Doc context, Enum<?> constant) {
        URI href = null;
        ClassDoc target =
            getClassDoc(context,
                        constant.getDeclaringClass().getCanonicalName());

        if (target != null) {
            href =
                href(getContainingClassDoc(context), target)
                .resolve("#" + constant.name());
        }

        return href;
    }

    /**
     * See {@link #a(URI,Node)}.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   target          The target {@link ClassDoc}.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element}.
     */
    protected FluentNode a(Doc context, ClassDoc target, Node node) {
        return a(href(getContainingClassDoc(context), target),
                 (node != null) ? node : code(target.name()));
    }

    /**
     * See {@link #a(URI,Node)}.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   type            The target {@link Class}.
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element}.
     */
    protected FluentNode a(Doc context, Class<?> type) {
        String brackets = EMPTY;

        while (type.isArray()) {
            brackets = "[]" + brackets;
            type = type.getComponentType();
        }

        ClassDoc target =
            getClassDoc(getContainingClassDoc(context),
                        type.getCanonicalName());
        URI href = null;

        if (target != null) {
            href = href(getContainingClassDoc(context), target);
        }

        return a(href,
                 code(((target != null)
                           ? target.name()
                           : type.getCanonicalName())
                      + brackets));
    }

    /**
     * See {@link #a(URI,Node)}.
     *
     * @param   context         The context {@link Doc} for calculating a
     *                          relative {@link URI}.
     * @param   constant        The target {@link Enum} constant.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  The {@code <a/>} {@link org.w3c.dom.Element}.
     */
    protected FluentNode a(Doc context, Enum<?> constant, Node node) {
        return a(href(context, constant),
                 (node != null) ? node : code(constant.name()));
    }

    protected FluentNode node(Doc context, Object object) {
        FluentNode node = null;

        if (object instanceof byte[]) {
            node =
                text(Arrays.stream(ArrayUtils.toObject((byte[]) object))
                     .map (t -> String.format("0x%02X", t))
                     .collect(Collectors.joining(", ", "[", "]")));
        } else if (object instanceof boolean[]) {
            node = text(Arrays.toString((boolean[]) object));
        } else if (object instanceof double[]) {
            node = text(Arrays.toString((double[]) object));
        } else if (object instanceof float[]) {
            node = text(Arrays.toString((float[]) object));
        } else if (object instanceof int[]) {
            node = text(Arrays.toString((int[]) object));
        } else if (object instanceof long[]) {
            node = text(Arrays.toString((long[]) object));
        } else if (object instanceof Object[]) {
            node = node(context, Arrays.asList((Object[]) object));
        } else if (object instanceof Class<?>) {
            node = a(context, (Class<?>) object);
        } else if (object instanceof Enum<?>) {
            node = a(context, ((Enum<?>) object), null);
        } else if (object instanceof Collection<?>) {
            List<Node> nodes =
                ((Collection<?>) object)
                .stream()
                .map(t -> node(context, t))
                .collect(Collectors.toList());

            for (int i = nodes.size() - 1; i > 0; i -= 1) {
                nodes.add(i, text(", "));
            }

            node =
                fragment()
                .add(text("["))
                .add(nodes)
                .add(text("]"));
        }

        if (node == null) {
            node = text(String.valueOf(object));
        }

        return node;
    }
}
