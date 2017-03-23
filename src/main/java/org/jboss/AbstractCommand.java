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
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.api.model.Project;
import io.fabric8.openshift.api.model.ProjectRequest;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteList;
import io.fabric8.openshift.api.model.RouteSpec;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.jboss.util.TableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

	protected final static String GET = "get";
	protected final static String PODS = "pods";
	protected final static String SERVICES = "services";
	protected final static String ROUTES = "routes";

	protected static void listPods(KubernetesClient client) {
		PodList podList = client.pods().list();
		List<Pod> pods = podList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Pods\n");
		builder.addRow("NAME","STATUS","IP");
		builder.addRow("====","======","==");
		for (Pod pod : pods) {
			builder.addRow(pod.getMetadata().getName(),
					pod.getStatus().getPhase(),
					pod.getStatus().getPodIP());
		}
		log(builder.toString());
	}

	protected static void listServices(KubernetesClient client) {
		ServiceList serviceList = client.services().list();
		List<Service> services = serviceList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Services\n");
		builder.addRow("NAME","CLUSTER-IP","PORT");
		builder.addRow("====","==========","====");
		for (Service service : services) {
			ServiceSpec serviceSpec = service.getSpec();
			builder.addRow(service.getMetadata().getName(),
					serviceSpec.getClusterIP(),
					serviceSpec.getPorts().get(0).getName());
		}
		log(builder.toString());
	}

	protected static void listRoutes(KubernetesClient client) {
		RouteList routeList = null;
		List<Route> routes = routeList.getItems();
		log("============ Routes ===========");
		for (Route route : routes) {
			RouteSpec routeSpec = route.getSpec();
			log("Route : " + route.getMetadata().getName() + ", " + "Status : " + route.getStatus());
		}
	}

	protected static ReplicationController createReplicationController() {
		// Create a RC
		return new ReplicationControllerBuilder()
				.withNewMetadata()
				.withName("nginx-controller").addToLabels("server", "nginx")
				.endMetadata()
				.withNewSpec()
				.withReplicas(2).withNewTemplate()
				.withNewMetadata()
				.addToLabels("server", "nginx")
				.endMetadata().withNewSpec()
				.addNewContainer().withName("nginx").withImage("nginx").addNewPort()
				.withContainerPort(80).endPort().endContainer().endSpec().endTemplate()
				.endSpec().build();
	}

	protected static void log(String action, Object obj) {
		logger.info("{}: {}", action, obj);
	}

	protected static void log(String action) {
		logger.info(action);
	}

}
