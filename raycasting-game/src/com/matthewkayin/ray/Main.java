package com.matthewkayin.ray;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;

public class Main extends JPanel{

    private boolean running;
    private long beforeTime;
    private long beforeSec;
    private final long SECOND = 1000000000;
    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = SECOND / TARGET_FPS;
    private int frames;
    private int fps;

    private final int SCREEN_WIDTH = 320;
    private final int SCREEN_HEIGHT = 200;

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();
        setBackground(Color.black);

        running = false;
    }

    public void run(){

        running = true;
        beforeTime = System.nanoTime();
        beforeSec = 0;
        frames = 0;

        while(running){

            long currentTime = System.nanoTime();
            long elapsed = currentTime - beforeTime;
            beforeTime = currentTime;
            double delta = elapsed / ((double)OPTIMAL_TIME);

            beforeSec += elapsed;
            frames++;

            if(beforeSec >= SECOND){

                fps = frames;
                frames = 0;
                beforeSec -= SECOND;
                System.out.println("FPS = " + fps);
            }

            update(delta);
            repaint();

            try{

                Thread.sleep((beforeTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);

            }catch (Exception e){

                e.printStackTrace();
            }
        }
    }

    public void update(double delta){


    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    public static void main(String[] args){

        JFrame window = new JFrame("Raycaster");
        window.setSize(320, 200);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Main game = new Main();
        window.add(game);
        window.pack();

        window.setVisible(true);

        game.run();
    }
}
