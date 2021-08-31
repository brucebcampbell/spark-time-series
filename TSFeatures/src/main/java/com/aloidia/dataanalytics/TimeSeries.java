package com.aloidia.datascience;

import java.io.Serializable;
import java.util.*;


public class TimeSeries implements Serializable {

    private Map<Integer,Double> dataContainer;

    public  TimeSeries()
    {
        this.dataContainer = new TreeMap<Integer,Double>();
    }

    public TimeSeries(TimeSeries src)
    {
        dataContainer = new TreeMap<Integer,Double>(src.dataContainer);
        setEndpointId(src.getEndpointId());
    }

    private int endpointId =0;

    private String meterKey= null;

    //TODO ask MB how to handle transformations of time series and
    public  TimeSeries standardize(double mean, double stdev)
    {
        TimeSeries result = new TimeSeries( );

        //TODO implement

        return result;
    }

    public Double[] acf(Integer lagmax ) {
        Double[] result = new Double[lagmax];

        //TODO implement

        return result;

    }

    public void addPoint(Integer sample_point, Double read_value)
    {
        dataContainer.put(sample_point,read_value);
    }

    public void merge(TimeSeries src)
    {
        //The TreeMap is self-sorting
        dataContainer.putAll(src.dataContainer);
    }

        public Double[] readValue()
    {
        List<Double> reads = new ArrayList<Double>();
        Set s = dataContainer.entrySet();
        Iterator it = s.iterator();
        while ( it.hasNext() ) {
            Map.Entry entry = (Map.Entry) it.next();
            Double value = (Double) entry.getValue();
            reads.add(value);
        }
        Double[] result = new Double[reads.size() ];

	    //TODO MB CR: why again do you need arrays vs. lists?
        reads.toArray(result);
        return  result;
    }

    public Integer[] samplePoint()
    {
        List<Integer> sample_points = new ArrayList<Integer>();
        Set s = dataContainer.entrySet();
        Iterator it = s.iterator();
        while ( it.hasNext() ) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            sample_points.add(key);
        }

	    //TODO MB CR: you could do just this:
	    //sample_points.addAll(dataContainer.keySet());

        Integer[] result = new Integer[sample_points.size() ];

        sample_points.toArray(result);
        return  result;
    }

    public Iterator< Map<Integer,Double> > getIterator()
    {
        Set s = dataContainer.entrySet();
        Iterator it = s.iterator();
        return it;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("endpointId=" + getEndpointId() + ";");

        Set s = dataContainer.entrySet();
        Iterator it = s.iterator();
        while ( it.hasNext() ) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            Double value = (Double) entry.getValue();
            sb.append(key + "," + value + ";");
        }//while

        return  sb.toString();
    }

    public int getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(int endpointId) {
        this.endpointId = endpointId;
    }

    public String getMeterKey() {
        return meterKey;
    }

    public void setMeterKey(String meterKey) {
        this.meterKey = meterKey;
    }

    //TODO MB CR: if you plan on doing anything with this class within a collection, you need to override hashCode() and equals()


}
