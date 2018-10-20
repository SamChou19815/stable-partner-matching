import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The utility to merge intervals.
 */
public final class IntervalMergingUtil {
    
    /**
     * A method that merges intervals to ensure they're not overlapping in the end.
     *
     * @param intervals: Initial list of intervals
     * @return intervals that doesn't overlap
     */
    @NotNull
    public static List<Interval> mergeInterval(@NotNull List<Interval> intervals) {
        // then compare the first
        if (intervals.size() <= 1) {
            return intervals;
        }
        intervals.sort((Comparator.comparing((Interval o) -> o.start).thenComparing(o -> o.end)));
        List<Interval> mergedInterval = new ArrayList<Interval>() {};
        Interval last = intervals.get(0);
        for (Interval current : intervals) {
            if (last.end >= current.start) {
                last = new Interval(last.start, Math.max(current.end, last.end));
            } else {
                mergedInterval.add(last);
                last = current;
            }
        }
        mergedInterval.add(last);
        return mergedInterval;
    }
    
}
