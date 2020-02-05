package ball.util.ant.types;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.beans.ConstructorProperties;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Class to provide a {@link String} value type for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class StringValueType extends OptionalTextType {
    private String value = null;

    /**
     * @param   value           The value.
     */
    @ConstructorProperties({ "value" })
    public StringValueType(String value) {
        this();

        setValue(value);
    }

    /**
     * No-argument constructor.
     */
    public StringValueType() { super(); }

    public String getValue() { return value; }
    public String setValue(String value) {
        String previous = this.value;

        this.value = value;

        return previous;
    }

    @Override
    public void addText(String text) {
        setValue((isEmpty(getValue()) ? EMPTY : getValue()) + text);
    }

    @Override
    public String toString() { return getValue(); }
}
