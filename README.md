# raycasting
## Introduction
Raycasting game in Java. This was meant to be a complete game to see if I could create a raycasting engine by doing the raycasting math myself. This is still something I'd like to try and do sometime, but this particular project never made it past the early stages of engine development.

To run the project, download the jar and the res folder into the same folder, then run "java -jar ray.jar". Use WSAD to move and the mouse to look around. Click inside the game window if the controls aren't working.

## What I Learned
This project gave me an insight into how 3D graphics overall works. A raycasting engine like this one works by using a 2D map and representing it by drawing slices of walls at different heights based on how far they are from the player. Farther away wall slices (or longer rays) are drawn smaller than those closer up, and this creates the illusion of depth. 

This is just one way to do 3D, of course, and the approach was done in an age where computers were much less capable. Other 3D approaches use actual 3D coordinates and transformation vectors which project the 3D information into a 2D space using perspective.
