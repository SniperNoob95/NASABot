package org.nasabot.nasabot.objects.marsweather;

public abstract class NumericReading {
    protected String units;
    protected double average;
    protected double count;
    protected double max;
    protected double min;

    public NumericReading(String units, double average, double count, double max, double min) {
        this.units = units;
        this.average = average;
        this.count = count;
        this.max = max;
        this.min = min;
    }

    public String getUnits() {
        return units;
    }

    public double getAverage() {
        return average;
    }

    public double getCount() {
        return count;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }
}
