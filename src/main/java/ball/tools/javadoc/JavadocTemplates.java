/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import ball.xml.HTMLTemplates;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Node;

/**
 * Common Javadoc HTML templates
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface JavadocTemplates extends HTMLTemplates {
    void set(Configuration configuration);
    Node toNode(Tag tag) throws Throwable;

    /**
     * {@code <p><b><u>tag</p></b></u>}{@code <!}{@code -- stack trace -->}
     *
     * @param   tag             The offending {@link Tag}.
     * @param   throwable       The {@link Throwable}.
     *
     * @return  A {@link Node} to include in javadoc output.
     */
    default Node warning(Tag tag, Throwable throwable) {
        System.err.println(tag.position() + ": " + throwable);

        String string = "@" + ((Taglet) this).getName() + " " + tag.text();

        if (((Taglet) this).isInlineTag()) {
            string = "{" + string + "}";
        }

        return fragment(p(b(u(string))),
                        comment(ExceptionUtils.getStackTrace(throwable)));
    }
}
