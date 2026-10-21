package game.view;

import game.controller.GameController;
import game.engine.Game;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class GameView {

    private final Scene   scene;
    private final GameController controller;

   
    private final BoardView        boardView     = new BoardView();
    private final MonsterInfoView  playerPanel   = new MonsterInfoView(true);
    private final MonsterInfoView  opponentPanel = new MonsterInfoView(false);

    
    private final Text  turnLabel       = styled("Turn 1",  16, FontWeight.BOLD,   "#FFD700");
    private final Text  currentLabel    = styled("",        14, FontWeight.BOLD,   "#FFFFFF");
    private final Text  diceDisplay     = styled("🎲  —",   38, FontWeight.BOLD,   "#FFD700");
    private final Text  diceSubLabel    = styled("Roll the dice", 11, FontWeight.NORMAL, "#90A4AE");
    private final Text  cellEffectLabel = styled("",        13, FontWeight.NORMAL, "#FFCC80");
    private final Text  cardInfoLabel   = styled("",        13, FontWeight.NORMAL, "#CE93D8");
    private final Text  frozenLabel     = styled("",        14, FontWeight.BOLD,   "#80DEEA");
    private final Text  shieldLabel     = styled("",        13, FontWeight.BOLD,   "#81D4FA");
    private final Label cardPileLabel   = miniLabel("🃏 Cards left: 25");
    private final TextArea logArea      = buildLog();

    
    private final Button powerupBtn = buildBtn("⚡  Use Powerup",  "#7B1FA2", "#6A1B9A");
    private final Button rollBtn    = buildBtn("🎲  Roll Dice",    "#1565C0", "#0D47A1");

    private int turnCount = 1;

    public GameView(GameController controller) {
        this.controller = controller;

       
        powerupBtn.setOnAction(e -> controller.handleUsePowerup());
        rollBtn.setOnAction(e -> controller.handleRollDice());

        
        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(225);
        leftPane.setStyle("-fx-background-color:#0A1628;");
        leftPane.getChildren().addAll(playerPanel, buildControlBox());

        
        VBox centrePane = new VBox(6);
        centrePane.setAlignment(Pos.TOP_CENTER);
        centrePane.setPadding(new Insets(8, 6, 8, 6));
        centrePane.setStyle("-fx-background-color:#111E2D;");
        centrePane.getChildren().addAll(
            buildHudBar(),
            buildLegendBar(),
            boardView,
            buildEffectsBar(),
            logArea
        );

       
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(225);
        rightPane.setStyle("-fx-background-color:#0A1628;");
        rightPane.getChildren().addAll(
            opponentPanel,
            buildDiceBox(),
            buildCardInfoBox(),
            buildColorKey()
        );

        HBox root = new HBox(leftPane, centrePane, rightPane);
        root.setStyle("-fx-background-color:#0A1628;");

        
        GameState init = new GameState();
        init.turnNumber  = 1;
        init.isPlayerTurn = true;
        init.actionLog   = "Game started! " + controller.getGame().getCurrent().getName() + " goes first.";
        refresh(init);

        scene = new Scene(root, 1230, 790);
    }



    public void refresh(GameState gs) {
        Game    game     = controller.getGame();
        Monster player   = game.getPlayer();
        Monster opponent = game.getOpponent();
        Monster current  = game.getCurrent();

        
        boardView.render(game.getBoard(), player, opponent);

       
        int pDelta = gs.playerEnergyAfter   - gs.playerEnergyBefore;
        int oDelta = gs.opponentEnergyAfter - gs.opponentEnergyBefore;
        boolean playerTurn = (current == player);
        playerPanel.update(player,   playerTurn,  pDelta);
        opponentPanel.update(opponent, !playerTurn, oDelta);

        
        turnLabel.setText("Turn " + gs.turnNumber);
        currentLabel.setText("🎮  " + current.getName() + "'s turn"
            + (gs.playerFrozenSkipped || gs.opponentFrozenSkipped ? " ❄️ SKIPPED" : ""));

        
        if (gs.diceRolled) {
            diceDisplay.setText("🎲  " + gs.diceRoll);
            diceSubLabel.setText("Last roll");
        } else if (gs.playerFrozenSkipped || gs.opponentFrozenSkipped) {
            diceDisplay.setText("❄️  SKIP");
            diceSubLabel.setText("Frozen — turn skipped");
        }

       
        boolean pFrozen = ReflectionHelper.getBool(player,   "frozen");
        boolean oFrozen = ReflectionHelper.getBool(opponent, "frozen");
        if (pFrozen || oFrozen) {
            String who = pFrozen ? player.getName() : opponent.getName();
            frozenLabel.setText("❄️  " + who + " is FROZEN — next turn will be skipped!");
        } else {
            frozenLabel.setText("");
        }

        
        if (gs.playerShieldBlocked || gs.opponentShieldBlocked) {
            String who = gs.playerShieldBlocked ? player.getName() : opponent.getName();
            shieldLabel.setText("🛡️  " + who + "'s shield BLOCKED the energy loss!");
        } else {
            shieldLabel.setText("");
        }

        
        cellEffectLabel.setText(gs.cellEffectMsg);

        
        if (gs.cardDrawn) {
            cardInfoLabel.setText("🃏 " + gs.cardName + ": " + gs.cardEffect);
            
            CardPopupView.show(gs.cardName, gs.cardEffect);
        } else {
            cardInfoLabel.setText("");
        }

        
        try {
            
            java.lang.reflect.Method getCards = game.getBoard().getClass().getMethod("getCards");
            Object pile = getCards.invoke(null);  
            if (pile instanceof java.util.List<?>) {

                int size = ((java.util.List<?>) pile).size();

            
                if(turnLabel.getText().contains("Turn 1") && size == 24)
                    size = 25;

                cardPileLabel.setText("🃏 Cards left: " + size);
            }
        } catch (Exception ignored) {}

        
        boolean isPlayerTurn = (current == player);
        powerupBtn.setDisable(!isPlayerTurn || player.getEnergy() < 500);
        rollBtn.setDisable(false);

        
        if (gs.hadError) {
            ErrorPopupView.show("Invalid Action", gs.errorMessage);
        }

        
        StringBuilder log = new StringBuilder();
        log.append("[Turn ").append(gs.turnNumber).append("] ");
        if (gs.powerupUsed) log.append("⚡ POWERUP used!  ");
        log.append(gs.actionLog).append("\n");
        if (!gs.cellEffectMsg.isEmpty())
            log.append("  Cell: ").append(gs.cellEffectMsg).append("\n");
        if (gs.cardDrawn)
            log.append("  Card: ").append(gs.cardName)
               .append(" — ").append(gs.cardEffect).append("\n");
        if (gs.playerShieldBlocked || gs.opponentShieldBlocked)
            log.append("  🛡️ Shield blocked!\n");
        log.append("  ").append(player.getName())
           .append(" energy=").append(player.getEnergy())
           .append(" pos=").append(player.getPosition())
           .append(" | ").append(opponent.getName())
           .append(" energy=").append(opponent.getEnergy())
           .append(" pos=").append(opponent.getPosition()).append("\n");
        logArea.insertText(0, log.toString());
    }

   

    private HBox buildHudBar() {
        HBox bar = new HBox(20, turnLabel, currentLabel, cardPileLabel);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(5, 8, 5, 8));
        bar.setStyle("-fx-background-color:#0A1628;-fx-background-radius:6;");
        return bar;
    }

    private HBox buildLegendBar() {
        HBox bar = new HBox(5);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(2, 0, 2, 0));

        String[][] items = {
            {"#AD1457","Scarer door"},{"#00695C","Laugher door"},
            {"#3E0000","Exhausted door"},{"#0D47A1","Monster cell"},
            {"#B71C1C","Card cell"},{"#1B5E20","Conveyor"},
            {"#E65100","Cont.Sock"},{"#4A148C","Start"},{"#F57F17","End(99)"},
        };
        for (String[] item : items) {
            Pane sw = new Pane();
            sw.setPrefSize(10, 10);
            sw.setStyle("-fx-background-color:" + item[0] + ";-fx-border-color:#555;-fx-border-width:0.5;");
            Label lb = new Label(item[1]);
            lb.setFont(Font.font("Monospaced", 8));
            lb.setTextFill(Color.web("#90A4AE"));
            bar.getChildren().addAll(sw, lb);
        }
        return bar;
    }

    private VBox buildEffectsBar() {
        VBox bar = new VBox(3, frozenLabel, shieldLabel, cellEffectLabel);
        bar.setPadding(new Insets(4, 6, 2, 6));
        bar.setMinHeight(40);
        return bar;
    }

    private VBox buildControlBox() {
        Text hdr = styled("── ACTIONS ──", 13, FontWeight.BOLD, "#90CAF9");
        Text tip = styled("1. Optionally use powerup\n2. Then roll dice", 11,
                FontWeight.NORMAL, "#78909C");

        powerupBtn.setMaxWidth(Double.MAX_VALUE);
        rollBtn.setMaxWidth(Double.MAX_VALUE);

        VBox box = new VBox(8, hdr, tip, powerupBtn, rollBtn);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#152030;-fx-background-radius:8;");
        return box;
    }

    private VBox buildDiceBox() {
        VBox box = new VBox(4, diceDisplay, diceSubLabel, frozenLabel);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-background-color:#152030;-fx-background-radius:8;");
        return box;
    }

    private VBox buildCardInfoBox() {
        Text hdr = styled("── LAST CARD ──", 12, FontWeight.BOLD, "#CE93D8");
        VBox box = new VBox(5, hdr, cardInfoLabel);
        box.setPadding(new Insets(10));
        box.setMinHeight(60);
        box.setStyle("-fx-background-color:#1A0B2E;-fx-background-radius:8;");
        return box;
    }

    private VBox buildColorKey() {
        VBox box = new VBox(5);
        box.setPadding(new Insets(10));
        box.setStyle("-fx-background-color:#152030;-fx-background-radius:8;");
        box.getChildren().add(styled("── ICON KEY ──", 12, FontWeight.BOLD, "#90CAF9"));

        String[][] items = {
            {"👤","Your monster"},{"🤖","Opponent"},
            {"👻","Active Scarer door"},{"😂","Active Laugher door"},
            {"✖","Exhausted door"},{"👾","Monster cell"},
            {"🃏","Card cell"},{"➡️","Conveyor belt"},
            {"🧦","Cont. Sock"},{"🚀","Cell 0 (Start)"},
            {"🚪","Cell 99 (End)"},
        };
        for (String[] item : items) {
            Label l = new Label(item[0] + "  " + item[1]);
            l.setFont(Font.font("Georgia", 11));
            l.setTextFill(Color.web("#CFD8DC"));
            box.getChildren().add(l);
        }
        return box;
    }

    private static TextArea buildLog() {
        TextArea ta = new TextArea();
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefHeight(90);
        ta.setFont(Font.font("Monospaced", 10));
        ta.setStyle("-fx-control-inner-background:#0A1628;-fx-text-fill:#90A4AE;");
        return ta;
    }

    private static Button buildBtn(String lbl, String bg, String hover) {
        Button b = new Button(lbl);
        b.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        b.setTextFill(Color.WHITE);
        b.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:8;" +
                   "-fx-padding:8 16;-fx-cursor:hand;");
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color:" + hover + ";-fx-background-radius:8;" +
                "-fx-padding:8 16;-fx-cursor:hand;"));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color:" + bg + ";-fx-background-radius:8;" +
                "-fx-padding:8 16;-fx-cursor:hand;"));
        return b;
    }

    private static Text styled(String s, int size, FontWeight w, String c) {
        Text t = new Text(s);
        t.setFont(Font.font("Georgia", w, size));
        t.setFill(Color.web(c));
        t.setWrappingWidth(210);
        return t;
    }

    private static Label miniLabel(String s) {
        Label l = new Label(s);
        l.setFont(Font.font("Georgia", 12));
        l.setTextFill(Color.web("#CE93D8"));
        return l;
    }

    public Scene getScene() { return scene; }
}