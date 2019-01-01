package com.matthewkayin.ray;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.lang.Math;

public class Main extends JPanel{

    private boolean running;
    private long beforeTime;
    private long beforeSec;
    private final long SECOND = 1000000000;
    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = SECOND / TARGET_FPS;
    private int frames;
    private int fps;

    private final int SCREEN_WIDTH = 1280;
    private final int SCREEN_HEIGHT = 720;

    private boolean keydown[] = {false, false, false, false, false, false};
    private final int W = 0;
    private final int S = 1;
    private final int A = 2;
    private final int D = 3;
    private final int Q = 4;
    private final int E = 5;
    private int mousex = 0;
    private int mousey = 0;
    private int oldmousex = 0;
    private int oldmousey = 0;
    private boolean lockMouse = false;

    private Level level;
    private Robot robot;

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();
        setBackground(Color.black);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e){

                if(!lockMouse){

                    lockMouse = true;
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter(){

            @Override
            public void mouseMoved(MouseEvent e){

                mousex = e.getX();
                mousey = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e){

                mousex = e.getX();
                mousey = e.getY();
            }
        });

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e){

                super.keyPressed(e);

                int keycode = e.getKeyCode();
                if(keycode == KeyEvent.VK_ESCAPE){

                    running = false;

                }else if(keycode == KeyEvent.VK_F2){

                    lockMouse = !lockMouse;
                }

                switch(keycode){
                    case KeyEvent.VK_W:
                        keydown[W] = true;
                        break;
                    case KeyEvent.VK_S:
                        keydown[S] = true;
                        break;
                    case KeyEvent.VK_A:
                        keydown[A] = true;
                        break;
                    case KeyEvent.VK_D:
                        keydown[D] = true;
                        break;
                    case KeyEvent.VK_Q:
                        keydown[Q] = true;
                        break;
                    case KeyEvent.VK_E:
                        keydown[E] = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e){

                super.keyReleased(e);

                int keycode = e.getKeyCode();
                switch(keycode){
                    case KeyEvent.VK_W:
                        keydown[W] = false;
                        break;
                    case KeyEvent.VK_S:
                        keydown[S] = false;
                        break;
                    case KeyEvent.VK_A:
                        keydown[A] = false;
                        break;
                    case KeyEvent.VK_D:
                        keydown[D] = false;
                        break;
                    case KeyEvent.VK_Q:
                        keydown[Q] = false;
                        break;
                    case KeyEvent.VK_E:
                        keydown[E] = false;
                        break;
                }
            }
        });

        level = new Level();

        try{

            robot = new Robot();

        }catch(Exception e){

            e.printStackTrace();
        }

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

            }catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    public void update(double delta){

        if(delta == 0){

            return;
        }

        if(lockMouse){

            int xdiff = mousex - (SCREEN_WIDTH / 2);
            int ydiff = mousey - (SCREEN_HEIGHT / 2);

            robot.mouseMove(this.getLocationOnScreen().x + (SCREEN_WIDTH / 2), this.getLocationOnScreen().y + (SCREEN_HEIGHT / 2));
            level.rotatePlayer(delta, (double)xdiff / (SCREEN_WIDTH / 17));
        }

        boolean shouldmove = true;
        double moveangle = 0;

        if(keydown[W]){

            if(keydown[A]){

                moveangle = 45;

            }else if(keydown[D]){

                moveangle = -45;
            }

        }else if(keydown[S]){

            if(keydown[A]){

                moveangle = 135;

            }else if(keydown[D]){

                moveangle = -135;

            }else{

                moveangle = 180;
            }

        }else if(keydown[A]){

            moveangle = 90;

        }else if(keydown[D]){

            moveangle = -90;

        }else{

            shouldmove = false;
        }

        if(shouldmove){

            level.movePlayer(delta, moveangle);
        }
    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.red);
        renderLevel(g2d);

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    private void renderLevel(Graphics2D g2d){

        int width = SCREEN_WIDTH / 320;
        double anglestep = 60 / (SCREEN_WIDTH * (1 / (double)width));
        for(int i = 0; i < SCREEN_WIDTH; i++){

            double angle = -30 + (anglestep * i);
            double distance = level.raycast(angle);
            if(distance != -1){

                distance = distance * Math.cos(Math.toRadians(angle));
                int height = (int)(SCREEN_HEIGHT / distance);
                int y = (SCREEN_HEIGHT - height) / 2;
                int x = (SCREEN_WIDTH - ((i * width) + 1));
                g2d.fillRect(x, y, width, height);
            }
        }
    }

    public static void main(String[] args){

        JFrame window = new JFrame("Raycaster");
        window.setSize(1280, 780);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        window.getContentPane().setCursor(blankCursor);

        Main game = new Main();
        window.add(game);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.run();
        window.dispose();
    }
}
