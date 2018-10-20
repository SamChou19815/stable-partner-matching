import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Interval {
    
    public final int start, end;
    
    public Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
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
    
    /**
     * A method that calculates the total overlapping time between two list of time intervals.
     *
     * @param intervals1 the first initial list of time intervals.
     * @param intervals2 the second initial list of time intervals.
     * @return total overlapping.
     */
    public static int totalOverlap(@NotNull List<Interval> intervals1,
                                   @NotNull List<Interval> intervals2) {
        List<Interval> combinedIntervals =
                new ArrayList<>(intervals1.size() + intervals2.size());
        combinedIntervals.addAll(intervals1);
        combinedIntervals.addAll(intervals2);
        int countDup = combinedIntervals.stream().mapToInt(i -> i.end - i.start).sum();
        List<Interval> mergedIntervals = mergeInterval(combinedIntervals);
        int countNoDup = mergedIntervals.stream().mapToInt(i -> i.end - i.start).sum();
        return countDup - countNoDup;
    }
    
}
