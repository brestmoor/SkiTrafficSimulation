package SkiComputations;

import java.util.concurrent.ThreadLocalRandom;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Filip on 14.12.2015.
 */
public class Slope {                                        //Klasa odpowiedzialna za symulacje
    static List<Skier> skiers = new ArrayList<Skier>();
    static List<Obstacle> obstacles = new ArrayList<Obstacle>();
    public static int xSize = 500;
    public static int ySize = 500;
    Edges edges;

    public Slope(int sNumber, int oNumber){                 //inicjalizacja na podstawie podanych parametrów

        for(int i=0; i<sNumber; i++)
            skiers.add(new Skier(ThreadLocalRandom.current().nextDouble(0.5, 0.8), ThreadLocalRandom.current().nextInt(15, 30), ThreadLocalRandom.current().nextInt(10, 20),
                    new Vector(ThreadLocalRandom.current().nextInt(100,400), 1),250, Equations.vector2Versor(new Vector(ThreadLocalRandom.current().nextDouble(-1,1), 1)), Slope.skiers));

        for(int i=0; i<oNumber; i++)
            obstacles.add(new Obstacle(skiers, new Vector(ThreadLocalRandom.current().nextInt(10, 490), ThreadLocalRandom.current().nextInt(20, 490))));

        edges = new Edges(skiers);
    }

    public void go() throws FileNotFoundException, InterruptedException {   //przebieg symulacji
        while(haveAllFinished() == false){
            for(Skier s: skiers){                                           //si³y socjalne narciarzy miedzy sob¹
                s.updateMechanism.update();
            }
            for(Obstacle o: obstacles){                                     //si³y socjalne obiekt-narciarz
                o.update();
            }
            edges.update();                                                 //si³y socjalne krawêdŸ-narciarz

            for(Skier s: skiers) {                                          //obliczenia i wykonywanie ruchu

                s.skierBrain.applyWaypointForce();
                s.calculatePhysicalForce();
                s.calculateVelocity();
                s.calculatePosition();
                s.calculateDirection();

                if(s.skierBrain.shouldTurn() == true){
                    s.skierBrain.performTurn();
                }

               s.skierBrain.checkWaypoint();

                s.socialForce.x = 0;
                s.socialForce.y = 0;

                if(s.getPosition().y > 500)
                    s.setFinished(true);
            }
            Thread.sleep(10);
        }
    }

    public List<Skier> getSkiers(){
        return skiers;
    }

    public List<Obstacle> getObstacles(){
        return obstacles;
    }

    private boolean haveAllFinished(){          //warunek zakoñczenia
        for(Skier s: skiers){
            if(s.hasFinished() == false)
                return false;
        }
        return true;
    }
}
