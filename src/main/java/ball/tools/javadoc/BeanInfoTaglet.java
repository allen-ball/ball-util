/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.util.BeanInfoUtil;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Inline {@link Taglet} to provide a table of bean properties.
 *
 * For example:
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("bean.info")
@NoArgsConstructor @ToString
public class BeanInfoTaglet extends AbstractInlineTaglet
                            implements SunToolsInternalToolkitTaglet {
    private static final BeanInfoTaglet INSTANCE = new BeanInfoTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        ClassDoc start = null;
        ClassDoc stop = null;
        String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

        if (! isEmpty(argv[0])) {
            start = getClassDocFor(tag, argv[0]);
        } else {
            start = getContainingClassDocFor(tag);
        }

        if (start == null) {
            throw new IllegalArgumentException("Class not specified");
        }

        if (argv.length > 1) {
            if (! Void.TYPE.getName().equals(argv[1])) {
                stop = getClassDocFor(tag, argv[1]);
            }
        } else {
            stop = getClassDocFor(tag, Object.class.getName());
        }

        return fragment(tag,
                        Introspector.getBeanInfo(getClassFor(start),
                                                 getClassFor(stop)));
    }

    private FluentNode fragment(Tag tag, BeanInfo info) {
        FluentNode node = fragment();

        append(node, tag, info);

        return node;
    }

    private void append(FluentNode node, Tag tag, BeanInfo info) {
        node.add(table(tag, info.getBeanDescriptor()));
        node.add(table(tag, info.getPropertyDescriptors()));

        Arrays.stream(Optional
                      .ofNullable(info.getAdditionalBeanInfo())
                      .orElse(new BeanInfo[] { }))
            .forEach(t -> append(node, tag, t));
    }

    private FluentNode table(Tag tag, BeanDescriptor descriptor) {
        return table(tr(td(b("Bean Class:")),
                        td(a(tag, descriptor.getBeanClass()))),
                     fragment(Stream.of(descriptor.getCustomizerClass())
                              .filter(t -> t != null)
                              .map(t -> tr(td(b("Customizer Class:")),
                                           td(a(tag, t))))
                              .collect(Collectors.toList())));
    }

    private FluentNode table(Tag tag, PropertyDescriptor[] rows) {
        return table(tr(Arrays.asList("Property", "Mode", "Type",
                                      "isIndexed", "isHidden", "isBound",
                                      "isConstrained", "Description")
                        .stream()
                        .map(t -> th(t))
                        .collect(Collectors.toList())),
                     fragment(Arrays.stream(rows)
                              .map(t -> tr(tag, t))
                              .collect(Collectors.toList())));
    }

    private FluentNode tr(Tag tag, PropertyDescriptor row) {
        boolean isIndexed = (row instanceof IndexedPropertyDescriptor);
        Class<?> type = row.getPropertyType();

        if (isIndexed) {
            type = ((IndexedPropertyDescriptor) row).getIndexedPropertyType();
        }

        return tr(td(row.getName()),
                  td(BeanInfoUtil.getMode(row)),
                  td(a(tag, type)),
                  td(code(isIndexed)),
                  td(code(row.isHidden())),
                  td(code(row.isBound())),
                  td(code(row.isConstrained())),
                  td(row.getShortDescription()));
    }
}
