/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.annotation.ServiceProviderFor;
import iprotium.util.BeanInfoUtil;
import iprotium.xml.HTML;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

import static iprotium.util.StringUtil.isNil;

/**
 * Inline {@link Taglet} to provide a table of bean properties.
 *
 * For example:
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("bean-info")
public class BeanInfoTaglet extends AbstractInlineTaglet {
    public static void register(Map<String,Taglet> map) {
        register(BeanInfoTaglet.class, map);
    }

    /**
     * Sole constructor.
     */
    public BeanInfoTaglet() { super(); }

    @Override
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        setConfiguration(writer.configuration());

        LinkedList<Object> list = new LinkedList<Object>();
        RootDoc root = writer.configuration().root;

        try {
            String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

            if (! isNil(argv[0])) {
                argv[0] = getQualifiedName(tag.holder(), argv[0]);
            } else {
                ClassDoc doc = getContainingClassDoc(tag.holder());

                if (doc != null) {
                    argv[0] = doc.qualifiedName();
                }
            }

            if (isNil(argv[0])) {
                throw new IllegalArgumentException("Class name not specified");
            }

            Class<?> start = Class.forName(argv[0]);
            Class<?> stop = Object.class;

            if (argv.length > 1) {
                argv[1] = getQualifiedName(tag.holder(), argv[1]);

                if (Void.TYPE.getName().equals(argv[1])) {
                    stop = null;
                } else {
                    stop = Class.forName(argv[1]);
                }
            }

            output(tag.holder(), list, Introspector.getBeanInfo(start, stop));
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return output(writer, list);
    }

    private void output(Doc doc, List<Object> list, BeanInfo info) {
        output(doc, list, info.getBeanDescriptor());
        output(doc, list, info.getPropertyDescriptors());
        output(doc, list, info.getAdditionalBeanInfo());
    }

    private void output(Doc doc,
                        List<Object> list, BeanDescriptor descriptor) {
        Element table = HTML.table(document);

        HTML.tr(table,
                HTML.b(table.getOwnerDocument(), "Bean Class:"),
                getClassDocLink(doc, descriptor.getBeanClass()));

        if (descriptor.getCustomizerClass() != null) {
            HTML.tr(table,
                    HTML.b(table.getOwnerDocument(), "Customizer Class:"),
                    getClassDocLink(doc, descriptor.getCustomizerClass()));
        }

        list.add(table);
    }

    private void output(Doc doc,
                        List<Object> list, PropertyDescriptor[] descriptors) {
        Element table = HTML.table(document);

        Object[] headers = new String[] {
            "Property", "Mode", "Type",
            "isHidden", "isBound", "isConstrained"
        };

        headers = HTML.b(table.getOwnerDocument(), headers);
        headers = HTML.u(table.getOwnerDocument(), headers);

        HTML.tr(table, headers);

        for (PropertyDescriptor row : descriptors) {
            HTML.tr(table,
                    row.getName(), BeanInfoUtil.getMode(row),
                    getClassDocLink(doc, row.getPropertyType()),
                    row.isHidden(), row.isBound(), row.isConstrained());
        }

        list.add(table);
    }

    private void output(Doc doc, List<Object> list, BeanInfo[] infos) {
        if (infos != null) {
            for (BeanInfo info : infos) {
                output(doc, list, info);
            }
        }
    }
}
