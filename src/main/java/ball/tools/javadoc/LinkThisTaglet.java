/*
 * $Id$
 *
 * Copyright 2015 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.ServiceProviderFor;
import ball.lang.Keyword;
import ball.xml.FluentNode;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Inline {@link Taglet} to provide {@link.this} links.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.this")
@NoArgsConstructor @ToString
public class LinkThisTaglet extends AbstractInlineTaglet
                            implements SunToolsInternalToolkitTaglet {
    private static final LinkThisTaglet INSTANCE = new LinkThisTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        if (! isEmpty(tag.text().trim())) {
            throw new IllegalArgumentException("Invalid argument");
        }

        return a(tag,
                 getClassFor(getContainingClassDocFor(tag)),
                 Keyword.THIS.lexeme());
    }
}
