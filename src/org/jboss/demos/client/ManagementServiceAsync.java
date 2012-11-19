package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.demos.shared.ClusterNode;

import java.util.List;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ManagementServiceAsync {

    void getClusterInfo(String input, AsyncCallback<List<ClusterNode>> callback) throws IllegalArgumentException;
}
