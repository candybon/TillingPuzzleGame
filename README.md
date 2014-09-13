TillingPuzzleGame
=================

Solve Tilling Puzzle using D. Knuth's [Dancing Links](http://en.wikipedia.org/wiki/Dancing_Links) Algorithm.

## The tiling puzzle
Given a set of 2D coloured rectilinear tiles, and a target board configuration, as bellow:
* **A Simple example :** <br>
![alt text](https://raw.githubusercontent.com/candybon/TillingPuzzleGame/master/readme/simple_pieces.png "Input pieces")        
![alt text](https://raw.githubusercontent.com/candybon/TillingPuzzleGame/master/readme/simple_target.png "Target Configuration")<br>
A possible solution:<br>
![alt text](https://raw.githubusercontent.com/candybon/TillingPuzzleGame/master/readme/simple_solution.png "A possible solution")<br>

* **A Larger example :** <br>
![alt text](https://raw.githubusercontent.com/candybon/TillingPuzzleGame/master/readme/complex_input.png "Input pieces")<br>

## Input format
A single text file (ASCII), different characters to represent the shapes and colors of the tiles; the target configuration is assumed to be the largest tile in that file. <br>

* Simple example above:
```txt
......b...bab....abba 
.....aa.....a....baba....b 
```

* Large example above:
```txt

         O     OXOXO         OX          
     X   XO        X  XO     XO          XOXOXOXO
     O    XO           X     O     X     OXOXOXOX
     X                       X     O     XOXOXOXO
           X   O     XO         OXOX     OXOXOXOX
    XOXO   O   X     OX                  XOXOXOXO
    O      X   OXO    O    X             OXOXOXOX
    X      O          X    O    OX       XOXOXOXO
           XO         O    X    XO       OXOXOXOX
                          XO     X       
                          O  
```

## Output
A html file will be generated that contains the following info:<br>
* Label each piece of rectilinear tiles with a single colour.
* Each tile(cell) in the piece was labelled with id to represent the relative position of the tile in the piece of rectilinear tiles.
* Various solutions on how to organize the pieces into the target configuration.
<br>
* **Solution to the larger example :**  
