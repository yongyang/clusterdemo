package org.jboss.demos.shared;

import java.io.Serializable;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/15/12 8:54 AM
 */
public class ClusterNode implements Serializable, Comparable<ClusterNode>{
    private String ip;
    private int port;

    public ClusterNode() {

    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getIdentity() {
        return ip + ":" +port;
    }

    @Override
    public String toString() {
        return "ClusterNode{ip=" + ip + ", port=" + port + "}";
    }

    public int compareTo(ClusterNode other) {
        if(other == null) return 0;
        return this.getIdentity().compareTo(other.getIdentity());
    }
}
