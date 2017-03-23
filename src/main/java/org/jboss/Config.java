package org.jboss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.fabric8.kubernetes.api.model.NamedAuthInfo;
import io.fabric8.kubernetes.api.model.NamedCluster;
import io.fabric8.kubernetes.api.model.NamedContext;
import io.fabric8.kubernetes.api.model.NamedExtension;
import io.fabric8.kubernetes.api.model.Preferences;

/**
 * @author <a href="mailto:cmoullia@redhat.com">Charles Moulliard</a>
 */
@JsonPropertyOrder({
		"apiVersion",
		"kind",
		"metadata",
		"clusters",
		"contexts",
		"current-context",
		"extensions",
		"preferences",
		"users"
})
public class Config {

	private String apiVersion;
	private List<NamedCluster> clusters = new ArrayList<NamedCluster>();
	private List<NamedContext> contexts = new ArrayList<NamedContext>();
	@JsonProperty("current-context")
	private String currentContext;
	private List<NamedExtension> extensions = new ArrayList<NamedExtension>();
	private String kind;
	private Preferences preferences;
	private List<NamedAuthInfo> users = new ArrayList<NamedAuthInfo>();
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	public Config() {
	}

	/**
	 * @param extensions
	 * @param preferences
	 * @param apiVersion
	 * @param currentContext
	 * @param kind
	 * @param contexts
	 * @param clusters
	 * @param users
	 */
	public Config(String apiVersion, List<NamedCluster> clusters,
			List<NamedContext> contexts, String currentContext,
			List<NamedExtension> extensions, String kind, Preferences preferences,
			List<NamedAuthInfo> users) {
		this.apiVersion = apiVersion;
		this.clusters = clusters;
		this.contexts = contexts;
		this.currentContext = currentContext;
		this.extensions = extensions;
		this.kind = kind;
		this.preferences = preferences;
		this.users = users;
	}

	/**
	 * @return The apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion The apiVersion
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	/**
	 * @return The clusters
	 */
	public List<NamedCluster> getClusters() {
		return clusters;
	}

	/**
	 * @param clusters The clusters
	 */
	public void setClusters(List<NamedCluster> clusters) {
		this.clusters = clusters;
	}

	/**
	 * @return The contexts
	 */
	public List<NamedContext> getContexts() {
		return contexts;
	}

	/**
	 * @param contexts The contexts
	 */
	public void setContexts(List<NamedContext> contexts) {
		this.contexts = contexts;
	}

	/**
	 * @return The currentContext
	 */
	@JsonProperty("current-context")
	public String getCurrentContext() {
		return currentContext;
	}

	/**
	 * @param currentContext The current-context
	 */
	@JsonProperty("current-context")
	public void setCurrentContext(String currentContext) {
		this.currentContext = currentContext;
	}

	/**
	 * @return The extensions
	 */
	public List<NamedExtension> getExtensions() {
		return extensions;
	}

	/**
	 * @param extensions The extensions
	 */
	public void setExtensions(List<NamedExtension> extensions) {
		this.extensions = extensions;
	}

	/**
	 * @return The kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind The kind
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}

	/**
	 * @return The preferences
	 */
	public Preferences getPreferences() {
		return preferences;
	}

	/**
	 * @param preferences The preferences
	 */
	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	/**
	 * @return The users
	 */
	public List<NamedAuthInfo> getUsers() {
		return users;
	}

	/**
	 * @param users The users
	 */
	public void setUsers(List<NamedAuthInfo> users) {
		this.users = users;
	}

	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}
