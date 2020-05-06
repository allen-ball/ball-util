package ball.tools;
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
import ball.annotation.ConstantValueMustConvertTo;
import ball.annotation.ServiceProviderFor;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.lang.reflect.Modifier.ABSTRACT;

/**
 * compiler.err.does.not.override.abstract {@link Remedy}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes({ "compiler.err.does.not.override.abstract" })
@NoArgsConstructor @ToString
public class DoesNotOverrideAbstractRemedy extends Remedy {
    @ConstantValueMustConvertTo(value = Pattern.class, method = "compile")
    private static final String REGEX =
        "(?m)^.*does not override abstract method ([\\p{Graph}]+[(][\\p{Graph}]*[)])[\\p{Space}]+in[\\p{Space}]+([\\p{Graph}]+)$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final int METHOD = 1;
    private static final int CLASS = 2;

    private static final String NL = "\n";

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        Matcher matcher = PATTERN.matcher(message);
        Method method = null;

        if (matcher.find()) {
            Class<?> type = getClassForNameFrom(matcher.group(CLASS), classes);

            if (type != null) {
                for (Method declared : type.getDeclaredMethods()) {
                    String string = declared.toGenericString();

                    if (string.contains(matcher.group(METHOD))) {
                        method = declared;
                        break;
                    }
                }
            }
        }

        return getRX(method);
    }

    protected String getRX(Method method) {
        String string = null;

        if (method != null) {
            string =
                String.format("@%s\n%s {\n}",
                              type(Override.class),
                              declaration(method.getModifiers() ^ ABSTRACT,
                                          method));
        }

        return string;
    }
}
