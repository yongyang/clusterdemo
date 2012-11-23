package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.jboss.demos.shared.ClusterInfo;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("managementservice")
public interface ManagementService extends RemoteService {

    ClusterInfo getClusterInfo(String targetNodeIp) throws IllegalArgumentException;

    boolean invokeOperation(String ip, String name, String[] parameters);

}
