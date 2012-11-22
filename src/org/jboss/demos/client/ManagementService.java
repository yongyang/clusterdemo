package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.jboss.demos.shared.ClusterInfo;
import org.jboss.demos.shared.ClusterNode;

import java.util.List;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("managementservice")
public interface ManagementService extends RemoteService {

    ClusterInfo getClusterInfo(String targetNodeIp) throws IllegalArgumentException;

    boolean invokeOperation(String name, String ip, String[] parameters) throws Exception;

}
