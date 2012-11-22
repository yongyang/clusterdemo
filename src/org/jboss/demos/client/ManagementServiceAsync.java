package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.demos.shared.ClusterInfo;
import org.jboss.demos.shared.ClusterNode;

import java.util.List;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface ManagementServiceAsync {

    void getClusterInfo(String targetNodeIp, AsyncCallback<ClusterInfo> callback) throws IllegalArgumentException;

    void invokeOperation(String name, String ip, String[] parameters, AsyncCallback<Boolean> callback) throws Exception;
}
