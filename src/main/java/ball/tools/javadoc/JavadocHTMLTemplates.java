/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.xml.FluentNode;
import ball.xml.HTMLTemplates;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.swing.table.TableModel;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Node;

import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Javadoc {@link HTMLTemplates}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface JavadocHTMLTemplates extends HTMLTemplates {
    FluentNode a(Tag tag, Class<?> type, Node node);
    FluentNode a(Tag tag, Member member, Node node);
    FluentNode a(Tag tag, String name, Node node);

    /**
     * {@code <}{@code p><b><u>}{@link Tag tag}{@code </u></b></p}{@code >}
     * {@code <!}{@code -- }{@link Throwable stack trace}{@code --}{@code >}
     *
     * @param   tag             The offending {@link Tag}.
     * @param   throwable       The {@link Throwable}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode warning(Tag tag, Throwable throwable) {
        System.err.println(tag.position() + ": " + throwable);

        String string = "@" + ((Taglet) this).getName();

        if (isNotEmpty(tag.text())) {
            string += SPACE + tag.text();
        }

        if (((Taglet) this).isInlineTag()) {
            string = "{" + string + "}";
        }

        return fragment(p(b(u(string))),
                        comment(ExceptionUtils.getStackTrace(throwable)));
    }

    /**
     * {@code <a href="}{@link ClassDoc type}{@code ">}{@link ClassDoc#name() ClassDoc.name()}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The target {@link Class}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Class<?> type) {
        return a(tag, type, (String) null);
    }

    /**
     * {@code <a href="}{@link ClassDoc type}{@code ">}{@link #code(String) code(name)}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The target {@link Class}.
     * @param   name            The link name.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Class<?> type, String name) {
        return a(tag, type, (name != null) ? code(name) : null);
    }

    /**
     * {@code <a href="}{@link com.sun.javadoc.MemberDoc member}{@code ">}{@link com.sun.javadoc.MemberDoc#name() MemberDoc.name()}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   member          The target {@link Member}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Member member) {
        return a(tag, member, (String) null);
    }

    /**
     * {@code <a href="}{@link com.sun.javadoc.MemberDoc member}{@code ">}{@link #code(String) code(name)}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   member          The target {@link Member}.
     * @param   name            The link name.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Member member, String name) {
        return a(tag, member, (name != null) ? code(name) : null);
    }

    /**
     * {@code <a href="}{@link ClassDoc annotation}{@code ">}{@link #code(String) code(String.valueOf(annotation))}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   annotation      The target {@link Annotation}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Annotation annotation) {
        return a(tag, annotation.annotationType(),
                 String.valueOf(annotation)
                 .replace(annotation.annotationType().getCanonicalName(),
                          annotation.annotationType().getSimpleName()));
    }

    /**
     * {@code <a href="}{@link ClassDoc constant}{@code ">}{@link Enum#name() constant.name()}{@code </a>}
     *
     * @param   tag             The {@link Tag}.
     * @param   constant        The target {@link Enum}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode a(Tag tag, Enum<?> constant) {
        return a(tag, constant.getDeclaringClass(), constant.name());
    }

    /**
     * Dispatches call to {@link #declaration(Tag,Field)} or
     * {@link #declaration(Tag,Method)} as appropriate.
     *
     * @param   tag             The {@link Tag}.
     * @param   member          The target {@link Member}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode declaration(Tag tag, Member member) {
        FluentNode node = null;

        if (member instanceof Field) {
            node = declaration(tag, (Field) member);
        } else if (member instanceof Method) {
            node = declaration(tag, (Method) member);
        } else {
            throw new IllegalArgumentException(String.valueOf(member));
        }

        return node;
    }

    /**
     * Method to generate a {@link Field} declaration with javadoc
     * hyperlinks.
     *
     * @param   tag             The {@link Tag}.
     * @param   field           The target {@link Field}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode declaration(Tag tag, Field field) {
        return fragment(modifiers(field.getModifiers()),
                        type(tag, field.getGenericType()),
                        code(SPACE),
                        a(tag, field, (String) null));
    }

    /**
     * Method to generate a {@link Method} declaration with javadoc
     * hyperlinks.
     *
     * @param   tag             The {@link Tag}.
     * @param   method          The target {@link Method}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode declaration(Tag tag, Method method) {
        FluentNode node =
            fragment(modifiers(method.getModifiers()),
                     type(tag, method.getGenericReturnType()),
                     code(SPACE),
                     a(tag, method, (String) null));

        Parameter[] parameters = method.getParameters();

        node.add(code("("));

        for (int i = 0; i < parameters.length; i += 1) {
            if (i > 0) {
                node.add(code(", "));
            }

            node.add(declaration(tag, parameters[i]));
        }

        node.add(code(")"));

        return node;
    }

    /**
     * Method to generate a {@link Parameter} declaration with javadoc
     * hyperlinks.
     *
     * @param   tag             The {@link Tag}.
     * @param   parameter       The target {@link Parameter}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode declaration(Tag tag, Parameter parameter) {
        return fragment(modifiers(parameter.getModifiers()),
                        type(tag, parameter.getParameterizedType()),
                        code(SPACE),
                        code(parameter.getName()));
    }

    /**
     * Method to generate modifiers for {@code declaration()} methods.
     *
     * @param   modifiers       See {@link Modifier}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode modifiers(int modifiers) {
        FluentNode node = fragment();
        String string = Modifier.toString(modifiers);

        if (isNotEmpty(string)) {
            node.add(code(string + SPACE));
        }

        return node;
    }

    /**
     * Method to generate types for {@code declaration()} methods.
     *
     * @param   tag             The {@link Tag}.
     * @param   type            The target {@link Type}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode type(Tag tag, Type type) {
        FluentNode node = null;

        if (type instanceof ParameterizedType) {
            node =
                fragment(type(tag, ((ParameterizedType) type).getRawType()));

            Type[] types = ((ParameterizedType) type).getActualTypeArguments();

            node = node.add(code("<"));

            for (int i = 0; i < types.length; i += 1) {
                if (i > 0) {
                    node.add(code(","));
                }

                node.add(type(tag, types[i]));
            }

            node.add(code(">"));
        } else if (type instanceof Class<?>) {
            node = a(tag, (Class<?>) type);
        } else {
            node = code(type.getTypeName());
        }

        return node;
    }

    /**
     * {@code <table>}{@link TableModel model}{@code </table>}
     *
     * @param   tag             The {@link Tag}.
     * @param   model           The {@link TableModel} to use to create the
     *                          new table {@link org.w3c.dom.Element}.
     * @param   stream          The {@link Stream} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Tag tag, TableModel model, Stream<Node> stream) {
        return table(tag, model, stream.toArray(Node[]::new));
    }

    /**
     * {@code <table>}{@link TableModel model}{@code </table>}
     *
     * @param   tag             The {@link Tag}.
     * @param   model           The {@link TableModel} to use to create the
     *                          new table {@link org.w3c.dom.Element}.
     * @param   iterable        The {@link Iterable} of {@link Node}s to
     *                          append to the newly created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Tag tag,
                             TableModel model, Iterable<Node> iterable) {
        return table(tag, model, FluentNode.toArray(iterable));
    }

    /**
     * {@code <table>}{@link TableModel model}{@code </table>}
     *
     * @param   tag             The {@link Tag}.
     * @param   model           The {@link TableModel} to use to create the
     *                          new table {@link org.w3c.dom.Element}.
     * @param   nodes           The {@link Node}s to append to the newly
     *                          created
     *                          {@link org.w3c.dom.Element}.
     *
     * @return  {@link org.w3c.dom.Element}
     */
    default FluentNode table(Tag tag, TableModel model, Node... nodes) {
        FluentNode table = table();
        String[] names =
            IntStream.range(0, model.getColumnCount())
            .boxed()
            .map(x -> model.getColumnName(x))
            .toArray(String[]::new);

        if (! isAllBlank(names)) {
            table
                .add(tr(Stream.of(names)
                        .map(t -> th(t))));
        }

        table
            .add(IntStream.range(0, model.getRowCount())
                 .boxed()
                 .map(y -> tr(IntStream.range(0, names.length)
                              .boxed()
                              .map(x -> td(toHTML(tag,
                                                  model.getValueAt(y, x)))))));

        return table.add(nodes);
    }

    /**
     * Method to get a Javadoc HTML representation of an {@link Object}.
     *
     * @param   tag             The {@link Tag}.
     * @param   object          The target {@link Object}.
     *
     * @return  {@link org.w3c.dom.Node}
     */
    default FluentNode toHTML(Tag tag, Object object) {
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
            node = toHTML(tag, Arrays.asList((Object[]) object));
        } else if (object instanceof Class<?>) {
            node = a(tag, (Class<?>) object);
        } else if (object instanceof Enum<?>) {
            node = a(tag, (Enum<?>) object);
        } else if (object instanceof Field) {
            node = a(tag, (Field) object);
        } else if (object instanceof Constructor) {
            node = a(tag, (Constructor) object);
        } else if (object instanceof Method) {
            node = a(tag, (Method) object);
        } else if (object instanceof Collection<?>) {
            List<Node> nodes =
                ((Collection<?>) object)
                .stream()
                .map(t -> toHTML(tag, t))
                .collect(Collectors.toList());

            for (int i = nodes.size() - 1; i > 0; i -= 1) {
                nodes.add(i, text(", "));
            }

            node =
                fragment()
                .add(text("["))
                .add(nodes)
                .add(text("]"));
        } else {
            node = text(String.valueOf(object));
        }

        return node;
    }
}
