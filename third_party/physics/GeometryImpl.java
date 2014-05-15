package physics;

import java.lang.Double; // import statement added to mollify javadoc
import java.util.Iterator;
import physics.Geometry.VectPair;
import physics.Geometry.DoublePair;

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
 *
 ***************************************************************************/

/**
 * GeometryImpl is the default implementation of GeometryInterface.
 * Most callers will not use GeometryImpl directly, but will use the
 * singleton Geometry instead.
 *
 * <p>
 * GeometryImpl uses two parameters to tune its behavior.
 *
 * <ul>
 *   <p><li>
 *
 *     <code>maximumForesight</code> indicates the maximal distance in
 *     the future that the client is interested in learning about
 *     collisions.  Collisions past this time might still be reported,
 *     but are not guaranteed to be found.  In short, this parameter
 *     causes the implementation to focus on finding collisions within
 *     this time at the possible expense of not detecting collisions
 *     past this time.  This default value is +infinity.
 *
 *   </li>
 *   <p><li>
 *
 *     <code>searchSlices</code> determines the number of slices used
 *     to search for roots when using iterative numerical root finding
 *     techniques.  The default value is 15.  Setting a higher value
 *     will increase the chance of finding collision in the
 *     timeUntilRotating* methods, but will cause them to run slower.
 *
 *   </li>
 * </ul>
 *
 * @see physics.Geometry
 **/
class GeometryImpl implements GeometryInterface
{

    protected final double maximumForesight;
    protected final int searchSlices;

    /**
     * Constructs a GeometryImpl with the default tuning
     * parameters as described in the class overview.
     **/
    public GeometryImpl() {
        this(Double.POSITIVE_INFINITY, 15);
    }

    /**
     * Constructs a GeometryImpl with the specified tuning
     * parameters as described in the class overview.
     * 
     * @param maximumForesight ; require that maximumForesight >= 0.0
     * 
     * @param searchSlices ; requires that searchSlices >= 1
     **/
    public GeometryImpl(double maximumForesight, int searchSlices) {
        if (!((maximumForesight >= 0.0) && (searchSlices >= 1))) {
            throw new IllegalArgumentException();
        }
        this.maximumForesight = maximumForesight;
        this.searchSlices = searchSlices;
    }

    /**
     * @see physics.Geometry#quadraticSolution
     **/
    public DoublePair quadraticSolution(double a, double b, double c) {
        if (a == 0.0) {
            if (b == 0.0) {
                return Geometry.DOUBLE_PAIR_NAN;
            } else {
                return new DoublePair(-c/b);
            }
        }
        else {
            double discriminant = (b * b) - (4.0 * a * c);
            if (discriminant < 0.0) {
                return Geometry.DOUBLE_PAIR_NAN;
            } else {
                double sqrt = Math.sqrt(discriminant);
                double twoA = 2.0 * a;
                double lesserNum = -b - sqrt;
                double greaterNum = -b + sqrt;
                if (a > 0) {
                    return new DoublePair(lesserNum/twoA, greaterNum/twoA);
                } else {
                    return new DoublePair(greaterNum/twoA, lesserNum/twoA);
                }
            }
        }
    }


    /**
     * @see physics.Geometry#minQuadraticSolution
     **/
    public double minQuadraticSolution(double a,
            double b,
            double c) {
        return quadraticSolution(a, b, c).d1;
    }


    /**
     * @see physics.Geometry#perpendicularPoint
     **/
    public Vect perpendicularPoint(LineSegment line,
            Vect point) {
        double x1 = line.p1().x(),
                y1 = line.p1().y(),
                x2 = line.p2().x(),
                y2 = line.p2().y(),
                a  = point.x(),
                b  = point.y();

        double height = (y2 - y1);
        double width = (x2 - x1);

        double lengthSquared = (height * height) + (width * width);
        double fraction = (((width * (a - x1)) + (height * (b - y1)))
                / lengthSquared);

        if (fraction < 0 || fraction > 1) {
            return null;
        }

        double pptx = x1 + fraction * width;
        double ppty = y1 + fraction * height;

        return new Vect(pptx, ppty);
    }

    /**
     * @see physics.Geometry#perpendicularPointWholeLine
     **/
    public Vect perpendicularPointWholeLine(LineSegment line,
            Vect point) {
        double x1 = line.p1().x(),
                y1 = line.p1().y(),
                x2 = line.p2().x(),
                y2 = line.p2().y(),
                a  = point.x(),
                b  = point.y();

        double height = (y2 - y1);
        double width = (x2 - x1);

        double lengthSquared = (height * height) + (width * width);
        double fraction = (((width * (a - x1)) + (height * (b - y1)))
                / lengthSquared);

        double pptx = x1 + fraction * width;
        double ppty = y1 + fraction * height;

        return new Vect(pptx, ppty);
    }

    /**
     * @see physics.Geometry#applyReflectionCoeff
     **/
    public Vect applyReflectionCoeff(Vect incidentVect,
            Vect reflectedVect,
            double rCoeff) {
        return incidentVect.plus(reflectedVect.
                minus(incidentVect).times(0.5 + 0.5 * rCoeff));
    }

    /**
     * @see physics.Geometry#timeUntilWallCollision
     **/
    public double timeUntilWallCollision(LineSegment line,
            Circle ball,
            Vect velocity) {
        double x1 = line.p1().x(),
                y1 = line.p1().y(),
                x2 = line.p2().x(),
                y2 = line.p2().y(),
                a  = ball.getCenter().x(),
                b  = ball.getCenter().y(),
                radius = ball.getRadius(),
                va = velocity.x(),
                vb = velocity.y();

        double width = x2 - x1;
        double height = y2 - y1;
        double F = ((va * height) - (vb * width));
        double G = ((a * height) - (b * width) + ((x2 * y1) - (x1 * y2)));
        double H = ((width * width) + (height * height));
        double A = F * F;
        double B = 2.0 * F * G;
        double C = G * G;
        double Cprime = C - (radius * radius * H);

        double collisionTime = minQuadraticSolution(A, B, Cprime);

        if (Double.isNaN(collisionTime)) {
            return Double.POSITIVE_INFINITY;
        }

        // now test if we're within the line segment
        double cX = a + (collisionTime * va);
        double cY = b + (collisionTime * vb);

        // find perpendicular point:
        double minS = ((width * (cX - x1)) + (height * (cY - y1))) /
                ((width * width) + (height * height));

        // the perpindicular point is the fraction "minS" along the line
        // segment, which means we need to check 0.0 <= minS < 1.0
        if ((0.0 <= minS) && (minS < 1.0)) {
            if (collisionTime > 0) {
                return collisionTime;
            } else {
                // if collisionTime is negative, return 0 instead if the
                // center of the ball is moving toward the segment (this
                // happens when the ball is overlapping the segment)
                double impactX = x1 + minS * width;
                double impactY = y1 + minS * height;
                if (velocity.dot(new Vect(a - impactX, b - impactY)) >= 0) {
                    return Double.POSITIVE_INFINITY;
                } else {
                    return 0;
                }
            }
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    /**
     * @see physics.Geometry#reflectWall
     **/
    public Vect reflectWall(LineSegment line,
            Vect velocity,
            double reflectionCoeff) {
        return applyReflectionCoeff(velocity,
                reflectWall(line, velocity),
                reflectionCoeff);
    }


    /**
     * @see physics.Geometry#reflectWall
     **/
    public Vect reflectWall(LineSegment line,
            Vect velocity)
    {
        double length = velocity.length();
        if (length == 0.0) {
            return velocity;
        }

        Angle theta = line.angle();     // angle of wall to horizontal
        Angle alpha = velocity.angle(); // angle of velocity to horizontal

        // The angle of incidence is alpha-theta, the angle of reflection
        // is therefor theta = (alpha-theta) = 2 theta - alpha.
        Angle phi = theta.plus(theta).minus(alpha);

        return new Vect(phi, length);
    }

    /****************************************************************************
     *
     * METHODS FOR CIRCLES
     *
     ***************************************************************************/

    /**
     * @see physics.Geometry#distanceSquared
     **/
    public double distanceSquared(Vect v1, Vect v2) {
        double height = (v2.y() - v1.y());
        double width = (v2.x() - v1.x());
        return ((width * width) + (height * height));
    }

    /**
     * @see physics.Geometry#distanceSquared
     **/
    public double distanceSquared(double x1, double y1,
            double x2, double y2) {
        double height = y2 - y1;
        double width = x2 - x1;
        return ((width * width) + (height * height));
    }

    /**
     * @see physics.Geometry#timeUntilCircleCollision
     **/
    public double timeUntilCircleCollision(Circle circle,
            Circle ball,
            Vect velocity) {
        Vect xy = circle.getCenter(),
                ab = ball.getCenter();
        double x       = xy.x(),
                y       = xy.y(),
                radius1 = circle.getRadius(),
                a       = ab.x(),
                b       = ab.y(),
                radius2 = ball.getRadius(),
                va      = velocity.x(),
                vb      = velocity.y();

        double distance = radius1 + radius2;
        double width = a - x;
        double height = b - y;
        double A = ((va * va) + (vb * vb));
        double B = 2.0 * ((va * width) + (vb * height));
        double C = (width * width) + (height * height) - (distance * distance);

        double ans = minQuadraticSolution(A, B, C);
        if (Double.isNaN(ans)) {
            return Double.POSITIVE_INFINITY;
        } else if (ans <= 0) {
            Vect impactDirection = ab.minus(xy);
            if (impactDirection.dot(velocity) >= 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return 0;
            }
        } else {
            // ans > 0 && ans <= +inf
            return ans;
        }
    }

    /**
     * @see physics.Geometry#reflectCircle
     **/
    public Vect reflectCircle(Vect circle,
            Vect ball,
            Vect velocity,
            double reflectionCoeff) {
        return applyReflectionCoeff(velocity,
                reflectCircle(circle, ball, velocity),
                reflectionCoeff);
    }

    /**
     * @see physics.Geometry#reflectCircle
     **/
    public Vect reflectCircle(Vect circle,
            Vect ball,
            Vect velocity)
    {
        double length = velocity.length();
        if (length == 0.0) {
            return velocity;
        }

        Vect incidence = circle.minus(ball);

        // It is as if the moving ball hits a wall perpendicular to the
        // angle of incidence.
        Angle theta = incidence.angle().plus(Angle.RAD_PI_OVER_TWO);

        // outgoing angle
        Angle phi = theta.plus(theta).minus(velocity.angle());

        return new Vect(phi, length);
    }

    /****************************************************************************
     *
     * METHODS FOR ROTATING LINE SEGMENTS AND CIRCLES
     *
     ***************************************************************************/

    /**
     * @see physics.Geometry#rotateAround
     **/
    public Vect rotateAround(Vect point, Vect cor, Angle a) {
        Vect original = point.minus(cor);
        Angle newAng = original.angle().plus(a);
        Vect after = new Vect(newAng, original.length());
        return cor.plus(after);
    }

    /**
     * @see physics.Geometry#rotateAround
     **/
    public LineSegment rotateAround(LineSegment line, Vect cor, Angle a) {
        return new LineSegment(rotateAround(line.p1(), cor, a),
                rotateAround(line.p2(), cor, a));
    }

    /**
     * @see physics.Geometry#rotateAround
     **/
    public Circle rotateAround(Circle circle, Vect cor, Angle a) {
        return new Circle(rotateAround(circle.getCenter(), cor, a),
                circle.getRadius());
    }

    /**
     * @see physics.Geometry#timeUntilCircleCollision
     **/
    public DoublePair timeUntilCircleCollision(Circle circle,
            Vect point,
            Vect velocity)
    {
        final double r = circle.getRadius();

        // position relative to the center of the circle
        final double x = point.x() - circle.getCenter().x();
        final double y = point.y() - circle.getCenter().y();

        // velocity relative to the center of the circle
        final double vx = velocity.x();
        final double vy = velocity.y();

        // solve for t in
        // |(x,y) + t*(vx,vy)|^2 = r^2
        final double a = vx * vx + vy * vy;
        final double b = 2 * (x * vx + y * vy);
        final double c = x * x + y * y - r * r;

        DoublePair result = quadraticSolution(a, b, c);

        if (Double.isNaN(result.d1)) {
            return new DoublePair(Double.POSITIVE_INFINITY);
        }

        return result;
    }

    /**
     * Restricts the ranges of times in <code>intervals</code> to only
     * contain periods of time when two pie slices (one rotating)
     * overlap.
     * 
     * Let the stationary arc consist of the arc running from
     * alpha to beta.  If bigSlice is true the arc should be
     * such that it subsumes more than pi radians, if bigSlice
     * is false the arc should be such that it subsumes less
     * than pi radians.  Also let the rotating arc start at
     * phi and extend width radians in the counter clockwise
     * direction.  Let the rotating arc be rotating at omega
     * radians per unit of time in the counter clockwise
     * direction and be located at the location described
     * above at time 0.  This function will restrict the
     * contents of intervals to only contain periods of time
     * during which the two arcs overlap.  This method only
     * operates and effects periods of time in the interval
     * from 0 to 4pi.
     * 
     * @param alpha ; requires that alpha to be in the range -2pi to 2pi
     * 
     * @param beta ; requires that beta to be in the range -2pi to 2pi
     * 
     * @param phi ; requires that phi to be in the range -2pi to 2pi
     * 
     * @param width ; requires that width to be in the range 0 to 2pi
     **/
    private void restrictIntervalByAngle(IntervalList intervals,
            double alpha,
            double beta,
            double omega,
            double phi,
            double width,
            boolean bigSlice) {
        if (alpha < 0) alpha += 2*Math.PI;
        if (beta < 0) beta += 2*Math.PI;

        double d = beta - alpha;
        if (d < 0 ) d += 2*Math.PI;

        if ((d < Math.PI && bigSlice) ||
                (d > Math.PI && !bigSlice)) {
            double t = alpha;
            alpha = beta;
            beta = t;
        }

        restrictIntervalByAngle(intervals, alpha, beta, omega, phi, width);
    }

    /**
     * Restricts the ranges of times in <code>intervals</code> to only
     * contain periods of time when two pie slices (one rotating)
     * overlap.
     * 
     * Let the stationary arc consist of the arc running from
     * alpha to beta in a counter clockwise direction and let
     * the rotating arc start at phi and extend width radians
     * in the counter clockwise direction.  Let the rotating
     * arc be rotating at omega radians per unit of time in
     * the counter clockwise direction and be located at the
     * location described above at time 0.  This function will
     * restrict the contents of intervals to only contain
     * periods of time during which the two arcs overlap.
     * This method only operates and effects periods of time
     * in the interval from 0 to 4pi.
     * 
     * @param alpha ; requires that alpha to be in the range -2pi to 2pi
     * 
     * @param beta ; requires that beta to be in the range -2pi to 2pi
     * 
     * @param phi ; requires that phi to be in the range -2pi to 2pi
     * 
     * @param width ; requires that width to be in the range 0 to 2pi
     * 
     * @modifies: intervals
     **/
    private void restrictIntervalByAngle(IntervalList intervals,
            double alpha,
            double beta,
            double omega,
            double phi,
            double width) {
        if (phi < 0) phi += 2*Math.PI;
        if (alpha < 0) alpha += 2*Math.PI;
        if (beta < 0) beta += 2*Math.PI;

        if (alpha > 2*Math.PI || alpha < 0 || Double.isNaN(alpha)) {
            throw new IllegalArgumentException();
        }
        if (beta > 2*Math.PI || beta < 0 || Double.isNaN(beta)) {
            throw new IllegalArgumentException();
        }
        if (phi > 2*Math.PI || phi < 0 || Double.isNaN(phi)) {
            throw new IllegalArgumentException();
        }
        if (width < 0 || width > 2*Math.PI || Double.isNaN(width)) {
            throw new IllegalArgumentException();
        }

        // from now on {alpha beta phi width} are [0, 2pi]

        if (omega < 0) {
            omega = -omega;
            alpha = -alpha;
            beta  = -beta;
            phi   = phi + width;
            if (phi > 2*Math.PI) {
                phi -= 2*Math.PI;
            }
            phi   = -phi;

            if (alpha < 0) alpha += 2*Math.PI;
            if (beta < 0) beta += 2*Math.PI;
            if (phi  < 0) phi  += 2*Math.PI;

            double t = alpha;
            alpha = beta;
            beta = t;
        }

        // omega >= 0

        alpha -= phi;
        beta -= phi;

        if (alpha < 0) alpha += 2*Math.PI;
        if (beta < 0) beta += 2*Math.PI;

        // phi is now effectively zero

        double t = Double.NaN;
        double nextT = 0;
        boolean validRegion = (alpha > beta || alpha < width);

        // When (alpha == beta + width), t stops incrementing after it
        // reaches (beta / omega) in the first iteration.  This is the
        // motivation for the (t != nextT) check.

        double twiceAround = 2 * (2*Math.PI) / omega;
        while ((nextT < twiceAround) && (t != nextT)) {
            t = nextT;
            if (validRegion) {
                double angleToBeta = beta - t*omega;
                while (angleToBeta < 0) angleToBeta += 2*Math.PI;
                // beta is next
                nextT = t + angleToBeta  / omega;
                validRegion = false;
            } else {
                double angleToAlpha = alpha - t*omega - width;
                while (angleToAlpha < 0) angleToAlpha += 2*Math.PI;
                // alpha is next
                nextT = t + angleToAlpha / omega;
                intervals.removeInterval(t, nextT);
                validRegion = true;
            }
        }
        t = nextT;

        if (validRegion == false && t < twiceAround) {
            intervals.removeInterval(t, twiceAround);
        }
    }

    /**
     * Given the path of a ball and the path of something rotating in a
     * circle, restricts the possible time periods during which they
     * might collide by doing some crazy confusing analysis originally
     * concocted by Professor Ernst.  Subclass SimpleGeometry disables these
     * optimizations.
     *
     * <p><img src="doc-files/restrictSearchInterval-page1.jpg">
     * <p><img src="doc-files/restrictSearchInterval-page2.jpg">
     * <p><img src="doc-files/restrictSearchInterval-page3.jpg">
     *
     * @see physics.SimpleGeometry
     *
     * @param intervals
     * 
     * @param inner_radius ; requires that inner_radius < outer_radius
     * 
     * @param outer_radius ; requires that outer_radius > 0
     * 
     * @param phi_1 ; requires that 0 <= phi_1 < 2pi
     * 
     * @param phi_2 ; requires that 0 <= phi_2 < 2pi
     * 
     * @param omega
     * 
     * @param center
     * 
     * @param ball
     * 
     * @param velocity
     *
     * @return an IntervalList containing a subset of the
     * times contained by <code>intervals</code>.  This subset
     * is based on when a ball whose initial condition at t=0
     * is represented by <code>ball</code> traveling at a
     * linear velocity of <code>velocity</code> might
     * intersect an object rotating at an angular velocity of
     * <code>omega</code> about a point specified by
     * <code>center</code> which subsumes an arc from
     * <code>phi_1</code> to <code>phi_2</code>.
     **/
    protected IntervalList restrictSearchInterval(IntervalList intervals,
            double inner_radius,
            double outer_radius,
            double phi_1,
            double phi_2,
            double omega,
            Vect center,
            Circle ball,
            Vect velocity) {

        // If memory serves, this method performs two varieties of optimization.
        // It was written in spring 2001 by Jeffrey Sheldon, with help from
        // Michael Ernst.
        //  1. The wall may carve out not a circle, but a donut; the outer circle
        //     is traversed by the end of the wall that is furthest from the
        //     center, and the inner circle is traversed by the part of the wall
        //     (perhaps and end, perhaps not) that is nearest the circle.  While
        //     the ball is traveling through the hole in the donut, no checking
        //     need be done, because collisions are impossible.
        //  2. Given the position and angular velocity of the wall, and the
        //     position and angular velocity of the ball, it may be that they are
        //     guaranteed never to collide.
        // These optimizations are particularly useful when the inner radius is
        // large -- for instance, when an object is rotating around a distant
        // point.
        // The hand-drawn documentation for the 12 cases appear in
        // doc-files/restrictSearchInterval-page[123].jpg.
        // -MDE 4/2003

        if (velocity.length() == 0) {
            Vect ballCenter = ball.getCenter();
            double dist = ballCenter.minus(center).length();
            if (dist - ball.getRadius() < outer_radius) {
                intervals.restrictSubIntervalLength(2*Math.PI/Math.abs(omega));
                return intervals;
            } else {
                return new IntervalList();
            }
        }

        // Now we wish to compute the interval of time during which the
        // ball is inside of the outer circle

        DoublePair dp =
                timeUntilCircleCollision(new Circle(center,
                        outer_radius + ball.getRadius()),
                        ball.getCenter(), velocity);
        if (!dp.areFinite()) {
            // This also covers CASE 4 of the angle slicing
            return new IntervalList();
        }

        intervals.restrictToInterval(dp.d1, dp.d2);

        if (intervals.isEmpty()) {
            return intervals;
        }

        if (inner_radius - ball.getRadius() > 0) {
            dp =
                    timeUntilCircleCollision(new Circle(center,
                            inner_radius - ball.getRadius()),
                            ball.getCenter(), velocity);
            if (dp.areFinite()) {
                intervals.removeInterval(dp.d1, dp.d2);
            }
        }

        if (intervals.min() < 0) {
            intervals.removeInterval(intervals.min(), 0.0);
        }

        intervals.restrictSubIntervalLength(2*2*Math.PI/Math.abs(omega));

        if (intervals.isEmpty()) {
            return intervals;
        }

        Vect ballVelocityNormal = velocity.rotateBy(Angle.RAD_PI_OVER_TWO);
        if (ballVelocityNormal.dot(ball.getCenter().minus(center)) < 0) {
            ballVelocityNormal = ballVelocityNormal.neg();
        }
        // ballVelocityNormal should now be normal to the balls velocity
        // such that when located at the balls position it points away
        // from the center of rotation

        ballVelocityNormal = ballVelocityNormal.unitSize().times(ball.getRadius());

        Vect farRailStartPoint = ball.getCenter().plus(ballVelocityNormal);
        Vect nearRailStartPoint = ball.getCenter().minus(ballVelocityNormal);

        // intersection of the far side of the balls locus of travel with
        // the outer edge of the doughnut swept by the line
        DoublePair farOuterIntersections =
                timeUntilCircleCollision(new Circle(center, outer_radius),
                        farRailStartPoint,
                        velocity);
        // intersection of the near side of the balls locus of travel with
        // the outer edge of the doughnut swept by the line
        DoublePair nearOuterIntersections =
                timeUntilCircleCollision(new Circle(center, outer_radius),
                        nearRailStartPoint,
                        velocity);
        // intersection of the far side of the balls locus of travel with
        // the inner edge of the doughnut swept by the line
        DoublePair farInnerIntersections =
                timeUntilCircleCollision(new Circle(center, inner_radius),
                        farRailStartPoint,
                        velocity);
        // intersection of the near side of the balls locus of travel with
        // the inner edge of the doughnut swept by the line
        DoublePair nearInnerIntersections =
                timeUntilCircleCollision(new Circle(center, inner_radius),
                        nearRailStartPoint,
                        velocity);

        Vect centerToFarBounds =
                perpendicularPointWholeLine(new LineSegment(farRailStartPoint,
                        farRailStartPoint.plus(velocity)),
                        center);
        Vect centerToNearBounds =
                perpendicularPointWholeLine(new LineSegment(nearRailStartPoint,
                        nearRailStartPoint.plus(velocity)),
                        center);

        // now compute locations of intersection
//        Vect farOuterVect1 =
//                farRailStartPoint.plus(velocity.times(farOuterIntersections.d1));
//        Vect farOuterVect2 =
//                farRailStartPoint.plus(velocity.times(farOuterIntersections.d2));
        Vect nearOuterVect1 =
                nearRailStartPoint.plus(velocity.times(nearOuterIntersections.d1));
        Vect nearOuterVect2 =
                nearRailStartPoint.plus(velocity.times(nearOuterIntersections.d2));
        Vect farInnerVect1 =
                farRailStartPoint.plus(velocity.times(farInnerIntersections.d1));
        Vect farInnerVect2 =
                farRailStartPoint.plus(velocity.times(farInnerIntersections.d2));
        Vect nearInnerVect1 =
                nearRailStartPoint.plus(velocity.times(nearInnerIntersections.d1));
        Vect nearInnerVect2 =
                nearRailStartPoint.plus(velocity.times(nearInnerIntersections.d2));

        // now compute angles of intersection

        //double farOuterAngle1 = farOuterVect1.minus(center).angle().radians();
        //double farOuterAngle2 = farOuterVect2.minus(center).angle().radians();
        double nearOuterAngle1 = nearOuterVect1.minus(center).angle().radians();
        double nearOuterAngle2 = nearOuterVect2.minus(center).angle().radians();
        double farInnerAngle1 = farInnerVect1.minus(center).angle().radians();
        double farInnerAngle2 = farInnerVect2.minus(center).angle().radians();
        double nearInnerAngle1 = nearInnerVect1.minus(center).angle().radians();
        double nearInnerAngle2 = nearInnerVect2.minus(center).angle().radians();


        // phi is is starting location of the arc swept by the line
        double phi = (phi_1 < phi_2) ? phi_1 : phi_2;
        if (phi < 0) phi += 2*Math.PI;
        double width = phi_1 - phi_2;
        if (width < 0) width = -width;

        if (!farInnerIntersections.areFinite()) {
            // Page 1 or Page 2:

            // note: the "whiff" (CASE 4) case has already been dealt with above.

            if (!farOuterIntersections.areFinite() &&
                    !nearOuterIntersections.areFinite()) {
                // CASE 3
                // this is the case where the size of the ball is larger than
                // the size of the doughnut and thus a hit is imminent.

                // in this case we need only ever consider a single full revolution
                intervals.restrictSubIntervalLength(2*Math.PI/Math.abs(omega));
            } else {
                if (centerToFarBounds.dot(centerToNearBounds) > 0) {
                    // CASE 1 or CASE 2 or CASE 7 or CASE 9
                    restrictIntervalByAngle(intervals,
                            nearOuterAngle1, nearOuterAngle2,
                            omega, phi, width, false);
                } else {
                    // CASE 5 or CASE 6 or CASE 8 or CASE 10
                    if (nearOuterIntersections.areFinite()) {
                        // PAGE 1
                        // CASE 5 or CASE 6
                        intervals.restrictSubIntervalLength(2*Math.PI/Math.abs(omega));
                    } else {
                        // PAGE 2
                        // CASE 8 or CASE 10
                        restrictIntervalByAngle(intervals,
                                nearInnerAngle1, nearInnerAngle2,
                                omega, phi, width, true);
                    }
                }
            }
        } else {
            // Page 3: If far outer already intersects we know far inner
            // will.  This puts us on page 3 with both rails intersecting
            // the inner circle
            if (!farInnerIntersections.areFinite() ||
                    !farOuterIntersections.areFinite() ||
                    !nearInnerIntersections.areFinite() ||
                    !nearOuterIntersections.areFinite()) {
                throw new IllegalArgumentException();
            }

            IntervalList intervals2 = new IntervalList(intervals);

            if(centerToFarBounds.dot(centerToNearBounds) > 0) {
                // CASE 11
                restrictIntervalByAngle(intervals,
                        nearOuterAngle1, farInnerAngle1,
                        omega, phi, width, false);
                restrictIntervalByAngle(intervals2,
                        farInnerAngle2, nearOuterAngle2,
                        omega, phi, width, false);
            } else {
                // CASE 12
                restrictIntervalByAngle(intervals,
                        nearInnerAngle1, farInnerAngle1,
                        omega, phi, width, false);
                restrictIntervalByAngle(intervals,
                        farInnerAngle2, nearInnerAngle2,
                        omega, phi, width, false);
            }
            intervals.addIntervalList(intervals2);
        }

        return intervals;
    }

    /**
     * @return the root of <code>distanceFunction</code> of the 
     * least value which occurs within <code>intervals</code>.  
     * If no such root exists, or no such root is found, returns +Inf.
     **/
    private double searchForCollision(Newton.Function distanceFunction,
            IntervalList intervals) {
        Iterator<IntervalList.Interval> iter = intervals.iterator();
        while (iter.hasNext()) {
            IntervalList.Interval interval = iter.next();

            if (interval.start() < 0) {
                if (interval.end() < 0) {
                    continue;
                }
                interval = new IntervalList.Interval(0, interval.end());
            }

            if (interval.end() < interval.start()) {
                continue;
            }

            double t_step = (interval.end() - interval.start()) / searchSlices;

            double collisionTime = Newton.findRoot(distanceFunction,
                    interval.start(),
                    interval.end(),
                    t_step);

            if (collisionTime >= 0) {
                Newton.Result result = distanceFunction.evaluate(collisionTime);
                if (result.f_prime < 0) {
                    return collisionTime;
                } else {
                    return Double.POSITIVE_INFINITY;
                }
            }
        }

        return Double.POSITIVE_INFINITY;
    }

    /**
     * @see physics.Geometry#timeUntilRotatingWallCollision
     **/
    public double timeUntilRotatingWallCollision(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity)
    {
        // we special case the non-rotating case because that scenario
        //   has a closed-form solution which is both faster to compute
        //   and also is more accurate

        if (angularVelocity == 0.0) {
            return timeUntilWallCollision(line, ball, velocity);
        }

        final double omega = angularVelocity;

        final double x1_0 = line.p1().x();
        final double y1_0 = line.p1().y();
        final double x2_0 = line.p2().x();
        final double y2_0 = line.p2().y();

        final double r_x = center.x();
        final double r_y = center.y();

        final double r_p1_2 = (r_x-x1_0)*(r_x-x1_0) + (r_y-y1_0)*(r_y-y1_0);
        final double r_p2_2 = (r_x-x2_0)*(r_x-x2_0) + (r_y-y2_0)*(r_y-y2_0);
        final double r_p1   = Math.sqrt(r_p1_2); // distance from center to p1
        final double r_p2   = Math.sqrt(r_p2_2); // distance from center to p2
        final double phi_1  = Math.atan2(y1_0 - r_y, x1_0 - r_x); // angle to p1
        final double phi_2  = Math.atan2(y2_0 - r_y, x2_0 - r_x); // angle to p2

        final double b_x  = ball.getCenter().x();
        final double b_y  = ball.getCenter().y();
        final double b_r  = ball.getRadius();
        final double vb_x = velocity.x();
        final double vb_y = velocity.y();

        class RotatingWallDistance implements Newton.Function {
            public Newton.Result evaluate(double t) {
                double p1_t_cos     = r_p1 * Math.cos(omega * t + phi_1);
                double p1_t_sin     = r_p1 * Math.sin(omega * t + phi_1);
                double p2_t_cos     = r_p2 * Math.cos(omega * t + phi_2);
                double p2_t_sin     = r_p2 * Math.sin(omega * t + phi_2);

                double x1_t         = p1_t_cos + r_x;
                double y1_t         = p1_t_sin + r_y;
                double x2_t         = p2_t_cos + r_x;
                double y2_t         = p2_t_sin + r_y;

                double b_x_t        = vb_x * t + b_x;
                double b_y_t        = vb_y * t + b_y;

                // now find the intercept of the line segment, and a perpendicular
                // line through the center of the ball.

                double height = y2_t - y1_t;
                double width  = x2_t - x1_t;

                double length_squared = width*width + height * height;
                double fraction = (((width  * (b_x_t - x1_t)) +
                        (height * (b_y_t - y1_t))) /
                        (length_squared));

                // require that the ball hits the segment, not the line
                if (!((0.0 <= fraction) && (fraction <= 1.0))) {
                    return new Newton.Result(Double.NaN, Double.NaN);
                }

                double x_t = x1_t + fraction * width;
                double y_t = y1_t + fraction * height;

                double dist_sq = (x_t - b_x_t) * (x_t - b_x_t) +
                        (y_t - b_y_t) * (y_t - b_y_t);

                double f = dist_sq - b_r*b_r;

                // now compute the derivative  - ugly

                double x1_t_prime  = -omega * p1_t_sin;
                double y1_t_prime  =  omega * p1_t_cos;
                double x2_t_prime  = -omega * p2_t_sin;
                double y2_t_prime  =  omega * p2_t_cos;

                double b_x_t_prime = vb_x;
                double b_y_t_prime = vb_y;

                double height_prime = y2_t_prime - y1_t_prime;
                double width_prime  = x2_t_prime - x1_t_prime;

                double length_squared_prime = 2 * width * width_prime +
                        2 * height * height_prime;

                double fraction_prime =
                        ((length_squared * ((width_prime * (b_x_t - x1_t) +
                                width * (b_x_t_prime - x1_t_prime)) +
                                (height_prime * (b_y_t - y1_t) +
                                        height * (b_y_t_prime - y1_t_prime))))
                                        -
                                        (length_squared_prime * ((width * (b_x_t - x1_t)) +
                                                (height * (b_y_t - y1_t)))))
                                                /
                                                (length_squared * length_squared);

                double x_t_prime = x1_t_prime +
                        fraction * width_prime +
                        fraction_prime * width;
                double y_t_prime = y1_t_prime +
                        fraction * height_prime +
                        fraction_prime * height;

                double f_prime =
                        2 * (x_t - b_x_t) * (x_t_prime - b_x_t_prime) +
                        2 * (y_t - b_y_t) * (y_t_prime - b_y_t_prime);

                return new Newton.Result(f, f_prime);
            }
        }

        // Look to see if the ball is already overlapping with the line.
        // If so return 0 iff the ball is headed toward the line, and +inf
        // if they are headed away from one another
        Newton.Function function = new RotatingWallDistance();
        Newton.Result initialDistance = function.evaluate(0);

        if (initialDistance.f <= 0) {
            if (initialDistance.f_prime >= 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return 0;
            }
        }

        // the outer radius of the doughnut that line sweeps out
        final double outer_radius = ((r_p1 > r_p2) ? r_p1 : r_p2);
        // the inner radius of the doughnut that line sweeps out
        final double inner_radius;
        Vect distToCenter = perpendicularPoint(line, center);
        if (distToCenter == null) {
            inner_radius = ((r_p1 > r_p2) ? r_p2 : r_p1);
        } else {
            inner_radius = distToCenter.minus(center).length();
        }

        IntervalList intervals = new IntervalList(0, maximumForesight);
        intervals = restrictSearchInterval(intervals,
                inner_radius,
                outer_radius,
                phi_1,
                phi_2,
                omega,
                center,
                ball,
                velocity);

        // Now that the intervals of interest have been reduced by
        // analyzing the angle that the line sweeps out, perform newton's
        // method on each interval, in order and return the first
        // collision, if any.
        return searchForCollision(function, intervals);
    }

    /**
     * @see physics.Geometry#reflectRotatingWall
     **/
    public Vect reflectRotatingWall(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity) {
        return reflectRotatingWall(line, center,
                angularVelocity,
                ball, velocity,
                1.0);
    }

    /**
     * @see physics.Geometry#reflectRotatingWall
     **/
    public Vect reflectRotatingWall(LineSegment line,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity,
            double reflectionCoeff)
    {
        // we special case the non-rotating case because that scenario
        //   has a simpler solution which may lead to a slightly more
        //   accurate result

        if (angularVelocity == 0.0) {
            return reflectWall(line, velocity, reflectionCoeff);
        }

        //double radius = ball.getRadius();

        // translate everything so that center is the origin
        Vect p1 = line.p1().minus(center);
        Vect p2 = line.p2().minus(center);
        Vect ballCenter = ball.getCenter().minus(center);
        line = new LineSegment(p1, p2);

        // calculate the point at which the ball and wall collide
        Vect perpPt = perpendicularPoint(line, ballCenter);

        if (perpPt == null) {
            return velocity;
        }

        // note that if the angular velocity is negative then this works
        // out because multiplying by the angular velocity makes the
        // resulting vector length negative, which is equivalent to a
        // rotation by PI radians.
        Vect myVel = perpPt.times(angularVelocity).rotateBy(Angle.RAD_PI_OVER_TWO);

        // translate into reference frame of the moving wall
        Vect relativeV = velocity.minus(myVel);

        Vect reflectV = reflectWall(line, relativeV, reflectionCoeff);

        // now translate back into the absolute reference frame.
        Vect absoluteV = myVel.plus(reflectV);

        return absoluteV;
    }

    /**
     * @see physics.Geometry#timeUntilRotatingCircleCollision
     **/
    public double timeUntilRotatingCircleCollision(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity)
    {
        // we special case the non-rotating case because that scenario
        //   has a closed-form solution which is both faster to compute
        //   and also is more accurate

        if (angularVelocity == 0.0) {
            return timeUntilCircleCollision(circle, ball, velocity);
        }

        final double omega = angularVelocity;

        final double c_x  = circle.getCenter().x();
        final double c_y  = circle.getCenter().y();
        final double c_r  = circle.getRadius();

        final double r_x  = center.x();
        final double r_y  = center.y();

        // if the circle is rotating about it's own center then we special
        // case it to call out to the speedier non rotating function
        if (r_x == c_x && r_y == c_y) {
            return timeUntilCircleCollision(circle, ball, velocity);
        }

        final double r_r2 = (r_x-c_x)*(r_x-c_x) + (r_y-c_y)*(r_y-c_y); // radius squared
        final double r_r  = Math.sqrt(r_r2);
        final double phi  = Math.atan2(c_y-r_y, c_x-r_x);  // radius of rotation

        final double b_x  = ball.getCenter().x();
        final double b_y  = ball.getCenter().y();
        final double b_r  = ball.getRadius();
        final double vb_x = velocity.x();
        final double vb_y = velocity.y();

        final double b_to_c_sq = (b_r + c_r) * (b_r + c_r);

        class RotatingCircleDistance implements Newton.Function {
            public Newton.Result evaluate(double t) {
                // essentially the approach is to compute the shortest
                // distance between the balls center and the center of the
                // rotating circle and then subtract both the radius of the
                // ball and the radius of the circle.  A collision will occur
                // when this value is 0.
                double c_t_cos   = r_r * Math.cos(omega * t + phi);
                double c_t_sin   = r_r * Math.sin(omega * t + phi);

                double c_x_t     = c_t_cos + r_x;
                double c_y_t     = c_t_sin + r_y;

                double b_x_t     = vb_x * t + b_x;
                double b_y_t     = vb_y * t + b_y;

                double dist_sq   = (c_x_t - b_x_t) * (c_x_t - b_x_t) +
                        (c_y_t - b_y_t) *  (c_y_t - b_y_t);

                double f = dist_sq - b_to_c_sq;

                double c_x_t_prime = -omega * c_t_sin;
                double c_y_t_prime =  omega * c_t_cos;

                double f_prime = 2 * (c_x_t - b_x_t) * (c_x_t_prime - vb_x) +
                        2 * (c_y_t - b_y_t) * (c_y_t_prime - vb_y);
                return new Newton.Result(f, f_prime);
            }
        }

        // Look to see if the ball is already overlapping with the circle.
        // If so return 0 iff the ball is headed toward the circle and
        // +inf if they are headed away from one another.
        Newton.Function function = new RotatingCircleDistance();
        Newton.Result initialDistance = function.evaluate(0);

        if (initialDistance.f <= 0) {
            if (initialDistance.f_prime >= 0) {
                return Double.POSITIVE_INFINITY;
            } else {
                return 0;
            }
        }

        final double outer_radius = r_r + c_r;
        final double inner_radius = (r_r - c_r) >= 0 ? r_r - c_r : 0;

        final double arcWidth = 2 * Math.asin(c_r / r_r);

        IntervalList intervals = new IntervalList(0, maximumForesight);
        intervals = restrictSearchInterval(intervals,
                inner_radius,
                outer_radius,
                phi - arcWidth,
                phi + arcWidth,
                omega,
                center,
                ball,
                velocity);

        // Now that the intervals of interest have been reduced by
        // analyzing the angle that the line sweeps out, perform newton's
        // method on each interval, in order and return the first
        // collision, if any.
        return searchForCollision(function, intervals);
    }

    /**
     * @see physics.Geometry#reflectRotatingCircle
     **/
    public Vect reflectRotatingCircle(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity) {
        return reflectRotatingCircle(circle, center,
                angularVelocity,
                ball, velocity,
                1.0);
    }


    /**
     * @see physics.Geometry#reflectRotatingCircle
     **/
    public Vect reflectRotatingCircle(Circle circle,
            Vect center,
            double angularVelocity,
            Circle ball,
            Vect velocity,
            double reflectionCoeff)
    {
        // we special case the non-rotating case because that scenario
        //   has a simpler solution which may lead to a slightly more
        //   accurate result

        if (angularVelocity == 0.0) {
            return reflectCircle(circle.getCenter(), ball.getCenter(), velocity,
                    reflectionCoeff);
        }

        double radius1 = circle.getRadius();
        double radius2 = ball.getRadius();

        // translate everything so that center is the origin
        Vect circleCenter = circle.getCenter().minus(center);
        Vect ballCenter = ball.getCenter().minus(center);

        // calculate the point at which the ball and circle collide
        double ratio = radius1 / (radius1 + radius2);
        Vect diff = circleCenter.minus(ballCenter);
        Vect collidePt = circleCenter.plus(diff.times(ratio));

        // note that if the angular velocity is negative then this works
        // out because multiplying by the angular velocity makes the
        // resulting vector length negative, which is equivalent to a
        // rotation by PI radians.
        Vect myVel =
                collidePt.times(angularVelocity).rotateBy(Angle.RAD_PI_OVER_TWO);
        // translate into reference frame of the moving circle
        Vect relativeV = velocity.minus(myVel);

        Vect reflectV = reflectCircle(circleCenter, ballCenter, relativeV,
                reflectionCoeff);

        // now translate back into the absolute reference frame.
        Vect absoluteV = myVel.plus(reflectV);

        return absoluteV;
    }

    /****************************************************************************
     *
     * METHODS FOR MULTI-BALL SIMULATIONS
     *
     ***************************************************************************/


    /**
     * @see physics.Geometry#timeUntilBallBallCollision
     **/
    public double timeUntilBallBallCollision(Circle ball1,
            Vect   vel1,
            Circle ball2,
            Vect   vel2) {
        Vect pos1             = ball1.getCenter();
        Vect pos2             = ball2.getCenter();
        double sizes          = ball1.getRadius() + ball2.getRadius();
        double initPosXDelta  = pos1.x() - pos2.x();
        double initPosYDelta  = pos1.y() - pos2.y();
        double velXDelta      = vel1.x() - vel2.x();
        double velYDelta      = vel1.y() - vel2.y();
        double sizes2         = sizes * sizes;
        double initPosXDelta2 = initPosXDelta * initPosXDelta;
        double initPosYDelta2 = initPosYDelta * initPosYDelta;
        double initGap2       = initPosXDelta2 + initPosYDelta2 - sizes2;

        // if the circles are overlapping (including exactly touching),
        // then we decide if they collide based upon their relative
        // velocity

        if (initGap2 <= 0.0) {
            // 1's velocity towards 2
            Vect velDelta = new Vect(velXDelta, velYDelta);
            // 1's position if 2 were at the origin
            Vect initPosDelta = new Vect(initPosXDelta, initPosYDelta);
            // hit iff the component of velDelta in the initPosDelta
            // direction is will decrease initPosDelta (i.e. is negative)
            if (velDelta.dot(initPosDelta) < 0.0) {
                return 0.0;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        }

        // otherwise, we do some math to figure out when they hit

        double a = velXDelta * velXDelta + velYDelta * velYDelta;
        double b = 2 * initPosXDelta * velXDelta + 2 * initPosYDelta * velYDelta;
        double c = initPosXDelta2 + initPosYDelta2 - sizes2;

        double t = minQuadraticSolution(a,b,c);
        if (t > 0) {
            return t;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }


    /**
     * @see physics.Geometry#reflectBalls
     **/
    public VectPair reflectBalls(Vect center1,
            double mass1,
            Vect velocity1,
            Vect center2,
            double mass2,
            Vect velocity2) {
        double m1 = mass1;
        double m2 = mass2;
        double m = m1/m2;

        Vect tHat = center1.minus(center2).unitSize();
        double vx1 = velocity1.x();
        double vx2 = velocity2.x();
        double vy1 = velocity1.y();
        double vy2 = velocity2.y();
        double tx  = tHat.x();
        double ty  = tHat.y();

        double gamma = (-2 * (vx1*tx*m1 + vy1*ty*m1 - vx2*tx*m*m2 - vy2*ty*m*m2)) /
                (tx*tx*m1 + ty*ty*m1 + m*m*tx*tx*m2 + m*m*ty*ty*m2);

        return new VectPair(velocity1.plus(tHat.times(gamma)),
                velocity2.plus(tHat.neg().times(gamma*m)));
    }
}
