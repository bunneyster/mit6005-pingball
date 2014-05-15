package physics;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A counter is a mutable ADT used to instrument code to
 * measure the time of blocks of code.
 *
 * name              : String   // a unique name for a counter
 * totalTime         : long     // total time of all measurements taken
 * totalInvocations  : int      // total number of measurements taken
 * averageTime       : double   // average time of all measurements (totalTime / totalInvocations)
 * recursionDetected : boolean  // true iff begin was ever called twice without an end in between
 * 
 *
 * <p>Example Use:
 * <p>Existing code:<p>
 * <pre>
 * public class ComputationallyExpensive
 * {
 *   public double compute(double a, double b, double c)
 *   {
 *     double total = 0.0;
 *     double result;
 *     // ... 
 *     while (condition) {
 *       // ... 
 *       total += helperCompute(a + b, a - c);
 *       // ... 
 *     }
 *     // ... 
 *     return result;
 *   }
 *   private double helperCompute(double x, double y)
 *   {
 *     return Math.sqrt(x * x + y * y);
 *   }
 * }
 * public class Main
 * {
 *   public static void main(String[] args)
 *   {
 *     System.out.println("Computation is " + (new ComputationallyExpensive()).compute(1.0, 2.0, 1.0));
 *   }
 * }
 * </pre>
 * <p>Instrumented code:<p>
 * <pre> 
 * public class ComputationallyExpensive
 * {
 *   <font color="blue">private final static Counter computeCounter = new Counter("compute");</font>
 *   public double compute(double a, double b, double c)
 *   {
 *     <font color="blue">computeCounter.begin();</font>
 *     double total = 0.0;
 *     double result;
 *     // ... 
 *     while (condition) {
 *       // ... 
 *       total += helperCompute(a + b, a - c);
 *       // ... 
 *     }
 *     // ... 
 *     <font color="blue">computeCounter.end();</font>
 *     return result;
 *   }
 *   <font color="blue">private final static Counter helperComputeCounter = new Counter("helperCompute");</font>
 *   private double helperCompute(double x, double y)
 *   {
 *     <font color="blue">helperComputeCounter.begin();</font>
 *     <font color="blue">double result =</font> Math.sqrt(x * x + y * y);
 *     <font color="blue">helperComputeCounter.end();</font>
 *     return <font color="blue">result</font>;
 *   }
 * }
 * public class Main
 * {
 *   public static void main(String[] args)
 *   {
 *     System.out.println("Computation is " + (new ComputationallyExpensive()).compute(1.0, 2.0, 1.0));
 *     <font color="blue">System.out.println("Instrumentation results:\n" + Counter.getAllResults());</font>
 *   }
 * }
 * </pre>
 * <p>Output of Main.main():
 * <pre>
 *   Computation is 3.2837652137612
 *   Instrumentation results:
 *   compute had 1 invocations over 5240ms, averaging 5240ms each
 *   helperCompute had 100 invocations over 4240ms, averaging 42.4ms each      
 * </pre>
 * <p>Interpreting the results:
 * <p>The time spent in compute() was 5.24 seconds, and the time spent in
 * helperCompute was 4.24 seconds.  Note that since compute() is calling
 * helperCompute(), to find out the time spent <i>only</i> in compute(),
 * you have to subtract out the time spent in helperCompute().  Therefore,
 * if helperCompute() is the only other method which compute() calls, we
 * now know that the time spent on code found in compute() was 1.0s, and
 * the time spent on code found in helperCompute() was 4.24s.
 *
 * <p>NOTE: This implementation of a Counter is not useful for
 * instrumentation of multi-threaded code.
 */
final class Counter
{

    // ============================== STATIC BEHAVIOR ==============================

    // static members
    private final static Map<String, Counter> counters = 
            new HashMap<String, Counter>(); // String -> Counter

    //
    // RI(s) =
    //   (s.counters != null) &&
    //   (all k in s.counters.keySet : k != null && k instanceof String) &&
    //   (all v in s.counters.values : v != null && v instanceof Counter) &&
    //   (all <k, v> in s.counters.entrySet : k = v.blockName)
    //

    //
    // AF(s) =
    //   s.counters.values is the set of all Counters ever instantiated
    //

    /**
     * @return Collection of Counters containing all Counters ever created
     */
    public final static Collection<Counter> getCounters()
    {
        return Collections.unmodifiableCollection(counters.values());
    }

    /**
     * @return concatenation of c.getResult() for all Counters ever created
     */
    public final static String getAllResults()
    {
        StringBuffer results = new StringBuffer();

        Iterator<Counter> counters = getCounters().iterator();
        while (counters.hasNext()) {
            Counter counter = counters.next();
            results.append(counter.getResult());
            results.append("\n");
        }

        return results.toString();
    }

    // ============================== NON-STATIC BEHAVIOR ==============================

    private final String blockName;
    private int totalInvocations;
    private long totalTime;
    private boolean recursionDetected;
    private int beginDepth;       // = [# calls to begin()] - [# calls to end()]
    private long beginTime;       // currentTime() when beginDepth became non-zero

    //
    // RI(c) = 
    //   (blockName != null) &&
    //   (beginDepth >= 0) &&
    //   (totalInvocations >= 0) &&
    //   (totalTime >= 0)
    //

    //
    // AF(c) = 
    //   specfield name              = this.blockName
    //   specfield totalInvocations  = this.totalInvocations
    //   specfield totalTime         = this.totalTime
    //   specfield recursionDetected = this.recursionDetected

    // ============================== CONSTRUCTORS ==============================

    /**
     * Constructs a Counter with the given block name
     * 
     * @param blockName name to use for this counter
     * 
     * @throws IllegalArgumentException if <tt>blockName</tt> is null
     * @throws IllegalArgumentException if the constructor has already been called with the given <tt>blockName</tt>
     */
    public Counter(String blockName)
    {
        if (blockName == null) {
            throw new IllegalArgumentException("blockName cannot be null");
        }
        if (counters.containsKey(blockName)) {
            throw new IllegalArgumentException("The name " + blockName + " has already been used");
        }

        this.blockName = blockName;
        this.beginDepth = 0;
        this.recursionDetected = false;
        this.totalInvocations = 0;
        this.totalTime = 0;

        // add ourself to the global collection of Counters
        counters.put(blockName, this);
    }

    // ============================== MUTATORS ==============================

    /**
     * Starts a measurement using this counter
     */
    public final void begin()
    {
        // start the timer iff it has not already been started
        beginDepth++;
        if (beginDepth == 1)
            beginTime = currentTime();
        else
            recursionDetected = true;
    }

    /**
     * Stops a measurement using this counter
     * 
     * @throws IllegalStateException if begin() has not been called more times than end() on this counter
     */
    public final void end()
    {
        if (beginDepth <= 0)
            throw new IllegalStateException("end without begin");

        // stop the timer iff this is the last matching end()
        beginDepth--;
        if (beginDepth == 0) {
            long duration = currentTime() - beginTime;
            totalTime += duration;
            totalInvocations++;
        }
    }

    /**
     * Resets this counter to its original state
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public void reset()
    {
        assertMatched();
        uncheckedReset();
    }

    // same as reset() except does not check matched begin/end
    private void uncheckedReset()
    {
        recursionDetected = false;
        totalInvocations = 0;
        totalTime = 0;
    }

    // ============================== OBSERVERS ==============================

    /**
     * @return this.totalInvocations
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public final int getTotalInvocations()
    {
        assertMatched();
        return totalInvocations;
    }

    /**
     * @return this.totalTime
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public final long getTotalTime()
    {
        assertMatched();
        return totalTime;
    }

    /**
     * @return this.averageTime
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public final double getAverageTime()
    {
        assertMatched();
        return ((double) totalTime) / ((double) totalInvocations);
    }

    /**
     * @return this.recursionDetected
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public final boolean isRecursionDetected()
    {
        assertMatched();
        return recursionDetected;
    }

    /**
     * @return a String summarizing the results of this counter
     * 
     * @throws IllegalStateException if the number of calls to begin() and end() was not equal
     */
    public String getResult()
    {
        return blockName + " had " +
                getTotalInvocations() + " invocations over " +
                getTotalTime() + "ms, averaging " +
                prettyPrintDouble(getAverageTime(), 1) + "ms each" +
                (recursionDetected ? "; recursion was detected" : "");
    }

    // ============================== HELPERS ==============================

    private void assertMatched()
    {
        if (beginDepth != 0) {
            uncheckedReset();
            beginDepth = 0;
            beginTime = 0;
            throw new IllegalStateException("begin was called " + beginDepth + " more times than end");
        }
    }

    // returns the current time, in milliseconds, from a
    //   fixed point in the past
    private final long currentTime()
    {
        return System.currentTimeMillis();
    }

    // returns a string representation of d, with at most
    //   'decimal' places after the decimal point
    private final String prettyPrintDouble(double d, int decimals)
    {
        int factor = 1;
        while (decimals > 0) {
            factor = factor * 10;
            decimals--;
        }
        return String.valueOf(Math.round(d * factor) / ((double) factor));
    }

}
