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
import ball.util.ant.taskdefs.NotNull;
import java.beans.ConstructorProperties;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class to provide a {@link String} name-value (attribute) for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class StringAttributeType extends StringValueType
                                 implements Map.Entry<String,String> {
    private String name = null;

    /**
     * @param   name            The attribute name.
     */
    @ConstructorProperties({ "name" })
    public StringAttributeType(String name) {
        super();

        this.name = name;
    }

    /**
     * No-argument constructor.
     */
    public StringAttributeType() { this(null); }

    @ConstructorProperties({ "name", "value" })
    private StringAttributeType(String name, String value) {
        this(name);

        setValue(value);
    }

    private StringAttributeType(String name, Object value) {
        this(name, (value != null) ? value.toString() : null);
    }

    @NotNull
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getKey() { return getName(); }

    @Override
    public String toString() { return getName() + "=" + getValue(); }
}
