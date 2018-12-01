# Project Requirements

This project will develop an Alien Attack Game. The Alien Attack Game has different sized Aliens falling from the screen at different speeds with the smaller aliens falling faster than the larger Aliens. The player is trying to dodge the falling aliens.  Each time, the player get hits his size decreases and the alien is destroyed.  The player scores points for each alien that passes them without hitting them.  As the game goes longer, more aliens appear each cycle. Implement the game with the following required features, NOTE: These requirements may change some as the project progresses based on classroom discussions:

Game Screen – 900x900
- Need buttons to start, pause, and end the game
- Need a display of the Score and Elapsed time.
- Needs to show final score and elapsed time and the top ten scores of all time.
Different sized Aliens (30x30, 60x60, 90x90)
- Default to 10, 25, and 50 point values
- You may decide what your aliens look like, but will be drawing them using graphics, not images.
Player starts at 90x90 and goes down to 60x60 and then 30x30 after the first and second hit.
Read in game settings from a configuration file. They include:
- Cycle Time - start with 200 ms
- Speeds for Aliens and Players (i.e. how far to move in in cycle) - starting speeds large – 20, medium – 30, small – 40, player 40
- Increase interval - how many cycles until the max and min aliens increase
- Increase size – by how many aliens does maximum and minimum new aliens increase
- Starting Maximum and Minimum number of aliens
- Alien point values
- Optional Requirements

Here are the optional requirements. These requirements can be implemented for extra points, up to 10 points for one requirement, up to 20 points for two requirements, up to 30 points for 4 requirements. Only attempt these if you have completed the initial requirements successfully. You must explicitly state which of these requirements you completed in your Project Readme file and the design document. 
- Power-ups
- Shields
- Shoot back
- Slow Down
- Developer Choice
- Demo mode
- Ability for the Player to change the difficulty
- Use of an Image for the game background
- Ability to choose the appearance of the player.
 
## Planning and Design Phase
Using Violet UML editor, create a class diagram of all of the classes you expect to include in the Alien Attack Game.

## Submission
Submit your project on GitHub. Do not fork your repository.
- Class
- Your code
- A design document describing your code components and they work.

## Rubric
Compiles successfully - 10
Well commented and documented code - 15
Class diagram - 15
Basic game display - 5
Aliens Display and Movement - 20
Player Movement - 20
Game Mechanics (scoring, dying, speed-up) - 25
Satisfaction of Design Requirements - 25
Design document - 15
1st Optional Requirement Extra credit - 10
2nd Optional Requirement Extra credit - 10
3rd and 4th Optional Requirement Extra credit - 10

Total - 150
