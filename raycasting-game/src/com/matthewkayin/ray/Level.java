package com.matthewkayin.ray;

import java.lang.Math;

public class Level{

    private double position[];
    private double direction[];
    private int map[][] =  {{1, 1, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 1, 1},
                            {1, 0, 0, 0, 0, 1, 1},
                            {1, 0, 1, 1, 0, 0, 1},
                            {1, 0, 1, 1, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 1}};
    private final int RANGE = 5;
    private final double SPEED = 0.01;
    private final double ROT_SPEED = 2;

    public Level(){

        position = new double[]{1.5, 1.5};
        direction = new double[]{RANGE, 0};
    }

    public int getWidth(){

        return map[0].length;
    }

    public int getHeight(){

        return map.length;
    }

    public double getPlayerX(){

        return position[0];
    }

    public double getPlayerY(){

        return position[1];
    }

    public double getDirX(){

        return direction[0];
    }

    public double getDirY(){

        return direction[1];
    }

    public int getMap(int x, int y){

        return map[y][x];
    }

    void movePlayer(double delta, double angle){

        double step[] = rotate(direction, angle);
        step[0] = step[0] * delta * SPEED;
        step[1] = step[1] * delta * SPEED;
        position[0] += step[0];
        position[1] += step[1];

        //Don't perform movement if movement will result in collision with wall
        if(map[(int)position[1]][(int)position[0]] != 0){

            position[0] -= step[0];
            position[1] -= step[1];
        }
    }

    void rotatePlayer(double delta, double input){

        direction = rotate(direction, delta * input * ROT_SPEED * -1);
    }

    public double[] raycast(double angle){

        double dir[] = rotate(direction, angle);
        double xdist = dir[0];
        double ydist = dir[1];
        double distance = 0;
        int xdir = 0;
        int ydir = 0;
        int offset = 0;

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

                distance = getMagnitude(new double[]{point[0] - position[0], point[1] - position[1]});

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

                distance = getMagnitude(new double[]{point[0] - position[0], point[1] - position[1]});

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
                distance = getMagnitude(new double[]{x2 - position[0], y2 - position[1]});
                if(dist <= distance){

                    distance = dist;
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

            return new double[]{-1, 0.0};
        }

        if((int)(point[0]) == point[0]){

            offset = (int)(point[1] * 64) % 64;

        }else{

            offset = (int)(point[0] * 64) % 64;
        }

        return new double[]{distance, offset};
    }

    private double[] getRaycastPoint(double dir[]){

        double xdist = dir[0];
        double ydist = dir[1];
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

    private double[] rotate(double other[], double angle){

        double rads = Math.toRadians(angle);
        return new double[]{(other[0] * Math.cos(rads)) - (other[1] * Math.sin(rads)), (other[0] * Math.sin(rads)) + (other[1] * Math.cos(rads))};
    }
}