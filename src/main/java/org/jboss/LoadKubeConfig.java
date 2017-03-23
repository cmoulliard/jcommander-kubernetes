package org.jboss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
public class LoadKubeConfig {

	public static void main(String[] args) throws IOException {

		String CFG_FILE = System.getProperty("user.home") + "/.kube/config";
		String MASTER_IP_PORT = "192.168.64.25:8443";
		String USER = "test";

		File cfgFile = new File(CFG_FILE);

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		Config kubeCfg = mapper.readValue(cfgFile, Config.class);

		String newCurrentContext = "test/" + MASTER_IP_PORT.replaceAll("\\.", "-") + "/" + USER;
		kubeCfg.setCurrentContext(newCurrentContext);

		FileWriter cfgWriter = new FileWriter(CFG_FILE);
		mapper.writeValue(cfgWriter,kubeCfg);
		cfgWriter.close();
	}

}
