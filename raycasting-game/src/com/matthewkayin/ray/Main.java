package com.matthewkayin.ray;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;

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
    private boolean showMap = false;

    private Level level;
    private Robot robot;
    private Texture texture;
    private BufferedImage offscreen;
    private int[] buffer;

    public Main(){

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setFocusable(true);
        requestFocus();
        setBackground(Color.pink);

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

                }else if(keycode == KeyEvent.VK_F1){

                    showMap = !showMap;
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
            System.exit(0);
        }

        texture = new Texture("/home/matt/Documents/raycasting/raycasting-game/res/texture.png");

        offscreen = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        buffer = ((DataBufferInt)offscreen.getRaster().getDataBuffer()).getData();

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

        fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, new int[]{255, 0, 0, 0});

        if(showMap){

            renderMap();

        }else{

            renderLevel();
        }

        g2d.drawImage(offscreen, 0, 0, null);
        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }

    private void renderLevel(){

        int width = SCREEN_WIDTH / 320;
        double anglestep = 60 / (SCREEN_WIDTH * (1 / (double)width));
        for(int i = 0; i < SCREEN_WIDTH; i++){

            double angle = -30 + (anglestep * i);
            double distance[] = level.raycast(angle);
            if(distance[0] != -1){

                distance[0] = distance[0] * Math.cos(Math.toRadians(angle));
                int height = (int)(SCREEN_HEIGHT / distance[0]);
                if(height > SCREEN_HEIGHT){

                    height = SCREEN_HEIGHT;
                }
                if(height > 0){

                    int y = (SCREEN_HEIGHT - height) / 2;
                    int x = (SCREEN_WIDTH - ((i * width) + 1));
                    //Image before = texture.getSubimage((int)distance[1], 0, width, 64).getScaledInstance(width, height, Image.SCALE_FAST);
                    //g2d.drawImage(before, x, y, null);
                    //g2d.drawImage(theTexture.getScaledSlice((int)distance[1], height), x, y, null);
                    //offScreenGraphics.fillRect(x, y, width, height);
                    //fillRect(x, y, width, height, new int[]{255, 255, 0, 0});
                    drawPixels(x, y, width, height, texture.getSlice( (int)distance[1], height ) );
                }
            }
        }
    }

    private void renderMap(){

        for(int i = 0; i < level.getWidth(); i++){

            for(int j = 0; j < level.getHeight(); j++){


                if(level.getMap(i, j) != 0){

                    fillRect(i * 32, j * 32, 32, 32, new int[]{255, 255, 0, 0});

                }else{

                    drawRect(i * 32, j * 32, 32, 32, new int[]{255, 255, 0, 0});
                }
            }
        }

        fillRect((int)(level.getPlayerX() * 32) - 2, (int)(level.getPlayerY() * 32) - 2, 4, 4, new int[]{255, 0, 255, 0});
        fillRect((int)(level.getDirX() * 32) - 2, (int)(level.getDirY() * 32) - 2, 4, 4, new int[]{255, 0, 0, 255});
    }

    private void drawPixels(int x, int y, int w, int h, int[] pixels){

        for(int i = 0; i < w; i++){

            for(int j = 0; j < h; j++){

                putpixel(x + i, y + j, pixels[i + (j * w)]);
            }
        }
    }

    private void fillRect(int x, int y, int w, int h, int[] color){

        for(int i = 0; i < w; i++){

            for(int j = 0; j < h; j++){

                putpixel(x + i, y + j, color);
            }
        }
    }

    private void drawRect(int x, int y, int w, int h, int[] color){

        for(int i = 0; i < w; i++){

            putpixel(x + i, y, color);
            putpixel(x + i, y + h - 1, color);
        }

        for(int i = 1; i < h - 1; i++){

            putpixel(x, y + i, color);
            putpixel(x + w - 1, y + i, color);
        }
    }

    private int[] getpixel(int x, int y){

        int index = (x + (y * offscreen.getWidth()));
        int argb[] = new int[4];
        argb[0] = (buffer[index] & 0xFF) << 24;
        argb[1] = (buffer[index] & 0xFF) << 16;
        argb[2] = (buffer[index] & 0xFF) << 8;
        argb[3] = (buffer[index] & 0xFF);

        return argb;
    }

    private void putpixel(int x, int y, int argb[]){

        if(x < 0 || x >= SCREEN_WIDTH || y < 0 || y >= SCREEN_HEIGHT){

            return;
        }

        int index = (x + (y * offscreen.getWidth()));
        buffer[index] = ((argb[0] << 24) + (argb[1] << 16) + (argb[2] << 8) + argb[3]);
    }

    private void putpixel(int x, int y, int pixel){

        if(x < 0 || x >= SCREEN_WIDTH || y < 0 || y >= SCREEN_HEIGHT){

            return;
        }

        int index = (x + (y * offscreen.getWidth()));
        buffer[index] = pixel;
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
