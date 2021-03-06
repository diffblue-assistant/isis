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

package org.apache.isis.viewer.wicket.ui.components.about;

import java.io.InputStream;

import com.google.inject.name.Named;

import org.apache.isis.viewer.wicket.model.models.AboutModel;
import org.apache.isis.viewer.wicket.ui.pages.home.HomePage;
import org.apache.isis.viewer.wicket.ui.panels.PanelAbstract;

/**
 * {@link PanelAbstract Panel} displaying welcome message (as used on
 * {@link HomePage}).
 */
public class AboutPanel extends PanelAbstract<AboutModel> {

    private static final long serialVersionUID = 1L;

    private static final String ID_MANIFEST_ATTRIBUTES = "manifestAttributes";

    @com.google.inject.Inject
    @Named("aboutMessage")
    private String aboutMessage;

    /**
     * We take care to read this only once.
     *
     * <p>
     *     Is <code>transient</code> because
     * </p>
     */
    @com.google.inject.Inject
    @Named("metaInfManifest")
    private transient InputStream metaInfManifestIs;

    private JarManifestModel jarManifestModel;

    public AboutPanel(final String id) {
        super(id);

        if(jarManifestModel == null) {
            jarManifestModel = new JarManifestModel(aboutMessage, metaInfManifestIs);
        }

        add(new JarManifestPanel(ID_MANIFEST_ATTRIBUTES, jarManifestModel));
    }


}
