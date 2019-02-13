/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Fluent {@link Document} interface
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface FluentDocument extends FluentNode, Document {
    @Override
    default FluentDocument owner() { return this; }

    @Override
    default FluentDocument add(Node... nodes) {
        return (FluentDocument) FluentNode.super.add(nodes);
    }
}
