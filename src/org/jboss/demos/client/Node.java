package org.jboss.demos.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/12/12 10:11 AM
 */
public class Node {
    private Vector position, goal;

    private ImageElement image;

    private String ip;

    public Node(ImageElement image) {
        position = new Vector(0, 0);
        goal = new Vector(0, 0);
        this.image = image;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setGoal(double x, double y){
        this.goal.x = x;
        this.goal.y = y;
    }

    public void update() {
        position.set(goal);
    }

    public void draw(Context2d context) {
        context.save();
        context.translate(this.position.x, this.position.y);
        context.drawImage(image, 0, 0);
        context.restore();
    }

}
