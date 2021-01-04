package ball.util;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.ToString;

/**
 * {@link Spliterator} implemenation to build {@link Stream}s to walk a tree
 * of {@code <T>} nodes.  See {@link #walk(Object,Function)}.
 *
 * @param       <T>             The type of node.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
public class Walker<T> extends AbstractSpliterator<T> {
    private final Stream<Supplier<Spliterator<T>>> stream;
    private Iterator<Supplier<Spliterator<T>>> iterator = null;
    private Spliterator<? extends T> spliterator = null;

    private Walker(Collection<? extends T> nodes,
                   Function<? super T,Collection<? extends T>> childrenOf) {
        super(Long.MAX_VALUE, IMMUTABLE | NONNULL);

        stream =
            nodes.stream()
            .map(t -> (() -> new Walker<T>(t, childrenOf)));
    }

    private Walker(T node,
                   Function<? super T,Collection<? extends T>> childrenOf) {
        super(Long.MAX_VALUE, IMMUTABLE | NONNULL);

        stream =
            Stream.of(node)
            .filter(Objects::nonNull)
            .map(childrenOf)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .map(t -> (() -> new Walker<T>(t, childrenOf)));
        spliterator = Stream.of(node).spliterator();
    }

    @Override
    public Spliterator<T> trySplit() {
        if (iterator == null) {
            iterator = stream.iterator();
        }

        return iterator.hasNext() ? iterator.next().get() : null;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        boolean accepted = false;

        while (! accepted) {
            if (spliterator == null) {
                spliterator = trySplit();
            }

            if (spliterator != null) {
                accepted = spliterator.tryAdvance(consumer);

                if (! accepted) {
                    spliterator = null;
                }
            } else {
                break;
            }
        }

        return accepted;
    }

    /**
     * Entry-point to create a {@link Stream} for traversing a tree of type
     * {@code <T>} nodes.  The caller need only supply the root node and a
     * {@link Function} to calculate the children nodes.
     *
     * @param   root            The root node.
     * @param   childrenOf      The {@link Function} to get the children of
     *                          a node.
     * @param   <T>             The type of node.
     *
     * @return  A {@link Stream}.
     */
    public static <T> Stream<T> walk(T root,
                                     Function<? super T,Collection<? extends T>> childrenOf) {
        return StreamSupport.stream(new Walker<>(root, childrenOf), false);
    }

    /**
     * Entry-point to create a {@link Stream} for traversing a tree of type
     * {@code <T>} nodes.  The caller need only supply the root nodes and a
     * {@link Function} to calculate the children nodes.
     *
     * @param   roots           The root nodes.
     * @param   childrenOf      The {@link Function} to get the children of
     *                          a node.
     * @param   <T>             The type of node.
     *
     * @return  A {@link Stream}.
     */
    public static <T> Stream<T> walk(Collection<? extends T> roots,
                                     Function<? super T,Collection<? extends T>> childrenOf) {
        return StreamSupport.stream(new Walker<>(roots, childrenOf), false);
    }
}
