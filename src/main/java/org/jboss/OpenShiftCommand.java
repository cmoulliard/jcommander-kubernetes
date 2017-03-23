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

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
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
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.ProjectRequest;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenShiftCommand {

	private static final Logger logger = LoggerFactory.getLogger(OpenShiftCommand.class);

	private CommandArgs cmdArgs;

	public static void main(String[] args) throws Exception {

		CommandArgs cmdArgs = new CommandArgs();
		JCommander cmdParser = new JCommander(cmdArgs);
		try {
			cmdParser.setProgramName(OpenShiftCommand.class.getName());
			cmdParser.parse(args);
		}
		catch (ParameterException e) {
			StringBuilder info = new StringBuilder(
					"Specify the url of the server to access using --url\n");
			cmdParser.usage(info);
			System.err.printf(info.toString());
			System.exit(1);
		}

		// Configure the Kubernetes client
		Config config;
		if (cmdArgs.token == null) {
			config = new ConfigBuilder()
					.withMasterUrl(cmdArgs.url)
					.withTrustCerts(true)
					.withUsername(cmdArgs.user)
					.withPassword(cmdArgs.password)
					.withNamespace(cmdArgs.namespace)
					.build();
		}
		else {
			config = new ConfigBuilder()
					.withMasterUrl(cmdArgs.url)
					.withTrustCerts(true)
					.withOauthToken(cmdArgs.token)
					.withNamespace(cmdArgs.namespace)
					.build();
		}

		// The OpenShiftClient must be used in order to include the okhttp interceptro : OpenShiftOAuthInterceptor
		// responsible to issue for each request the Authorization Bearer: token which is required to be correctly
		// authenticated on OpenShift
		OpenShiftClient client = new DefaultOpenShiftClient(config);

		try {
			log("Username  : " + cmdArgs.user);
			log("Namespace : " + cmdArgs.namespace);
			log("Master URL : " + config.getMasterUrl());
			log("==========================");

			// Let's create the project if it doesn't exist
			ProjectRequest request = null;
			try {
				Project project  = client.projects().withName(cmdArgs.namespace).get();
			} catch(KubernetesClientException kubex) {
				log("Project doesn't exist. So it will be created !");
				request = client.projectrequests().createNew()
						.withNewMetadata()
						.withName(cmdArgs.namespace)
						.endMetadata()
						.done();
				log("The project " + cmdArgs.namespace + " has been created !");
			}

			log("Created RC",
					client.replicationControllers().inNamespace(cmdArgs.namespace)
							.create(createReplicationController()));

			// Get the RC by label
			log("Get RC by label",
					client.replicationControllers().withLabel("server", "nginx").list());
			// Get the RC without label
			log("Get RC without label",
					client.replicationControllers().withoutLabel("server", "apache")
							.list());

			Thread.sleep(5000);
			listPods(client);
			listServices(client);

			client.replicationControllers().inNamespace(cmdArgs.namespace)
					.withName("nginx-controller").delete();
			log("Deleted RC");

		}
		catch (KubernetesClientException e) {
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
		for (Pod pod : pods) {
			log("Pod : " + pod.getMetadata().getName() + ", " + "Status : " + pod
					.getStatus().getPhase() + ", " + "IP : " + pod.getStatus()
					.getPodIP());
		}
	}

	private static void listServices(KubernetesClient client) {
		ServiceList serviceList = client.services().list();
		List<Service> services = serviceList.getItems();
		log("============ Services ===========");
		for (Service service : services) {
			ServiceSpec serviceSpec = service.getSpec();
			log("Service : " + service.getMetadata().getName() + ", " + "Cluster IP : "
					+ serviceSpec.getClusterIP() + ", " + "Port if : " + serviceSpec
					.getPorts().get(0).getName());
		}
	}

	private static ReplicationController createReplicationController() {
		// Create an RC
		return new ReplicationControllerBuilder().withNewMetadata()
				.withName("nginx-controller").addToLabels("server", "nginx").endMetadata()
				.withNewSpec().withReplicas(2).withNewTemplate().withNewMetadata()
				.addToLabels("server", "nginx").endMetadata().withNewSpec()
				.addNewContainer().withName("nginx").withImage("nginx").addNewPort()
				.withContainerPort(80).endPort().endContainer().endSpec().endTemplate()
				.endSpec().build();
	}

	private static void log(String action, Object obj) {
		logger.info("{}: {}", action, obj);
	}

	private static void log(String action) {
		logger.info(action);
	}

}
