/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.internal.toolkit.taglets.Taglet;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletOutput;
import com.sun.tools.doclets.internal.toolkit.taglets.TagletWriter;
import iprotium.annotation.ServiceProviderFor;
import iprotium.util.Regex;
import iprotium.xml.HTML;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;

/**
 * Inline {@link Taglet} providing links to {@link.man man(1)} pages.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Taglet.class })
@TagletName("link.man")
public class LinkManTaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        register(LinkManTaglet.class, map);
    }

    @Regex
    public static final String REGEX = "(?is)(.+)[(]([\\p{Alnum}])[)]";
    public static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final int NAME_GROUP = 1;
    public static final int SECTION_GROUP = 2;

    /**
     * Sole constructor.
     */
    public LinkManTaglet() { super(true, true, true, true, true, true, true); }

    @Override
    public TagletOutput getTagletOutput(Tag tag,
                                        TagletWriter writer) throws IllegalArgumentException {
        Element element = null;

        try {
            Matcher matcher = PATTERN.matcher(tag.text().trim());

            if (matcher.matches()) {
                String name = matcher.group(NAME_GROUP).trim();
                String section = matcher.group(SECTION_GROUP).trim();
                File path = new File(File.separator);

                path = new File(path, "usr");
                path = new File(path, "share");
                path = new File(path, "man");
                path = new File(path, "htmlman" + section);
                path = new File(path, name + "." + section + ".html");

                element =
                    HTML.a(document, path.toURI(), name + "(" + section + ")");
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException(tag.position().toString(),
                                               exception);
        }

        return output(writer, element);
    }
}
