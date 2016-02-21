/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.types;

import static ball.util.StringUtil.NIL;
import static ball.util.StringUtil.isNil;

/**
 * Class to provide a {@link String} value type for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StringValueType extends OptionalTextType {
    private String value = null;

    /**
     * Sole constructor.
     */
    public StringValueType() { super(); }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void addText(String text) {
        setValue((isNil(getValue()) ? NIL : getValue()) + text);
    }

    @Override
    public String toString() { return getValue(); }
}