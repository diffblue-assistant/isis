/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.applib.events.lifecycle;

public abstract class ObjectPersistedEvent<S> extends AbstractLifecycleEvent<S> {

    private static final long serialVersionUID = 1L;

    // -- Default class
    /**
     * This class is the default for the
     * {@link org.apache.isis.applib.annotation.DomainObject#persistedLifecycleEvent()} annotation attribute.  Whether this
     * raises an event or not depends upon the "isis.reflector.facet.domainObjectAnnotation.persistedLifecycleEvent.postForDefault"
     * configuration property.
     */
    public static class Default extends ObjectPersistedEvent<Object> {
        private static final long serialVersionUID = 1L;
        public Default() {}

        @Override
        public String toString() {
            return "ObjectPersistedEvent$Default{source=" + getSource() + "}";
        }
    }


    // -- Noop class

    /**
     * Convenience class to use indicating that an event should <i>not</i> be posted (irrespective of the configuration
     * property setting for the {@link Default} event.
     */
    public static class Noop extends ObjectPersistedEvent<Object> {
        private static final long serialVersionUID = 1L;
    }


    // -- Doop class

    /**
     * Convenience class meaning that an event <i>should</i> be posted (irrespective of the configuration
     * property setting for the {@link Default} event..
     */
    public static class Doop extends ObjectPersistedEvent<Object> {
        private static final long serialVersionUID = 1L;
    }


    public ObjectPersistedEvent() {
    }
    public ObjectPersistedEvent(final S source) {
        super(source);
    }

}