package SkiComputations;

import java.text.DecimalFormat;

/**
 * Created by Filip on 14.12.2015.
 */
public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "[" + Equations.df.format(x) + ", " + Equations.df.format(y) + "]";
    }
}

class Circle{
    public double circleParamA;
    public double circleParamB;
    public double radius;

    Circle(double circleParamA, double circleParamB, double radius)
    {
        this.circleParamA = circleParamA;
        this.circleParamB = circleParamB;
        this.radius = radius;
    }
}