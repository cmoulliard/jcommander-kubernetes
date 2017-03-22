package org.jboss;

import com.beust.jcommander.Parameter;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
public class CommandArgs {
	@Parameter(names = "--url", description = "The url of the kubernetes server", required = true)
	public String url;
	@Parameter(names = "--namespace", description = "The namespace or project", required = false)
	public String namespace = "default";
	@Parameter(names = "--user", description = "The user to authenticate as", required = false)
	public String user = "admin";
	@Parameter(names = "--password", description = "The password to authenticate with", required = false)
	public String password = "admin";
	@Parameter(names = "--token", description = "The openshift user token", required = false)
	public String token;
}
