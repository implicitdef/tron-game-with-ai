$(function(){

    function getCell(pos) {
        return $("table tr:nth-child(" + (pos.y + 1) +") td:nth-child(" + (pos.x + 1) +")");
    }
    function markAsA(pos){
        getCell(pos).css('background', 'pink');
    }
    function markAsB(pos){
        getCell(pos).css('background', 'lightblue');
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

    markAsA(aPos);
    markAsB(bPos);

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
    loop();

});