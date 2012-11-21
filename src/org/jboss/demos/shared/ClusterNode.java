package org.jboss.demos.shared;

import java.io.Serializable;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/15/12 8:54 AM
 */
public class ClusterNode implements Serializable, Comparable<ClusterNode>{
    private String ip;
    private int port;

    private long receivedBytes = 0;

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

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public String getIdentity() {
        return ip + ":" +port;
    }

    @Override
    public String toString() {
        return "ClusterNode{ip=" + ip + ", port=" + port + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterNode that = (ClusterNode) o;

        if (port != that.port) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    public int compareTo(ClusterNode other) {
        if(other == null) return 0;
        return this.getIdentity().compareTo(other.getIdentity());
    }
}
