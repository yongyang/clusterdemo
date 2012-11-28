package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.jboss.demos.shared.ClusterInfo;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * The client side stub for the RPC service.
 */
@Remote
public interface ManagementService {

    ClusterInfo getClusterInfo(String targetNodeIp) throws IllegalArgumentException;

    boolean invokeOperation(String ip, String name, String[] parameters);

}
