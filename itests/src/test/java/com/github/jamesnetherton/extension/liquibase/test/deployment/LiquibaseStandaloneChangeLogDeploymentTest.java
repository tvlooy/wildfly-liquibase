/*-
 * #%L
 * wildfly-liquibase-itests
 * %%
 * Copyright (C) 2017 James Netherton
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
 * #L%
 */
package com.github.jamesnetherton.extension.liquibase.test.deployment;

import java.net.URL;

import com.github.jamesnetherton.liquibase.arquillian.LiquibaseTestSupport;
import com.github.jamesnetherton.liquibase.arquillian.ChangeLogDefinition;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.arquillian.container.ManagementClient;
import org.jboss.as.controller.client.helpers.standalone.ServerDeploymentHelper;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LiquibaseStandaloneChangeLogDeploymentTest extends LiquibaseTestSupport {

    @ChangeLogDefinition(format = "xml", fileName = "changes.xml")
    private String tableNameXML;

    @ArquillianResource
    private ManagementClient managementClient;

    @Deployment
    public static Archive<?> deployment() {
        return ShrinkWrap.create(JavaArchive.class, "liquibase-standalone-changelog-deployment-test.jar")
            .setManifest(new StringAsset("Manifest-Version: 1.0\nDependencies: org.jboss.as.controller-client\n"));
    }

    @Test
    public void testStandaloneXMLFileDeployment() throws Exception {
        String runtimeName = deployChangeLog("changes.xml", "changelog.xml");
        try {
            assertTableModified(tableNameXML);
        } finally {
            undeployChangeLog(runtimeName);
        }
    }

    private String deployChangeLog(String originalFileName, String runtimeName) throws Exception {
        URL url = getClass().getResource("/" + originalFileName);
        ServerDeploymentHelper server = new ServerDeploymentHelper(managementClient.getControllerClient());
        return server.deploy(runtimeName, url.openStream());
    }

    private void undeployChangeLog(String runtimeName) throws Exception {
        ServerDeploymentHelper server = new ServerDeploymentHelper(managementClient.getControllerClient());
        server.undeploy(runtimeName);
    }
}
