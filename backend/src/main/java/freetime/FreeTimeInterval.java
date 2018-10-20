package freetime;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class that deals with free time intervals.
 */
public final class FreeTimeInterval {
    
    /**
     * Start and end time.
     */
    final int start, end;
    
    /**
     * Constructor for GSON.
     */
    private FreeTimeInterval() {
        this(0, 0);
    }
    
    /**
     * Construct by start and end time.
     *
     * @param start start time.
     * @param end end time.
     */
    FreeTimeInterval(int start, int end) {
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
    static List<FreeTimeInterval> mergeInterval(@NotNull List<FreeTimeInterval> intervals) {
        // then compare the first
        if (intervals.size() <= 1) {
            return intervals;
        }
        intervals.sort((Comparator.comparing((FreeTimeInterval o) -> o.start)
                .thenComparing(o -> o.end)));
        List<FreeTimeInterval> mergedInterval = new ArrayList<FreeTimeInterval>() {};
        FreeTimeInterval last = intervals.get(0);
        for (FreeTimeInterval current : intervals) {
            if (last.end >= current.start) {
                last = new FreeTimeInterval(last.start, Math.max(current.end, last.end));
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
    public static int totalOverlap(@NotNull List<FreeTimeInterval> intervals1,
                                   @NotNull List<FreeTimeInterval> intervals2) {
        List<FreeTimeInterval> combinedIntervals =
                new ArrayList<>(intervals1.size() + intervals2.size());
        combinedIntervals.addAll(intervals1);
        combinedIntervals.addAll(intervals2);
        int countDup = combinedIntervals.stream().mapToInt(i -> i.end - i.start).sum();
        List<FreeTimeInterval> mergedIntervals = mergeInterval(combinedIntervals);
        int countNoDup = mergedIntervals.stream().mapToInt(i -> i.end - i.start).sum();
        return countDup - countNoDup;
    }
    
}
