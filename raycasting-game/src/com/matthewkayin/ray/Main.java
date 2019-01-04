package com.matthewkayin.ray;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
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
    private BufferedImage texture;
    private BufferedImage offScreenImage;
    private int[] buffer;
    private int[] color = new int[]{0, 0, 0, 0};
    //private Graphics2D offScreenGraphics;

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
        try{

            File file = new File("/home/matt/Documents/raycasting/raycasting-game/res/texture.png");
            texture = ImageIO.read(file);

        }catch(IOException e){

            e.printStackTrace();
            System.exit(0);
        }

        offScreenImage = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        //offScreenGraphics = (Graphics2D)offScreenImage.getGraphics();
        final int[] a = ((DataBufferInt)offScreenImage.getRaster().getDataBuffer()).getData();
        buffer = new int[a.length];
        System.arraycopy(a, 0, buffer, 0, a.length);

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

        //offScreenGraphics.setColor(Color.black);
        //offScreenGraphics.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        //offScreenGraphics.setColor(Color.red);
        renderLevel();

        final int[] a = ((DataBufferInt)offScreenImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, a, 0, buffer.length);
        g2d.drawImage(offScreenImage, 0, 0, null);

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
                    fillRect(x, y, width, height);
                }
            }
        }
    }

    private void fillRect(int x, int y, int width, int height){

        if(x < 0){

            width += x;
            x = 0;

        }else if(x >= SCREEN_WIDTH){

            width -= (SCREEN_WIDTH - x - 1);
            x = SCREEN_WIDTH - 1;
        }

        if(y < 0){

            height += y;
            y = 0;

        }else if(y >= SCREEN_HEIGHT){

            height -= SCREEN_HEIGHT - y - 1;
            y = SCREEN_HEIGHT - 1;
        }

        if(width <= 0 || height <= 0){

            return;
        }

        for(int i = x; i < x + width; i++){

            for(int j = y; j < y + height; j++){

                int index = i + (j * SCREEN_WIDTH);
                if(index == -1){

                    System.out.println(x + ", " + y);
                }
                putPixel(index);
            }
        }
    }

    private void putPixel(int index){

        try{
            buffer[index] = (color[0] << 24) | (color[1] << 16) | (color[2] << 8) | color[3];

        }catch(ArrayIndexOutOfBoundsException e){

            System.out.println(index);
            e.printStackTrace();
            System.exit(0);
        }
    }

    private int[] getPixel(int index){

        int p = buffer[index];
        return new int[]{(p >> 24) & 0xFF, (p >> 16) & 0xFF, (p >> 8) & 0xFF, p & 0xFF};
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
