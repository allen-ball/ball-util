package ball.lang.reflect;
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
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * Java 8 implementation of
 * {@link InvocationHandler#invoke(Object,Method,Object[])} to invoke an
 * interface default method.  Implementation detail of
 * {@link DefaultInvocationHandler}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface DefaultInterfaceMethodInvocationHandler extends InvocationHandler {

    /**
     * {@inheritDoc}
     *
     * This method assumes {@link Method#isDefault() method.isDefault()} and
     * will invoke {@link Method} directly.
     *
     * @throws  Exception       If the {@link Method} cannot be invoked.
     */
    @Override
    default Object invoke(Object proxy,
                          Method method, Object[] argv) throws Throwable {
        Constructor<MethodHandles.Lookup> constructor =
            MethodHandles.Lookup.class
            .getDeclaredConstructor(Class.class);

        constructor.setAccessible(true);

        Class<?> declarer = method.getDeclaringClass();
        Object result =
            constructor.newInstance(declarer)
            .in(declarer)
            .unreflectSpecial(method, declarer)
            .bindTo(proxy)
            .invokeWithArguments(argv);

        return result;
    }
}

