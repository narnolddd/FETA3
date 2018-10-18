package feta;

/** Half open integer-valued time interval of form [ t_start, t_end ) */

public class TimeInterval {
    public long start_;
    public long end_;
    public long length_;

    public TimeInterval(long tStart, long tEnd) {

        if (tStart < 0 || tStart >= tEnd) throw new IllegalArgumentException("Invalid time interval "+toString());
        start_=tStart;
        end_=tEnd;
        length_=end_-start_;
    }

    /** Does it overlap with time interval T2? */
    public boolean overlapsWith(TimeInterval T2) {
        if (T2.end_ <= start_) {
            return false;
        }
        if (T2.start_ >= end_) {
            return false;
        }
        return true;
    }

    public String toString() {
        return "[ "+start_+" "+end_+" )\n";
    }

    public boolean contains(int node) {
        if (node >= end_)
            return false;
        if (node < start_)
            return false;
        return true;
    }

}
