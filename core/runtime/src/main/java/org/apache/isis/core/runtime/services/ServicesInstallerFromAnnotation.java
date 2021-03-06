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

package org.apache.isis.core.runtime.services;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.not;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import javax.annotation.PreDestroy;

import org.apache.isis.applib.AppManifest;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.DomainServiceLayout;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.metamodel.facets.object.domainservice.DomainServiceMenuOrder;
import org.apache.isis.core.metamodel.util.DeweyOrderComparator;
import org.apache.isis.core.plugins.classdiscovery.ClassDiscovery;
import org.apache.isis.core.plugins.classdiscovery.ClassDiscoveryPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ServicesInstallerFromAnnotation extends ServicesInstallerAbstract {

    // -- constants

    private static final Logger LOG = LoggerFactory.getLogger(ServicesInstallerFromAnnotation.class);

    public static final String NAME = "annotation";
    public final static String PACKAGE_PREFIX_KEY = "isis.services.ServicesInstallerFromAnnotation.packagePrefix";

    /**
     * These package prefixes (core and modules) are always included.
     *
     * <p>
     * It's important that any services annotated {@link org.apache.isis.applib.annotation.DomainService} and residing
     * in any of these packages must have no side-effects.
     *
     * <p>
     *     Services are ordered according to the {@link org.apache.isis.applib.annotation.DomainService#menuOrder() menuOrder},
     *     with the first service found used.
     * </p>
     */
    public final static String PACKAGE_PREFIX_STANDARD = Joiner.on(",").join(AppManifest.Registry.FRAMEWORK_PROVIDED_SERVICES);


    // -- constructor, fields

    private final ServiceInstantiator serviceInstantiator;

    public ServicesInstallerFromAnnotation(final IsisConfigurationDefault isisConfiguration) {
        this(new ServiceInstantiator(), isisConfiguration);
    }

    public ServicesInstallerFromAnnotation(
            final ServiceInstantiator serviceInstantiator,
            final IsisConfigurationDefault isisConfiguration) {
        super(NAME, isisConfiguration);
        this.serviceInstantiator = serviceInstantiator;
    }


    // -- packagePrefixes
    private String packagePrefixes;

    /**
     * For integration testing.
     *
     * <p>
     *     Otherwise these are read from the {@link org.apache.isis.core.commons.config.IsisConfiguration}
     * </p>
     */
    public void withPackagePrefixes(final String... packagePrefixes) {
        this.packagePrefixes = Joiner.on(",").join(packagePrefixes);
    }


    // -- init, shutdown

    @Override
    public void init() {
        initIfRequired();
    }

    private boolean initialized = false;

    protected void initIfRequired() {
        if(initialized) {
            return;
        }

        if(getConfiguration() == null) {
            throw new IllegalStateException("No IsisConfiguration injected - aborting");
        }
        try {

            // lazily copy over the configuration to the instantiator
            serviceInstantiator.setConfiguration(getConfiguration());

            if(packagePrefixes == null) {
                this.packagePrefixes = PACKAGE_PREFIX_STANDARD;
                String packagePrefixes = getConfiguration().getString(PACKAGE_PREFIX_KEY);
                if(!Strings.isNullOrEmpty(packagePrefixes)) {
                    this.packagePrefixes = this.packagePrefixes + "," + packagePrefixes;
                }
            }

        } finally {
            initialized = true;
        }
    }

    @Override
    @PreDestroy
    public void shutdown() {
    }



    // -- helpers

    private Predicate<Class<?>> instantiatable() {
        return and(not(nullClass()), not(abstractClass()));
    }

    private static Function<String,String> trim() {
        return new Function<String,String>(){
            @Override
            public String apply(final String input) {
                return input.trim();
            }
        };
    }

    private static Predicate<Class<?>> nullClass() {
        return new Predicate<Class<?>>() {

            @Override
            public boolean apply(final Class<?> input) {
                return input == null;
            }
        };
    }

    private static Predicate<Class<?>> abstractClass() {
        return new Predicate<Class<?>>() {

            @Override
            public boolean apply(final Class<?> input) {
                return Modifier.isAbstract(input.getModifiers());
            }
        };
    }



    // -- getServices (API)

    private List<Object> services;

    @Override
    public List<Object> getServices() {
        initIfRequired();

        if(this.services == null) {

            final SortedMap<String, SortedSet<String>> positionedServices = Maps.newTreeMap(new DeweyOrderComparator());
            appendServices(positionedServices);

            this.services = ServicesInstallerUtils.instantiateServicesFrom(positionedServices, serviceInstantiator);
        }
        return services;
    }


    // -- appendServices

    public void appendServices(final SortedMap<String, SortedSet<String>> positionedServices) {
        initIfRequired();

        final List<String> packagePrefixList = asList(packagePrefixes);

        Set<Class<?>> domainServiceTypes = AppManifest.Registry.instance().getDomainServiceTypes();
        if(domainServiceTypes == null) {
            // if no appManifest
            final ClassDiscovery discovery = ClassDiscoveryPlugin.get().discover(packagePrefixList);

            domainServiceTypes = discovery.getTypesAnnotatedWith(DomainService.class);
        }

        final List<Class<?>> domainServiceClasses = Lists.newArrayList(Iterables.filter(domainServiceTypes, instantiatable()));
        for (final Class<?> cls : domainServiceClasses) {

            final String order = DomainServiceMenuOrder.orderOf(cls);
            // we want the class name in order to instantiate it
            // (and *not* the value of the @DomainServiceLayout(named=...) annotation attribute)
            final String fullyQualifiedClassName = cls.getName();
            final String name = nameOf(cls);

            ServicesInstallerUtils.appendInPosition(positionedServices, order, fullyQualifiedClassName);
        }
    }



    // -- helpers: nameOf, asList

    private static String nameOf(final Class<?> cls) {
        final DomainServiceLayout domainServiceLayout = cls.getAnnotation(DomainServiceLayout.class);
        String name = domainServiceLayout != null ? domainServiceLayout.named(): null;
        if(name == null) {
            name = cls.getName();
        }
        return name;
    }

    private static List<String> asList(final String csv) {
        return Lists.newArrayList(Iterables.transform(Splitter.on(",").split(csv), trim()));
    }


    // -- domain events
    public static abstract class PropertyDomainEvent<T>
    extends org.apache.isis.applib.events.domain.PropertyDomainEvent<ServicesInstallerFromAnnotation, T> {
    }

    public static abstract class CollectionDomainEvent<T>
    extends org.apache.isis.applib.events.domain.CollectionDomainEvent<ServicesInstallerFromAnnotation, T> {
    }

    public static abstract class ActionDomainEvent
    extends org.apache.isis.applib.events.domain.ActionDomainEvent<ServicesInstallerFromAnnotation> {
    }


    // -- getTypes (API)

    @Override
    public List<Class<?>> getTypes() {
        return listOf(List.class); // ie List<Object.class>, of services
    }



}
