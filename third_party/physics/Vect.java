package physics;

/****************************************************************************
 * Copyright (C) 1999-2014 by the Massachusetts Institute of Technology,
 *                     Cambridge, Massachusetts.
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
 * @author: Matt Frank, MIT Laboratory for Computer Science,
 *          mfrank@lcs.mit.edu
 *          1999-Apr-03
 *
 * @author: Jeffrey Sheldon (jeffshel@mit.edu)
 *          Fall 2000, Spring 2001
 *          Extra convenience methods, addition of faster dual representation
 * 
 * @author: Robert C. Miller (rcm@mit.edu), Danny Yuan
 *          Spring 2014
 * 
 *
 ***************************************************************************/

import java.awt.geom.Point2D;

/**
 * Vect is an immutable abstract data type which models
 * the mathematical notion of a vector or a point in 2-space.
 */
public final class Vect {

    private Angle theta;
    private double r;
    private int mode; // -1 for r-theta, +1 for x-y, 0 for both
    private double x;
    private double y;

    // Rep. Invariant:
    //   (mode <= 0) ==>
    //     ((theta != null) &&
    //      (r >= 0.0) &&
    //      (r = 0.0) ==> (theta = 0)) &&
    //   (mode == 0) ==>
    //     (( x = r * theta.cos ) &&
    //      ( y = r * theta.sin ))

    // Abstraction Function:
    //   If mode is >= 0, the vector in the Cartesian plane with an x
    //   coordinate x and a y coordinate y, else the vector in the
    //   Cartesian plane with angle to the horizontal equal to theta and
    //   length equal to r.


    /** A Vect with zero length */
    public static final Vect ZERO = new Vect(0.0, 0.0, 0.0, Angle.ZERO);

    /** A unit vector in the positive x direction */
    public static final Vect X_HAT = new Vect(1.0, 0.0, 1.0, Angle.ZERO);

    /** A unit vector in the positive y direction */
    public static final Vect Y_HAT = new Vect(0.0, 1.0, 1.0, Angle.DEG_90);

    // CONSTRUCTORS:

    /**
     * @param angle ; requires that <code>angle</code> is not null
     * 
     * Constructs a new unit vector in the direction of <code>angle</code>.
     */
    public Vect(Angle angle) {
        this(angle, 1.0);
    }

    /**
     * @param angle ; requires that <code>angle</code> is not null
     * 
     * Constructs a new vector in the direction of <code>angle</code>
     * with length <code>length</code>.
     */
    public Vect(Angle angle, double length) {
        if (angle == null) throw new IllegalArgumentException();
        if (length == 0.0) {
            theta = Angle.ZERO;
            r = 0.0;
        } else if (length > 0.0) {
            theta = angle;
            r = length;
        } else {
            theta = angle.plus(Angle.RAD_PI);
            r = -length;
        }
        mode = -1;
    }

    /**
     * Constructs a new vector in Cartesian space with coordinates (x,y).
     */
    public Vect(double x, double y) {
        this.x = x;
        this.y = y;
        mode = 1;
    }

    private Vect(double x, double y, double r, Angle theta) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.theta = theta;
        mode = 0;
    }

    /**
     * @param p ; requires that <code>p</code> is not null
     * 
     * Constructs a new vector in Cartesian space located at the
     * coordinates specified by <code>p</code>
     */
    public Vect(Point2D p) {
        this(p.getX(), p.getY());
    }

    private void computeXY() {
        if (mode < 0) {
            x = r * theta.cos();
            y = r * theta.sin();
            mode = 0;
        }
    }

    private void computeRT() {
        if (mode > 0) {
            r = Math.sqrt((x * x) + (y * y));
            if (r == 0.0) {
                theta = Angle.ZERO;
            } else {
                theta = new Angle(x, y);
            }
            mode = 0;
        }
    }

    // OBSERVERS:

    /**
     * @return the angle of <code>this</code> in polar coordinates
     */
    public Angle angle() {
        computeRT();
        return theta;
    }

    /**
     * @return the length of <code>this</code>
     */
    public double length() {
        computeRT();
        return r;
    }

    /**
     * @return the horizontal coordinate of <code>this</code>
     * in Cartesian coordinates
     */
    public double x() {
        computeXY();
        return x;
    }

    /**
     * @return the vertical coordinate of <code>this</code>
     * in Cartesian coordinates
     */
    public double y() {
        computeXY();
        return y;
    }

    /**
     * @param b vect of the d^2 to be computed; requires that <code>b</code> is not null
     * 
     * @return the square of the distance between the points
     * represented by <code>this</code> and <code>b</code>
     */
    public double distanceSquared(Vect b) {
        // effects: returns the distance between <this> and <b>
        computeXY();
        b.computeXY();

        double width = this.x - b.x;
        double height = this.y - b.y;

        return ((width * width) + (height * height));
    }

    // PRODUCERS:

    /**
     * @param b vector to add to; requires that <code>b</code> is not null
     * 
     * @return the vector sum of <code>this</code> and <code>b</code>
     */
    public Vect plus(Vect b) {
        computeXY();
        b.computeXY();
        return new Vect(this.x + b.x, this.y + b.y);
    }

    /**
     * @param b vect to subtract from; requires that <code>b</code> is not null
     * 
     * @return the vector difference of <code>this</code> and
     * <code>b</code>
     */
    public Vect minus(Vect b) {
        computeXY();
        b.computeXY();
        return new Vect(this.x - b.x, this.y - b.y);
    }

    /**
     * @param a angle to rotate by; requires that <code>a</code> is not null
     * 
     * @return a vector that is equivalent to <code>this</code>
     * having been rotated around the origin by <code>a</code>.
     */
    public Vect rotateBy(Angle a) {
        computeRT();
        return new Vect(this.theta.plus(a), r);
    }

    /**
     * @return a vector that is equivalent to <code>this</code>
     * being rotated by pi radians.
     */
    public Vect neg() {
        if (mode < 0) {
            return rotateBy(Angle.RAD_PI);
        } else if (mode > 0) {
            return new Vect(-x, -y);
        } else {
            // mode == 0
            return new Vect(-x, -y, r, theta.plus(Angle.RAD_PI));
        }
    }

    /**
     * @return a vector equivalent to <code>this</code> scaled
     * by <code>amt</code>.
     */
    public Vect times(double amt) {
        if (mode < 0) {
            return new Vect(theta, r * amt);
        } else if (mode > 0) {
            return new Vect(x*amt, y*amt);
        } else {
            // mode == 0
            return new Vect(x*amt, y*amt, r*amt, theta);
        }
    }

    /**
     * Returns the projection of this onto <code>b</code><br>
     *
     * <img src="doc-files/project.gif">
     *
     * @param b vect to project on; requires that <code>b</code> is not null
     * 
     * @return a vector resulting from projecting <code>this</code> onto
     * <code>b</code>.  The resulting vector has the same angle as
     * <code>b</code>, but its length is such that <code>this</code> -
     * <code>c</code> is perpendicular to <code>c</code>.
     **/
    public Vect projectOn(Vect b) {
        b.computeRT();
        return new Vect(b.theta, this.dot(b));
    }

    /**
     * @return a unit vector with the same angle as <code>this</code>
     */
    public Vect unitSize() {
        computeRT();
        return new Vect(theta, 1.0);
    }

    /**
     * @param b vect to take dot product of; requires that 
     * <code>b</code> is not null
     * 
     * @return the dot product of <code>this</code> and
     * <code>b</code>.
     */
    public double dot(Vect b) {
        if (mode < 0 && b.mode < 0) {
            return r * theta.minus(b.theta).cos();
        } else {
            computeXY();
            b.computeXY();
            return x*b.x + y*b.y;
        }
    }

    /**
     * @return a new <code>Point2D</code> object which is
     * located at the same point as <code>this</code>.
     */
    public Point2D.Double toPoint2D()  {
        computeXY();
        return new Point2D.Double(x, y);
    }

    public String toString() {
        return "<" + x() + "," + y() + ">";
    }

    public boolean equals(Vect v) {
        if (v == null) return false;
        if (mode < 0 && v.mode < 0) {
            return ((this.r == v.r) && this.theta.equals(v.theta));
        } else {
            computeXY();
            v.computeXY();
            return (this.x == v.x) && (this.y == v.y);
        }
    }

    public boolean equals(Object o) {
        return (o instanceof Vect) && this.equals((Vect) o);
    }

    public int hashCode() {
        return (new Double(x())).hashCode() + (new Double(y())).hashCode();
    }
}
