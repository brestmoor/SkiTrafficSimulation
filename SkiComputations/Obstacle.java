package SkiComputations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filip on 15.12.2015.
 */
public class Obstacle implements Updater {
    List<Skier> skiers;
    Vector myPosition;

    Obstacle(List<Skier> skiers, Vector position){
        this.skiers = skiers;
        this.myPosition = position;
    }

    private Vector getForce(Vector velocity, Vector position){
        double u = Equations.U(Equations.length(new Vector(myPosition.x - position.x, myPosition.y - position.y)));
        Vector towardsVel = Equations.getVectorComponent(velocity, position, this.myPosition);
        return new Vector(towardsVel.x * -1 * u, towardsVel.y * -1 * u);
    }

    @Override
    public void update() {
        for (Skier s: skiers){
            if(Equations.distance(s.getPosition(), myPosition) <= Equations.visibility && s.getPosition().y < myPosition.y)
            {
                s.applySocialForce(getForce(s.getVelocity(), s.getPosition()));
            }
        }
    }

    public Vector getPosition(){
        return myPosition;
    }
}
