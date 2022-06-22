package feta.network.measurements;

import feta.network.DirectedNetwork;
import feta.network.UndirectedNetwork;

import java.util.ArrayList;

public class Assortativity extends Measurement{

    private double assort_;
    private double InInAssort_;
    private double InOutAssort_;
    private double OutInAssort_;
    private double OutOutAssort_;

    public Assortativity() {
        nameDirected_="AssortII AssortIO AssortOI AssortOO";
        nameUndirected_="Assortativity";
    }

    @Override
    public void update(DirectedNetwork net) {
        int noNodes_= net.noNodes_;
        int noLinks_= net.noLinks_;

        double assSumInIn = 0.0;
        double assSumInOut = 0.0;
        double assSumOutIn = 0.0;
        double assSumOutOut = 0.0;

        double assProdInIn = 0.0;
        double assProdInOut = 0.0;
        double assProdOutIn = 0.0;
        double assProdOutOut = 0.0;

        double degSqIn = 0.0;
        double degSqOut = 0.0;

        for (int i = 0; i < noNodes_; i++) {
            int srcDegIn = net.getInDegree(i);
            int srcDegOut = net.getOutDegree(i);

            degSqIn+= (1.0/noLinks_)*srcDegIn * srcDegIn;
            degSqOut+= (1.0/noLinks_)*srcDegOut * srcDegOut;

            ArrayList<Integer> links = net.outLinks_.get(i);
            for (int l : links) {
                if (l < i)
                    continue;
                int dstDegIn = net.getInDegree(l);
                int dstDegOut = net.getOutDegree(l);

                assSumInIn += (1.0 / noNodes_) * (srcDegIn + dstDegIn);
                assSumInOut += (1.0 / noNodes_) * (srcDegIn + dstDegOut);
                assSumOutIn += (1.0 / noNodes_) * (srcDegOut + dstDegIn);
                assSumOutOut += (1.0 / noNodes_) * (srcDegOut + dstDegOut);

                assProdInIn += (1.0 / noLinks_) * srcDegIn * dstDegIn;
                assProdInOut += (1.0 / noLinks_) * srcDegIn * dstDegOut;
                assProdOutIn += (1.0 / noLinks_) * srcDegOut * dstDegIn;
                assProdOutOut += (1.0 / noLinks_) * srcDegOut * dstDegOut;
            }
        }

        double numInIn = assProdInIn - assSumInIn + (1.0/noNodes_)*(noLinks_/noNodes_);
        double numInOut = assProdInOut - assSumInOut + (1.0/noNodes_)*(noLinks_/noNodes_);
        double numOutIn = assProdOutIn - assSumOutIn + (1.0/noNodes_)*(noLinks_/noNodes_);
        double numOutOut = assProdOutOut - assSumOutOut + (1.0/noNodes_)*(noLinks_/noNodes_);

        double sigmaIn = Math.sqrt(degSqIn - noLinks_/noNodes_);
        double sigmaOut = Math.sqrt(degSqOut - noLinks_/noNodes_);

        double denInIn = sigmaIn*sigmaIn;
        double denInOut = sigmaIn*sigmaOut;
        double denOutOut = sigmaOut*sigmaOut;

        InInAssort_= numInIn/denInIn;
        InOutAssort_= numInOut/denInOut;
        OutInAssort_= numOutIn/denInOut;
        OutOutAssort_= numOutOut/denOutOut;
    }

    @Override
    public void update(UndirectedNetwork net) {
        double assSum = 0.0;
        double assProd = 0.0;
        double assSq = 0.0;

        for (int i = 0; i < net.noNodes_; i++) {
            ArrayList<Integer> links = net.neighbours_.get(i);
            for (int l : links) {
                if (l < i)
                    continue;
                int srcDeg = net.getDegree(i);
                int dstDeg = net.getDegree(l);
                assSum += 0.5 * (1.0 / net.noLinks_) * (srcDeg + dstDeg);
                assProd += srcDeg * dstDeg;
                assSq += 0.5 * (1.0 / net.noLinks_) * (srcDeg * srcDeg + dstDeg * dstDeg);
            }
        }
        double assNum = (1.0/net.noLinks_) * assProd - assSum * assSum;
        double assDom = assSq - assSum * assSum;
        assort_= assNum/assDom;
    }

    @Override
    public String toStringDirected() {
        return String.format("%1$f %2$f %3$f %4$f", InInAssort_,InOutAssort_,OutInAssort_,OutOutAssort_);
    }

    @Override
    public String toStringUndirected() {
        return String.format("%f",assort_);
    }
}
