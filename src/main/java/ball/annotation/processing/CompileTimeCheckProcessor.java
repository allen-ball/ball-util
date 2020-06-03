package ball.annotation.processing;
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
import ball.annotation.CompileTimeCheck;
import ball.annotation.ServiceProviderFor;
import ball.tools.javac.AbstractTaskListener;
import com.sun.source.util.TaskEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
/* import javax.lang.model.element.AnnotationValue; */
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.STATIC;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link CompileTimeCheck} {@link Processor}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ServiceProviderFor({ Processor.class })
@For({ CompileTimeCheck.class })
@NoArgsConstructor @ToString
public class CompileTimeCheckProcessor extends AnnotatedProcessor {
    private static final EnumSet<Modifier> FIELD_MODIFIERS =
        EnumSet.of(STATIC, FINAL);

    private final Map<String,String> map = new TreeMap<>();

    @Override
    protected void whenAnnotationProcessingFinished() {
        javac.addTaskListener(new TaskListenerImpl());
    }

    @Override
    protected void process(RoundEnvironment roundEnv,
                           TypeElement annotation, Element element) {
        super.process(roundEnv, annotation, element);

        switch (element.getKind()) {
        case FIELD:
            TypeElement type = (TypeElement) element.getEnclosingElement();
            String key =
                type.getQualifiedName() + ":" + element.getSimpleName();
            String value = elements.getBinaryName(type).toString();

            if (! map.containsKey(key)) {
                if (with(FIELD_MODIFIERS, t -> t.getModifiers()).test(element)) {
                    map.put(key, value);
                } else {
                    print(ERROR, element,
                          "%s must be %s", element.getKind(), FIELD_MODIFIERS);
                }
            }
            break;

        default:
            throw new IllegalStateException(element.getKind().name());
            /* break; */
        }
    }

    @NoArgsConstructor @ToString
    private class TaskListenerImpl extends AbstractTaskListener {
        @Override
        public void finished(TaskEvent event) {
            switch (event.getKind()) {
            case GENERATE:
                ClassLoader loader = getClassPathClassLoader(fm);
                Iterator<Map.Entry<String,String>> iterator =
                    map.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String,String> entry = iterator.next();
                    String[] names = entry.getKey().split(":", 2);
                    TypeElement type = elements.getTypeElement(names[0]);
                    VariableElement element =
                        fieldsIn(type.getEnclosedElements())
                        .stream()
                        .filter(t -> t.getSimpleName().contentEquals(names[1]))
                        .findFirst().orElse(null);
                    AnnotationMirror annotation =
                        getAnnotationMirror(element, CompileTimeCheck.class);
                    /*
                     * AnnotationValue value =
                     *     getAnnotationValue(annotation, "value");
                     */
                    try {
                        Class.forName(entry.getValue(), true, loader);
                        iterator.remove();
                    } catch (ClassNotFoundException exception) {
                        continue;
                    } catch (NoClassDefFoundError error) {
                        continue;
                    } catch (Throwable throwable) {
                        while (throwable instanceof ExceptionInInitializerError) {
                            throwable = throwable.getCause();
                        }

                        while (throwable instanceof InvocationTargetException) {
                            throwable = throwable.getCause();
                        }

                        print(WARNING, element /* , annotation */,
                              "Invalid %s initializer\n%s: %s",
                              element.getKind(),
                              throwable.getClass().getName(),
                              throwable.getMessage());
                        iterator.remove();
                        continue;
                    }
                }
                break;

            default:
                break;
            }
        }
    }
}
