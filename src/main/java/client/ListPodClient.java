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
package client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.openshift.restclient.ClientBuilder;
import com.openshift.restclient.IClient;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.fabric8.kubernetes.client.Config.KUBERNETES_KUBECONFIG_FILE;

public class ListPodClient {

    private static final Logger logger = LoggerFactory.getLogger(ListPodClient.class);

    private CommandArgs cmdArgs;

    public static void main(String[] args) throws InterruptedException, IOException {

        CommandArgs cmdArgs = new CommandArgs();
        JCommander cmdParser = new JCommander(cmdArgs);
        try {
            cmdParser.setProgramName(ListPodClient.class.getName());
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

        System.out.println("Cluster status : " + oclient.getServerReadyStatus());
        System.out.println("Username : " + oclient.getAuthorizationContext().getUserName());
        System.out.println("Authorized : " + oclient.getAuthorizationContext().isAuthorized());
        System.out.println("Token : " + oclient.getAuthorizationContext().getToken());

        System.out.println("Pod : " + oclient.list("Pod"));
        System.out.println("Users : " + oclient.list("User"));

        // Configure the Kubernetes client
        Config config = new ConfigBuilder()
                .withUsername(cmdArgs.user)
                .withNamespace("default")
                // Token is required otherwise the current user will be used
                .withOauthToken(oclient.getAuthorizationContext().getToken())
                .withMasterUrl(cmdArgs.url)
                .build();

        try (final KubernetesClient client = new DefaultKubernetesClient(config)) {

            System.out.println("User : " + cmdArgs.user);
            System.out.println("Namespace : " + cmdArgs.namespace);
            System.out.println("Url : " + cmdArgs.url);
            System.out.println("Master URL : " + config.getMasterUrl());

            Namespace namespace = client.namespaces().withName(cmdArgs.namespace).get();
            System.out.println(
                    "Type : " + namespace.getKind() + ", " + namespace.getMetadata().getName()
            );

            PodList podList = client.pods().list();
            List<Pod> pods = podList.getItems();
            for(Pod pod : pods) {
                System.out.println(
                     "Type : " + pod.getKind() + ", " +
                     "Name : " + pod.getMetadata().getName() + ", " +
                     "Status : " + pod.getStatus().getPhase() + ", " +
                     "IP : " + pod.getStatus().getPodIP()
                );
            }

            ServiceList serviceList = client.services().list();
            List<Service> services = serviceList.getItems();
            for (Service service : services) {
                ServiceSpec serviceSpec = service.getSpec();
                System.out.println(
                        "Name       : " + service.getMetadata().getName() + ", " +
                        "Cluster IP : " + serviceSpec.getClusterIP() + ", " +
                        "Port    if : " + serviceSpec.getPorts().get(0).getName()
                );
            }

            client.close();
        } catch (KubernetesClientException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static Config parseConfig(File file) throws IOException {
        ObjectMapper mapper = null;
        File kubeConfigFile = new File(
                Utils.getSystemPropertyOrEnvVar(KUBERNETES_KUBECONFIG_FILE,
                        new File(getHomeDir(), ".kube" + File.separator + "config").toString()));
        boolean kubeConfigFileExists = Files.isRegularFile(kubeConfigFile.toPath());
        if (kubeConfigFileExists) {
            mapper = new ObjectMapper(new YAMLFactory());
        }
        return mapper.readValue(kubeConfigFile, Config.class);
    }

    private static String getHomeDir() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("win")) {
            String homeDrive = System.getenv("HOMEDRIVE");
            String homePath = System.getenv("HOMEPATH");
            if (homeDrive != null && !homeDrive.isEmpty() && homePath != null && !homePath.isEmpty()) {
                String homeDir = homeDrive + homePath;
                File f = new File(homeDir);
                if (f.exists() && f.isDirectory()) {
                    return homeDir;
                }
            }
            String userProfile = System.getenv("USERPROFILE");
            if (userProfile != null && !userProfile.isEmpty()) {
                File f = new File(userProfile);
                if (f.exists() && f.isDirectory()) {
                    return userProfile;
                }
            }
        }
        String home = System.getenv("HOME");
        if (home != null && !home.isEmpty()) {
            File f = new File(home);
            if (f.exists() && f.isDirectory()) {
                return home;
            }
        }

        //Fall back to user.home should never really get here
        return System.getProperty("user.home", ".");
    }

}
