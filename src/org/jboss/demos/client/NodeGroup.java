package org.jboss.demos.client;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import org.jboss.demos.shared.ClusterNode;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final double width;
    private final double height;
    private final double radius;

    private final int nodeImageWidth = 80;
    private final int nodeImageHeight = 80;

    private Image nodeImg;
    private Map<String, Node> nodesMap;
    private boolean imageLoaded;

    private double step;

    volatile boolean inUpdating = false;
    volatile boolean inDrawing = false;

    private Node currentNode = null;

    public NodeGroup(double width, double height, double radius) {
        this.width = width;
        this.height = height;
        this.radius = radius;

        // init logos array
        nodesMap = new HashMap<String, Node>();

        // init image
        nodeImg = new Image("cluster_node-80x80.png");
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
        System.out.println("UpdateClusterInfo: " + clusterNodes.size() + ", " + Arrays.toString(clusterNodes.toArray()));

/*
        while (inDrawing) {
            Timer sleepTimer = new Timer() {
                @Override
                public void run() {

                }
            };
            sleepTimer.schedule(5);
        }
*/

        inUpdating = true;
        Map<String, Node> newNodesMap = new HashMap<String, Node>();
        for(ClusterNode clusterNode : clusterNodes) {
            String id = clusterNode.getIdentity();
            if(nodesMap.containsKey(id)) {
                Node node = nodesMap.remove(id);
                node.updateNodeInfo(clusterNode);
                newNodesMap.put(id, node);
            }
            else {
                Node node = new Node(clusterNode);
                node.setPosition(NodeGroup.this.width / 2, NodeGroup.this.height / 2);
                newNodesMap.put(id, node);
            }
        }
        // the left ones in NodesMap need to be removed
        for(Node node : nodesMap.values()){
            String id = node.getIdentity();
            node.setRemoved();
            newNodesMap.put(id, node);
        }
        nodesMap.clear();
        nodesMap.putAll(newNodesMap);
        inUpdating = false;
    }

    synchronized void draw(Context2d context, int mouseX, int mouseY) {
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

        this.currentNode = null;
        inDrawing = true;
        step = (step + Math.PI/2.0 * 0.003);


        List<Node> nodes = new ArrayList<Node>(nodesMap.values());


        for(Iterator<Node> it = nodes.iterator(); it.hasNext(); ){
            Node node = it.next();
            //TODO: remove ones can be removed now
            if(node.isTimeToRemove()) {
                nodesMap.remove(node.getIdentity());
                it.remove();
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
        for (Node node  : nodesMap.values()) {

            context.save();
            context.beginPath();

            //onMouseOver, shadow
            if(mouseX > 0 && mouseY > 0 &&  mouseX - node.getPosition().getX() > 0 &&  mouseX - node.getPosition().getX() < nodeImageWidth  && mouseY - node.getPosition().getY() > 0 && mouseY - node.getPosition().getY() < nodeImageHeight ) {
//                System.out.println("shadow, node: " + node.getPosition().getX() + ", " + node.getPosition().getY() + "; mouse: " + mouseX + ", " + mouseY);
                this.currentNode = node;
                context.setShadowOffsetX(5);
                context.setShadowOffsetY(5);
                context.setShadowBlur(30);
                context.setShadowColor("black");
            }

            //TODO: How to blink, blink with yellow blue, remove with Alpha
            //to remove
            if(node.isRemoving()) {
                context.setShadowOffsetX(5);
                context.setShadowOffsetY(5);
                context.setShadowBlur(50);
                context.setShadowColor("red");
//                context.setGlobalAlpha(0.8);
            }

            //TODO: is New
            if(node.isNewing()) {
                //TODO: blink green or scale in
                context.setShadowOffsetX(5);
                context.setShadowOffsetY(5);
                context.setShadowBlur(50);
                context.setShadowColor("green");

                if(node.isRemoving()) {
                    //TODO: restarting!!! blink yellow
                }

            }

            context.translate(node.getPosition().getX(), node.getPosition().getY());
            context.drawImage((ImageElement) nodeImg.getElement().cast(), 0, 0);
            context.setFillStyle(CssColor.make("blue"));
            context.fillText(node.getIdentity(), 0, nodeImageHeight+20);
            context.closePath();



            context.restore();
        }
        inDrawing = false;
    }


    public Node getCurrentNode() {
        return currentNode;
    }

    private ImageData scaleImage(Image image, double scaleToRatio) {
        Canvas canvasTmp = Canvas.createIfSupported();
        Context2d context = canvasTmp.getContext2d();

        double ch = (image.getHeight() * scaleToRatio) + 100;
        double cw = (image.getWidth() * scaleToRatio) + 100;

        canvasTmp.setCoordinateSpaceHeight((int) ch);
        canvasTmp.setCoordinateSpaceWidth((int) cw);

        ImageElement imageElement = ImageElement.as(image.getElement());

        // s = source
        // d = destination
        double sx = 0;
        double sy = 0;
        double sw = imageElement.getWidth();
        double sh = imageElement.getHeight();

        double dx = 0;
        double dy = 0;
        double dw = imageElement.getWidth();
        double dh = imageElement.getHeight();

        // tell it to scale image
        context.scale(scaleToRatio, scaleToRatio);

        // draw image to canvas
        context.drawImage(imageElement, sx, sy, sw, sh, dx, dy, dw, dh);

        // get image data
        double w = dw * scaleToRatio;
        double h = dh * scaleToRatio;
        ImageData imageData = context.getImageData(0, 0, w, h);
        return imageData;
    }

}
