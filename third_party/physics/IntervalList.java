package physics;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
 * @author: Jeffrey Sheldon (jeffshel@mit.edu)
 *          Spring 2001
 *
 * @author: Robert C. Miller (rcm@mit.edu), Danny Yuan
 *          Spring 2014
 *
 *
 ***************************************************************************/

/**
 * An IntervalList is a mutable abstraction of a set of ranges of
 * continuous numbers.
 **/

class IntervalList
{

    // Representation Invariant:
    // forall 0 <= i < j < intervals.size,
    //            intervals[i].end < intervals[j].start

    // Abstraction Function:
    //   An interval list represents the set of all numbers contained 
    //   within any of the intervals in intervals.  


    /**
     * An Interval is an immutable representation of a single contiguous
     * range of numbers from start to end.
     **/
    public static class Interval implements Comparable<Interval>
    {

        // Representation Invariant:
        //    start < end

        // Abstraction Function:
        //   The set of all numbers between start and end

        public final double start;
        public final double end;

        /**
         * @param start ; requires that end >= start
         * 
         * @param end ; requires that end >= start
         *
         * Creates a new Interval representing the numbers from
         * <code>start</code> to <code>end</code>.
         **/    
        public Interval(double start, double end) {
            if (Double.isNaN(start) || Double.isNaN(end)) {
                throw new IllegalArgumentException();
            }
            if (end < start) {
                System.out.println("start = " + start);
                System.out.println("end   = " + end);
                throw new IllegalArgumentException();
            }
            this.start = start;
            this.end = end;
        }

        /**
         * @return the lower bound of this Interval.
         **/
        public double start() {
            return start;
        } 

        /**
         * @return the upper bound of this Interval.
         **/
        public double end() {
            return end;
        }

        // returns true of this and i overlap
        private boolean overlaps(Interval i) {
            if (this.start <= i.start) {
                if (i.start > this.end) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return i.overlaps(this);
            }
        }

        // requires that this an i overlap, returns a single interval
        // representing the union of the numbers contained within.
        private Interval merge(Interval i) {
            if (overlaps(i)) {
                if (this.start <= i.start) {
                    return new Interval(this.start, (i.end>this.end) ? i.end : this.end);
                } else {
                    return i.merge(this);
                }
            } else {
                throw new IllegalArgumentException();
            }
        }

        // returns a new interval with the same start point as this but
        // with an endpoint which is the nearer of this.end and this.start
        // + length
        private Interval restrictLength(double length) {
            if (!Double.isInfinite(start) && !Double.isInfinite(end) &&
                    (end - start > length)) {
                return new Interval(start, start + length);
            } else if (!Double.isInfinite(start) && Double.isInfinite(end)) {
                return new Interval(start, start + length);
            } else {
                return this;
            }
        }

        // requires this overlaps with i, returns a new Interval which
        // contains the intersection of numbers from this and i.
        private Interval restrictTo(Interval i) {
            if (!overlaps(i)) {
                throw new IllegalArgumentException();
            }
            return new Interval(start > i.start ? start : i.start,
                    end   < i.end   ? end   : i.end);
        }

        // compares based only on the ordering of the start value
        public int compareTo(Interval i) {
            //Interval i = (Interval) o;
            if (this.start < i.start) {
                return -1;
            } else if (this.start > i.start) {
                return 1;
            } else {
                return 0;
            }
        }

        public boolean equals(Object o) {
            if (o instanceof Interval) {
                Interval i = (Interval) o;
                if (i == this) { return true; }
                if (this.start == i.start &&
                        this.end == i.end) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        public int hashCode() {
            return (new Double(start)).hashCode();
        }

        public String toString() {
            return "[" + start + " - " + end + "]";
        }     
    }

    private final List<Interval> intervals;

    /**
     * @param start ; requires that end >= start
     * @param end ; requires that end >= start
     *
     * Creates a new IntervalList containing the range from
     * <code>start</code> to <code>end</code>.
     **/
    public IntervalList(double start, double end) {
        intervals = new LinkedList<Interval>();
        intervals.add(new Interval(start, end));
    }

    /**
     * Creates a new empty IntervalList (contains no numbers)
     **/
    public IntervalList() {
        intervals = new LinkedList<Interval>();
    }

    /**
     * Creates a new IntervalList which contains a copy of the
     * ranges represented by <code>il</code>.
     **/   
    public IntervalList(IntervalList il) {
        intervals = new LinkedList<Interval>(il.intervals);
    }

    /**
     * @return true iff <code>this</code> represents the empty
     * set of intervals.
     **/
    public boolean isEmpty() {
        return intervals.size() == 0;
    }

    /**
     * @return the lower bound of the lowest range of this, or
     * Double.NaN if this represents an empty set of intervals.
     **/
    public double min() {
        if (intervals.size() == 0) {
            return Double.NaN;
        }
        return ((Interval)intervals.get(0)).start();
    }

    /**
     * @return the upper bound of the highest range of this, or
     * Double.NaN if this represents an empty set of intervals.
     **/
    public double max() {
        if (intervals.size() == 0) {
            return Double.NaN;
        }
        return ((Interval)intervals.get(intervals.size()-1)).end();
    }

    /**
     * Canonicalizes <code>intervals</code> to contain sorted
     * non-overlapping ranges.
     **/
    private void canonicalize() {
        Collections.sort(intervals);
        Interval lastElement;
        Interval currentElement;

        ListIterator<Interval> iter = intervals.listIterator();
        if (!iter.hasNext()) {
            return;
        }
        currentElement = iter.next();
        while (iter.hasNext()) {
            lastElement = currentElement;
            currentElement = iter.next();
            if (lastElement.overlaps(currentElement)) {
                Interval newElement = lastElement.merge(currentElement);
                iter.remove();
                iter.previous();
                iter.remove();
                iter.add(newElement);
                currentElement = newElement;
            }
        }
    }

    /**
     * @param start starting of Interval to be added, requires that end >= start
     * 
     * @param end ending of Interval to be added, requires that end >= start
     * 
     * Adds the range from <code>start</code> to
     * <code>end</code> to this.
     **/  
    public void addInterval(double start, double end) {
        addIntervalInternal(start, end);
        canonicalize();
    }

    // adds the range from start to end to this, but does not canonicalize
    private void addIntervalInternal(double start, double end) {
        intervals.add(new Interval(start, end));
    }

    /**
     * @param start starting of Interval to be removed, requires that end >= start
     * 
     * @param end ending of Interval to be removed, requires that end >= start
     * 
     * Removes the range of numbers from <code>start</code> to
     * <code>end</code> from this.
     **/  
    public void removeInterval(double start, double end) {
        removeIntervalInternal(start, end);
        canonicalize();
    }

    // removes the range of numbers from start to end from this but does
    // not canonicalize
    private void removeIntervalInternal(double start, double end) {
        ListIterator<Interval> iter = intervals.listIterator();
        Interval toRemove = new Interval(start, end);
        while (iter.hasNext()) {
            Interval curr = iter.next();
            if (curr.overlaps(toRemove)) {
                if (start > curr.start()) {
                    if (end < curr.end()) {
                        iter.remove();
                        iter.add(new Interval(curr.start(), start));
                        iter.add(new Interval(end, curr.end()));
                    } else {
                        iter.remove();
                        iter.add(new Interval(curr.start(), start));
                    }
                } else {
                    if (end < curr.end()) {
                        iter.remove();
                        iter.add(new Interval(end, curr.end()));
                    } else {
                        iter.remove();
                    }
                }
            }
        }
    }

    /**
     * Adds to this all of numbers represented by
     * <code>il</code>.
     **/
    public void addIntervalList(IntervalList il) {
        Iterator<Interval> iter = il.intervals.iterator();
        while (iter.hasNext()) {
            Interval i = iter.next();
            addIntervalInternal(i.start(), i.end());
        }
        canonicalize();
    }

    /**
     * Removes from this all of the numbers represented by
     * <code>il</code>.
     **/
    public void removeIntervalList(IntervalList il) {
        Iterator<Interval> iter = il.intervals.iterator();
        while (iter.hasNext()) {
            Interval i = iter.next();
            removeIntervalInternal(i.start(), i.end());
        }
        canonicalize();
    }

    /**
     * @param start starting of Interval to be removed, requires that end >= start
     * 
     * @param end ending of Interval to be removed, requires that end >= start
     * 
     * Removes from this all of the numbers which are not
     * between <code>start</code> and <code>end</code>.
     **/
    public void restrictToInterval(double start, double end) {
        restrictToInterval(new Interval(start, end));
    }

    /**
     * Removes from this all of the numbers which are not
     * contained in the range represented by <code>i</code>.
     **/
    public void restrictToInterval(Interval i) {
        ListIterator<Interval> iter = intervals.listIterator();
        while (iter.hasNext()) {
            Interval curr = iter.next();
            if (curr.overlaps(i)) {
                iter.set(curr.restrictTo(i));
            } else {
                iter.remove();
            }
        }
    }

    /**
     * @return true if it shrinks the size of each continuous range of numbers
     * contained in this to only be of <code>length</code>.  Each
     * continuous range will be aligned to have the same start it
     * originally had, but the end point will be no farther than
     * <code>length</code> away.
     **/
    public boolean restrictSubIntervalLength(double length) {
        // returns true if it changed
        boolean changed = false;
        ListIterator<Interval> iter = intervals.listIterator();
        while (iter.hasNext()) {
            Interval curr = iter.next();
            Interval n = curr.restrictLength(length);
            if (!n.equals(curr)) {
                changed = true;
                iter.set(n);
            }
        }
        return changed;
    }

    /**
     * @return an Iterator which will return, in increasing
     * order,  Intervals representing the numbers contained in this.
     **/   
    public Iterator<Interval> iterator() {
        return (Collections.unmodifiableList(intervals)).iterator();
    }

    public boolean equals(Object o) {
        if (o instanceof IntervalList) {
            IntervalList il = (IntervalList) o;
            return intervals.equals(il.intervals);
        }
        return false;
    }

    public int hashCode() {
        return intervals.hashCode();
    }

    public String toString() {
        return intervals.toString();
    }
}
