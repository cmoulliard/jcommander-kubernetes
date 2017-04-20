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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

			// Extract the command
			/* Using @Subparameter - doesn't work
			String operation = cmdArgs.cmdParameters.operation;
			String type = cmdArgs.cmdParameters.type;
			String item = cmdArgs.cmdParameters.item;
			String output = cmdArgs.cmdParameters.output;

						if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(PODS))) {
				listPods(client);
			}

			if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(POD)) && (item.toLowerCase() != null)) {
				if(output == null) {
					getPod(client, item.toLowerCase());
				} else {
					getPod(client, item.toLowerCase(),output);
				}
			}

			if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(SERVICES))) {
				listServices(client);
			}

			if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(SERVICE)) && (item.toLowerCase() != null)) {
				getService(client,item.toLowerCase());
			}

			if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(ROUTES))) {
				listRoutes(client);
			}

			if((operation.toLowerCase().equals(GET)) && (type.toLowerCase().equals(ROUTE)) && (item.toLowerCase() != null)) {
					getRoute(client,item.toLowerCase());
			}
			*/

			//String [] cmdParams = cmdArgs.cmd.split(" ");

			List<String> cmdParamsList = cmdArgs.cmdParameters;

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(ROLEBINDINGS))) {
				listRoleBindings(client);
			}
			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(ROLEBINDING)) && (cmdParamsList.get(2).toLowerCase() != null)) {
				listRoleBindings(client);
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(PODS))) {
				listPods(client);
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(POD)) && (cmdParamsList.get(2).toLowerCase() != null)) {
				if(cmdParamsList.size() == 3) {
					getPod(client, cmdParamsList.get(2).toLowerCase());
				} else {
					getPod(client, cmdParamsList.get(2).toLowerCase(),cmdParamsList.get(3));
				}
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(SERVICES))) {
				listServices(client);
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(SERVICE)) && (cmdParamsList.get(2).toLowerCase() != null)) {
				getService(client,cmdParamsList.get(3).toLowerCase());
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(ROUTES))) {
				listRoutes(client);
			}

			if((cmdParamsList.get(0).toLowerCase().equals(GET)) && (cmdParamsList.get(1).toLowerCase().equals(ROUTE)) && (cmdParamsList.get(2).toLowerCase() != null)) {
					getRoute(client,cmdParamsList.get(3).toLowerCase());
			}

		}
		catch (Exception e) {
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

}
