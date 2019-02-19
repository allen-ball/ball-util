/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.util.ant.taskdefs.AntTask;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Task;
import org.w3c.dom.Node;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Inline {@link Taglet} to document {@link.uri http://ant.apache.org/ Ant}
 * {@link Task}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("ant.task")
@NoArgsConstructor @ToString
public class AntTaskTaglet extends AbstractInlineTaglet
                            implements SunToolsInternalToolkitTaglet {
    private static final AntTaskTaglet INSTANCE = new AntTaskTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private static final String ____ = "____";
    private static final char COLON = ':';

    private static final Object[][] CODEC = new Object[][] {
        { COLON, "-COLON-" },
        { '$', "-DOLLAR-" }
    };

    private static final String ADD_CONFIGURED = "addConfigured";
    private static final String ADD_TEXT = "addText";

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        ClassDoc doc = null;
        String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

        if (! isEmpty(argv[0])) {
            doc = getClassDocFor(tag, argv[0]);
        } else {
            doc = getContainingClassDocFor(tag);
        }

        Class<?> type = getClassFor(doc);

        if (! Task.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getCanonicalName()
                                               + " is not a subclass of "
                                               + Task.class.getCanonicalName());
        }

        FluentNode node = div(attr("class", "block"));
        String[] tokens = render(template(tag, type)).split(____);

        for (int i = 0; i < tokens.length; i += 1) {
            if ((i % 2) == 0) {
                node.add(code(tokens[i]));
            } else {
                String[] subtokens = decode(tokens[i]);

                node.add(a(tag,
                           subtokens[0],
                           isNotEmpty(subtokens[1]) ? code(subtokens[1]) : null));
            }
        }

        return node;
    }

    private FluentNode template(Tag tag, Class<?> type) {
        AntTask annotation = type.getAnnotation(AntTask.class);
        FluentNode node =
            element(encode(type.getCanonicalName(),
                           (annotation != null) ? annotation.value() : null))
            .add(attributes(tag,
                            getBeanInfo(type, Task.class)
                            .getPropertyDescriptors()))
            .add(Arrays.stream(type.getMethods())
                 .filter(t -> t.getName().startsWith(ADD_CONFIGURED))
                 .filter(t -> t.getParameterTypes().length == 1)
                 .map(t -> configured(tag, t))
                 .collect(Collectors.toList()));

        Arrays.stream(type.getMethods())
            .filter(t -> t.getName().equals(ADD_TEXT))
            .filter(t -> t.getParameterTypes().length == 1)
            .findFirst()
            .ifPresent(t -> node.add(text("text")));

        return node;
    }

    private Node[] attributes(Tag tag, PropertyDescriptor[] descriptors) {
        List<Node> list =
            Arrays.stream(descriptors)
            .filter(t -> (! (t instanceof IndexedPropertyDescriptor)))
            .filter(t -> t.getWriteMethod() != null)
            .map(t -> attr(t.getName(),
                           ____ + t.getPropertyType().getName() + ____))
            .collect(Collectors.toList());

        return list.toArray(new Node[] { });
    }

    private FluentNode configured(Tag tag, Method method) {
        String name =
            Introspector
            .decapitalize(method.getName().substring(ADD_CONFIGURED.length()));
        Class<?> type = method.getParameterTypes()[0];

        return fragment(element(encode(type.getCanonicalName(), name),
                                attributes(tag,
                                           getBeanInfo(type)
                                           .getPropertyDescriptors())),
                        text("..."));
    }

    private String encode(String type, String name) {
        String string = type;

        if (isNotEmpty(name)) {
            string += String.valueOf(COLON) + name;
        }

        for (Object[] codec : CODEC) {
            string =
                string.replaceAll(Pattern.quote(codec[0].toString()),
                                  codec[1].toString());
        }

        string = ____ + string + ____;

        return string;
    }

    private String[] decode(String string) {
        for (Object[] codec : CODEC) {
            string =
                string.replaceAll(Pattern.quote(codec[1].toString()),
                                  codec[0].toString());
        }

        String[] tokens = string.split(String.valueOf(COLON), 2);

        return (tokens.length > 1) ? tokens : new String[] { tokens[0], null };
    }

    private BeanInfo getBeanInfo(Class<?> start, Class<?> stop) {
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

    private BeanInfo getBeanInfo(Class<?> start) {
        return getBeanInfo(start, null);
    }
}
