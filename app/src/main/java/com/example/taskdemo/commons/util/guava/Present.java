/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.taskdemo.commons.util.guava;


import androidx.arch.core.util.Function;
import androidx.core.util.Supplier;

import java.util.Collections;
import java.util.Set;

import static androidx.core.util.Preconditions.checkNotNull;

/**
 * Implementation of an {@link Optional} containing a reference.
 */

final class Present<T> extends Optional<T> {
    private final T reference;

    Present(T reference) {
        this.reference = reference;
    }

    @Override public boolean isPresent() {
        return true;
    }

    @Override public T get() {
        return reference;
    }

    @Override public T or(T defaultValue) {
        checkNotNull(defaultValue, "use orNull() instead of or(null)");
        return reference;
    }

    @Override public Optional<T> or(Optional<? extends T> secondChoice) {
        checkNotNull(secondChoice);
        return this;
    }

    @Override public T or(Supplier<? extends T> supplier) {
        checkNotNull(supplier);
        return reference;
    }

    @Override public T orNull() {
        return reference;
    }

    @Override public Set<T> asSet() {
        return Collections.singleton(reference);
    }

    @Override public <V> Optional<V> transform(Function<? super T, V> function) {
        return new Present<V>(checkNotNull(function.apply(reference),
                "Transformation function cannot return null."));
    }

    @Override public boolean equals(Object object) {
        if (object instanceof Present) {
            Present<?> other = (Present<?>) object;
            return reference.equals(other.reference);
        }
        return false;
    }

    @Override public int hashCode() {
        return 0x598df91c + reference.hashCode();
    }

    @Override public String toString() {
        return "Optional.of(" + reference + ")";
    }

    private static final long serialVersionUID = 0;
}
