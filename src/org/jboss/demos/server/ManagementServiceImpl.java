package org.jboss.demos.server;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.demos.client.ManagementService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.JChannel;

import java.util.List;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ManagementServiceImpl extends RemoteServiceServlet implements
                                                              ManagementService {

  public String getClusterInfo(String input) {

      JChannel channel = (JChannel) CurrentServiceContainer.getServiceContainer().getService(ServiceName.JBOSS.append("jgroups", "channel", "web")).getValue();
      List<Address> members = channel.getView().getMembers();

      return null;

  }
}
