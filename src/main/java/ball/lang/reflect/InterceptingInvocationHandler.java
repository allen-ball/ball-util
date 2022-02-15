package ball.lang.reflect;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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
import java.lang.reflect.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;

/**
 * "Intercepting" {@link java.lang.reflect.InvocationHandler}
 * implementation.
 *
 * @param       <T>     The type of the "wrapped" target.
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@RequiredArgsConstructor @ToString
public class InterceptingInvocationHandler<T> extends DefaultInvocationHandler {
    @Getter private final T target;

    /**
     * Subclasses may declare methods with the same signature of a proxied
     * interface method which will be invoked (as a sort of listener) before
     * invoking the target method.  If the invoked {@link Method}'s
     * declaring class is assignable from the target's class, the
     * {@link Method} is invoked on the target.
     *
     * @param   proxy           The proxy instance.
     * @param   method          The {@link Method}.
     * @param   argv            The argument array.
     *
     * @return  The value to return from the {@link Method} invocation.
     *
     * @throws  Exception       If the {@link Method} cannot be invoked.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] argv) throws Throwable {
        try {
            invokeMethod(this, true, method.getName(), argv, method.getParameterTypes());
        } catch (Exception exception) {
        }

        Object result = null;

        if (method.isDefault()) {
            result = super.invoke(proxy, method, argv);
        } else if (method.getDeclaringClass().isAssignableFrom(target.getClass())) {
            result = method.invoke(target, argv);
        } else {
            result = super.invoke(proxy, method, argv);
        }

        return result;
    }
}
