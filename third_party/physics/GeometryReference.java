package physics;


/****************************************************************************
 * Copyright (C) 2001-2014 by the Massachusetts Institute of Technology,
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
 * @author: Jeremy Nimmer (jwnimmer@alum.mit.edu)
 *          Spring 2001
 *
 * @author: Robert C. Miller (rcm@mit.edu), Danny Yuan
 *          Spring 2014
 *
 ***************************************************************************/

/**
 * GeometryReference is a reference implementation of
 * GeometryInterface which can be used to check results from other
 * implementations.  This class is intended for debugging.
 *
 * @see physics.Geometry
 **/
class GeometryReference extends SimpleGeometry
{
    /**
     * Returns a new instance of a reference Geometry object
     **/
    public GeometryReference()
    {
        super(Double.POSITIVE_INFINITY, 300);
    }

}
