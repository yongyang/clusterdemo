package org.jboss.demos.client;

import org.jboss.demos.shared.ClusterNode;

/**
* @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
* @create 11/19/12 8:22 AM
*/
class Node {

    private Vector position = new Vector(0, 0);

    private ClusterNode clusterNode;

    // 2s
    private long lastForStatusChange = 2000;
    private boolean isNew = true;
    private boolean isRemoved = false;

    public Node(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public Vector getPosition() {
        return position;
    }

    public ClusterNode getClusterNode() {
        return clusterNode;
    }

    public void updateClusterNodeInfo(ClusterNode clusterNode) {
        // new ???
    }

    public void setRemoved() {
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public boolean isNew(){
        return isNew;
    }

    public String getIdentity() {
        return clusterNode.getIdentity();
    }

}
