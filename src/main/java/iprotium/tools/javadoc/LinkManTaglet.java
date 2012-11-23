/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import iprotium.util.Regex;
import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Inline {@link Taglet} providing links to {@link.man man(1)} pages.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class LinkManTaglet extends AbstractTaglet {
    public static void register(Map<String,Taglet> map) {
        Taglet taglet = new LinkManTaglet();
        String key = taglet.getName();

        map.remove(key);
        map.put(key, taglet);
    }

    @Regex
    public static final String REGEX = "(?is)(.+)[(]([\\p{Alnum}])[)]";
    public static final Pattern PATTERN = Pattern.compile(REGEX);
    public static final int NAME_GROUP = 1;
    public static final int SECTION_GROUP = 2;

    /**
     * Sole constructor.
     */
    public LinkManTaglet() {
        super("link.man", true, true, true, true, true, true, true);
    }

    @Override
    public String toString(Tag tag) {
        String in = tag.text().trim();
        String out = in;

        try {
            Matcher matcher = PATTERN.matcher(in);

            if (matcher.matches()) {
                String name = matcher.group(NAME_GROUP).trim();
                String section = matcher.group(SECTION_GROUP).trim();
                File path = new File(File.separator);

                path = new File(path, "usr");
                path = new File(path, "share");
                path = new File(path, "man");
                path = new File(path, "htmlman" + section);
                path = new File(path, name + "." + section + ".html");

                out = a(name + "(" + section + ")", path.toURI());
            }
        } catch (Exception exception) {
        }

        return out;
    }
}
