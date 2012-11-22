package org.jboss.demos.shared;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/22/12 1:51 PM
 */
public class ClusterInfo implements Serializable {
    private long receivedBytes = 0;

    private List<ClusterNode> clusterNodes;

    public ClusterInfo() {
    }

    public long getReceivedBytes() {
        return receivedBytes;
    }

    public void setReceivedBytes(long receivedBytes) {
        this.receivedBytes = receivedBytes;
    }

    public List<ClusterNode> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(List<ClusterNode> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }
}
