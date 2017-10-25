package com.dalimao.mytaxi.cloud9car.common.lbs;

/**
 * Created by zouxiaobang on 10/25/17.
 */

public class RouteInfo {
    private float distance;
    private float taxiCost;
    private int duration;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getTaxiCost() {
        return taxiCost;
    }

    public void setTaxiCost(float taxiCost) {
        this.taxiCost = taxiCost;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "distance = " + distance + " ----- cose = " + taxiCost + " ----- duration = " + duration;
    }
}
