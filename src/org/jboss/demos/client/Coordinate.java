package org.jboss.demos.client;

/**
 * @author <a href="mailto:yyang@redhat.com">Yong Yang</a>
 * @create 11/12/12 10:12 AM
 */
public class Coordinate {

    private double x, y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Coordinate clone(Coordinate coordinate) {
        return new Coordinate(coordinate.x, coordinate.y);
    }


}
