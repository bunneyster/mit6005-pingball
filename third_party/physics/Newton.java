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
 * @author: Jeffrey Sheldon
 *
 * @author: Robert C. Miller (rcm@mit.edu), Danny Yuan
 *          Spring 2014
 *
 ***************************************************************************/

/**
 * This class contains the logic required to perform netwon's method
 * of iterative root finding on single variable functions.
 */

class Newton {

    // nobody should be constructing a "Newton"
    private Newton() {
    }

    /**
     * Newton.Result is a record type which aggregates the results of
     * evaluating both a function at a point and its derivative.
     **/
    public final static class Result {
        public final double f;
        public final double f_prime;

        /** Creates a new Result record with the given values **/
        public Result(double f, double f_prime) {
            this.f = f;
            this.f_prime = f_prime;
        }

        /** @return true if either f or f' is NaN **/
        public boolean undefined() {
            return Double.isNaN(f) || Double.isNaN(f_prime);
        }

        /** @return true if this result's sign is not identical to other's **/
        public boolean funcSignChance(Result other) {
            return (f * other.f <= 0);
        }

        /** @return true if this result's derivative's sign is not identical to other's **/
        public boolean derivativeSignChance(Result other) {
            return (f_prime * other.f_prime <= 0);
        }

        /** @return funcSignChange() || derivativeSignChange() **/
        public boolean signChange(Result other) {
            return (f * other.f <= 0) || (f_prime * other.f_prime <= 0);
        }

        public String toString() {
            return "f(t)=" + f + ";f'(t)=" + f_prime + "";
        }
    }

    /** Convenience name for an undefined function result **/
    public static final Result UNDEFINED = new Result(Double.NaN, Double.NaN);

    /**
     * Newton.Function is an interface which specifies a function whose
     * roots can be found by this class.
     **/
    public static interface Function {
        /**
         * @return the value of the function evaluated at <code>t</code>
         **/
        public abstract Result evaluate(double t);
    }

    private static final int MAX_STEPS = 30;
    private static final double epsilon =  0.000000001;

    /**
     * @param t_min ; requires that t_min <= t_max
     * 
     * @param t_max ; requires that t_min <= t_max
     * 
     * @param t_step ; requires that t_step > 0
     *
     * @return the solution to possible roots of <code>function</code> by looking
     * for sign changes.  If a possible root is found, newton's method is
     * performed to try to determine the precise value of the root.
     * This function searches for roots between <code>t_min</code> and
     * <code>t_max</code> with a step size of <code>t_step</code>.  If
     * no solution is found returns <code>NaN</code>.
     *
     * <p>Note: The returned value may not actually be with in these bounds.  
     */
    public static double findRoot(Function function, 
            double t_min,
            double t_max,
            double t_step)
    {
        // initialize to NaN so that sign-change? is initially false
        Result eval = UNDEFINED;

        for (double t = t_min; t < t_max + t_step; t += t_step) {
            Result old = eval;
            eval = function.evaluate(t);
            // System.out.println("at " + t + " " + evan);

            if (eval.undefined()) {
                continue;
            }

            // did f or f' change sign?      
            if (eval.signChange(old)) {
                double root = findRoot(function, 
                        (old.f_prime <= 0) ? t - t_step : t);
                // check to make sure it was within the bounds of this check
                if ((t - t_step <= root) && (root <= t)) {
                    // System.out.println("returning " + root);
                    return root;
                }
            }
        }

        return Double.NaN;
    }

    /**
     * @return the solution to netwon's method on <code>function</code> 
     * starting at <code>initial_t</code>.  If no solution
     * is found, returns <code>NaN</code>
     **/
    public static double findRoot(Function function,
            double initial_t)
    {
        double t = initial_t;    

        for (int count = 0; count < MAX_STEPS; count++) {
            Result eval = function.evaluate(t);
            if (eval.undefined()) {
                return Double.NaN;
            }

            double t_next = t - eval.f/eval.f_prime;

            if (Math.abs(t_next - t) < epsilon) {
                if (Math.abs(eval.f) < 1000*epsilon) {
                    // claim it's close enough to call a hit (as opposed
                    // to a local minima)
                    return t_next;
                } else {
                    return Double.NaN;
                }
            }

            t = t_next;
        }

        return Double.NaN;
    }
}
