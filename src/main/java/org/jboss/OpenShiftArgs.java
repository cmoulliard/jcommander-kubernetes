package org.jboss;

import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
public class OpenShiftArgs extends AuthArgs {
	@Parameter(names = "--cmd", description = "OpenShift command", required = true, variableArity = true)
	// public String cmd;
	// public CmdParameters cmdParameters;
	public List<String> cmdParameters;
}
