/*
 * $Id: Remedy.java 679 2011-07-03 06:21:47Z ball $
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.util.Map;

/**
 * Inline {@link Taglet} providing links to external RFCs.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 679 $
 */
public class LinkRFCTaglet implements Taglet {
    public static void register(Map<String,Taglet> map) {
        Taglet taglet = new LinkRFCTaglet();
        String key = taglet.getName();

        map.remove(key);
        map.put(key, taglet);
    }

    /**
     * Sole constructor.
     */
    public LinkRFCTaglet() { }

    @Override
    public String getName() { return "link.rfc"; }

    @Override
    public boolean isInlineTag() { return true; }

    @Override
    public boolean inPackage() { return true; }

    @Override
    public boolean inOverview() { return true; }

    @Override
    public boolean inField() { return true; }

    @Override
    public boolean inConstructor() { return true; }

    @Override
    public boolean inMethod() { return true; }

    @Override
    public boolean inType() { return true; }

    @Override
    public String toString(Tag[] tags) { return null; }

    @Override
    public String toString(Tag tag) {
        String in = tag.text().trim();
        String out = in;

        try {
            int rfc = Integer.valueOf(in);
            String link =
                "http://www.ietf.org/rfc/rfc"
                + String.valueOf(rfc) + ".txt";

            out = "<a href=\"" + link  + "\">"
                + "RFC " + String.valueOf(rfc)
                + "</a>";
        } catch (Exception exception) {
        }

        return out;
    }
}
