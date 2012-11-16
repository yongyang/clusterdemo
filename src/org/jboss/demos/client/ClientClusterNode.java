package org.jboss.demos.client;

import org.jboss.demos.shared.ClusterNodeInfo;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/12/12 10:11 AM
 */
public class ClientClusterNode {

    private Vector position = new Vector(0, 0);

    private ClusterNodeInfo clusterNodeInfo;

    public ClientClusterNode(ClusterNodeInfo clusterNodeInfo) {
        this.clusterNodeInfo = clusterNodeInfo;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public Vector getPosition() {
        return position;
    }

    public ClusterNodeInfo getClusterNodeInfo() {
        return clusterNodeInfo;
    }

}
