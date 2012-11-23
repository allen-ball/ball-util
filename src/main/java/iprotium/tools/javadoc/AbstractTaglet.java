/*
 * $Id$
 *
 * Copyright 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.tools.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import java.beans.ConstructorProperties;
import java.net.URI;

/**
 * Abstract {@link Taglet} base class.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class AbstractTaglet implements Taglet {
    private final String name;
    private final boolean isInlineTag;
    private final boolean inPackage;
    private final boolean inOverview;
    private final boolean inField;
    private final boolean inConstructor;
    private final boolean inMethod;
    private final boolean inType;

    /**
     * Sole constructor.
     */
    @ConstructorProperties({ "name",
                             "isInlineTag", "inPackage", "inOverview",
                             "inField", "inConstructor", "inMethod",
                             "inType" })
    public AbstractTaglet(String name,
                          boolean isInlineTag, boolean inPackage,
                          boolean inOverview, boolean inField,
                          boolean inConstructor, boolean inMethod,
                          boolean inType) {
        if (name != null) {
            this.name = name;
        } else {
            throw new NullPointerException("name");
        }

        this.isInlineTag = isInlineTag;
        this.inPackage = inPackage;
        this.inOverview = inOverview;
        this.inField = inField;
        this.inConstructor = inConstructor;
        this.inMethod = inMethod;
        this.inType = inType;
    }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isInlineTag() { return isInlineTag; }

    @Override
    public boolean inPackage() { return inPackage; }

    @Override
    public boolean inOverview() { return inOverview; }

    @Override
    public boolean inField() { return inField; }

    @Override
    public boolean inConstructor() { return inConstructor; }

    @Override
    public boolean inMethod() { return inMethod; }

    @Override
    public boolean inType() { return inType; }

    @Override
    public String toString(Tag[] tags) { return null; }

    @Override
    public String toString(Tag tag) { return null; }

    protected String a(String text, URI href) {
        return "<a href=\"" + href.toASCIIString()  + "\">" + text + "</a>";
    }
}
