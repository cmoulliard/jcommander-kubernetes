package org.jboss;

import com.beust.jcommander.Parameter;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
public class OpenShiftArgs extends AuthArgs {
	@Parameter(names = "--cmd", description = "OpenShift command", required = true)
	public String cmd;
}
