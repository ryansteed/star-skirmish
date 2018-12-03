# CSCI 2113.11 - Fall 2018 Project 2 (100 pts) - Due December 6 at 11:59PM


## Name: Ryan Steed

## GWID: G49358053

## Instructions

Clone this repository and develop the Alien Attack game on your own. Do not copy any additional source code from the Internet other than the code from the Project 2 repository. Use the Project 2 documentation on Blackboard as your requirements. 

Note: The Project 2 requirements may change during the development of the project based on student feedback.

## Comments and Notes

### TODO
A good design reference: http://zetcode.com/tutorials/javagamestutorial/collision/

Build:
<!-- - Implement game controller skeleton, holding a list of gameobjects -->
<!-- - Implement black background (board) based on exercise 8, leaving room for a score -->
<!-- - Build a basic player by modifying game piece from exercise 8 - start with a blue square -->
<!-- - Build basic aliens based on GameObject abstract - start with a green square -->
<!-- - Package aliens into waves -->
<!-- - Create automatic alien movement with PhysicsEngine, instantiate with waves (of a certain speed) -->
<!-- - Create user-controlled motion for player with PhysicsEngine -->
<!-- - Add boundaries to user motion -->
<!-- - Display a score - +1 for every alien passed -->
<!-- - Add collisions & count lives beside score 
    - use rectangle contact boxes - then .intersect is handy for comparing positions -->
<!-- - Create more waves of aliens moving at increasing speeds -->
- Game controls
    <!-- - Construct meta overlay
        - Lives
        - Current Score
        - High score -->
    <!-- - Player size depends on number of lives -->
    - Key bindings to start, pause, reset
- Design player appearance
- Design alien appearance
- Add stars
- Pause game to do death animation on contact
- Try separating the animation timer (along with requisite wave, etc.) and the start/stop controller - turn the update class into an entirely separate class and create a new one on game start

Finalize:
- Javadoc annotations
- Refactor for readable modularity - multiple files!
- Refactor to standard java directory (maven standard)
- Turn this into a real Maven package and .jar in resources folder using lecture slide tutorial

Bonus:
- Build a player missile
- Bonus features

### Class Notes 19Nov18
Problem with repainting on interval - what if multiple inputs withinn the interval?

- Why not repaint components separately, on command? e.g. player only repaints on input, other game objects repaint on interval
> Because then it will be impossible to accurately assess collisions
- Solution: pause main interval in favor of local interval

How to assess collisions? Keep game objects in sorted data structure, for quick querying?

`SwingUtilities.invokeLater`

Design document: how to play the game, design decisions made

A useful reference: http://zetcode.com/tutorials/javagamestutorial/spaceinvaders/

## Grading Rubric

Project details | Points | Score
---|:---:|:---:
Compiles successfully.|10|[GA entry]
Well commented and documented code.|15|[GA entry]
Class diagram. |15|[GA entry]
Basic game display.|5|[GA entry]
Aliens Display and Movement|20|[GA entry]
Player movement movement|20|[GA entry]
Game Mechanics (scoring, dying, speed-up)|25|[GA entry]
Satisfaction of Design Requirements|25|[GA entry]
Design document.|15|[GA entry]
1st Optional Requirement Extra credit.|10|[GA entry]
2nd Optional Requirement Extra credit.|10|[GA entry]
3rd and 4th Optional Requirement Extra credit.|10|[GA entry]
**Total**|150|[GA entry]