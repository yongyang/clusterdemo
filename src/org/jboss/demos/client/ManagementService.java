package org.jboss.demos.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("managementservice")
public interface ManagementService extends RemoteService {

    String getClusterInfo(String name) throws IllegalArgumentException;

}
