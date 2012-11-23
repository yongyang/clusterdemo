package org.jboss.demos.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/15/12 8:54 AM
 */
public class ClusterNode implements Serializable, Comparable<ClusterNode>, IsSerializable {
    private String ip;
    private int port;

    private double memUsage = 0f;
    private double threadUsage = 0f;

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

    public double getMemUsage() {
        return memUsage;
    }

    public void setMemUsage(double memUsage) {
        this.memUsage = memUsage;
    }

    public double getThreadUsage() {
        return threadUsage;
    }

    public void setThreadUsage(double threadUsage) {
        this.threadUsage = threadUsage;
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
