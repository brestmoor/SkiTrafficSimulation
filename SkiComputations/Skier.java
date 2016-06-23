package SkiComputations;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Filip on 14.12.2015.
 */
public class Skier {
    public double turnTH = 0.5;     //cosinus kąta po jakim narciarz wykona skręt
    public double radius = 7;       //promień skrętu
    public double mass = 70;
    public Circle circle;
    public Vector position = new Vector(0,0);
    public Vector velocity = new Vector(0,0.1);
    public Vector direction;                    //wersor kierunku poruszania się narciarza
    public Vector socialForce = new Vector(0,0);    //wypadkowa sił
    public Vector socialDirection = new Vector(0,0);    //wypadkowa sił socjlanych
    public Vector force = new Vector(0,0);
    public Waypoint waypoint = new Waypoint(0, 100);    //punkt do którego chce dotrzeć narciarz
    public boolean hasChanged = false;
    public double desire = 40;                      //jak bardzo narciarz chce dotrzeć do celu (siła socjalna)
    public double turnSign;
    public boolean finished=false;

    public SkierBrain  skierBrain = new SkierBrain();
    public SkierUpdateMechanism updateMechanism;

    Skier(double turnTH, double radius, double mass, Vector position, double desire, Vector direction, List<Skier> skiers){
        this.turnTH = turnTH;
        this.radius = radius;
        this.mass = mass;
        this.direction = direction;
        this.desire = desire;
        this.updateMechanism = new SkierUpdateMechanism(skiers);
        this.position = position;
        this.circle = new Circle(0,0,radius);
        this.waypoint = new Waypoint(position.x, position.y + 120);
    }


    class SkierBrain{
        boolean shouldTurn(){       //czy powinien nastąpić skręt
            socialDirection = Equations.vector2Versor(socialForce);
            double cos = Equations.calcCos(direction, socialDirection);
            double socialTurnSign = Equations.crossProduct(direction, socialDirection);

            if(cos<turnTH && socialTurnSign * turnSign <0)
                return true;
            else
                return false;
        }

        boolean checkWaypoint(){        //czy jest potrzeba zmiany punktu docelowego
            if (waypoint.y < position.y + 5){
                setWaypoint();
                return true;
            }
            else
                return false;
        }

        void setWaypoint(){         //ustawianie nowego punktu docelowego
            Vector[] vectors = Equations.calcCosArg(turnTH, direction);
            double xVersor = ThreadLocalRandom.current().nextDouble(Math.min(vectors[0].x, vectors[1].x), Math.max(vectors[0].x, vectors[1].x));
            double yVersor = ThreadLocalRandom.current().nextDouble(Math.min(vectors[0].y, vectors[1].y), Math.max(vectors[0].y, vectors[1].y));
            waypoint.x = position.x + xVersor * 50/Math.sqrt(xVersor*xVersor + yVersor*yVersor);
            waypoint.y = position.y + yVersor * 50/Math.sqrt(xVersor*xVersor + yVersor*yVersor);
        }

        void applyWaypointForce(){  //siła z jaką narciarz chce dotrzeć do celu
            double vectorLength = Equations.length(new Vector(waypoint.x - position.x, waypoint.y - position.y));
            socialForce.x += desire * (waypoint.x - position.x)/vectorLength;
            socialForce.y += desire * (waypoint.y - position.y)/vectorLength;
        }

        void performTurn(){
            if(hasChanged == true)
                hasChanged = false;
            else
                hasChanged = true;
        }


    }

    class SkierUpdateMechanism  implements Updater{     //klasa odpowiedzialna za oddziaływanie siłą socjalną na innych narciarzy
        private List<Skier> skiers;

        SkierUpdateMechanism(List<Skier> skiers){
            this.skiers = skiers;
        }

        private Vector getForce(Skier s){
            double u = Equations.U(Equations.length(new Vector(position.x - s.getPosition().x, s.getPosition().y - s.position.y)));
            Vector distance = new Vector (s.getPosition().x - position.x,s.getPosition().y - position.y);
            Vector towardsDirection = Equations.vector2Versor(distance);
            return new Vector(towardsDirection.x* 3 * (u + 50), towardsDirection.y * 3 * (u + 50));
        }

        @Override
        public void update() {
            for (Skier s: skiers){
                if(Equations.distance(s.getPosition(), position) <= Equations.visibility && s.getPosition().y < position.y){
                    s.applySocialForce(getForce(s));
                }
            }
        }
    }

    void calculatePosition(){
        position.x = position.x + velocity.x * Equations.timeInterval;
        position.y = position.y + velocity.y * Equations.timeInterval;
    }

    void calculateVelocity(){
        velocity.x = velocity.x + (force.x / mass)*Equations.timeInterval;
        velocity.y = velocity.y + (force.y / mass)*Equations.timeInterval;
    }

    void calculatePhysicalForce(){          //obliczanie sił fizycznych
        Vector forceP = new Vector(0,0);
        Vector forcelat = new Vector(0,0);
        Vector forceLat = new Vector(0,0);
        Vector forceC = new Vector(0,0);
        forceP.x = mass * Equations.gravity * Math.sin(Equations.alpha) * Equations.calcSinB(direction) * direction.x;
        forceP.y = mass * Equations.gravity * Math.sin(Equations.alpha) * Equations.calcSinB(direction) * direction.y;
        forcelat.x = forceP.x * -1;
        forcelat.y = mass * Equations.gravity * Math.sin(Equations.alpha) - forceP.y;
        if(hasChanged == false){
            forceC.x = (mass / radius) * Equations.length(velocity) * Equations.length(velocity) * forcelat.x / Equations.length(forcelat);
            forceC.y = (mass / radius) * Equations.length(velocity) * Equations.length(velocity) * forcelat.y / Equations.length(forcelat);
        }else{
            forceC.x = (mass / radius) * Equations.length(velocity) * Equations.length(velocity) * forcelat.x / Equations.length(forcelat) * -1;
            forceC.y = (mass / radius) * Equations.length(velocity) * Equations.length(velocity) * forcelat.y / Equations.length(forcelat) * -1;
        }

        forceLat.x = forcelat.x - forceC.x;
        forceLat.y = forcelat.y - forceC.y;

        double forceNMagn = mass * Equations.gravity * Math.cos(Math.toRadians(Equations.alpha));
        double forceEffMagn = Math.sqrt(Math.pow(Equations.length(forceC) + Equations.length(forceLat),2) + Math.pow(forceNMagn, 2));
        Vector forceGround = new Vector(Equations.frictionCoeff * forceEffMagn * direction.x, Equations.frictionCoeff * forceEffMagn * direction.y);
        Vector forceAir = new Vector(-1/2 * Equations.dragCoeff * Equations.airDens * Equations.frontalArea * Equations.length(velocity) * Equations.length(velocity) * direction.x,
                -1/2 * Equations.dragCoeff * Equations.airDens * Equations.frontalArea * Equations.length(velocity) * Equations.length(velocity) * direction.y);

        force.x = forceP.x + forceC.x + forceGround.x + forceAir.x;
        force.y = forceP.y + forceC.y + forceGround.y + forceAir.y;

    }

    void calculateDirection(){      //obliczanie kierunku
        Vector temp = new Vector(direction.x, direction.y);
        double tempX = direction.x;
        direction.x = velocity.x / Equations.length(velocity);
        direction.y = velocity.y / Equations.length(velocity);
        double elx = velocity.x / Equations.length(velocity);
        double ely = velocity.y / Equations.length(velocity);
        turnSign = Equations.crossProduct(temp, direction);

        if(direction.x * tempX < 0)
            if(hasChanged == true)
                hasChanged = false;
            else
                hasChanged = true;
    }

    public Vector getPosition(){
        return position;
    }

    public Vector getVelocity() {return velocity;}

    public void applySocialForce(Vector force){
        socialForce.x = socialForce.x + force.x;
        socialForce.y = socialForce.y + force.y;
    }

    public boolean hasFinished(){
        return finished;
    }

    public void setFinished(boolean finished){
        this.finished = finished;
    }
}
