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
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenShiftCommand extends AbstractCommand {

	private static final Logger logger = LoggerFactory.getLogger(OpenShiftCommand.class);

	public static void main(String[] args) throws Exception {

		OpenShiftArgs cmdArgs = new OpenShiftArgs();
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
			config = new ConfigBuilder().withMasterUrl(cmdArgs.url).withTrustCerts(true)
					.withUsername(cmdArgs.user).withPassword(cmdArgs.password)
					.withNamespace(cmdArgs.namespace).build();
		}
		else {
			config = new ConfigBuilder().withMasterUrl(cmdArgs.url).withTrustCerts(true)
					.withOauthToken(cmdArgs.token).withNamespace(cmdArgs.namespace)
					.build();
		}

		// The OpenShiftClient must be used in order to include the okhttp interceptor : OpenShiftOAuthInterceptor
		// responsible to issue for each request the Authorization Bearer: token which is required to be correctly
		// authenticated on OpenShift
		OpenShiftClient client = new DefaultOpenShiftClient(config);
		ProjectRequest request = null;

		try {
			log("Username  : " + cmdArgs.user);
			log("Namespace : " + cmdArgs.namespace);
			log("Master URL : " + config.getMasterUrl());
			log("==========================");

			// Let's create the project if it doesn't exist

			Project project = client.projects().withName(cmdArgs.namespace).get();
			if (project == null) {
				log("Project doesn't exist. So it will be created !");
				request = client.projectrequests()
						        .createNew()
						        .withNewMetadata()
						          .withName(cmdArgs.namespace)
						        .endMetadata()
						        .done();
				log("The project " + cmdArgs.namespace + " has been created !");
			}

			// Extract the command
			String[] cmdParams = cmdArgs.cmd.split(" ");

			if((cmdParams[0].toLowerCase().equals(GET)) && (cmdParams[1].toLowerCase().equals(PODS))) {
				listPods(client);
			}

			if((cmdParams[0].toLowerCase().equals(GET)) && (cmdParams[1].toLowerCase().equals(SERVICES))) {
				listServices(client);
			}

			if((cmdParams[0].toLowerCase().equals(GET)) && (cmdParams[1].toLowerCase().equals(ROUTES))) {
				listServices(client);
			}

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
		finally {
			if (request != null) {
				client.projects().withName(cmdArgs.namespace).delete();
				log("Project " + cmdArgs.namespace + " has been deleted.");
			}
		}
	}

}
