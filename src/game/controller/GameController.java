package game.controller;

import game.engine.*;
import game.engine.exceptions.*;
import game.engine.monsters.Monster;
import game.view.*;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class GameController {

    private final Game  game;
    private       GameView view;
    private final Stage stage;
    private       int   turnNumber = 1;

    public GameController(Role playerRole, Stage stage) throws Exception {
        this.game  = new Game(playerRole);
        this.stage = stage;
    }

    public void setView(GameView v) { this.view = v; }
    public Game  getGame()          { return game;   }
    public Stage getStage()         { return stage;  }

   
    public void handleUsePowerup() {
        GameState gs = new GameState();
        gs.turnNumber   = turnNumber;
        gs.isPlayerTurn = (game.getCurrent() == game.getPlayer());

        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        gs.playerEnergyBefore   = player.getEnergy();
        gs.opponentEnergyBefore = opponent.getEnergy();

        try {
            game.usePowerup();
            gs.powerupUsed = true;
            gs.actionLog   = current.getName() + " activated their POWERUP! (-500 energy)";
        } catch (OutOfEnergyException e) {
            gs.hadError     = true;
            gs.errorMessage = "Not enough energy to use powerup!\n\n"
                    + current.getName() + " has " + current.getEnergy()
                    + " energy but needs 500.";
        }

        gs.playerEnergyAfter   = player.getEnergy();
        gs.opponentEnergyAfter = opponent.getEnergy();
        view.refresh(gs);
    }


    public void handleRollDice() {
        GameState gs = new GameState();
        gs.turnNumber   = turnNumber;
        gs.isPlayerTurn = (game.getCurrent() == game.getPlayer());

        Monster current  = game.getCurrent();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();

  
        gs.playerPosBefore      = player.getPosition();
        gs.opponentPosBefore    = opponent.getPosition();
        gs.playerEnergyBefore   = player.getEnergy();
        gs.opponentEnergyBefore = opponent.getEnergy();

        boolean pShBefore = ReflectionHelper.getBool(player,   "shielded");
        boolean oShBefore = ReflectionHelper.getBool(opponent, "shielded");

        int    cardSizeBefore = getCardPileSize();
        Object topCardBefore  = getTopCard();   
        boolean frozen = ReflectionHelper.getBool(current, "frozen");
        if (frozen) {
            gs.playerFrozenSkipped   = (current == player);
            gs.opponentFrozenSkipped = (current == opponent);
            gs.actionLog = current.getName() + " is FROZEN ❄️ — turn skipped!";
            try { game.playTurn(); } catch (Exception ignored) {}
            captureAfter(gs, player, opponent, current);
            finalizeTurn(gs);
            return;
        }

       
        try {
            int posBefore = current.getPosition();
            game.playTurn();

            captureAfter(gs, player, opponent, current);

           
            int rawDelta = (current.getPosition() - posBefore + Constants.BOARD_SIZE) % Constants.BOARD_SIZE;
            gs.diceRolled = true;
            gs.diceRoll   = inferDice(current, rawDelta);

           
            boolean pShAfter = ReflectionHelper.getBool(player,   "shielded");
            boolean oShAfter = ReflectionHelper.getBool(opponent, "shielded");
            gs.playerShieldBlocked   = pShBefore && !pShAfter
                    && gs.playerEnergyAfter >= gs.playerEnergyBefore;
            gs.opponentShieldBlocked = oShBefore && !oShAfter
                    && gs.opponentEnergyAfter >= gs.opponentEnergyBefore;

           
            int cardSizeAfter = getCardPileSize();
            boolean cardDrawn = cardSizeBefore >= 0
                    && cardSizeAfter >= 0
                    && cardSizeAfter < cardSizeBefore;

            
            if (!cardDrawn && cardSizeBefore == 1 && cardSizeAfter > 1) {
                cardDrawn = true; 
            }

            if (cardDrawn && topCardBefore != null) {
                gs.cardDrawn  = true;
                gs.cardName   = getCardName(topCardBefore);
                gs.cardEffect = getCardEffect(topCardBefore);
            }

           
            buildCellEffectMsg(gs, current, posBefore);

            gs.actionLog = current.getName()
                    + " rolled 🎲 " + gs.diceRoll
                    + " — moved from " + posBefore + " → " + current.getPosition();

            finalizeTurn(gs);

            
            Monster winner = game.getWinner();
            if (winner != null) {
                GameOverView.show(stage, player, opponent, winner);
            }

        } catch (InvalidMoveException e) {
            gs.hadError     = true;
            gs.errorMessage = "Cell occupied by opponent!\n\n"
                    + "You cannot land on the same cell as your opponent.\n"
                    + "Please roll again.";
            gs.actionLog    = "⚠️  Invalid move — cell is occupied. Roll again.";
            captureAfter(gs, player, opponent, current);
            view.refresh(gs);
        }
    }

    
    private void captureAfter(GameState gs, Monster player, Monster opponent, Monster current) {
        gs.playerEnergyAfter   = player.getEnergy();
        gs.opponentEnergyAfter = opponent.getEnergy();
        gs.playerPosAfter      = player.getPosition();
        gs.opponentPosAfter    = opponent.getPosition();
        gs.currentName         = game.getCurrent().getName();
    }

    private void finalizeTurn(GameState gs) {
        turnNumber++;
        gs.turnNumber = turnNumber;
        view.refresh(gs);
    }

    
    private int getCardPileSize() {
        try {
            java.lang.reflect.Method m = Board.class.getMethod("getCards");
            Object result = m.invoke(null);
            if (result instanceof ArrayList) return ((ArrayList<?>) result).size();
        } catch (Exception ignored) {}
        return -1;
    }

    /**
     * Returns the card at index 0 of Board.cards — the next one to be drawn.
     * Board.drawCard() does cards.remove(0), so this is exactly what will be drawn.
     */
    private Object getTopCard() {
        try {
            java.lang.reflect.Method m = Board.class.getMethod("getCards");
            Object result = m.invoke(null);
            if (result instanceof ArrayList) {
                ArrayList<?> list = (ArrayList<?>) result;
                if (!list.isEmpty()) return list.get(0);
            }
        } catch (Exception ignored) {}
        return null;
    }

    // ── Card name/effect lookup ───────────────────────────────────────────
    // Hardcoded from cards.csv — guaranteed to display even if field names differ
    private static final java.util.Map<String, String> CARD_EFFECTS =
            new java.util.HashMap<String, String>() {{
        put("Position Swap",      "Swap places with opponent (if player is behind)");
        put("Super Shield",       "Block the next negative energy effect for your team");
        put("Small Snatcher",     "Steal 50 energy from opponent");
        put("Sneaky Thief",       "Steal 100 energy from opponent");
        put("Mega Drain",         "Steal 150 energy from opponent");
        put("Contamination Code", "Player returns to cell 0");
        put("2319 Alert",         "Opponent returns to cell 0");
        put("Mind Scramble",      "Both monsters confused for 2 turns (roles swapped)");
        put("Total Confusion",    "Both monsters confused for 3 turns (roles swapped)");
    }};

    private String getCardName(Object card) {
        if (card == null) return "Unknown Card";
        // Try "name" field — walk up class hierarchy
        String[] fieldNames = {"name", "cardName", "title"};
        for (String fn : fieldNames) {
            try {
                Class<?> cls = card.getClass();
                while (cls != null) {
                    try {
                        java.lang.reflect.Field f = cls.getDeclaredField(fn);
                        f.setAccessible(true);
                        Object v = f.get(card);
                        if (v != null && !v.toString().isEmpty()) return v.toString();
                        break;
                    } catch (NoSuchFieldException e) { cls = cls.getSuperclass(); }
                }
            } catch (Exception ignored) {}
        }
        return card.getClass().getSimpleName();
    }

    private String getCardEffect(Object card) {
        if (card == null) return "";
        // First get the card name so we can look up the effect from our hardcoded map
        String name = getCardName(card);
        if (CARD_EFFECTS.containsKey(name)) return CARD_EFFECTS.get(name);

        // Fallback: try "description", "effect", "cardEffect" fields
        String[] fieldNames = {"description", "effect", "cardEffect", "desc"};
        for (String fn : fieldNames) {
            try {
                Class<?> cls = card.getClass();
                while (cls != null) {
                    try {
                        java.lang.reflect.Field f = cls.getDeclaredField(fn);
                        f.setAccessible(true);
                        Object v = f.get(card);
                        if (v != null && !v.toString().isEmpty()) return v.toString();
                        break;
                    } catch (NoSuchFieldException e) { cls = cls.getSuperclass(); }
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    private int inferDice(Monster m, int rawDelta) {
        int momentum    = ReflectionHelper.getInt(m, "momentumTurns");
        int normalSpeed = ReflectionHelper.getInt(m, "normalSpeedTurns");
        String type     = m.getClass().getSimpleName();
        if ("Dasher".equals(type)) {
            int mult = (momentum > 0) ? 3 : 2;
            int d = rawDelta / mult;
            return Math.max(1, Math.min(6, d == 0 ? rawDelta : d));
        }
        if ("MultiTasker".equals(type)) {
            int d = (normalSpeed > 0) ? rawDelta : rawDelta * 2;
            return Math.max(1, Math.min(6, d));
        }
        return Math.max(1, Math.min(6, rawDelta == 0 ? 1 : rawDelta));
    }

    private void buildCellEffectMsg(GameState gs, Monster current, int posBefore) {
        StringBuilder sb = new StringBuilder();
        int pDelta = gs.playerEnergyAfter - gs.playerEnergyBefore;
        int oDelta = gs.opponentEnergyAfter - gs.opponentEnergyBefore;

        if (gs.playerShieldBlocked)
            sb.append("🛡️ Shield blocked energy loss for ")
              .append(game.getPlayer().getName()).append("!  ");
        if (gs.opponentShieldBlocked)
            sb.append("🛡️ Shield blocked energy loss for ")
              .append(game.getOpponent().getName()).append("!  ");
        if (pDelta != 0)
            sb.append(game.getPlayer().getName())
              .append(pDelta > 0 ? " +" : " ").append(pDelta).append(" ⚡  ");
        if (oDelta != 0)
            sb.append(game.getOpponent().getName())
              .append(oDelta > 0 ? " +" : " ").append(oDelta).append(" ⚡  ");
        int curPos = current.getPosition();
        if (curPos != posBefore && Math.abs(curPos - posBefore) != gs.diceRoll)
            sb.append("📦 Transported to cell ").append(curPos).append("!  ");

        gs.cellEffectMsg = sb.toString().trim();
    }
}