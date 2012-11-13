package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ManagementServiceAsync {

    void getClusterInfo(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
}
