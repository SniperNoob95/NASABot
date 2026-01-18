package org.nasabot.nasabot.objects.marsweather;

public class WindDirection {
    private final double degrees;
    private final double right;
    private final double up;
    private final String point;
    private final int count;

    public WindDirection(double degrees, double right, double up, String point, int count) {
        this.degrees = degrees;
        this.right = right;
        this.up = up;
        this.point = point;
        this.count = count;
    }

    public double getDegrees() {
        return degrees;
    }

    public double getRight() {
        return right;
    }

    public double getUp() {
        return up;
    }

    public String getPoint() {
        return point;
    }

    public int getCount() {
        return count;
    }
}
