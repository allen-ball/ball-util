/*
 * $Id$
 *
 * Copyright 2012 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.annotation.MatcherGroup;
import ball.annotation.PatternRegex;
import ball.annotation.ServiceProviderFor;
import ball.util.PatternMatcherBean;
import ball.xml.FluentNode;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.io.File;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Inline {@link Taglet} providing links to {@link.man man(1)} pages.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.man")
@NoArgsConstructor @ToString
@PatternRegex("(?is)(.+)[(]([\\p{Alnum}])[)]")
public class LinkManTaglet extends AbstractInlineTaglet
                           implements SunToolsInternalToolkitTaglet,
                                      PatternMatcherBean {
    private static final LinkManTaglet INSTANCE = new LinkManTaglet();

    public static void register(Map<Object,Object> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private String name = null;
    private String section = null;

    @MatcherGroup(1)
    protected void setName(String string) { name = string; }

    @MatcherGroup(2)
    protected void setSection(String string) { section = string; }

    @Override
    public FluentNode toNode(Tag tag) throws Throwable {
        PatternMatcherBean.super.initialize(tag.text().trim());

        File path = new File(File.separator);

        path = new File(path, "usr");
        path = new File(path, "share");
        path = new File(path, "man");
        path = new File(path, "htmlman" + section);
        path = new File(path, name + "." + section + ".html");

        return a(path.toURI(), code(name + "(" + section + ")"));
    }
}
