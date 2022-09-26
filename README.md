Very old repo.

A simple game of "Tron", i.e. a snake-like game where two players move
on a grid, trying to survive as long as possible without hitting their own
tail or the opponent's.

![Screenshot](./screenshot.png?raw=true)

The opponent is played by a bot, which is a simplistic version of what's described
here : http://www.a1k0n.net/2010/03/04/google-ai-postmortem.html. Basically, it's a minimax, where the evaluation function to rate the state of the game
is : the difference between the numbers of cells each player can reach before the
other.

The code is very old (Maven, old version scala, JS with jquery etc.) and I didn't aim for performance.

## Install tips (2022)

Apparently this needs an old java. Works with java 8, maybe others. Didn't work with java 11.

For exemple with sdkman :

    # display current version of java
    sdk current
    # list installable versions of java
    sdk list java
    # install this one for example
    # (when it asks if you want it as default, better to say no)
    sdk install java 8.0.345-zulu
    # use it for this shell
    sdk use java 8.0.345-zulu

Run it with :

    mvn scala:run

Should be accessible on http://localhost:7070.
Play with arrow keys.
