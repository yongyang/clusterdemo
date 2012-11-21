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
    private long lastForStatusChange = 5000;
    private long newStart = 0;
    private long removeStart = 0;

    private static final String STATUS_STARTING = "STARTING";
    private static final String STATUS_OK = "OK";
    private static final String STATUS_REMOVING = "REMOVING";
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

        if(this.clusterNode.getReceivedBytes() < clusterNode.getReceivedBytes()){
            this.isReceiving = true;
        }
/*
        if(status.equals(STATUS_REMOVING)) { // re-loading
            removeStart = 0;
            newStart = System.currentTimeMillis();
            this.status = STATUS_STARTING;
        }
*/

        this.clusterNode = clusterNode;
        // new ???
    }

    public void setRemoving() {
        if(!status.equals(STATUS_REMOVING)) {
            // avoid set removed repeatedly, so the removeStart reset
            removeStart = System.currentTimeMillis();
            status = STATUS_REMOVING;
        }
    }

    public boolean isRemoving() {
        if(!status.equals(STATUS_REMOVING)) {
            return false;
        }
        return System.currentTimeMillis() - removeStart  < lastForStatusChange;
    }

    public boolean isStarting(){
        boolean newing =  System.currentTimeMillis() - newStart  < lastForStatusChange;
        if(!newing) {
            if(status.equals(STATUS_STARTING)) {
                status = STATUS_OK;
            }
            return false;
        }
        return true;
    }

    //TODO: if reload, it's to fast for removing and newing to show on UI ????
    public boolean isReloading() {
        return (removeStart < newStart) && isRemoving() && isStarting();
    }

    public boolean isReceiving() {
        boolean receiving =  System.currentTimeMillis() - receivingStart  < lastForStatusChange;
        if(!receiving) {
            if(isReceiving) {
                isReceiving = false;
            }
        }
        return isReceiving;
    }

    public boolean isTimeToRemove(){
        if(!status.equals(STATUS_REMOVING)) {
            return false;
        }
        return removeStart !=0 && System.currentTimeMillis() - removeStart  > lastForStatusChange;
    }


    public String getIdentity() {
        return clusterNode.getIdentity();
    }

}
