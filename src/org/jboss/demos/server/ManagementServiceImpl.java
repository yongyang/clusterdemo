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

    List<ClusterNode> clusterNodes;
    {
        clusterNodes = new ArrayList<ClusterNode>();

        for(int i=0; i<10; i++){
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(9000 + i);
            clusterNodes.add(node);
        }
    }


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

        count++;
        if(count == 10 ) {
            clusterNodes.remove(0);
        }

        if(count == 20) {
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(8888);
            if(!clusterNodes.contains(node)) {
                clusterNodes.add(node);
            }
        }

        if(count == 30) {
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(7777);
            if(!clusterNodes.contains(node)) {
                clusterNodes.add(node);
            }
            clusterNodes.remove(0);
            count = 0;
        }


        System.out.println("count: " + count);
        return clusterNodes;
    }

}
