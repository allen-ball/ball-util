package ball.text;
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
import java.text.MessageFormat;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * {@link MessageFormat} subclass that provides {@link #format(String,Map)}
 * method that translates the pattern {@link String} and named to parameters
 * in the argument {@link Map} to positional parameters for a call to
 * {@link #format(String,Object...)}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class ParameterizedMessageFormat extends MessageFormat {
    private static final long serialVersionUID = -5750488498631645637L;

    private ParameterizedMessageFormat() { super(null); }

    /**
     * Method that translates the pattern {@link String} and named to
     * parameters in the argument {@link Map} to positional parameters for a
     * call to {@link #format(String,Object...)}.
     *
     * @param   pattern         The pattern {@link String}.
     * @param   map             The parameter {@link Map}.
     *
     * @return  The formatted {@link String}.
     */
    public static String format(String pattern, Map<String,?> map) {
        Object[] values = new Object[map.size()];
        int i = 0;

        for (Map.Entry<String,?> entry : map.entrySet()) {
            pattern =
                pattern.replaceAll(Pattern.quote("{" + entry.getKey() + "}"),
                                   "{" + i + "}");
            values[i] = entry.getValue();

            i += 1;
        }

        return format(pattern, values);
    }
}
