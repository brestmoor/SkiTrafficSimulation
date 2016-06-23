package SkiComputations;

import java.util.List;

/**
 * Created by Filip on 15.12.2015.
 */
public class Edges implements Updater{
    List<Skier> skiers;
    Vector myPosition;

    Edges(List<Skier> skiers){
        this.skiers = skiers;
    }

    private Vector leftRightPosition(Vector position){      //Pseudo pozycja krawêdzi obliczana na podstawie pozycji narciarza
        if(position.x < 250)
            return new Vector(0, position.y);
        else
            return new Vector(500, position.y);
    }

    private Vector getForce(Vector velocity, Vector position){  //Si³a z jak¹ zadzia³a przeszkoda na podstawie odleg³oœci i szybkoœci narciarza
        double u = Equations.U(Equations.length(new Vector(myPosition.x - position.x, myPosition.y - position.y)));
        Vector towardsVel = Equations.getVectorComponent(velocity, position, myPosition);
        Vector distance = new Vector (position.x - myPosition.x, position.y - myPosition.y);
        Vector towardsDirection = Equations.vector2Versor(distance);
        return new Vector(towardsDirection.x * (u*40), towardsDirection.y * (u*40));
    }

    @Override
    public void update() {      //przeszkoda dzia³a si³¹ na ka¿dego z narciarzy w zasiêgu wzroku
        for (Skier s: skiers){
            myPosition = leftRightPosition(s.getPosition());
            if(Equations.distance(s.getPosition(), myPosition) <= Equations.visibility)
                s.applySocialForce(getForce(s.getVelocity(), s.getPosition()));
        }
    }
}
