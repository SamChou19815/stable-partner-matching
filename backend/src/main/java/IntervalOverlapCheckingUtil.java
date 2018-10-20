import java.util.List;


public final class IntervalOverlapCheckingUtil {

    /**
     * A method that calculates the total overlapping time between two list of time intervals.
     * @param intervals1: Person1's initial list of time intervals
     * @param intervals2: Person2's initial list of time intervals
     * @return total overlapping time
     */

    public int checkOverlap(List<Interval> intervals1, List<Interval> intervals2) {
        intervals1.addAll(intervals2);
        int count1 = 0;
        for (int i = 0; i < intervals1.size(); i++){
            count1 += (intervals1.get(i).end - intervals1.get(i).start);
        }
        IntervalMergingUtil t = new IntervalMergingUtil();
        List<Interval> mergedInterval = t.mergeInterval(intervals1);
        int count2 = 0;
        for (int i = 0; i < mergedInterval.size(); i++){
            count2 += (mergedInterval.get(i).end - mergedInterval.get(i).start);
        }
        return count1 - count2;
    }
    


}
