package ball.annotation.processing;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2021 Allen D. Ball
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
import ball.tools.javac.AbstractTaskListener;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TaskEvent;
import java.util.Set;
import java.util.TreeSet;
import javax.lang.model.util.Elements;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Java 8 {@link TaskListener} implementation to simulate
 * {@code COMPILATION} finished event: Tracks objects that pass through
 * {@link TaskEvent.Kind#GENERATED} and compares the {@link Set} to those
 * processed.  When the {@link Set}s are equal, compilation is complete.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@RequiredArgsConstructor @ToString
/* package */ class COMPILATIONFinishedTaskListener extends AbstractTaskListener {
    private final JavacTask javac;
    private final Elements elements;
    private final Set<String> processed;
    private final Runnable callback;
    private final Set<String> generated = new TreeSet<>();

    @Override @SuppressWarnings({ "fallthrough" })
    public void finished(TaskEvent event) {
        switch (event.getKind()) {
        case GENERATE:
            String name = elements.getBinaryName(event.getTypeElement()).toString();

            generated.add(name);
            /*
             * Fall-through
             */
        case ANNOTATION_PROCESSING:
            if (processed.isEmpty() || generated.containsAll(processed)) {
                javac.removeTaskListener(this);
                callback.run();
            }
            break;

        default:
            break;
        }
    }
}
