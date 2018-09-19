/**
 * Copyright 2018 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corp. 2015-2018
 */
package com.eventstreams.samples;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

class ArgumentParser {

    static class ArgumentParserBuilder {
        private final Set<String> flags = new HashSet<>();
        private final Set<String> options = new HashSet<>();

        private ArgumentParserBuilder() {}

        ArgumentParserBuilder flag(final String name) {
            flags.add(name);
            return this;
        }

        ArgumentParserBuilder option(final String name) {
            options.add(name);
            return this;
        }

        ArgumentParser build() {
            return new ArgumentParser(this);
        }

        private Set<String> flags() {
            return flags;
        }

        private Set<String> options() {
            return options;
        }
    }

    private final Set<String> flags;
    private final Set<String> options;

    private ArgumentParser(final ArgumentParserBuilder builder) {
        this.flags = builder.flags();
        this.options = builder.options();
    }

    static ArgumentParserBuilder builder() {
        return new ArgumentParserBuilder();
    }

    Map<String, String> parseArguments(final String... argArray) throws IllegalArgumentException {
        final Queue<String> args = new LinkedList<>(Arrays.asList(argArray));
        final Map<String, String> result = new HashMap<>();
        while (!args.isEmpty()) {
            final String arg = args.poll();
            if (flags.contains(arg)) {
                result.put(arg, "");
            } else if (options.contains(arg)) {
                final String value = args.poll();
                if (value == null) {
                    throw new IllegalArgumentException("Command line argument '" + arg
                            + "' must be followed by another argument");
                }
                result.put(arg, value);
            } else {
                throw new IllegalArgumentException("Unexpected command line argument: " + arg);
            }
        }
        return result;
    }
}
