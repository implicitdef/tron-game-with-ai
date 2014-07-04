## It's a game

Just a simple game of "Tron", i.e. a snake-like game where two players move
on a grid, trying to survive as long as possible without hitting their own
tail or the opponent's.

![Screenshot](http://s23.postimg.org/e3mtxjevf/rsz_screenshot_from_2014_07_04_232051.jpg)

## With an AI

The opponent is played by a bot, which is a very simplistic version of what's described
here : http://www.a1k0n.net/2010/03/04/google-ai-postmortem.html


Basically, it's a minimax where the evaluation function to rate of "state" of the game
is : the difference between the numbers of cells each player can reach before the
other.


## Ingredients

- Scala 2.10.2
- Finatra (https://github.com/twitter/finatra)

## You can run it

    mvn scala:run

Should be accessible on http://localhost:7070 in your favorite browser.
Play with arrow keys.



