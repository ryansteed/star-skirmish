# CSCI 2113.11 - Fall 2018 Project 2 (100 pts) - Due December 6 at 11:59PM


## Name: Ryan Steed

## GWID: G49358053

## Instructions

Clone this repository and develop the Alien Attack game on your own. Do not copy any additional source code from the Internet other than the code from the Project 2 repository. Use the Project 2 documentation on Blackboard as your requirements. 

Note: The Project 2 requirements may change during the development of the project based on student feedback.

## Comments and Notes

Problem with repainting on interval - what if multiple inputs withinn the interval?

- Why not repaint components separately, on command? e.g. player only repaints on input, other game objects repaint on interval
> Because then it will be impossible to accurately assess collisions
- Could pause main interval in favor of local interval

How to assess collisions? Keep game objects in sorted data structure, for quick querying?

`SwingUtilities.invokeLater`

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