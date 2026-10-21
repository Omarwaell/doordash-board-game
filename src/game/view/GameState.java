package game.view;

import game.engine.monsters.Monster;


public class GameState {

    
    public int    turnNumber       = 1;
    public String currentName      = "";
    public boolean isPlayerTurn    = true;

    
    public int    diceRoll         = 0;
    public boolean diceRolled      = false;

    
    public int    playerPosBefore  = 0;
    public int    playerPosAfter   = 0;
    public int    opponentPosBefore= 0;
    public int    opponentPosAfter = 0;

    
    public String cellEffectMsg    = "";   
    public boolean positionChanged = false;

    
    public int    playerEnergyBefore   = 0;
    public int    playerEnergyAfter    = 0;
    public int    opponentEnergyBefore = 0;
    public int    opponentEnergyAfter  = 0;
    public boolean playerShieldBlocked = false;   
    public boolean opponentShieldBlocked = false; 

    
    public boolean cardDrawn       = false;
    public String  cardName        = "";
    public String  cardEffect      = "";

    
    public boolean playerFrozenSkipped   = false;
    public boolean opponentFrozenSkipped = false;
    public boolean powerupUsed           = false;

    
    public String  actionLog       = "";

    
    public boolean hadError        = false;
    public String  errorMessage    = "";
}