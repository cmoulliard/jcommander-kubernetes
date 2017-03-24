package org.jboss;

import com.beust.jcommander.SubParameter;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
public class CmdParameters {

	@SubParameter(order = 0)
	String operation;

	@SubParameter(order = 1)
	String type;

	@SubParameter(order = 2)
	String item;

	@SubParameter(order = 3)
	String output;
}
