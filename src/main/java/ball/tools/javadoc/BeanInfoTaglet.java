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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        this.configuration = writer.configuration();

        LinkedList<Object> list = new LinkedList<>();

        try {
            ClassDoc start = null;
            ClassDoc stop = null;
            String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

            if (! isEmpty(argv[0])) {
                start = getClassDoc(tag.holder(), argv[0]);
            } else {
                start = getContainingClassDoc(tag.holder());
            }

            if (start == null) {
                throw new IllegalArgumentException("Class not specified");
            }

            if (argv.length > 1) {
                if (! Void.TYPE.getName().equals(argv[1])) {
                    stop = getClassDoc(tag.holder(), argv[1]);
                }
            } else {
                stop = getClassDoc(tag.holder(), Object.class.getName());
            }

            output(tag.holder(), list,
                   Introspector.getBeanInfo(getClassFor(start),
                                            getClassFor(stop)));
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, list);
    }

    private void output(Doc doc, List<Object> list, BeanInfo info) {
        output(doc, list, info.getBeanDescriptor());
        output(doc, list, info.getPropertyDescriptors());
        output(doc, list, info.getAdditionalBeanInfo());
    }

    private void output(Doc doc,
                        List<Object> list, BeanDescriptor descriptor) {
        list.add(table(doc, descriptor));
    }

    private FluentNode table(Doc doc, BeanDescriptor descriptor) {
        FluentNode node =
            table(tr(td(b("Bean Class:")),
                     td(a(doc, descriptor.getBeanClass()))))
            .add(Stream.of(descriptor.getCustomizerClass())
                 .filter(t -> t != null)
                 .map(t -> tr(td(b("Customizer Class:")),
                              td(a(doc, t))))
                 .collect(Collectors.toList()));

        return node;
    }

    private void output(Doc doc,
                        List<Object> list, PropertyDescriptor[] descriptors) {
        list.add(table(doc, descriptors));
    }

    private FluentNode table(Doc doc, PropertyDescriptor[] rows) {
        FluentNode table =
            table(tr(Arrays.asList("Property", "Mode", "Type",
                                   "isIndexed", "isHidden", "isBound",
                                   "isConstrained", "Description")
                     .stream()
                     .map(t -> th(t))
                     .collect(Collectors.toList())))
            .add(Arrays.stream(rows)
                 .map(t -> tr(doc, t))
                 .collect(Collectors.toList()));

        return table;
    }

    private FluentNode tr(Doc doc, PropertyDescriptor row) {
        boolean isIndexed = (row instanceof IndexedPropertyDescriptor);
        Class<?> type = row.getPropertyType();

        if (isIndexed) {
            type = ((IndexedPropertyDescriptor) row).getIndexedPropertyType();
        }

        return tr(td(row.getName()),
                  td(BeanInfoUtil.getMode(row)),
                  td(a(doc, type)),
                  td(code(isIndexed)),
                  td(code(row.isHidden())),
                  td(code(row.isBound())),
                  td(code(row.isConstrained())),
                  td(row.getShortDescription()));
    }

    private void output(Doc doc, List<Object> list, BeanInfo[] infos) {
        if (infos != null) {
            for (BeanInfo info : infos) {
                output(doc, list, info);
            }
        }
    }
}
