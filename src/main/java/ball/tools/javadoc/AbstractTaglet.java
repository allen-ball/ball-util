/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.xml.FluentDocument;
import ball.xml.FluentDocumentBuilderFactory;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.util.DocLink;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Abstract {@link com.sun.tools.doclets.Taglet} base class.
 *
 * <p>Note: {@link #getName()} implementation requires the subclass is
 * annotated with {@link TagletName}.</p>
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractTaglet implements AnnotatedTaglet,
                                                JavadocHTMLTemplates {
    private static final String NO = "no";
    private static final String YES = "yes";

    private static final String INDENT_AMOUNT =
        "{http://xml.apache.org/xslt}indent-amount";

    private final boolean isInlineTag;
    private final boolean inPackage;
    private final boolean inOverview;
    private final boolean inField;
    private final boolean inConstructor;
    private final boolean inMethod;
    private final boolean inType;
    private final Transformer transformer;
    private final FluentDocument document;
    private Configuration configuration = null;

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
            transformer.setOutputProperty(INDENT, NO);

            document =
                FluentDocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .newDocument();
            document
                .add(element("html",
                             element("head",
                                     element("meta",
                                             attr("charset", "utf-8"))),
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
    public FluentDocument document() { return document; }

    @Override
    public String toString(Tag[] tags) throws IllegalStateException {
        throw new IllegalStateException();
    }

    @Override
    public String toString(Tag tag) throws IllegalStateException {
        Node node = null;

        try {
            node = toNode(tag);
        } catch (IllegalStateException exception) {
            throw exception;
        } catch (Throwable throwable) {
            node = warning(tag, throwable);
        }

        return render(node);
    }

    /**
     * Implementation for {@link SunToolsInternalToolkitTaglet}.
     *
     * @param   configuration   The {@link Configuration}.
     */
    public void set(Configuration configuration) {
        this.configuration = configuration;
    }

    protected abstract Node toNode(Tag tag) throws Throwable;

    /**
     * Method to render a {@link Node} to a {@link String} without
     * formatting or indentation.
     *
     * @param   node            The {@link Node}.
     *
     * @return  The {@link String} representation.
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected String render(Node node) { return render(node, 0); }

    /**
     * Method to render a {@link Node} to a {@link String} with or without
     * formatting or indentation.
     *
     * @param   node            The {@link Node}.
     * @param   indent          The amount to indent; {@code <= 0} for no
     *                          indentation.
     *
     * @return  The {@link String} representation.
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected String render(Node node, int indent) {
        StringWriter writer = new StringWriter();

        try {
            transformer
                .setOutputProperty(INDENT, (indent > 0) ? YES : NO);
            transformer
                .setOutputProperty(INDENT_AMOUNT,
                                   String.valueOf(indent > 0 ? indent : 0));
            transformer
                .transform(new DOMSource(node),
                           new StreamResult(writer));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Error error) {
            throw error;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return writer.toString();
    }

    /**
     * Convenience method to attempt to find a {@link ClassDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The {@link Class}.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDocFor(Tag tag, Class<?> type) {
        return getClassDocFor(tag.holder(), type.getCanonicalName());
    }

    /**
     * Convenience method to attempt to find a {@link ClassDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   name            The name to qualify.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDocFor(Tag tag, String name) {
        return getClassDocFor(tag.holder(), name);
    }

    private ClassDoc getClassDocFor(Doc context, String name) {
        return getClassDocFor(getContainingClassDocFor(context), name);
    }

    private ClassDoc getClassDocFor(ClassDoc context, String name) {
        return ((context != null)
                    ? (isNotEmpty(name) ? context.findClass(name) : context)
                    : null);
    }

    /**
     * Convenience method to attempt to find a {@link FieldDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   field           The {@link Field}.
     *
     * @return  The {@link FieldDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected FieldDoc getFieldDocFor(Tag tag, Field field) {
        FieldDoc fieldDoc = null;
        ClassDoc classDoc = getClassDocFor(tag, field.getDeclaringClass());

        if (classDoc != null) {
            fieldDoc =
                Arrays.stream(classDoc.fields(true))
                .filter(t -> t.name().equals(field.getName()))
                .findFirst().orElse(null);
        }

        return fieldDoc;
    }

    /**
     * Convenience method to attempt to find a {@link ConstructorDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   constructor     The {@link Constructor}.
     *
     * @return  The {@link ConstructorDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ConstructorDoc getConstructorDocFor(Tag tag,
                                                  Constructor<?> constructor) {
        ConstructorDoc constructorDoc = null;
        ClassDoc classDoc =
            getClassDocFor(tag, constructor.getDeclaringClass());

        if (classDoc != null) {
            constructorDoc =
                Arrays.stream(classDoc.constructors(true))
                .filter(t -> t.signature().equals(signature(constructor)))
                .findFirst().orElse(null);
        }

        return constructorDoc;
    }

    /**
     * Convenience method to attempt to find a {@link MethodDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   method          The {@link Method}.
     *
     * @return  The {@link MethodDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected MethodDoc getMethodDocFor(Tag tag, Method method) {
        MethodDoc methodDoc = null;
        ClassDoc classDoc = getClassDocFor(tag, method.getDeclaringClass());

        if (classDoc != null) {
            methodDoc =
'                Arrays.stream(classDoc.methods(true))
                .filter(t -> t.name().equals(method.getName()))
                .filter(t -> t.signature().equals(signature(method)))
                .findFirst().orElse(null);
        }

        return methodDoc;
    }

    private String signature(Executable executable) {
        String signature =
            Arrays.stream(executable.getParameterTypes())
            .map(t -> t.getCanonicalName())
            .collect(Collectors.joining(",", "(", ")"));

        return signature;
    }

    /**
     * Convenience method to get the containing {@link ClassDoc}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The containing {@link ClassDoc} or {@code null} if there is
     *          none.
     */
    protected ClassDoc getContainingClassDocFor(Tag tag) {
        return getContainingClassDocFor(tag.holder());
    }

    private ClassDoc getContainingClassDocFor(Doc doc) {
        ClassDoc container = null;

        if (doc instanceof ClassDoc) {
            container = (ClassDoc) doc;
        } else if (doc instanceof ProgramElementDoc) {
            container =
                getContainingClassDocFor(((ProgramElementDoc) doc)
                                         .containingClass());
        }

        return container;
    }

    /**
     * Method to get the corresponding {@link Class} for a {@link ClassDoc}.
     *
     * @param   doc             The {@link ClassDoc} (may be {@code null}).
     *
     * @return  The corresponding {@link Class}.
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected Class<?> getClassFor(ClassDoc doc) {
        Class<?> type = null;

        try {
            type = (doc != null) ? Class.forName(getClassNameFor(doc)) : null;
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Error error) {
            throw error;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return type;
    }

    private String getClassNameFor(ClassDoc doc) {
        String name = null;

        if (doc != null) {
            name =
                (doc.containingClass() != null)
                    ? (getClassNameFor(doc.containingClass())
                       + "$" + doc.simpleTypeName())
                    : doc.qualifiedName();
        }

        return name;
    }

    /**
     * See {@link Introspector#getBeanInfo(Class,Class)}.
     *
     * @param   start           The start {@link Class}.
     * @param   stop            The stop {@link Class}.
     *
     * @return  {@link BeanInfo}
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected BeanInfo getBeanInfo(Class<?> start, Class<?> stop) {
        BeanInfo info = null;

        try {
            info = Introspector.getBeanInfo(start, stop);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Error error) {
            throw error;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return info;
    }

    /**
     * See {@link #getBeanInfo(Class,Class)}.
     *
     * @param   start           The start {@link Class}.
     *
     * @return  {@link BeanInfo}
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected BeanInfo getBeanInfo(Class<?> start) {
        return getBeanInfo(start, Object.class);
    }

    private URI href(Tag tag, ProgramElementDoc target) {
        return href(getContainingClassDocFor(tag.holder()), target);
    }

    private URI href(ClassDoc source, ProgramElementDoc target) {
        URI href = null;

        if (target != null) {
            ClassDoc classDoc = getContainingClassDocFor(target);
            PackageDoc packageDoc = classDoc.containingPackage();

            if (target.isIncluded()) {
                String path = "./";
                int depth = countMatches(source.qualifiedName(), ".");

                path += String.join(EMPTY, Collections.nCopies(depth, "../"));

                if (isNotEmpty(packageDoc.name())) {
                    path +=
                        String.join("/", packageDoc.name().split("[.]"))
                        + "/";
                }

                path += classDoc.name() + ".html";

                href = URI.create(path).normalize();
            } else {
                if (configuration != null) {
                    DocLink link =
                        configuration.extern
                        .getExternalLink(packageDoc.name(),
                                         null, classDoc.name() + ".html");
                    /*
                     * Link might be null because the class cannot be
                     * loaded.
                     */
                    if (link != null) {
                        href = URI.create(link.toString());
                    }
                }
            }
        }

        if (href != null) {
            if (target instanceof MemberDoc) {
                String fragment = "#" + target.name();

                if (target instanceof ExecutableMemberDoc) {
                    fragment +=
                        ((ExecutableMemberDoc) target).signature()
                        .replaceAll("[(),]", "-");
                }

                href = href.resolve(fragment);
            }
        }

        return href;
    }

    private URI href(Tag tag, Class<?> type) {
        URI href = null;
        Doc context = tag.holder();
        ClassDoc source = getContainingClassDocFor(context);
        ClassDoc target = getClassDocFor(source, type.getCanonicalName());

        if (target != null) {
            href = href(source, target);
        }

        return href;
    }

    private URI href(Tag tag, Member member) {
        URI href = null;
        ProgramElementDoc target = null;

        if (member instanceof Field) {
            target = getFieldDocFor(tag, (Field) member);
        } else if (member instanceof Constructor) {
            target = getConstructorDocFor(tag, (Constructor) member);
        } else if (member instanceof Method) {
            target = getMethodDocFor(tag, (Method) member);
        }

        if (target != null) {
            href = href(tag, target);
        }

        return href;
    }

    /**
     * {@code <a href="}{@link ClassDoc type}{@code ">}{@link Node node}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The target {@link Class}.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  {@link org.w3c.dom.Element}
     */
    @Override
    public FluentNode a(Tag tag, Class<?> type, Node node) {
        String brackets = EMPTY;

        while (type.isArray()) {
            brackets = "[]" + brackets;
            type = type.getComponentType();
        }

        ClassDoc target = getClassDocFor(tag, type);
        String name =
            ((target != null) ? target.name() : type.getCanonicalName());

        return a(tag, target, (node != null) ? node : code(name + brackets));
    }

    /**
     * {@code <a href="}{@link ClassDoc member}{@code ">}{@link Node node}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   member          The target {@link Member}.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  {@link org.w3c.dom.Element}
     */
    @Override
    public FluentNode a(Tag tag, Member member, Node node) {
        return a(href(tag, member),
                 (node != null) ? node : code(member.getName()));
    }

    /**
     * {@code <a href="}{@link ClassDoc type}{@code ">}{@link Node node}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   name            The target {@link Class} name.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  {@link org.w3c.dom.Element}
     */
    @Override
    public FluentNode a(Tag tag, String name, Node node) {
        ClassDoc target = getClassDocFor(tag, name);

        if (target != null) {
            name = target.name();
        }

        return a(tag, target, (node != null) ? node : code(name));
    }

    private FluentNode a(Tag tag, ClassDoc target, Node node) {
        return a((target != null) ? href(tag, target) : null,
                 (node != null) ? node : code(target.name()));
    }
}
