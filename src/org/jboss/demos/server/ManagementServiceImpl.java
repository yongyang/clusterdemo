package org.jboss.demos.server;

import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.demos.client.ManagementService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.PhysicalAddress;
import org.jgroups.stack.IpAddress;
import org.jgroups.util.UUID;

import java.util.Arrays;
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

      StringBuffer sb = new StringBuffer();
      for(Address address : members){
          IpAddress ipAddress = (IpAddress)channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
          sb.append(ipAddress.getClass().getName()).append(": ").append(ipAddress.getIpAddress().getHostAddress()).append(":").append(ipAddress.getPort()).append("\n");
      }

      return sb.toString();

  }
}
