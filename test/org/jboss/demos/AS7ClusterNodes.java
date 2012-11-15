package org.jboss.demos;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12-11-14
 * Time: 下午11:23
 * To change this template use File | Settings | File Templates.
 */
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Channel;
import org.jgroups.JChannel;

import java.util.Hashtable;
import java.util.List;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class AS7ClusterNodes {
    public static void main(String[] args) throws Exception {

        String host = "127.0.0.1"; // Your JBoss Native Interface Bin Address default is localhost
        int port = 9999;             // management port     // In Domain Mode you should use  4447 port of individual server
        String urlString = "service:jmx:remoting-jmx://" + host + ":" + port;
        System.out.println(" \n\n\t**** urlString: " + urlString);
        String webClusterObjectName="jgroups:type=channel,cluster=\"web\"";
        //String ejbClusterObjectName="jgroups:type=channel,cluster=\"ejb\"";

        JMXServiceURL serviceURL = new JMXServiceURL(urlString);

        Hashtable h = new Hashtable();
        String[] credentials = new String[] { "admin", "123456" };
        h.put("jmx.remote.credentials", credentials);

        JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL,null);
        MBeanServerConnection connection = jmxConnector.getMBeanServerConnection();
        ObjectName objectName = new ObjectName(webClusterObjectName);
        String clusterView = (String) connection.getAttribute(objectName,"View");
        Long receivedMessages = (Long) connection.getAttribute(objectName,"ReceivedMessages");
        String name = (String) connection.getAttribute(objectName, "Name");
        String clusterName = (String) connection.getAttribute(objectName,"ClusterName");

        System.out.println(" clusterView = " + clusterView);
        System.out.println(" receivedMessages = " + receivedMessages);
        System.out.println(" name = " + name);
        System.out.println(" clusterName = " + clusterName);
        jmxConnector.close();
    }

    public static void main2(String[] args) throws Exception{
        JChannel channel = (JChannel) CurrentServiceContainer.getServiceContainer().getService(ServiceName.JBOSS.append("jgroups", "channel", "web")).getValue();
        List<Address> members = channel.getView().getMembers();
    }

}
