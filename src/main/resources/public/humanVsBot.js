$(function(){

    var isListening = true;

    function getCell(pos) {
        return $("table tr:nth-child(" + (pos.y + 1) +") td:nth-child(" + (pos.x + 1) +")");
    }
    function markAsPlayer(pos){
        getCell(pos).css('background', '#51DBDB');
    }
    function markAsServer(pos){
        getCell(pos).css('background', '#CCB289');
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
    function isKeyEventRelevant(e){
        return e.which >= 37 && e.which <= 40
    }
    function getDirForKeyEvent(e){
        switch(e.which){
            case 37 : return "w";
            case 38 : return "n";
            case 39 : return "e";
            case 40 : return "s";
            default : return null;
        }
    }


    markAsPlayer(playerPos);
    markAsServer(serverPos);

    $(document).keydown(function(e){
        if (isKeyEventRelevant(e) && isListening) {
            var dir = getDirForKeyEvent(e);
            isListening = false;
            $.ajax("/move", {
                data : {
                    d : dir
                }
            }).done(function(res){
                //the player may have done a bad move
                if(res.isPlayerDead){
                    alert('You died.');
                }
                else {
                    //display what the user did
                    playerPos = applyMove(playerPos, dir);
                    markAsPlayer(playerPos);
                    //the server may have done a bad move
                    if(res.isServerDead){
                        alert('The bot died. You win.');
                    } else {
                        serverPos = applyMove(serverPos, res.serverMove);
                        markAsServer(serverPos);
                    }
                }
                isListening = true;
            })
        }
    });


});