#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/**
O *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package domainapp.application.bdd.specglue;

import org.apache.isis.applib.Module;
import org.apache.isis.core.integtestsupport.ModuleBuilder;
import org.apache.isis.core.runtime.headless.HeadlessWithBootstrappingAbstract;

public class BootstrappingGlueAbstract extends HeadlessWithBootstrappingAbstract {

    public BootstrappingGlueAbstract(final Module module) {
        super(ModuleBuilder.create(module)
                .withHeadlessTransactionSupport()
                .withIntegrationTestConfigFallback()
                .build());
    }

}
