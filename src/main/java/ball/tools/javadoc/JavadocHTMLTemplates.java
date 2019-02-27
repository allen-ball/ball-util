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
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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

/**
 * Javadoc {@link HTMLTemplates}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface JavadocHTMLTemplates extends HTMLTemplates {
    FluentNode a(Tag tag, Class<?> type, Node node);
    FluentNode a(Tag tag, String name, Node node);

    /**
     * {@code <}{@code p><b><u>}{@link Tag tag}{@code </p}{@code ></b></u>}
     * {@code <!}{@code -- }{@link Throwable stack trace}{@code --}{@code >}
     *
     * @param   tag             The offending {@link Tag}.
     * @param   throwable       The {@link Throwable}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode warning(Tag tag, Throwable throwable) {
        System.err.println(tag.position() + ": " + throwable);

        String string = "@" + ((Taglet) this).getName() + SPACE + tag.text();

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
     * {@code <code>}{@link Field#getModifiers() field.getModifiers()}{@code </code>}
     * {@code <a href="}{@link ClassDoc field.getType()}{@code ">}{@link ClassDoc#name() ClassDoc.name()}{@code </a>}
     * {@code <code>}{@link Field#getName() field.getName()}{@code </code>}
     *
     * @param   tag             The {@link Tag}.
     * @param   field           The target {@link Field}.
     *
     * @return  {@link org.w3c.dom.DocumentFragment}
     */
    default FluentNode code(Tag tag, Field field) {
        return fragment(code(Modifier.toString(field.getModifiers()) + SPACE),
                        a(tag, field.getType()),
                        code(SPACE + field.getName()));
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
        return table(tag, model, document().toArray(iterable));
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
