/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.xml;

import java.util.stream.Stream;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Fluent {@link Document} interface.  Note: This interface is an
 * implementation detail of {@link FluentDocumentBuilder} and should not be
 * extended directly.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface FluentDocument extends FluentNode, Document {
    @Override
    default FluentDocument owner() { return this; }

    @Override
    default FluentDocument add(Stream<Node> stream) {
        return add(stream.toArray(Node[]::new));
    }

    @Override
    default FluentDocument add(Iterable<Node> iterable) {
        return (FluentDocument) FluentNode.super.add(iterable);
    }

    @Override
    default FluentDocument add(Node... nodes) {
        return (FluentDocument) FluentNode.super.add(nodes);
    }
}
