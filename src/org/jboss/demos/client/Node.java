package org.jboss.demos.client;

import org.jboss.demos.shared.ClusterNode;

import java.sql.Time;

/**
* @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
* @create 11/19/12 8:22 AM
*/
class Node {

    private Vector position = new Vector(0, 0);

    private ClusterNode clusterNode;

    // 2s
    private long lastForStatusChange = 5000;
    private long newStart = 0;
    private long removeStart = 0;

    private static final String STATUS_OK = "OK";
    private static final String STATUS_REMOVING = "REMOVING";
    private static final String STATUS_RESTARTING = "RESTARTING";

    private String status = STATUS_OK;

    public Node(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
        this.newStart = System.currentTimeMillis();
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

    public void updateNodeInfo(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
        // new ???
    }

    public void setRemoved() {
        if(!status.equals(STATUS_REMOVING)) {
            // avoid set removed repeatedly, so the removeStart reset
            removeStart = System.currentTimeMillis();
            status = STATUS_REMOVING;
        }
    }

    //TODO: if reload, it's to fast for removing and newing to show on UI ????
    public boolean isRemoving() {
        return System.currentTimeMillis() - removeStart  < lastForStatusChange;
    }

    public boolean isNewing(){
        return  System.currentTimeMillis() - newStart  < lastForStatusChange;
    }

    public boolean isTimeToRemove(){
        return removeStart !=0 && System.currentTimeMillis() - removeStart  > lastForStatusChange;
    }

    public boolean hasNewed(){
        return newStart !=0 && System.currentTimeMillis() - newStart  > lastForStatusChange;
    }

    public String getIdentity() {
        return clusterNode.getIdentity();
    }

}
