package ball.tools.javadoc;
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
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
 * See {@link #toNode(Tag)}.
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

    /**
     * Implementation method for
     * {@code public static void register(Map<Object,Object> map)}.
     *
     * @param   map             The {@link Map} to update.
     * @param   taglet          The {@link AbstractTaglet} instance to
     *                          register.
     */
    protected static void register(Map<Object,Object> map,
                                   AbstractTaglet taglet) {
        map.putIfAbsent(taglet.getName(), taglet);
    }

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
        } catch (Error error) {
            throw error;
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

    /**
     * Abstract method to be overridden by subclass implementations.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The {@link Node} representing the output.
     *
     * @throws  Throwable       If the method fails for any reason.
     */
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
     * Method to get the containing {@link ClassDoc}.  See
     * {@link ProgramElementDoc#containingClass()}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The containing {@link ClassDoc} (may be {@code null}).
     */
    protected ClassDoc containingClass(Tag tag) {
        return containingClass(tag.holder());
    }

    private ClassDoc containingClass(Doc holder) {
        ClassDoc doc = null;

        if (holder instanceof ClassDoc) {
            doc = (ClassDoc) holder;
        } else if (holder instanceof ProgramElementDoc) {
            doc = ((ProgramElementDoc) holder).containingClass();
        }

        return doc;
    }

    /**
     * Method to get the containing {@link PackageDoc}.  See
     * {@link ProgramElementDoc#containingPackage()}.
     *
     * @param   tag             The {@link Tag}.
     *
     * @return  The containing {@link PackageDoc} (may be {@code null}).
     */
    protected PackageDoc containingPackage(Tag tag) {
        return containingPackage(tag.holder());
    }

    private PackageDoc containingPackage(Doc holder) {
        PackageDoc doc = null;

        if (holder instanceof PackageDoc) {
            doc = (PackageDoc) holder;
        } else if (holder instanceof ProgramElementDoc) {
            doc = ((ProgramElementDoc) holder).containingPackage();
        }

        return doc;
    }

    /**
     * Method to attempt to find a {@link ClassDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   name            The {@link Class} name.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDocFor(Tag tag, String name) {
        return findClass(tag.holder(), name);
    }

    private ClassDoc findClass(Doc holder, String name) {
        ClassDoc doc = null;

        if (holder instanceof ClassDoc) {
            doc = findClass((ClassDoc) holder, name);
        } else if (holder instanceof PackageDoc) {
            doc = findClass((PackageDoc) holder, name);
        } else if (holder instanceof MemberDoc) {
            doc = findClass(((MemberDoc) holder).containingClass(), name);
        }

        return doc;
    }

    private ClassDoc findClass(ClassDoc holder, String name) {
        return holder.findClass(name);
    }

    private ClassDoc findClass(PackageDoc holder, String name) {
        ClassDoc doc =
            Stream.of(holder.allClasses(true))
            .map(t -> t.findClass(name))
            .filter(Objects::nonNull)
            .findFirst().orElse(holder.findClass(name));

        return doc;
    }

    /**
     * Method to attempt to find a {@link ClassDoc}.
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The {@link Class}.
     *
     * @return  The {@link ClassDoc} if it can be found; {@code null}
     *          otherwise.
     */
    protected ClassDoc getClassDocFor(Tag tag, Class<?> type) {
        return getClassDocFor(tag, type.getCanonicalName());
    }

    /**
     * Method to attempt to find a {@link FieldDoc}.
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
     * Method to attempt to find a {@link ConstructorDoc}.
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
     * Method to attempt to find a {@link MethodDoc}.
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
                Arrays.stream(classDoc.methods(true))
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
     * Method to get the corresponding {@link Class}
     * ({@code package-info.class}) for a {@link PackageDoc}.
     *
     * @param   doc             The {@link PackageDoc} (may be {@code null}).
     *
     * @return  The corresponding {@link Class}.
     *
     * @throws  RuntimeException
     *                          Instead of checked {@link Exception}.
     */
    protected Class<?> getClassFor(PackageDoc doc) {
        Class<?> type = null;

        try {
            if (doc != null) {
                String name = doc.name() + ".package-info";

                type = Class.forName(name);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Error error) {
            throw error;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        return type;
    }

    /**
     * Method to get the corresponding {@link Class} for a
     * {@link ClassDoc}.
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
            if (doc != null) {
                type = Class.forName(getClassNameFor(doc));
            }
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
     * Method to get a {@link Class}'s resource path.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link Class}'s resource path (as a {@link String}).
     */
    protected String getResourcePathOf(Class<?> type) {
        String path =
            String.join("/", type.getName().split(Pattern.quote(".")))
            + ".class";

        return path;
    }

    /**
     * Method to get the {@link URL} to a {@link Class}.
     *
     * @param   type            The {@link Class}.
     *
     * @return  The {@link Class}'s {@link URL}.
     */
    protected URL getResourceURLOf(Class<?> type) {
        return type.getResource("/" + getResourcePathOf(type));
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

    /**
     * {@code <a href="}{@link ClassDoc type}{@code ">}{@link Node node}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   target          The target {@link ClassDoc}.
     * @param   node            The child {@link Node} (may be
     *                          {@code null}).
     *
     * @return  {@link org.w3c.dom.Element}
     */
    @Override
    public FluentNode a(Tag tag, ProgramElementDoc target, Node node) {
        if (node == null) {
            node = code(target.name());
        }

        return a((target != null) ? href(tag, target) : null, node);
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

        if (node == null) {
            String name =
                ((target != null) ? target.name() : type.getCanonicalName());

            node = code(name + brackets);
        }

        return a(tag, target, node);
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
        if (node == null) {
            node = code(member.getName());
        }

        return a(href(tag, member), node);
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

        if (node == null) {
            node = code((target != null) ? target.name() : name);
        }

        return a(tag, target, node);
    }

    private URI href(Tag tag, ProgramElementDoc target) {
        URI href = null;

        if (target != null) {
            ClassDoc classDoc = containingClass(target);
            PackageDoc packageDoc = containingPackage(target);

            if (target.isIncluded()) {
                String path = "./";
                int depth =
                    countMatches(containingPackage(tag).name(), ".") + 1;

                path += String.join(EMPTY, Collections.nCopies(depth, "../"));

                if (isNotEmpty(packageDoc.name())) {
                    path +=
                        String.join("/",
                                    packageDoc.name().split(Pattern.quote(".")))
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
        ClassDoc target = getClassDocFor(tag, type);

        if (target != null) {
            href = href(tag, target);
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
}
