package ball.annotation.processing;
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
import ball.annotation.ServiceProviderFor;
import ball.tools.javac.AbstractTaskListener;
import com.sun.source.util.TaskEvent;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.util.ElementFilter.fieldsIn;
import static javax.tools.Diagnostic.Kind.WARNING;

/**
 * {@link Processor} implementation to check subclasses of
 * {@link Serializable} to verify a {@code serialVersionUID} field has been
 * defined.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@ServiceProviderFor({ Processor.class })
@ForElementKinds({ CLASS })
@ForSubclassesOf(Serializable.class)
@NoArgsConstructor @ToString
public class SerializableProcessor extends AnnotatedNoAnnotationProcessor {
    private static abstract class PROTOTYPE {
        private static final long serialVersionUID = 0L;
    }

    private static final Field PROTOTYPE = PROTOTYPE.class.getDeclaredFields()[0];

    static { PROTOTYPE.setAccessible(true); }

    private final Set<TypeElement> set = new TreeSet<>(Comparator.comparing(t -> t.getQualifiedName().toString()));

    @Override
    protected void whenAnnotationProcessingFinished() {
        javac.addTaskListener(new TaskListenerImpl());
    }

    @Override
    protected void process(RoundEnvironment roundEnv, Element element) {
        if (! isGenerated(element)) {
            TypeElement type = (TypeElement) element;
            VariableElement field =
                fieldsIn(type.getEnclosedElements()).stream()
                .filter(t -> t.getSimpleName().contentEquals(PROTOTYPE.getName()))
                .findFirst().orElse(null);

            if (! (field != null
                   && field.getModifiers().containsAll(getModifiers(PROTOTYPE))
                   && isAssignableTo(PROTOTYPE.getType()).test(field))) {
                set.add(type);
            }
        }
    }

    @NoArgsConstructor @ToString
    private class TaskListenerImpl extends AbstractTaskListener {
        @Override
        public void finished(TaskEvent event) {
            switch (event.getKind()) {
            case GENERATE:
                ClassLoader loader = getClassPathClassLoader(fm);
                Iterator<TypeElement> iterator = set.iterator();

                while (iterator.hasNext()) {
                    TypeElement type = iterator.next();
                    String name = elements.getBinaryName(type).toString();

                    try {
                        Class<?> cls = Class.forName(name, true, loader);
                        long uid = ObjectStreamClass.lookup(cls).getSerialVersionUID();

                        print(WARNING, type,
                              "%s %s has no definition of %s\n%s = %dL;",
                              getForSubclassesOf().getSimpleName(), type.getKind(), PROTOTYPE.getName(),
                              declaration(PROTOTYPE), uid);

                        iterator.remove();
                    } catch (ClassNotFoundException exception) {
                        continue;
                    } catch (NoClassDefFoundError error) {
                        continue;
                    } catch (Throwable throwable) {
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
