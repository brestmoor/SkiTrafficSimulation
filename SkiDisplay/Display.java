package SkiDisplay;

import SkiComputations.Obstacle;
import SkiComputations.Skier;
import SkiComputations.Slope;
import SkiComputations.Vector;


import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * Created by Filip on 27.01.2016.
 */
public class Display {
    public static void main(String[] args) {
        //tutaj kod zczytujacy parametry do slope
        

        Map<Integer, Color> colorPalette = new HashMap<>();
        colorPalette.put(0, Color.BLACK);
        colorPalette.put(1, Color.BLUE);
        colorPalette.put(2, Color.PINK);
        colorPalette.put(3, Color.ORANGE);
        colorPalette.put(4, Color.GREEN);



        Slope slope;
        if(args.length == 2) {
            slope = new Slope(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }else{
            slope = new Slope(3, 20);
        }

        List<Skier> skiers = slope.getSkiers();
        List<ColoredSkier> coloredSkiers = new ArrayList<ColoredSkier>();
        for(int i=0; i<skiers.size(); i++){
            coloredSkiers.add(new ColoredSkier(skiers.get(i), colorPalette.get(i%5)));
        }
        System.out.println(coloredSkiers.size());
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                displaySkiTraffic(coloredSkiers, slope.getObstacles());
            }
        });

        Runnable slopeThread = new Runnable() {
            @Override
            public void run() {
                try {
                    slope.go();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        slopeThread.run();
    }

    public static void displaySkiTraffic(List<ColoredSkier> coloredSkiers, List<Obstacle> obstacles){
        JFrame frame = new JFrame("Ski traffic");
        frame.add(new MyPanel(coloredSkiers, obstacles));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

class MyPanel extends JPanel{
    List<ColoredSkier> coloredSkiers;
    List<Obstacle> obstacles;
    Graphics2D gr;
    Timer timer;
    Map<Integer, Color> greyPalette = new HashMap<>();



    ActionListener paintListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            paintPos();
        }
    };

    public MyPanel(List<ColoredSkier> coloredSkiers, List<Obstacle> obstacles){
        setBorder(BorderFactory.createLineBorder(Color.black));
        this.coloredSkiers = coloredSkiers;
        this.obstacles = obstacles;
        greyPalette.put(0, Color.decode("#808080")); greyPalette.put(5, Color.decode("#bfbfbf"));
        greyPalette.put(1, Color.decode("#8c8c8c")); greyPalette.put(6, Color.decode("#cccccc"));
        greyPalette.put(2, Color.decode("#999999")); greyPalette.put(7, Color.decode("#dfdfdf"));
        greyPalette.put(3, Color.decode("#a6a6a6")); greyPalette.put(8, Color.decode("#eaeaea"));
        greyPalette.put(4, Color.decode("#b2b2b2")); greyPalette.put(9, Color.decode("#ececec"));
        greyPalette.put(10, Color.decode("#ededed"));

    }

    public Dimension getPreferredSize() {
        return new Dimension(500,500);
    }

    public void paintPos(){
        for(ColoredSkier cs: coloredSkiers) {
            gr.setPaint(cs.getColor());
            Vector pos = cs.getPos();
            gr.drawRect((int) pos.x, (int) pos.y, 3, 3);
            for(int i=0; i<cs.trace.size(); i++){
                Vector v = cs.trace.get(i);
                gr.setPaint(greyPalette.get(10 - i / 10));
                gr.drawLine((int) v.x, (int) v.y,(int) v.x, (int) v.y);
            }

            gr.setPaint(Color.BLACK);

            if(cs.trace.size()>0)

            if((int)cs.trace.peekLast().x != (int)pos.x || (int)cs.trace.peekLast().y != (int)pos.y){

                if(cs.trace.size() == 100){
                        cs.trace.poll();
                        cs.trace.offer(new Vector(cs.getPos().x, cs.getPos().y));
                    }
                    else{
                        cs.trace.offer(new Vector(cs.getPos().x, cs.getPos().y));
                    }
                }
        }


        repaint();
        timer.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        gr = (Graphics2D)g;
        timer = new Timer(50, paintListener);
        for(Obstacle ob: obstacles){
            Vector pos = ob.getPosition();
            gr.drawOval((int) pos.x, (int) pos.y, 4, 4);
        }
        paintPos();
    }
}

class ColoredSkier{
    Skier skier;
    Color color;
    LinkedList<Vector> trace;

    ColoredSkier(Skier skier, Color color){
        this.skier = skier;
        this.color = color;
        this.trace = new LinkedList<Vector>(Arrays.asList(new Vector(skier.getPosition().x, skier.getPosition().y)));
    }

    Vector getPos(){
        return skier.getPosition();
    }

    Color getColor(){
        return color;
    }
}