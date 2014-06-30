/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.util.BeanInfoUtil;
import ball.xml.HTML;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

import static ball.util.StringUtil.isNil;

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

        try {
            ClassDoc start = null;
            ClassDoc stop = null;
            String[] argv = tag.text().trim().split("[\\p{Space}]+", 2);

            if (! isNil(argv[0])) {
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
