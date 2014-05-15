package physics;

import physics.Geometry.DoublePair;
// import statement added to mollify javadoc

/****************************************************************************
 * Copyright (C) 2001-2014 by the Massachusetts Institute of Technology,
 *                    Cambridge, Massachusetts.
 *
 *                      All Rights Reserved
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
 * SimpleGeometry alters the behavior of GeometyImpl by removing the
 * doughnut optimizations.  This often reduces the running time of the
 * timeUntilRotating* methods, but <b>will</b> reduce accuracy unless
 * a small maximumForesight is used.  Most callers will not use
 * SimpleGeometry directly, but will use the singleton Geometry
 * instead.
 *
 * <p>
 *
 * The doughnut optimizations are used by the timeUntilRotating*
 * methods to narrow the possible times during which a collision might
 * happen, in order to narrow the search space and improve accuracy.
 *
 * <p>
 *
 * When doughnut optimizations are disabled, the timeUntilRotating*
 * methods will always evaluate at least <code>searchSlices</code>
 * data points between 0 and <code>maximumForesight</code> to search
 * for a root.  This will be faster for the cases where the doughnut
 * calculations do not lead to a useful decrease in the time being
 * searched, but will be slower for the cases where the doughnut
 * optimizations would have deduced that no collision was possible.
 *
 * @see physics.GeometryImpl
 * @see physics.Geometry
 **/
class SimpleGeometry extends GeometryImpl
{

    /**
     * @param maximumForesight ; requires that (maximumForesight >= 0.0) && 
     * (searchSlices >= 1) && ((searchSlices >= 200) || 
     * (maximumForesight / searchSlices <= 0.01))
     * 
     * @param searchForCollisionSlices
     * 
     * Constructs a SimpleGeometry with the specified tuning
     * parameters, which are described in the class overview of
     * GeometryImpl.
     *
     * @see physics.GeometryImpl
     **/
    public SimpleGeometry(double maximumForesight, int searchForCollisionSlices) {
        super(maximumForesight, searchForCollisionSlices);
        if (!((searchSlices >= 200) || ((maximumForesight / searchSlices) <= 0.01))) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Performs the operation in a more conservative way than
     * the superclass implementation (omits the doughnut optimizations).
     **/
    protected IntervalList restrictSearchInterval(IntervalList intervals,
            double inner_radius,
            double outer_radius,
            double phi_1,
            double phi_2,
            double omega,
            Vect center,
            Circle ball,
            Vect velocity)
    {
        // Compute the interval where we are in the outer circle

        Circle outer_plus_ball =
                new Circle(center, outer_radius + ball.getRadius());

        DoublePair dp = timeUntilCircleCollision(outer_plus_ball,
                ball.getCenter(), velocity);

        // If we never hit, we have no interval
        if (!dp.areFinite()) {
            return new IntervalList();
        }

        // Limit to the outer circle time
        intervals.restrictToInterval(dp.d1, dp.d2);

        return intervals;
    }

}
