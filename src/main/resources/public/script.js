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
    function markAsA(pos){
        markPlayerPresence(pos, 'a');
    }
    function markAsB(pos){
        markPlayerPresence(pos, 'b');
    }
    function markPlayerPresence(pos, playerLetter){
        getCell(pos).css('background', playerColors[playerLetter]);
    }
    function markPlayerStart(pos, directionLetter, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'start_' + directionLetter);
    }
    function markPlayerStraightLine(pos, directionLetter, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'straight_' + translateDirectionLetterIntoAxis(directionLetter));
    }
    function markPlayerTurn(pos, directionLetterFrom, directionLetterTo, playerLetter){
        setTrackBackgroundImage(pos, playerLetter,  'turn_' + translateDirectionLettersIntoTurn(directionLetterFrom, directionLetterTo));
    }
    function setTrackBackgroundImage(pos, playerLetter, fileNameCore){
        setBackgroundImage(pos, 'track_' + playerColors[playerLetter] + '_' + fileNameCore + '.png');
    }
    function setBackgroundImage(pos, fileName){
        getCell(pos).css('background', 'url(\'img/' + fileName + '\')');
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
                        markAsA(aPos);
                        loop();
                    }
                } else {
                    bPos = applyMove(bPos, res.move);
                    if(res.died){
                        log("The bot B died.");
                    } else {
                        markAsB(bPos);
                        loop();
                    }
                }
            }, 1);

        });
    }

    //-- Launch
    markAsA(aPos);
    markAsB(bPos);
    loop();


});