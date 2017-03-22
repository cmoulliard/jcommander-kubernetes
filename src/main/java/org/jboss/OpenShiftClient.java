/**
 * Copyright (C) 2015 Red Hat, Inc.
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
package org.jboss;

import java.io.IOException;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenShiftClient {

    private static final Logger logger = LoggerFactory.getLogger(OpenShiftClient.class);

    private CommandArgs cmdArgs;

    public static void main(String[] args) throws InterruptedException, IOException {

        CommandArgs cmdArgs = new CommandArgs();
        JCommander cmdParser = new JCommander(cmdArgs);
        try {
            cmdParser.setProgramName(OpenShiftClient.class.getName());
            cmdParser.parse(args);
        } catch (ParameterException e) {
            StringBuilder info = new StringBuilder("Specify the url of the server to access using --url\n");
            cmdParser.usage(info);
            System.err.printf(info.toString());
            System.exit(1);
        }

        // Let's authenticate the client
        IClient oclient = null;
        if(cmdArgs.token == null) {
            oclient = new ClientBuilder(cmdArgs.url).withUserName(cmdArgs.user).withPassword(cmdArgs.password).build();
        } else {
            oclient = new ClientBuilder(cmdArgs.url).usingToken(cmdArgs.token).build();
        }

        log("==========================");
        log("Cluster status : " + oclient.getServerReadyStatus());
        log("Username : " + oclient.getAuthorizationContext().getUserName());
        log("Authorized : " + oclient.getAuthorizationContext().isAuthorized());
        log("Token : " + oclient.getAuthorizationContext().getToken());
        log("==========================");

        // Configure the Kubernetes client
        Config config = new ConfigBuilder()
                .withTrustCerts(true)
                .withUsername(cmdArgs.user)
                .withNamespace(cmdArgs.namespace)
                // Token is required otherwise the current user authenticated on the console (oc login) will be used
                //.withOauthToken(oclient.getAuthorizationContext().getToken())
                .withMasterUrl(cmdArgs.url)
                .build();

        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {
            log("Username  : " + cmdArgs.user);
            log("Namespace : " + cmdArgs.namespace);
            log("Master URL : " + config.getMasterUrl());
            log("==========================");

            log("Created RC", client.replicationControllers().inNamespace("test").create(createReplicationController()));

            // Get the RC by label
            log("Get RC by label", client.replicationControllers().withLabel("server", "nginx").list());
            // Get the RC without label
            log("Get RC without label", client.replicationControllers().withoutLabel("server", "apache").list());

            Thread.sleep(5000);
            listPods(client);
            listServices(client);

            client.replicationControllers().inNamespace("test").withName("nginx-controller").delete();
            log("Deleted RC");

            client.close();
        } catch (KubernetesClientException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);

            Throwable[] suppressed = e.getSuppressed();
            if (suppressed != null) {
                for (Throwable t : suppressed) {
                    logger.error(t.getMessage(), t);
                }
            }
        }
    }

    private static void listPods(KubernetesClient client) {
        PodList podList = client.pods().list();
        List<Pod> pods = podList.getItems();
        log("============ Pods ===========");
        for(Pod pod : pods) {
            log(
                    "Pod : " +  pod.getMetadata().getName() + ", " +
                           "Status : " + pod.getStatus().getPhase() + ", " +
                           "IP : " + pod.getStatus().getPodIP()
            );
        }
    }
    private static void listServices(KubernetesClient client) {
        ServiceList serviceList = client.services().list();
        List<Service> services = serviceList.getItems();
        log("============ Services ===========");
        for (Service service : services) {
            ServiceSpec serviceSpec = service.getSpec();
            log(
                    "Service : " + service.getMetadata().getName() + ", " +
                           "Cluster IP : " + serviceSpec.getClusterIP() + ", " +
                           "Port if : " + serviceSpec.getPorts().get(0).getName()
            );
        }
    }

    private static ReplicationController createReplicationController() {
        // Create an RC
        return new ReplicationControllerBuilder()
                .withNewMetadata().withName("nginx-controller").addToLabels("server", "nginx").endMetadata()
                .withNewSpec().withReplicas(2)
                .withNewTemplate()
                .withNewMetadata().addToLabels("server", "nginx").endMetadata()
                .withNewSpec()
                .addNewContainer().withName("nginx").withImage("nginx")
                .addNewPort().withContainerPort(80).endPort()
                .endContainer()
                .endSpec()
                .endTemplate()
                .endSpec().build();
    }

    private static void log(String action, Object obj) {
        logger.info("{}: {}", action, obj);
    }

    private static void log(String action) {
        logger.info(action);
    }

}
