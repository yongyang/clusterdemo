package org.jboss.demos.server;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.as.server.CurrentServiceContainer;
import org.jboss.demos.client.ManagementService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.jboss.demos.server.dmr.ModelDescriptionConstants;
import org.jboss.demos.server.dmr.ModelNode;
import org.jboss.demos.shared.ClusterInfo;
import org.jboss.demos.shared.ClusterNode;
import org.jboss.msc.service.ServiceName;
import org.jgroups.Address;
import org.jgroups.Event;
import org.jgroups.JChannel;
import org.jgroups.stack.IpAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.jboss.demos.server.dmr.ModelDescriptionConstants.MODEL_DESCRIPTION;
import static org.jboss.demos.server.dmr.ModelDescriptionConstants.OP;
import static org.jboss.demos.server.dmr.ModelDescriptionConstants.ADDRESS;
import static org.jboss.demos.server.dmr.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.demos.server.dmr.ModelDescriptionConstants.INCLUDE_RUNTIME;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ManagementServiceImpl extends RemoteServiceServlet implements ManagementService {

    public static final String MANAGEMENT_PORT = "MANAGEMENT_PORT";
    public static final String MANAGEMENT_USER = "MANAGEMENT_USER";
    public static final String MANAGEMENT_PASSWORD = "MANAGEMENT_PASSWORD";
    public static final String APPLICATION_DMR_ENCODED = "application/dmr-encoded";

    private long count = 0;

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


    public ClusterInfo getClusterInfo(String targetNodeIp) {

        JChannel channel = (JChannel) CurrentServiceContainer.getServiceContainer().getService(ServiceName.JBOSS.append("jgroups", "channel", "web")).getValue();
        List<Address> members = channel.getView().getMembers();

        List<ClusterNode> clusterNodes = new ArrayList<ClusterNode>(members.size());
        for(Address address : members){
            IpAddress ipAddress = (IpAddress)channel.down(new Event(Event.GET_PHYSICAL_ADDRESS, address));
            ClusterNode node = new ClusterNode();
            node.setIp(ipAddress.getIpAddress().getHostAddress());
            node.setPort(ipAddress.getPort());
            // TODO: get the status of recivedBytes: channel.getReceivedBytes();
            if(count % 5 == 0) { // every 5
                double usage = getMemoryUsage(node.getIp());
                System.out.println("usage: " + usage);
                node.setMemUsage(usage);
            }
            clusterNodes.add(node);
        }

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterNodes(clusterNodes);
        clusterInfo.setReceivedBytes(channel.getReceivedBytes());

        count++;
        return clusterInfo;
    }

    // for mock test
/*
    public ClusterInfo getClusterInfo(String targetNodeIp) {

        count++;


        if(count == 10) { // test add
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(8888);
            if(!clusterNodes.contains(node)) {
                clusterNodes.add(node);
            }

            // test receiving msg
            clusterNodes.get(0).setReceivedBytes(System.currentTimeMillis());

        }

        if(count == 21) { // test reloading, remove/start
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(8888);
            clusterNodes.remove(node);
        }

        if(count == 22) { // start
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(8888);
            if(!clusterNodes.contains(node)) {
                clusterNodes.add(node);
            }
        }


        if(count == 23) { // test start
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(6666);
            if(!clusterNodes.contains(node)) {
                clusterNodes.add(node);
            }
        }
        if(count == 25) { // test shutdown
            ClusterNode node = new ClusterNode();
            node.setIp("127.0.0.1");
            node.setPort(6666);
            clusterNodes.remove(node);
        }

        if(count == 30) {
            count = 0;
        }
        Collections.sort(clusterNodes);
        System.out.println("count: " + count);

        ClusterInfo clusterInfo = new ClusterInfo();
        clusterInfo.setClusterNodes(clusterNodes);
        if(count % 6 == 0) {
            clusterInfo.setReceivedBytes(System.currentTimeMillis());
        }
        return clusterInfo;
    }
*/

    public boolean invokeOperation(String ip, String name,  String[] parameters) {
        ModelNode operation = new ModelNode();
        operation.get(OP).set(name);
        operation.get(ModelDescriptionConstants.ADDRESS).add("/");

        try {
            return !invokeOperationByHttp(ip, operation).isFailure();
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ModelNode invokeOperationByHttp(String ip, ModelNode operModelNode) throws Exception {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {

            int port = Integer.parseInt(getServletContext().getInitParameter(MANAGEMENT_PORT).trim());
            String user = getServletContext().getInitParameter(MANAGEMENT_USER);
            String password = getServletContext().getInitParameter(MANAGEMENT_PASSWORD);


            httpClient.getCredentialsProvider().setCredentials(
                    new AuthScope(AuthScope.ANY_HOST, port, "ManagementRealm"),
                    new UsernamePasswordCredentials(user, password));

            //private String domainApiUrl = "http://localhost:9990/management";
            HttpPost httppost = new HttpPost("http://" + ip + ":" + port + "/management");
            httppost.setEntity(new StringEntity(operModelNode.toBase64String()));
            httppost.setHeader("Accept", APPLICATION_DMR_ENCODED);
            httppost.setHeader("Content-Type", APPLICATION_DMR_ENCODED);

//            System.out.println("executing request " + httppost.getRequestLine() + ", " + Arrays.toString(httppost.getAllHeaders()));
//            System.out.println(operModelNode.toString());

            HttpResponse response;
            response = httpClient.execute(httppost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
//                System.out.println("Response content length: " + entity.getContentLength());
                BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));

                StringBuffer sb = new StringBuffer();
                String s = null;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                }

                EntityUtils.consume(entity);

                ModelNode returnModelNode = ModelNode.fromBase64(sb.toString());
//                System.out.println(returnModelNode.toString());
                return returnModelNode;

            }
            return null;
        }
        finally {
            httpClient.getConnectionManager().shutdown();
        }
    }

    /*

       // memory

       ModelNode memory = new ModelNode();
       memory.get(ADDRESS).set(address);
       memory.get(ADDRESS).add("core-service", "platform-mbean");
       memory.get(ADDRESS).add("type", "memory");
       memory.get(OP).set(READ_RESOURCE_OPERATION);
       memory.get(INCLUDE_RUNTIME).set(true);

       steps.add(memory);

       // threads

       ModelNode threads = new ModelNode();
       threads.get(ADDRESS).set(address);
       threads.get(ADDRESS).add("core-service", "platform-mbean");
       threads.get(ADDRESS).add("type", "threading");
       threads.get(OP).set(READ_RESOURCE_OPERATION);
       threads.get(INCLUDE_RUNTIME).set(true);

    */


    private double getMemoryUsage(String ip){
        ModelNode memory = new ModelNode();
        memory.get(ADDRESS).set(new ModelNode());
        memory.get(ADDRESS).add("core-service", "platform-mbean");
        memory.get(ADDRESS).add("type", "memory");
        memory.get(OP).set(READ_RESOURCE_OPERATION);
        memory.get(INCLUDE_RUNTIME).set(true);
        try {
            ModelNode resultModelNode = invokeOperationByHttp(ip, memory);
            double used = resultModelNode.get("result").get("heap-memory-usage").get("used").asDouble();
            double max = resultModelNode.get("result").get("heap-memory-usage").get("max").asDouble();
            return used/max;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void main(String[] args) throws Exception{
        ManagementServiceImpl ms = new ManagementServiceImpl();
        ms.getMemoryUsage("127.0.0.1");
    }
}
