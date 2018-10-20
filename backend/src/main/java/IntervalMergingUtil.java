import java.util.*;


public final class IntervalMergingUtil {

    /**
     * A method that merges intervals to ensure they're not overlapping in the end.
     * @param intervals: Initial list of intervals
     * @return intervals that doesn't overlap
     */
    public List<Interval> mergeInterval (List<Interval> intervals) {
        // then compare the first
        if (intervals.size() <=1) return intervals;
        Collections.sort(intervals, (Comparator.comparing((Interval o) -> o.start).thenComparing(o -> o.end)));


        List<Interval> mergedInterval = new ArrayList<>() {};
        Interval last = intervals.get(0);
        for (int i = 0; i < intervals.size(); i++){
            Interval current = intervals.get(i);
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
