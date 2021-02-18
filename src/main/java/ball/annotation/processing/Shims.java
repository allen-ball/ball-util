package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
/* import com.sun.tools.javac.processing.JavacProcessingEnvironment; */
/* import com.sun.tools.javac.util.Context; */
import java.lang.reflect.Method;
import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileManager;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

/**
 * JDK 8, 9, and 10-specific methods for {@link AbstractProcessor}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PRIVATE) @ToString
public abstract class Shims {

    /**
     * Method to get the {@link JavaFileManager} from a
     * {@link ProcessingEnvironment}.
     *
     * @param   env             The {@link ProcessingEnvironment}.
     *
     * @return  The {@link JavaFileManager} if it can be obtained;
     *          {@code null} otherwise.
     */
    @SuppressWarnings({ "sunapi" })
    protected static JavaFileManager getJavaFileManager(ProcessingEnvironment env) {
        JavaFileManager fm = null;
        /*
         * Use reflection to avoid tickling
         * https://bugs.openjdk.java.net/browse/JDK-8209058 on JDK 9 and 10.
         */
        try {
            /*
             * Context context =
             *     ((JavacProcessingEnvironment) env).getContext();
             */
            Method getContext = env.getClass().getMethod("getContext");

            getContext.setAccessible(true);

            Object context = getContext.invoke(env);
            /*
             * fm = context.get(JavaFileManager.class);
             */
            Method get = context.getClass().getMethod("get", Class.class);

            get.setAccessible(true);

            fm = (JavaFileManager) get.invoke(context, JavaFileManager.class);
        } catch (Exception exception) {
        }

        return fm;
    }
}
