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
import ball.xml.HTML;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Content;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import java.io.File;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.w3c.dom.Element;

/**
 * Inline {@link Taglet} providing links to {@link.man man(1)} pages.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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

    public static void register(Map<String,Taglet> map) {
        map.putIfAbsent(INSTANCE.getName(), INSTANCE);
    }

    private String name = null;
    private String section = null;

    @MatcherGroup(1)
    protected void setName(String string) { name = string; }

    @MatcherGroup(2)
    protected void setSection(String string) { section = string; }

    @Override
    public Content getTagletOutput(Tag tag,
                                   TagletWriter writer) throws IllegalArgumentException {
        this.configuration = writer.configuration();

        Element element = null;

        try {
            PatternMatcherBean.super.initialize(tag.text().trim());

            File path = new File(File.separator);

            path = new File(path, "usr");
            path = new File(path, "share");
            path = new File(path, "man");
            path = new File(path, "htmlman" + section);
            path = new File(path, name + "." + section + ".html");

            element =
                HTML.a(DOCUMENT, path.toURI(), name + "(" + section + ")");
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return content(writer, element);
    }
}
