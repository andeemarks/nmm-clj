# morris

This is a Clojure implementation of the classic board game [Nine Men's Morris](https://en.wikipedia.org/wiki/Nine_Men%27s_Morris).

The game is played as a two player turn based game, with the command line used for player input and the board shown as an image on the screen.  The UI was a low priority when building this application and a current issue is the need to kill the process rendering the board before you can input your command.  Fixing this will be my next objective.

## Dependencies

This code leans heavily on [GraphViz](http://graphviz.org/) to format the board and also the Bash shell and the [ImageMagik display command](https://www.imagemagick.org/script/display.php) to render the board on screen.

All development has been done using Leiningen 2.7.1 on Ubuntu 16.06 and ImageMagick 6.8.9.

## How to run the app

`lein run` will run the app.  Make sure you have the dependencies listed above installed.

## How to run the tests

`lein midje` will run all tests.
