package org.jboss.demos.server;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.demos.client.ManagementService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.demos.shared.ClusterNode;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.stack.IpAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ManagementServiceImpl extends RemoteServiceServlet implements
                                                              ManagementService {

    private int count = 0;

/*
    public List<ClusterNode> getClusterInfo(String input) {

        JChannel channel = (JChannel) CurrentServiceContainer.getServiceContainer().getService(ServiceName.JBOSS.append("jgroups", "channel", "web")).getValue();
        List<Address> members = channel.getView().getMembers();

        List<ClusterNode> clusterNodes = new ArrayList<ClusterNode>(members.size());
        for(Address address : members){
            IpAddress ipAddress = (IpAddress)channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
            ClusterNode node = new ClusterNode(ipAddress.getIpAddress().getHostAddress(), ipAddress.getPort());
            clusterNodes.add(node);
        }

        return clusterNodes;
    }
*/

    // for mock test
    public List<ClusterNode> getClusterInfo(String input) {

        List<ClusterNode> clusterNodes = new ArrayList<ClusterNode>();

        count++;
        System.out.println("count: " + count);
        for(int i=0; i<10; i++){
            if(count > 10 && i==9) {
                System.out.println("break: " + i);
                break;
            }
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(9000 + i);
            clusterNodes.add(node);
        }
        return clusterNodes;
    }

}
