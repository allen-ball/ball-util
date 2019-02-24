/*
 * $Id$
 *
 * Copyright 2013 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.beans.PropertyDescriptorsTableModel;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.Map;
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
        ClassDoc doc = null;
        String name = tag.text().trim();

        if (! isEmpty(name)) {
            doc = getClassDocFor(tag, name);
        } else {
            doc = getContainingClassDocFor(tag);
        }

        Class<?> type = getClassFor(doc);
        PropertyDescriptorsTableModel model =
            new PropertyDescriptorsTableModel(getBeanInfo(type)
                                              .getPropertyDescriptors());

        return div(attr("class", "block"),
                   table(tag, model));
    }
}
