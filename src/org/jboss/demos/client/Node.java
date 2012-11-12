package org.jboss.demos.client;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.ImageElement;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/12/12 10:11 AM
 */
public class Node {

    private Vector position = new Vector(0, 0);

    private String ip;

    public Node() {

    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public Vector getPosition() {
        return position;
    }
}
