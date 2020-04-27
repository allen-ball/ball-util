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
import ball.annotation.ServiceProviderFor;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Locale;
import java.util.SortedSet;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * compiler.warn.missing.SVUID {@link Remedy}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Remedy.class })
@Codes({ "compiler.warn.missing.SVUID" })
@NoArgsConstructor @ToString
public class MissingSVUIDRemedy extends Remedy implements Serializable {
    private static final long serialVersionUID = -6900234880629237267L;

    private static abstract class PROTOTYPE {
        private static final long serialVersionUID = 0L;
    }

    private static final Field FIELD = PROTOTYPE.class.getDeclaredFields()[0];

    @Override
    public String getRx(Diagnostic<? extends JavaFileObject> diagnostic,
                        StandardJavaFileManager fm,
                        SortedSet<Class<?>> classes) {
        JavaFileObject source = diagnostic.getSource();
        String message = diagnostic.getMessage(Locale.ROOT);
        String name = fm.inferBinaryName(StandardLocation.SOURCE_PATH, source);
        int start = message.lastIndexOf(name);
        int end =
            (start >= 0)
                ? message.indexOf(" ", start)
                : message.length();

        if (0 <= start && start < end) {
            name = message.substring(start, end);
        }

        Class<?> type = null;

        for (Class<?> member : classes) {
            if (name.equals(member.getCanonicalName())) {
                type = member;
                break;
            }
        }

        return getRX(type);
    }

    protected String getRX(Class<?> type) {
        String remedy = null;

        if (type != null) {
            long uid = ObjectStreamClass.lookup(type).getSerialVersionUID();

            remedy = String.format("%s = %dL;", declaration(FIELD), uid);
        }

        return remedy;
    }
}
