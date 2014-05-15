package physics;


/****************************************************************************
 * Copyright (C) 1999-2014 by the Massachusetts Institute of Technology,
 *                       Cambridge, Massachusetts.
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
 * @author: Rob Pinder, Phil Sarin, Lik Mui
 *          Spring 2000
 *          Exception handling and argument type refinemnt
 *
 * @author: Jeffrey Sheldon (jeffshel@mit.edu)
 *          Fall 2000, Spring 2001
 *          Major rewrites and improvements to iterative solving
 *
 * @author: Jeremy Nimmer (jwnimmer@alum.mit.edu)
 *          Fall 2000, Spring 2001
 *          Editorial role (testing and specification editing)
 * 
 * @author: Robert C. Miller (rcm@mit.edu), Danny Yuan
 *          Spring 2014
 *
 ***************************************************************************/

/**
 * The Geometry library contains procedural abstractions which are useful
 * in modeling the physical interactions between objects.
 *
 * <p>The library is described in terms of these concepts:
 * <br><ul>
 * <li> object  - a ball or a bouncer
 * <li> ball    - a circle with position and velocity
 * <li> bouncer - a line segment or circle with position and angular velocity
 * </ul>
 *
 * <p>
 * The intended use of the Geometry library is as follows:
 *
 * <p><ol><li>
 * The client calls the timeUntilCollision() methods to calculate the
 * times at which the ball(s) will collide with each of the bouncers
 * or with another ball.
 * The minimum of all these times (call it "mintime") is the
 * time of the next collision.
 *
 * <li>
 * The client updates the position of the ball(s) and the bouncers to
 * account for mintime passing.  At this point, the ball and the object
 * it is about to hit are exactly adjacent to one another.
 *
 * <li>
 * The client calls the appropriate reflect() method to calculate the
 * change in the ball's velocity.
 *
 * <li>The client updates the ball's velocity and repeats back to step 1.
 *
 * </ol>
 *
 * <p><a name="constant_velocity"></a>
 *
 * <p>The timeUntilCollision() methods assume constant ball velocity.
 * That is, no force will be acting on the ball, so it will follow a
 * straight-line path.  Therefore, if external forces (such as gravity
 * or friction) need to be accounted for, the client must do so before
 * or after the of the "time until / update position / reflect" series
 * of steps - never in between those three steps.
 * 
 * <p><a name="endpoint_effects"></a>
 *
 * <b>Important note</b>:
 * The methods which deal with line segment bouncers do NOT deal with
 * the end-points of the segment.  To ensure realistic behavior, shapes
 * should be constructed from a combination of line segments with
 * zero-radius circles at the end points.
 *
 * <p>
 * For example: A ball is located at (0,0) and is moving in the
 * (1,1) direction towards two line segments; one segments spans the
 * points (1,1),(1,2) and the other spans (1,1),(2,1).
 * The ball will hit the ends of both line segments at a 45 degree angle and
 * something REALLY WEIRD will happen.  However, if a circle with zero radius
 * is placed at (1,1) then the ball will bounce off the circle in the
 * expected manner.
 **/
public class Geometry {

    // nobody should be constructing a "Geometry"
    private Geometry() {
    }

    private static GeometryInterface geometry = new GeometryImpl();

    /**
     * @param impl the object to be used as the singleton
     *
     * Changes which implementation of
     * <code>GeometryInterface</code> will be used to service the static
     * methods of this class.  Most users will prefer to use
     * <code>setForesight</code> or <code>setTuningParameters</code>
     * instead.
     *
     * @see #setForesight
     * @see #setTuningParameters
     **/
    static void setGeometry(GeometryInterface impl) {
        if (impl == null) { 
            throw new IllegalArgumentException();
        } 
        geometry = impl;
    }

    /**
     * Modifies the behavior of this class to use the specified
     * <code>maximumForesight</code> and <code>numberOfSlices</code>.  If
     * <code>useDoughnut</code> is true then doughnut optimizations are
     * enabled.  The values used by default are &lt;+Inf, true, 15&gt;.
     * Many uses may prefer to simply use <code>setForesight</code>
     * instead.
     *
     * @param maximumForesight The maximal time in the future that a
     * collision will be searched for.  Collisions may still be returned
     * that happen farther than <code>maximumForesight</code> in the
     * future, but no extra effort will be made to find them.  If set to
     * +Infinity, <code>useDoughnut</code> must also be true.
     *
     * @param useDoughnut When true, the timeUntilRotating* methods
     * perform extra calculations to reduce the time during which
     * collisions are searched for.  If maximumForesight is small, it is
     * sometimes quicker to skip these additional checks.  Must be true
     * if maximumForesight is +Infinity.
     *
     * @param numberOfSlices The number of slices that the time being
     * searched for a possible collision is divided into.  Since some
     * methods (notably timeUntilRotating*) cannot use closed form
     * formula, they must search for possible collisions over some time
     * frame.  Increasing the size of this will decrease the likelihood
     * of one of the timeUntilRotating* methods missing a collision, but
     * will also cause them to run slower.
     *
     * @see #setForesight
     * @see Double#POSITIVE_INFINITY
     **/
    public static void setTuningParameters(double maximumForesight,
            boolean useDoughnut,
            int numberOfSlices) {
        if (useDoughnut) {
            setGeometry(new GeometryImpl(maximumForesight, numberOfSlices));
        } else {
            setGeometry(new SimpleGeometry(maximumForesight, numberOfSlices));
        }
    }

    /**
     * Modifies the behavior of this class to use the specified
     * <code>maximumForesight</code>.
     *
     * @param maximumForesight The maximal time in the future that a
     * collision will be searched for.  Collisions may still be returned
     * that happen farther than <code>maximumForesight</code> in the
     * future, but no extra effort will be made to find them.
     *
     * @see Double#POSITIVE_INFINITY
     **/
    public static void setForesight(double maximumForesight) {
        if (maximumForesight <= 0.1) {
            setGeometry(new SimpleGeometry(maximumForesight, 15));
        } else {
            setGeometry(new GeometryImpl(maximumForesight, 15));
        }
    }

    /**
     * <code>DoublePair</code> is a simple immutable record type representing
     * a pair of <code>double</code>s.
     **/
    public static class DoublePair
    {
        public final double d1;
        public final double d2;

        /**
         * Creates a DoublePair with <code>d1</code> and
         * <code>d2</code> as given
         **/
        public DoublePair (double d1, double d2) {
            this.d1 = d1;
            this.d2 = d2;
        }

        /**
         * Creates a DoublePair with <code>d1</code> and
         * <code>d2</code> both set to the given argument
         **/
        public DoublePair (double both) {
            this(both, both);
        }

        public boolean areFinite() {
            return !Double.isInfinite(d1) && !Double.isInfinite(d2) &&
                    !Double.isNaN(d1) && !Double.isNaN(d2);
        }

        public String toString() {
            return "[" + d1 + "," + d2 + "]";
        }

        public boolean equals(Object o) {
            return (o instanceof DoublePair) && equals((DoublePair) o);
        }

        public boolean equals(DoublePair p) {
            if (p == null) return false;
            return (d1 == p.d1) && (d2 == p.d2);
        }

        public int hashCode() {
            return (new Double(d1)).hashCode() + (new Double(d2)).hashCode();
        }
    }

    /**
     * <code>VectPair</code> is a simple immutable record type representing
     * a pair of <code>Vect</code>s.
     * @see Vect
     **/
    public static class VectPair
    {
        public final Vect v1;
        public final Vect v2;

        /**
         * Creates a VectPair with <code>v1</code> and
         * <code>v2</code> as given
         **/
        public VectPair(Vect v1, Vect v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public String toString() {
            return "[" + v1 + "," + v2 + "]";
        }

        public boolean equals(Object o) {
            return (o instanceof VectPair) && equals((VectPair) o);
        }

        public boolean equals(VectPair p) {
            if (p == null) return false;
            return
                    ((v1 == null) ? (p.v1 == null) : v1.equals(p.v1)) &&
                    ((v2 == null) ? (p.v2 == null) : v2.equals(p.v2));
        }

        public int hashCode() {
            return
                    ((v1 == null) ? 0 : (3 * v1.hashCode())) +
                    ((v2 == null) ? 0 : (7 * v2.hashCode()));
        }
    }

    /**
     * DoublePair with both <code>d1</code> and <code>d2</code>
     * set to <code>Double.NaN</code>
     * @see java.lang.Double#NaN
     **/
    public static final DoublePair DOUBLE_PAIR_NAN =
            new DoublePair(Double.NaN);

    /**
     * Solves the quadratic equation.
     * 
     * @return a pair containing the roots of the equation
     *   a*x<sup>2</sup> + b*x + c = 0 with the lesser of the two roots
     *   in <code>result.d1</code>.  If no real roots exist, the
     *   returned pair will contain <code>NaN</code> for both values.
     *
     * @see java.lang.Double#NaN
     **/
    public static DoublePair quadraticSolution(double a, double b, double c) {
        return geometry.quadraticSolution(a, b, c);
    }


    /**
     * Solves the quadratic equation.
     *
     * @return the lesser of the two roots of the quadratic
     * equation specified by a*x<sup>2</sup> + b*x + c = 0, or
     * <code>NaN</code> if no real roots exist.
     *
     * @see java.lang.Double#NaN
     **/
    public static double minQuadraticSolution(double a,
            double b,
            double c) {
        return geometry.minQuadraticSolution(a, b, c);
    }


    /***************************************************************************
     *
     * METHODS FOR LINE SEGMENTS
     *
     * Suppose we have a line running through the points <x,y> and <w,z>.
     * And we have a point <a,b>.  We'd like to find the distance from the
     * point to the line.  We can calculate this by finding the minimum
     * distance between the point and all points on the line. (Write the
     * line as a function of s: j[s] = x + (w-x)s, k[s] = y + (z-y)s,
     * then write the distance squared as a function of s:
     *     (a - j[s])^2 + (b - k[s])^2
     * Take the derivative with respect to s and set it equal 0.  The
     * result is that the distance squared between the point and the line
     * is:
     *
     * (b(x-w) - a(y-z) + (w y - x z))^2 / ((x-w)^2 + (y-z)^2)
     *
     *
     * Furthermore, the point on the line that is perpendicular to the
     * point is given by:
     *
     * minS = ((w-x)(a-x) + (z-y)(b-y)) / ((w-x)^2 + (y-z)^2)
     * minX = j[minS], minY = k[minS]
     *
     * Okay, now assume that the point is moving.  a[t] = u t + c,
     * b[t] = v t + d.  We want to find the time, t, at which the distance
     * between the point and the line will be exactly "r".
     *
     * Then the numerator of the previous expression will be a quadratic
     * expression of the variable t, with At^2 + Bt + C where
     *
     * F = (v(x-w) - u(y-z))
     * G = (d(x-w) - c(y-z) + (w y - x z))
     * H = ((x-w)^2 + (y-z)^2)
     *
     * A = F^2
     * B = 2 F G
     * C = G^2
     *
     * So to find the answer we let:
     * Cprime = C - r^2 H
     *
     * and finally:
     *
     * t = (-B +/- Sqrt(B^2 - 4 A Cprime)) / (2 A)
     *
     ***************************************************************************/


    /**
     * Returns the point on <code>line</code> which forms a line with
     * <code>point</code> that is perpendicular to <code>line</code>.
     *
     * @param line ; requires that <code>line</code> has non-zero length
     * 
     * @return the point on <code>line</code> which forms a line with
     * <code>point</code> that is perpendicular to <code>line</code>, or
     * <code>null</code> if no such point exists within the given line
     * segment.
     *
     * @see #perpendicularPointWholeLine(LineSegment, Vect)
     **/
    static public Vect perpendicularPoint(LineSegment line,
            Vect point) {
        return geometry.perpendicularPoint(line, point);
    }

    /**
     * Returns the point on the infinitely long line represented by
     * <code>line</code> which forms a line with <code>point</code> that
     * is perpendicular to <code>line</code>.
     *
     * @param line ; requires that <code>line</code> has non-zero length
     *
     * @return the point on the infinitely long line represented by
     * <code>line</code> which forms a line with <code>point</code> that
     * is perpendicular to <code>line</code>, or <code>null</code> if no
     * such point exists within the given line segment.
     *
     * @see #perpendicularPoint(LineSegment, Vect)
     **/
    public static Vect perpendicularPointWholeLine(LineSegment line,
            Vect point) {
        return geometry.perpendicularPointWholeLine(line, point);
    }

    /**
     * Accounts for the effects of inelastic collisions given the initial
     * and resulting velocities of the collision assuming elasticity.
     *
     * @param incidentVect the initial velocity of the ball
     * @param reflectedVect the resulting velocity after the collision
     * assuming elasticity.
     * @param rCoeff the reflection coefficient; requires that <code>rCoeff</code> >= 0
     *
     * @return the resulting velocity of the
     * collision had it been inelastic with the given reflection
     * coefficient.  If the reflection coefficient is 1.0, the resulting
     * velocity will be equal to <code>reflectedVect</code>.  A
     * reflection coefficient of 0 implies that the collision will
     * absorb any energy that was reflected in the elastic case.
     **/
    public static Vect applyReflectionCoeff(Vect incidentVect,
            Vect reflectedVect,
            double rCoeff) {
        return geometry.applyReflectionCoeff(incidentVect,
                reflectedVect,
                rCoeff);
    }


    /**
     * Computes the time until a ball, represented by a circle,
     * traveling at a specified velocity collides with a specified line
     * segment.
     *
     * @param line the line segment representing a wall or (part of) an
     * object that might be collided with; requires that <code>line</code> 
     * has non-zero length
     *
     * @param ball a circle indicate the size and location of a ball
     * which might collide with the given line segment
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the time until a circular ball
     * traveling at a specified velocity collides with a specified line
     * segment.  If no collision will occur, <tt>POSITIVE_INFINITY</tt> is
     * returned.  This method assumes that the ball will travel with
     * constant velocity until impact.
     *
     * @see Double#POSITIVE_INFINITY
     * @see <a href="#endpoint_effects">endpoint effects</a>
     **/
    public static double timeUntilWallCollision(LineSegment line,
            Circle ball,
            Vect velocity) {
        return geometry.timeUntilWallCollision(line, ball, velocity);
    }

    /**
     * Computes the new velocity of a ball after bouncing (reflecting)
     * off a wall.
     *
     * @param line the line segment representing the wall which is being hit;
     * requires that <code>line</code> has non-zero length
     *
     * @param velocity the velocity of the ball before impact
     *
     * @param reflectionCoeff the reflection coefficient; requires that 
     * <code>reflectionCoeff</code> >= 0 
     *
     * @return the new velocity of a ball reflecting off of a
     * wall.  The velocity resulting from this method corresponds to
     * collision against a surface with the given reflection
     * coefficient.  A reflection coefficient of 1 indicates a
     * perfectly elastic collision.  This method assumes that the ball
     * is at the point of impact.
     **/
    public static Vect reflectWall(LineSegment line,
            Vect velocity,
            double reflectionCoeff) {
        return geometry.reflectWall(line, velocity, reflectionCoeff);
    }


    /**
     * Computes the new velocity of a ball after bouncing (reflecting)
     * off a wall.
     *
     * @param line the line segment representing the wall which is being hit;
     * requires that <code>line</code> has non-zero length
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the new velocity of a ball reflecting off of a
     * wall.  The velocity resulting from this method corresponds to a
     * perfectly elastic collision.  This method assumes that the ball
     * is at the point of impact.
     **/
    public static Vect reflectWall(LineSegment line,
            Vect velocity) {
        return geometry.reflectWall(line, velocity);
    }

    /****************************************************************************
     *
     * METHODS FOR CIRCLES
     *
     ***************************************************************************/

    /**
     * @return the square of the distance between two points
     * represented by <code>v1</code> and <code>v2</code>.
     **/
    static public double distanceSquared(Vect v1, Vect v2) {
        return geometry.distanceSquared(v1, v2);
    }

    /**
     * @return the square of the distance between two points
     * represented by <code>(x1, y1)</code> and <code>(x2,
     * y2)</code>.
     **/
    static public double distanceSquared(double x1, double y1,
            double x2, double y2) {
        return geometry.distanceSquared(x1, y1, x2, y2);
    }


    /**
     * Computes the time until a ball represented by a circle,
     * traveling at a specified velocity collides with a specified
     * circle.
     *
     * @param circle a circle representing the circle with which the
     * ball may collide
     *
     * @param ball a circle representing the size and initial location
     * of the ball; requires that ball.radius > 0
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the time until a ball represented by a circle,
     * traveling at a specified velocity collides with a specified
     * circle.  If no collision will occur <tt>POSITIVE_INFINITY</tt> is
     * returned.  This method assumes the ball travels with constant
     * velocity until impact.
     *
     * @see Double#POSITIVE_INFINITY
     **/
    static public double timeUntilCircleCollision(Circle circle,
            Circle ball,
            Vect velocity) {
        return geometry.timeUntilCircleCollision(circle, ball, velocity);
    }

    /**  
     * Computes the new velocity of a ball reflecting off of a
     * circle.
     *
     * @param circle the center point of the circle which is being hit
     *
     * @param ball the center point of the ball
     *
     * @param velocity the velocity of the ball before impact
     *
     * @param reflectionCoeff the reflection coefficient; requires that 
     * <code>reflectionCoeff</code> >= 0
     *
     * @return the new velocity of a ball reflecting off of a
     * circle.  The velocity resulting from this method corresponds to a
     * collision against a surface with the given reflection
     * coefficient.  A reflection coefficient of 1 indicates a perfectly
     * elastic collision.  This method assumes that the ball is at the
     * point of impact. 
     **/
    public static Vect reflectCircle(Vect circle,
            Vect ball,
            Vect velocity, 
            double reflectionCoeff) {
        return geometry.reflectCircle(circle, ball, velocity, reflectionCoeff);
    }

    /**  
     * Computes the new velocity of a ball reflecting off of a
     * circle.
     *
     * @param circle the center point of the circle which is being hit
     *
     * @param ball the center point of the ball
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the new velocity of a ball reflecting off of a
     * circle.  The velocity resulting from this method corresponds to a
     * perfectly elastic collision.  This method assumes that the ball
     * is at the point of impact. 
     **/
    public static Vect reflectCircle(Vect circle,
            Vect ball,
            Vect velocity) {
        return geometry.reflectCircle(circle, ball, velocity);
    }

    /****************************************************************************
     *
     * METHODS FOR ROTATING LINE SEGMENTS AND CIRCLES
     *
     ***************************************************************************/

    /**
     * Rotates the point represented by <code>p</code> by
     * <code>a</code> around the center of rotation, <code>cor</code>,
     * and returns the result.
     *
     * @param point the initial location of the point to be rotated
     *
     * @param cor the point indicating the center of rotation
     *
     * @param a the amount by which to rotate <code>point</code>
     *
     * @return point <code>point</code> rotated around <code>cor</code>
     * by <code>a</code>
     **/
    public static Vect rotateAround(Vect point, Vect cor, Angle a) {
        return geometry.rotateAround(point, cor, a);
    }

    /**
     * Rotates the line segment represented by
     * <code>line</code> by <code>a</code> around the center of
     * rotation, <code>cor</code>, and returns the result.
     * @param line the initial location of the line segment to be rotated
     *
     * @param cor the point indicating the center of rotation
     *
     * @param a the amount by which to rotate <code>point</code>
     *
     * @return line segment <code>line</code> rotated around <code>cor</code>
     * by <code>a</code>
     **/
    public static LineSegment rotateAround(LineSegment line, Vect cor, Angle a) {
        return geometry.rotateAround(line, cor, a);
    }

    /**
     * Rotates the circle represented by
     * <code>circle</code> by <code>a</code> around the center of
     * rotation, <code>cor</code>, and returns the result.
     * 
     * @param circle the initial location of the circle to be rotated
     *
     * @param cor the point indicating the center of rotation
     *
     * @param a the amount by which to rotate <code>point</code>
     *
     * @return circle <code>circle</code> rotated around <code>cor</code>
     * by <code>a</code>
     **/
    public static Circle rotateAround(Circle circle, Vect cor, Angle a) {
        return geometry.rotateAround(circle, cor, a);
    }

    /**
     * Computes the times when the point moving along the given
     * trajectory will intersect the given circle
     *
     * @param circle circle to find collisions with
     *
     * @param point initial position of the point
     *
     * @param velocity linear velocity of the point
     *
     * @return the times until intersection, with lesser result in d1,
     * or <tt>+Inf</tt>s if no collisions will occur
     *
     * @see Double#POSITIVE_INFINITY
     **/
    public static DoublePair timeUntilCircleCollision(Circle circle,
            Vect point,
            Vect velocity)
    {
        return geometry.timeUntilCircleCollision(circle, point, velocity);
    }

    /**
     * Computes the time until a ball traveling at a specified
     * velocity collides with a rotating line segment.
     *
     * <p><img src="doc-files/rotate_line.gif">
     *
     * @param line the initial position of the rotating line segment (wall);
     * requires that <code>line</code> has non-zero length
     *
     * @param center the center of rotation for <code>line</code>
     *
     * @param angularVelocity the angular velocity of the rotation of
     * <code>line</code> in radians per second.  A positive angular
     * velocity denotes a rotation in the direction from the positive
     * x-axis to the positive y-axis.
     *
     * @param ball the size and initial location of the ball
     *
     * @param velocity the initial velocity of the ball.  The ball is
     * assumed to travel at a constant velocity until impact.
     *
     * @return the time until a circular ball
     * traveling at a specified velocity collides with a specified line
     * segment which is rotating at a fixed angular velocity about a
     * fixed center of rotation.
     *
     * @see Double#POSITIVE_INFINITY
     * @see <a href="#endpoint_effects">endpoint effects</a>
     **/
    public static double timeUntilRotatingWallCollision(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity)
    {
        return geometry.timeUntilRotatingWallCollision(line,
                center,
                angularVelocity,
                ball,
                velocity);
    }

    /**
     * Computes the new velocity of a ball reflecting off of a
     * wall which is rotating about a point with constant angular
     * velocity.
     *
     * @param line the line segment representing the initial position of
     * the rotating wall; requires that <code>line</code> has non-zero length
     *
     * @param center the point about which <code>line</code> rotates; requires
     * that the ball is at the point of impact
     *
     * @param angularVelocity the angular velocity at which
     * <code>line</code> rotates, in radians per second.  A positive angular
     * velocity denotes a rotation in the direction from the positive
     * x-axis to the positive y-axis.
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the new velocity of a ball reflecting off of a
     * wall which is rotating about a point with constant angular
     * velocity.  The velocity resulting from this method corresponds to
     * a perfectly elastic collision.  This method assumes that the ball
     * is at the point of impact.  If the ball does not hit in between
     * the endpoints of <code>line</code>, <code>velocity</code> is
     * returned.
     **/
    public static Vect reflectRotatingWall(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity) {
        return geometry.reflectRotatingWall(line,
                center,
                angularVelocity,
                ball,
                velocity);
    }

    /**
     * Computes the new velocity of a ball reflecting off of a
     * wall which is rotating about a point with constant angular
     * velocity.
     *
     * @param line the line segment representing the initial position of
     * the rotating wall; requires that <code>line</code> has non-zero length
     *
     * @param center the point about which <code>line</code> rotates; requires
     * that the ball is at the point of impact
     *
     * @param angularVelocity the angular velocity at which
     * <code>line</code> rotates, in radians per second.  A positive angular
     * velocity denotes a rotation in the direction from the positive
     * x-axis to the positive y-axis.
     *
     * @param velocity the velocity of the ball before impact
     *
     * @param reflectionCoeff the reflection coefficient; requires that
     * <code>reflectionCoeff</code> >= 0
     *
     * @return the new velocity of a ball reflecting off of a
     * wall which is rotating about a point with constant angular
     * velocity.  The velocity resulting from this method corresponds to
     * a collision against a surface of the given reflection
     * coefficient.  A reflection coefficient of 1 indicates a perfectly
     * elastic collision.  This method assumes that the ball is at the
     * point of impact.  If the ball does not hit in between the
     * endpoints of <code>line</code>, <code>velocity</code> is
     * returned.
     **/
    public static Vect reflectRotatingWall(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity,
            double reflectionCoeff)
    {
        return geometry.reflectRotatingWall(line,
                center,
                angularVelocity,
                ball,
                velocity,
                reflectionCoeff);
    }

    /**
     * Computes the time until a ball traveling at a specified
     * velocity collides with a rotating circle.
     *
     * <p>
     * <img src="doc-files/rotate_circle.gif">
     *
     * @param circle a circle representing the initial location and size
     * of the rotating circle
     *
     * @param center the point around which the circle is rotating
     *
     * @param angularVelocity the angular velocity with which
     * <code>circle</code> is rotating about <code>center</code>, in
     * radians per second.  A positive angular velocity denotes a
     * rotation in the direction from the positive x-axis to the
     * positive y-axis.
     *
     * @param ball a circle representing the size and initial position
     * of the ball
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the time until a circular ball
     * traveling at a specified velocity collides with a specified circle
     * that is rotating about a given center of rotation at a given
     * angular velocity.  If no collision will occur <tt>POSITIVE_INFINITY</tt>
     * is returned. This method assumes the
     * ball will travel with constant velocity until impact.
     * 
     * @see Double#POSITIVE_INFINITY
     **/

    public static double timeUntilRotatingCircleCollision(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity)
    {
        return geometry.timeUntilRotatingCircleCollision(circle,
                center,
                angularVelocity,
                ball,
                velocity);
    }

    /**
     * Computes the new velocity of a ball reflected off of a rotating
     * circle.
     *
     * @param circle the rotating circle
     *
     * @param center the point about which <code>circle</code> is
     * rotating
     *
     * @param angularVelocity the angular velocity with which
     * <code>circle</code> is rotating about <code>center</code>, in
     * radians per second.  A positive angular velocity denotes a
     * rotation in the direction from the positive x-axis to the
     * positive y-axis.
     *
     * @param ball the size and position of the ball before impact;
     * requires that the ball is at the point of impact
     *
     * @param velocity the velocity of the ball before impact
     *
     * @return the new velocity of a ball reflected off of a
     * circle which is rotating with constant angular velocity around a
     * point.  The velocity resulting from this method corresponds to a
     * perfectly elastic collision.
     **/
    public static Vect reflectRotatingCircle(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity) {
        return geometry.reflectRotatingCircle(circle,
                center,
                angularVelocity,
                ball,
                velocity);
    }


    /**
     * Computes the new velocity of a ball reflected off of a rotating
     * circle.
     *
     * @param circle the rotating circle
     *
     * @param center the point about which <code>circle</code> is
     * rotating
     *
     * @param angularVelocity the angular velocity with which
     * <code>circle</code> is rotating about <code>center</code>, in
     * radians per second.  A positive angular velocity denotes a
     * rotation in the direction from the positive x-axis to the
     * positive y-axis.
     *
     * @param ball the size and position of the ball before impact;
     * requires that the ball is at the point of impact
     *
     * @param velocity the velocity of the ball before impact
     * 
     * @param reflectionCoeff the reflection coefficient
     *
     * @return the new velocity of a ball reflected off of a
     * circle which is rotating with constant angular velocity around a
     * point.  The velocity resulting from this method corresponds to a
     * collision against a surface with the given reflection
     * coefficient.  A reflection coefficient of 1.0 indicates a
     * perfectly elastic collision.
     **/
    public static Vect reflectRotatingCircle(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity,
            double reflectionCoeff)
    {
        return geometry.reflectRotatingCircle(circle,
                center,
                angularVelocity,
                ball,
                velocity,
                reflectionCoeff);
    }

    /****************************************************************************
     *
     * METHODS FOR MULTI-BALL SIMULATIONS
     *
     ***************************************************************************/


    /**
     * Computes the time until two balls collide.
     *
     * @param ball1 a circle representing the size and initial position
     * of the first ball.
     *
     * @param vel1 the velocity of the first ball before impact
     *
     * @param ball2 a circle representing the size and initial position
     * of the second ball.
     *
     * @param vel2 the velocity of the second ball before impact
     *
     * @return the time until two balls, represented by two
     * circles, traveling at specified constant velocities, collide.
     * If no collision will occur <tt>POSITIVE_INFINITY</tt> is returned.
     * This method assumes that both balls will travel at constant
     * velocity until impact.
     *
     * @see Double#POSITIVE_INFINITY
     **/
    public static double timeUntilBallBallCollision(Circle ball1,
            Vect   vel1,
            Circle ball2,
            Vect   vel2) {
        return geometry.timeUntilBallBallCollision(ball1, vel1,
                ball2, vel2);
    }


    /**
     * Computes the resulting velocities of two balls which collide.
     *
     * @param center1 the position of the center of the first ball; requires
     * that the distance between the two balls is approximately equal to the
     * sum of their radii; that is, the balls are positioned at the point of impact.
     *
     * @param mass1 the mass of the first ball; requires that mass1 > 0
     *
     * @param velocity1 the velocity of the first ball before impact
     *
     * @param center2 the position of the center of the second ball
     *
     * @param mass2 the mass of the second ball; requires that mass2 > 0
     *
     * @param velocity2 the velocity of the second ball before impact
     *
     * @return a <code>VectPair</code>, where the first <code>Vect</code> is
     * the velocity of the first ball after the collision and the second
     * <code>Vect</code> is the velocity of the second ball after the collision.
     **/
    public static VectPair reflectBalls(Vect center1,
            double mass1,
            Vect velocity1,
            Vect center2,
            double mass2,
            Vect velocity2) {
        return geometry.reflectBalls(center1,
                mass1,
                velocity1,
                center2,
                mass2,
                velocity2);
    }

}
