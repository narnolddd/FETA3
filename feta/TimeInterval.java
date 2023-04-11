package feta;

import java.util.Objects;
import static java.lang.Math.toIntExact;

/** Half open integer-valued time interval of form [ t_start, t_end ) */

public class TimeInterval {
    public long start_;
    public long end_;
    public long length_;

    public TimeInterval(long tStart, long tEnd) {

        start_=tStart;
        end_=tEnd;
        length_=end_-start_;
    }

    /** Does it overlap with time interval T2? */
    public boolean overlapsWith(TimeInterval T2) {
        if (T2.end_ <= start_) {
            return false;
        }
        return T2.start_ < end_;
    }

    public String toString() {
        return "[ " + start_ + " " + end_ + " )\n";
    }

    public boolean contains(long time) {
        if (time > end_)
            return false;
        return time >= start_;
    }

    public boolean equals(Object T2) {
        if(this == T2)
            return true;

        if (T2 == null)
            return false;

        if (getClass() != T2.getClass())
            return false;

        TimeInterval TimeInt = (TimeInterval) T2;

        return Objects.equals(start_, TimeInt.start_) && Objects.equals(end_,TimeInt.end_);
    }

//    /** Get hashcode right for intervals */
//    public int hashCode() {
//        int t1 = toIntExact(start_);
//        int t2 = toIntExact(end_);
//        return t1+t2;
//    }

}
