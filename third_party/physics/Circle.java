package physics;

/**********************************************************************
 * Copyright (C) 1999, 2000 by the Massachusetts Institute of Technology,
 *                      Cambridge, Massachusetts.
 *
 *                        All Rights Reserved
 *
 * Permission to use, copy, modify, and distribute this software and
 * its documentation for any purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all
 * copies and that both that copyright notice and this permission
 * notice appear in supporting documentation, and that MIT's name not
 * be used in advertising or publicity pertaining to distribution of
 * the software without specific, written prior permission.
 *
 * THE MASSACHUSETTS INSTITUTE OF TECHNOLOGY DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS.  IN NO EVENT SHALL THE MASSACHUSETTS
 * INSTITUTE OF TECHNOLOGY BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
 * OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *
 *
 * @author   Lik Mui
 *
 * @author   Robert C. Miller (rcm@mit.edu), Danny Yuan
 *           Spring 2014
 * 
 *
 *********************************************************************/

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * Circle is an immutable abstract data type which models the
 * mathematical notion of a circle in Cartesian space.
 */
public final class Circle {

    private final Vect centerPoint;
    private final double radius;

    // Rep. Invariant:
    //   centerPoint != null &&
    //   radius >= 0.0

    // Abstraction Function:
    //   The circle with a center at 'centerPoint' and a radius 'radius'

    // Constructors -----------------------------------

    /**
     * Creates a new circle with the specified size and location.
     *
     * @param center the center point of the circle; requires that <code>center</code> != null
     * @param r the radius of the circle; requires that <code>r</code> >= 0
     */
    public Circle(Vect center, double r) {
        if ((r < 0) || (center == null)) {
            throw new IllegalArgumentException();
        }
        centerPoint = center;
        radius = r;
    }

    /**
     * Creates a new circle with the specified size and location.
     *
     * @param cx the x coordinate of the center point of the circle
     * @param cy the y coordinate of the center point of the circle
     * @param r the radius of the circle; requires that <code>r</code> >= 0
     */
    public Circle(double cx, double cy, double r) {
        this(new Vect(cx, cy), r);
    }

    /**
     * Creates a new circle with the specified size and location.
     *
     * @param center the center point of the circle; requires that <code>center</code> != null
     * @param r the radius of the circle; requires that <code>r</code> >= 0
     */
    public Circle(Point2D center, double r) {
        this(new Vect(center), r);
    }

    // Observers --------------------------------------

    /**
     * @return the center point of this circle.
     */
    public Vect getCenter() {
        return centerPoint;
    }

    /**
     * @return the radius of this circle.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return a new Ellipse2D which is the same as this circle
     */
    public Ellipse2D toEllipse2D() {
        return new Ellipse2D.Double(centerPoint.x() - radius,
                centerPoint.y() - radius,
                2 * radius,
                2 * radius);
    }

    // Object methods --------------------------------------

    public boolean equals(Circle c) {
        if (c == null) return false;
        return (radius == c.radius) && centerPoint.equals(c.centerPoint);
    }

    public boolean equals(Object o) {
        if (o instanceof Circle)
            return equals((Circle) o);
        else
            return false;
    }

    public String toString() {
        return "[Circle center=" + centerPoint + " radius=" + radius + "]";
    }

    public int hashCode() {
        return centerPoint.hashCode() + 17 * (new Double(radius)).hashCode();
    }
}
