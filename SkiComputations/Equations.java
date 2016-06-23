package SkiComputations;
import java.lang.*;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by Filip on 14.12.2015.
 */
public class Equations {                        //klasa zawieraj¹ca sta³e i metody pomocne przy obliczeniach matematycznych
    public static double alpha = 0.45;
    public static double timeInterval = 0.02;
    public static double gravity = 9.89;
    public static Random random = new Random(27);
    public static int visibility = 80;
    public static DecimalFormat df = new DecimalFormat("#.##");
    public static double frictionCoeff = -0.10;
    public static double dragCoeff = 0.1;
    public static double airDens = 1;
    public static double frontalArea = 1;


    public static Vector vector2Versor(Vector vector){
        double length = Equations.length(vector);
        return new Vector(vector.x / length, vector.y / length);
    }

    public static Vector getVectorComponent(Vector vector, Vector fromPosition, Vector toPosition){
        Vector fromToVector = new Vector(toPosition.x - fromPosition.x, toPosition.y - fromPosition.y);
        double cos = Equations.calcCos(vector, fromToVector);
        double length = Equations.length(vector);
        Vector versor = Equations.vector2Versor(fromToVector);
        double newLength = cos * length;
        return new Vector(newLength * versor.x, newLength * versor.y);
    }

    public static double U(double x){
        return (500/(x + 15)) ;
    }

    public static double length(Vector vector){
        return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }

    public static double distance(Vector vector1, Vector vector2){
        return Equations.length(new Vector(vector2.x - vector1.x, vector2.y - vector1.y));
    }

    public static double crossProduct(Vector vector1, Vector vector2){
        return (vector1.x*vector2.y) - (vector1.y*vector2.x);
    }

    public static double calcSinB(Vector direction){
        double cosB = (1 * direction.x + 0 * direction.y)/((1) * length(direction));
        if (direction.y > 0)
            return Math.sqrt(1 - cosB * cosB);
        else
            return -Math.sqrt(1 - cosB * cosB);     //ostatnia zmiana
    }

    public static double calcCos(Vector first, Vector second) {
        double lengthFirst = Equations.length(first);
        double lengthSecond = Equations.length(second);
        if (lengthFirst == 0 || lengthSecond == 0)
            throw new RuntimeException("calcCos: Vector cannot be equal to 0.");
        else
            return (first.x * second.x + first.y * second.y) / (lengthFirst * lengthSecond);
    }

    public static Vector[] calcCosArg(double cos, Vector direction){
        double a1, a2, a3, a4, b1=direction.x, b2=direction.y, c=cos;
        if(b2 == 0 && b1!=0){
            a1 = c/b1;
            a2 = Math.sqrt((b1*b1-c*c)/b1*b1);
            a3 = c/b1;
            a4 = -Math.sqrt((b1*b1-c*c)/b1*b1);
        }else {
            a1 = (b1*c - Math.sqrt(b2*b2*(b1*b1 + b2*b2 - c*c)))/(b1*b1 + b2*b2);
            a2 = ( b1*Math.sqrt(b2*b2*(b1*b1 + b2*b2 - c*c))+b2*b2*c)/(b2*(b1*b1 + b2*b2)) ;
            a3 = (b1*c + Math.sqrt(b2*b2*(b1*b1 + b2*b2 - c*c)))/(b1*b1+ b2*b2);
            a4 = (-b1*Math.sqrt(b2*b2*(b1*b1 + b2*b2 - c*c))+b2*b2*c)/(b2*(b1*b1 + b2*b2));
        }

        return new Vector[]{new Vector(a1, a2), new Vector(a3, a4)};
    }

}
