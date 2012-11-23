package org.jboss.demos.client;

import org.jboss.demos.shared.ClusterNode;

import static org.jboss.demos.client.NodeGroup.lastForStatusChange;
/**
* @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
* @create 11/19/12 8:22 AM
*/
class Node {

    private Vector position = new Vector(0, 0);

    private ClusterNode clusterNode;

    // 3s
    private long newStart = 0;
    private long removeStart = 0;

    private static final String STATUS_STARTING = "STARTING";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_REMOVING = "REMOVING";
    private static final String STATUS_REMOVED = "REMOVED";
    private static final String STATUS_RELOADING = "RESTARTING";

    private String status = STATUS_OK;

    private boolean isReceiving = false;
    private long receivingStart = 0;

    public Node(ClusterNode clusterNode) {
        this.clusterNode = clusterNode;
        this.newStart = System.currentTimeMillis();
        this.status = STATUS_STARTING;
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

        //TODO: do all status change here!!!

        if(status.equals(STATUS_STARTING)) {
            if(System.currentTimeMillis() - newStart  > lastForStatusChange) {
                status = STATUS_OK;
                removeStart = 0;
            }
        }
        else if(status.equals(STATUS_REMOVING)) { // new update in Removing, means restarting, otherwise never get update for node in removing
            status = STATUS_RELOADING;
            removeStart = 0;
            newStart = System.currentTimeMillis();
        }
        else if(status.equals(STATUS_RELOADING)){
            if(System.currentTimeMillis() - newStart  > lastForStatusChange - 1000) { // reloading for 2s, so can switch to STARTING status
                status = STATUS_STARTING;
                removeStart = 0;
            }
        }

        if(clusterNode.getMemUsage() == 0) {
            clusterNode.setMemUsage(this.clusterNode.getMemUsage());

        }
        this.clusterNode = clusterNode;

    }

    public void setRemoving() { // set REMOVING status or update status to REMOVED
        if(!status.equals(STATUS_REMOVING)) {
            // avoid set removed repeatedly, so the removeStart reset
            removeStart = System.currentTimeMillis();
            status = STATUS_REMOVING;
        }
        else {
            if(System.currentTimeMillis() - removeStart  > lastForStatusChange) {
                status = STATUS_REMOVED;
            }
        }
    }

    public boolean isRemoving() {
        return status.equals(STATUS_REMOVING);
    }

    public boolean isStarting(){
        return status.equals(STATUS_STARTING);
    }

    public boolean isReloading() {
        return status.equals(STATUS_RELOADING);
    }

    public boolean isReceiving() {
        if(isRemoving() || isRemoved()) {
            return false;
        }
        return isReceiving;
    }

    public boolean isRemoved(){
        return status.equals(STATUS_REMOVED);
    }


    public String getIdentity() {
        return clusterNode.getIdentity();
    }

}
