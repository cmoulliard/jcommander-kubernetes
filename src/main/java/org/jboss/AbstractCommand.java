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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.openshift.api.model.Route;
import io.fabric8.openshift.api.model.RouteList;
import io.fabric8.openshift.api.model.RouteSpec;
import io.fabric8.openshift.client.OpenShiftClient;
import org.jboss.model.Config;
import org.jboss.util.TableBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

	protected final static String GET = "get";
	protected final static String PODS = "pods";
	protected final static String POD = "pod";
	protected final static String SERVICES = "services";
	protected final static String SERVICE = "service";
	protected final static String ROUTES = "routes";
	protected final static String ROUTE = "route";

	protected static void listPods(OpenShiftClient client) {
		PodList podList = client.pods().list();
		List<Pod> pods = podList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Pods\n");
		builder.addRow("NAME", "STATUS", "IP");
		builder.addRow("====", "======", "==");
		for (Pod pod : pods) {
			builder.addRow(pod.getMetadata().getName(), pod.getStatus().getPhase(),
					pod.getStatus().getPodIP());
		}
		log(builder.toString());
	}

	protected static void getPod(OpenShiftClient client, String content)
			throws JsonProcessingException {
		getPod(client,content,"");
	}

	protected static void getPod(OpenShiftClient client, String content, String format)
			throws JsonProcessingException {
		PodList podList = client.pods().list();
		List<Pod> pods = podList.getItems();
		Pod podChoosen = null;
		for (Pod pod : pods) {
			if(pod.getMetadata().getName().equals(content)) {
				podChoosen = pod;
			}
			logFormat(format,podChoosen);
			break;
		}
	}

	private static void logFormat(String format, Object object)
			throws JsonProcessingException {
		switch (format) {
		case "yaml":
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			log(mapper.writeValueAsString(object));
			break;
		case "json":
			mapper = new ObjectMapper();
			log(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
			break;
		default:
			Pod pod = null;
			if(Pod.class.isInstance(object)) {
				pod = (Pod)object;
			}
			TableBuilder builder = new TableBuilder();
			builder.addRow("Pod\n");
			builder.addRow("NAME", "STATUS", "IP");
			builder.addRow("====", "======", "==");
			builder.addRow(pod.getMetadata().getName(),
					pod.getStatus().getPhase(),
					pod.getStatus().getPodIP());
			log(builder.toString());
			break;
		}
	}

	protected static void listServices(OpenShiftClient client) {
		ServiceList serviceList = client.services().list();
		List<Service> services = serviceList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Services\n");
		builder.addRow("NAME", "CLUSTER-IP", "PORT");
		builder.addRow("====", "==========", "====");
		for (Service service : services) {
			ServiceSpec serviceSpec = service.getSpec();
			builder.addRow(service.getMetadata().getName(), serviceSpec.getClusterIP(),
					serviceSpec.getPorts().get(0).getName());
		}
		log(builder.toString());
	}

	protected static void getService(OpenShiftClient client, String content) {
		ServiceList serviceList = client.services().list();
		List<Service> services = serviceList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Service\n");
		builder.addRow("NAME", "CLUSTER-IP", "PORT");
		builder.addRow("====", "==========", "====");
		for (Service service : services) {
			if (service.getMetadata().getName().equals(content)) {
				ServiceSpec serviceSpec = service.getSpec();
				builder.addRow(service.getMetadata().getName(),
						serviceSpec.getClusterIP(),
						serviceSpec.getPorts().get(0).getName());
			}
			break;
		}
		log(builder.toString());
	}

	protected static void listRoutes(OpenShiftClient client) {
		RouteList routeList = client.routes().list();
		List<Route> routes = routeList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Routes\n");
		builder.addRow("NAME", "HOST/PORT", "SERVICES", "PORT");
		builder.addRow("====", "=========", "========", "====");
		for (Route route : routes) {
			RouteSpec routeSpec = route.getSpec();
			builder.addRow(route.getMetadata().getName(), routeSpec.getHost(),
					routeSpec.getTo().getName(),
					routeSpec.getPort().getTargetPort().getStrVal());
		}
		log(builder.toString());
	}

	protected static void getRoute(OpenShiftClient client, String content) {
		RouteList routeList = client.routes().list();
		List<Route> routes = routeList.getItems();
		TableBuilder builder = new TableBuilder();
		builder.addRow("Route\n");
		builder.addRow("NAME", "HOST/PORT", "SERVICES", "PORT");
		builder.addRow("====", "=========", "========", "====");
		for (Route route : routes) {
			if (route.getMetadata().getName().equals(content)) {
				RouteSpec routeSpec = route.getSpec();
				builder.addRow(route.getMetadata().getName(), routeSpec.getHost(),
						routeSpec.getTo().getName(),
						routeSpec.getPort().getTargetPort().getStrVal());
			}
			break;
		}
		log(builder.toString());
	}

	protected static ReplicationController createReplicationController() {
		// Create a RC
		return new ReplicationControllerBuilder().withNewMetadata()
				.withName("nginx-controller").addToLabels("server", "nginx").endMetadata()
				.withNewSpec().withReplicas(2).withNewTemplate().withNewMetadata()
				.addToLabels("server", "nginx").endMetadata().withNewSpec()
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
