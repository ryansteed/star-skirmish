# Star Skirmish V1.0 Design Document

## Controls

Move the player:
|   | ↑ |   |
|---|---|---|
| ← |   | → |
|   | ↓ |   |

Start/Resume: SPACE

Pause: ESC

Reset Highscore: R

## Aesthetic

Star Wars meets Galaga in this arcade-style 2D space scroller, **Star Skirmish**.

Fly your X-Wing across a beautiful starscape, avoiding the Death Star's TIE fighters. Earn points for every enemy avoided and become strong in the Force.

Turn on your sound to hear the original NAMCO Galaga soundtrack and sound effects.

## Game

The player's ship is operated with real physics! Use the arrow keys to fire the engines and accelerate the vehicle. Watch out - there's no friction in space.

The game sends enemies in waves, which increase in size and speed as the player progresses. Passing enemies accrues points based on enemy size.

The player loses a life if the ship collides with an enemy, but has a brief immunity period after death to recover. The number of lives is displayed in the bottom left corner.

Game configurations can be edited in the `resources/StarSkirmish.properties` file.

## Software

The software class design can be viewed in `StarSkirmish.class.violet.html`.

In general, a view-controller architecture is implemented, split between the `GameEngine` and `GameFrame` classes and components. The `GameEngine` triggers changes in the game view, which is managed by the `GameFrame`. Score and other static displays are produced using `Overlay`s.

The `GameObject` component is the base class for all moving objects in the game. The `Player` and `Alien` objects dictate those components' behavior. Each object has a `Physics` engine to calculate new positions from step to step.

The code is fully documented with javadoc annotations, which can be generated with the Maven plugin.

See the `star-skirmish` directory for instructions on using Maven to build the application and `.jar` yourself.