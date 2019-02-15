/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.lang.Keyword;
import ball.xml.FluentNode;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Inline {@link Taglet} to provide {@link.this} links.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.this")
@NoArgsConstructor @ToString
public class LinkThisTaglet extends AbstractInlineTaglet
                            implements SunToolsInternalToolkitTaglet {
    private static final LinkThisTaglet INSTANCE = new LinkThisTaglet();

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        this.configuration = writer.configuration();

        FluentNode node = code(Keyword.THIS.lexeme());

        try {
            if (! isEmpty(tag.text().trim())) {
                throw new IllegalArgumentException("Invalid argument");
            }

            ClassDoc target = getContainingClassDoc(tag.holder());

            if (target != null) {
                node = a(tag.holder(), target, node);
            }
        } catch (Exception exception) {
            node = warning(tag, exception);

            if (exception instanceof IllegalArgumentException) {
                throw (IllegalArgumentException) exception;
            }
        }

        return content(writer, node);
    }
}
