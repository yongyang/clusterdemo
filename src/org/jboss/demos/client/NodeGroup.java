package org.jboss.demos.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.demos.shared.ClusterNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/12/12 10:32 AM
 */
public class NodeGroup {

    final double width;
    final double height;
    final double radius;

    Image nodeImg;
    Map<String, Node> nodesMap;
    boolean imageLoaded;

    double step;

    volatile boolean inUpdating = false;
    volatile boolean inDrawing = false;

    public NodeGroup(double width, double height, double radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;

        // init logos array
        nodesMap = new HashMap<String, Node>();

        // init image
        nodeImg = new Image("raspeberry-pi-logo.jpg");
        nodeImg.addLoadHandler(new LoadHandler() {
            public void onLoad(LoadEvent event) {
                imageLoaded = true;
                // once image is loaded, init logo objects
/*
                ImageElement imageElement = (ImageElement) nodeImg.getElement().cast();
                for (int i = NodeGroup.this.numNodes - 1; i >= 0; i--) {
                    Node node = new Node(null);
                    node.setPosition(NodeGroup.this.width / 2, NodeGroup.this.height / 2);
                    nodesMap.put(node.getIdentity(), node);
                }
*/
            }
        });
        nodeImg.setVisible(false);
        RootPanel.get().add(nodeImg); // image must be on page to fire load
    }

    public synchronized void updateClusterInfo(List<ClusterNode> clusterNodes){
        if (!imageLoaded) {
            return;
        }

        while (inDrawing) {
            Timer sleepTimer = new Timer() {
                @Override
                public void run() {

                }
            };
            sleepTimer.schedule(5);
        }

        inUpdating = true;
        Map<String, Node> newNodesMap = new HashMap<String, Node>();
        for(ClusterNode clusterNode : clusterNodes) {
            String id = clusterNode.getIdentity();
            if(nodesMap.containsKey(id)) {
                Node node = nodesMap.remove(id);
                node.updateClusterNodeInfo(clusterNode);
                newNodesMap.put(id, node);
            }
            else {
                Node node = new Node(clusterNode);
                node.setPosition(NodeGroup.this.width / 2, NodeGroup.this.height / 2);
                newNodesMap.put(id, node);
            }
        }
        // the lefe ones in NodesMap need to be removed
        for(Map.Entry<String, Node> entry : nodesMap.entrySet()){
            String id = entry.getKey();
            Node node = entry.getValue();
            node.setRemoved();
            newNodesMap.put(id, node);
        }
        nodesMap.clear();
        nodesMap.putAll(newNodesMap);
        inUpdating = false;
    }

    synchronized void draw(Context2d context) {
        if (!imageLoaded) {
            return;
        }

        while (inUpdating) {
            Timer sleepTimer = new Timer() {
                @Override
                public void run() {

                }
            };
            sleepTimer.schedule(5);
        }

        inDrawing = true;
        step = (step + Math.PI/2.0 * 0.003);


        List<Node> nodes = new ArrayList<Node>(nodesMap.values());


        for(Iterator<Node> it = nodes.iterator(); it.hasNext(); ){
            Node node = it.next();
            //TODO: remove ones can be removed now
            if(node.isTimeToRemove()) {
                nodesMap.remove(node.getIdentity());
            }

        }

        Collections.sort(nodes, new Comparator<Node>() {
            public int compare(Node o1, Node o2) {
                return o1.getIdentity().compareTo(o2.getIdentity());
            }
        });
        int numNodes = nodes.size();

        //TODO: re-calculate position when remove and new
        for (int i = numNodes - 1; i >= 0; i--) {
            Node node = nodes.get(i);

            // update position
            double perPI = 2 * Math.PI * i / numNodes;
            Vector goal = new Vector(width / 2 + radius * Math.cos(step + perPI),
                    height / 2 + radius * Math.sin(step + perPI));
            node.setPosition(goal.getX(), goal.getY());
        }

/*
        context.beginPath();
        context.setFillStyle(CssColor.make("blue"));
        context.arc(100, 100, 100, 0, Math.PI);
        context.fill();
        context.closePath();
*/
        for (Map.Entry<String, Node> entry : nodesMap.entrySet()) {
            Node node = entry.getValue();
            context.save();
            context.beginPath();
            context.translate(node.getPosition().getX(), node.getPosition().getY());
            context.drawImage((ImageElement) nodeImg.getElement().cast(), 0, 0);
            context.setFillStyle(CssColor.make("blue"));
            context.fillText(node.getIdentity(), 0, 80);
            context.closePath();

            //TODO: How to blink

            //TODO: if to remove
            if(node.isRemoving()) {
                //TODO: blink read
            }

            //TODO: is New
            if(node.isNewing()) {
                //TODO: blink green

                if(node.isRemoving()) {
                    //TODO: restarting!!! blink yellow
                }

            }

            context.restore();
        }
        inDrawing = false;
    }

}
