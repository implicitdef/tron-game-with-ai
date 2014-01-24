$(function(){

    //--- Subfunctions

    var playerColors = {
        a : 'pink',
        b : 'blue'
    }
    var turns = ['ne', 'es', 'sw', 'wn'];

    function getCell(pos) {
        return $("table tr:nth-child(" + (pos.y + 1) +") td:nth-child(" + (pos.x + 1) +")");
    }
    function translateDirectionLettersIntoTurn(directionLetterFrom, directionLetterTo){
        var turn = directionLetterFrom + directionLetterTo;
        if($.inArray(turn, turns) !== -1){
            return turn;
        }
        return translateDirectionLettersIntoTurn(directionLetterTo, directionLetterFrom);
    }
    function translateDirectionLetterIntoAxis(directionLetter){
        return directionLetter == 'n' || directionLetter == 's'
               ? 'vertical'
               : 'horizontal';
    }
    function markPlayerPresence(pos, playerLetter){
        console.log(arguments);
        getCell(pos).css('background', playerColors[playerLetter]);
        console.log(getCell(pos));
        console.log(getCell(pos).css('background'));
    }
    function markPlayerPartial(pos, directionLetter, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'partial_' + directionLetter);
    }
    function markPlayerStraightLine(pos, directionLetter, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'straight_' + translateDirectionLetterIntoAxis(directionLetter));
    }
    function markPlayerTurn(pos, directionLetterFrom, directionLetterTo, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'turn_' + translateDirectionLettersIntoTurn(directionLetterFrom, directionLetterTo));
    }
    function markByCompletingPartial(pos, directionLetterTo, playerLetter){
        var directionLetterFrom = readOriginDirectionAtPos(pos);
        if(isTurn(directionLetterFrom, directionLetterTo)){
            markPlayerStraightLine(pos, directionLetterTo, playerLetter);
        } else {
            markPlayerTurn(pos, directionLetterFrom, directionLetterTo, playerLetter);
        }
    }
    function isTurn(directionLetterFrom, directionLetterTo){
        //TODO
    }
    function readOriginDirectionAtPos(pos){
        var url = getCell(pos).css('background-image');
        if(url.indexOf("partial") == -1){
            console.error("trying to read the origin direction at a cell which doesn't have the 'partial' image");
        }
        var regexp = /.*_([ensw])\.png.*/;
        return url.replace(regexp, "$1");
    }


    function setTrackBackgroundImage(pos, playerLetter, fileNameCore){
        setBackgroundImage(pos, 'track_' + playerColors[playerLetter] + '_' + fileNameCore + '.png');
    }
    function setBackgroundImage(pos, fileName){
        getCell(pos).css('background-image', 'url(\'img/' + fileName + '\')');
    }
    function applyMove(pos, move){
        return {
            x : move == "n" || move == "s"
                ? pos.x
                : (move == "w"
                    ? pos.x - 1
                    : pos.x + 1),
            y : move == "w" || move == "e"
                ? pos.y
                : (move == "n"
                    ? pos.y - 1
                    : pos.y + 1)
        }
    }
    function log(msg){
        $("#log").append("<p>" + msg + "</p>");
    }


    //-- Core function
    //Ask the server for the next move
    //Display it
    //Relaunch itself if the game is not over
    function loop(){
        $.ajax("/next").done(function(res){
            window.setTimeout(function(){
                if(res.movingPlayerId == 1){
                    aPos = applyMove(aPos, res.move);
                    if(res.died){
                        log("The bot A died.");
                    } else {
                        //TODO complete the partial from previous Pos
                        //TODO mark the new pos as a partial
                        markPlayerStraightLine(aPos, "n", "a");
                        loop();
                    }
                } else {
                    bPos = applyMove(bPos, res.move);
                    if(res.died){
                        log("The bot B died.");
                    } else {
                        markPlayerStraightLine(bPos, "e", "b");
                        loop();
                    }
                }
            }, 1);

        });
    }

    $(function(){
        //-- Launch
        //TODO make an image with just a dot for the very first step
        markPlayerPartial(aPos, "n", "a");
        markPlayerPartial(bPos, "e", "b");
        loop();
    });

});