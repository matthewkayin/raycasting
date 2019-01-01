package com.matthewkayin.ray;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
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

    private final int SCREEN_WIDTH = 320;
    private final int SCREEN_HEIGHT = 200;

    private boolean keydown[] = {false, false, false, false, false, false};
    private final int W = 0;
    private final int S = 1;
    private final int A = 2;
    private final int D = 3;
    private final int Q = 4;
    private final int E = 5;

    private double playerx = 1.5;
    private double playery = 1.5;
    private double directionx = 5;
    private double directiony = 0;
    private boolean showMap = false;
    private int map[][] =   {{1, 1, 1, 1, 1, 1},
                             {1, 0, 0, 0, 0, 1},
                             {1, 0, 0, 0, 0, 1},
                             {1, 0, 1, 1, 0, 1},
                             {1, 0, 1, 1, 0, 1},
                             {1, 1, 1, 1, 1, 1}};

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();
        setBackground(Color.black);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e){

                super.keyPressed(e);

                int keycode = e.getKeyCode();
                if(keycode == KeyEvent.VK_ESCAPE){

                    running = false;
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

        if(keydown[Q]){

            double newDir[] = rotate(directionx, directiony, 1 * delta);
            directionx = newDir[0];
            directiony = newDir[1];

        }else if(keydown[E]){

            double newDir[] = rotate(directionx, directiony, -1 * delta);
            directionx = newDir[0];
            directiony = newDir[1];
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

            double SPEED = 0.01;
            double newDir[] = rotate(directionx * SPEED * delta, directiony * SPEED * delta, moveangle);
            playerx += newDir[0];
            playery += newDir[1];

            if(map[(int)playery][(int)playerx] != 0){

                playerx -= newDir[0];
                playery -= newDir[1];
            }
        }
    }

    public void paint(Graphics g){

        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;

        g2d.setColor(Color.red);
        if(showMap){

            for(int i = 0; i < map.length; i++){

                for(int j = 0; j < map[i].length; j++){

                    if(map[i][j] == 1){

                        g2d.fillRect(j * 32, i * 32, 32, 32);

                    }else{

                        g2d.drawRect(j * 32, i * 32, 32, 32);
                    }
                }
            }

            g2d.setColor(Color.green);
            g2d.fillRect((int)(playerx * 32) - 2, (int)(playery * 32) - 2, 4, 4);
            g2d.setColor(Color.yellow);
        }

        for(int i = 0; i < SCREEN_WIDTH; i++){

            double angle = -30 + (0.1875 * i);
            double newDir[] = rotate(directionx, directiony, angle);
            double point[] = raycast(new double[]{playerx, playery}, newDir);
            if(showMap){

                if(i == 0 || i == SCREEN_WIDTH - 1){

                    g2d.drawLine((int)(playerx * 32), (int)(playery * 32), (int)((playerx + newDir[0]) * 32), (int)((playery + newDir[1]) * 32));
                }
                if(point[0] != -1){

                    g2d.fillRect((int)(point[0] * 32) - 2, (int)(point[1] * 32) - 2, 4, 4);
                }

            }else{

                if(point[0] != -1){

                    double distance = (getMagnitude(new double[]{point[0] - playerx, point[1] - playery})) * Math.cos(Math.toRadians(angle));
                    int height = (int)((64 * 3) / distance);
                    int start = (SCREEN_HEIGHT - height) / 2;
                    int x = (SCREEN_WIDTH - i - 1);
                    g2d.drawLine(x, start, x, start + height);
                }
            }
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    private double[] rotate(double x, double y, double angle){

        double rVal[] = {x, y};
        double rm[][] = getRotationMatrix(angle);
        rVal[0] = (rm[0][0] * x + rm[1][0] * y);
        rVal[1] = (rm[0][1] * x + rm[1][1] * y);

        return rVal;
    }

    private double[][] getRotationMatrix(double angle){

        double rads = Math.toRadians(angle);
        double a = (Math.cos(rads));
        double b = Math.sin(rads);
        b *= -1;
        double c = Math.sin(rads);

        double rVal[][] = {{a, b}, {c, a}};

        return rVal;
    }

    private double[] raycast(double position[], double direction[]){

        double xdist = direction[0];
        double ydist = direction[1];
        final double RANGE = getMagnitude(direction);
        int xdir = 0;
        int ydir = 0;

        if(xdist > 0){

            xdir = 1;

        }else if(xdist < 0){

            xdir = -1;
        }
        if(ydist > 0){

            ydir = 1;

        }else if(ydist < 0) {

            ydir = -1;
        }

        double point[] = {position[0], position[1]};
        boolean hit = false;

        while(!hit && getMagnitude(new double[]{point[0] - position[0], point[1] - position[1]}) <= RANGE){

            if(xdir == 0){

                if((int)(point[1]) != point[1]){

                    if(ydir > 0){

                        point[1] = (int)(point[1] + 1);

                    }else{

                        point[1] = (int)(point[1]);
                    }

                }else{

                    point[1] += ydir;
                }

            }else if(ydir == 0){

                if((int)(point[0]) != point[0]){

                    if(xdir > 0){

                        point[0] = (int)(point[0] + 1);

                    }else{

                        point[0] = (int)(point[0]);
                    }

                }else{

                    point[0] += xdir;
                }

            }else{

                //first try going x
                double x = point[0];
                double y = point[1];
                double x2 = point[0];
                double y2 = point[1];
                double dist = 0;
                if((int)(x) != x){

                    if(xdir > 0){

                        x = (int)(x + 1);

                    }else{

                        x = (int)(x);
                    }

                }else{

                    x += xdir;
                }
                y += (x - point[0]) / (xdist / ydist);
                dist = getMagnitude(new double[]{x - position[0], y - position[1]});
                if((int)(y2) != y2){

                    if(ydir > 0){

                        y2 = (int)(y2 + 1);

                    }else{

                        y2 = (int)(y2);
                    }

                }else{

                    y2 += ydir;
                }
                x2 += (y2 - point[1]) * (xdist / ydist);
                if(dist <= getMagnitude(new double[]{x2 - position[0], y2 - position[1]})){

                    point[0] = x;
                    point[1] = y;

                }else{

                    point[0] = x2;
                    point[1] = y2;
                }
            }

            hit = onWall(point[0], point[1], xdir, ydir);
        }

        if(!hit || getMagnitude(new double[]{point[0] - position[0], point[1] - position[1]}) > RANGE){

            point[0] = -1;
            point[1] = -1;
        }

        return point;
    }

    private boolean onWall(double x, double y, int xdir, int ydir){

        int ix = (int)x;
        int iy = (int)y;

        if(x == ix && y == iy){

            if(xdir == 0){

                if(ydir < 0){

                    return checkPoint(ix - 1, iy - 1) || checkPoint(ix, iy - 1);

                }else{

                    return checkPoint(ix, iy) || checkPoint(ix - 1, iy);
                }

            }else if(ydir == 0){

                if(xdir < 0){

                    return checkPoint(ix - 1, iy) || checkPoint(ix - 1, iy - 1);

                }else{

                    return checkPoint(ix, iy) || checkPoint(ix, iy - 1);
                }

            }else if(xdir < 0 && ydir < 0){

                return checkPoint(ix - 1, iy - 1);

            }else if(xdir < 0 && ydir > 0){

                return checkPoint(ix - 1, iy);

            }else if(xdir > 0 && ydir < 0){

                return checkPoint(ix, iy - 1);

            }else if(xdir > 0 && ydir > 0){

                return checkPoint(ix, iy);

            }else{

                return false;
            }

        }else if(iy != y && ix == x){

            if(xdir > 0){

                return checkPoint(ix, iy);

            }else{

                return checkPoint(ix - 1, iy);
            }

        }else if(iy == y && ix != x){

            if(ydir > 0){

                return checkPoint(ix, iy);

            }else{

                return checkPoint(ix, iy - 1);
            }

        }else{

            return false;
        }
    }

    private boolean checkPoint(int x, int y){

        return (x >= 0 && x <= map[0].length - 1 && y >= 0 && y <= map.length - 1 && map[y][x] != 0);
    }

    private double getMagnitude(double point[]){

        return Math.sqrt( (point[0] * point[0]) + (point[1] * point[1]) );
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
        window.dispose();
    }
}
