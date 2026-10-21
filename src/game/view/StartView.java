package game.view;

import game.controller.GameController;
import game.engine.Role;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


public class StartView {

    private final Scene scene;

    public StartView(Stage stage) {
        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 60, 36, 60));
        root.setStyle("-fx-background-color:linear-gradient(to bottom,#0D0221,#1A0845,#0D1B2A);");

        
        Text title = new Text("DoorDasH");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 60));
        title.setFill(Color.web("#FFD700"));
        DropShadow glow = new DropShadow(28, Color.web("#FFD700"));
        title.setEffect(glow);

        Text sub = new Text("Scare vs Laugh Touchdown");
        sub.setFont(Font.font("Georgia", FontWeight.NORMAL, 24));
        sub.setFill(Color.web("#E0E0E0"));

        Text tagline = new Text(
            "\"We scare because we care.\"  —  or  —  \"We laugh, that's our path.\"");
        tagline.setFont(Font.font("Georgia", FontPosture.ITALIC, 14));
        tagline.setFill(Color.web("#90A4AE"));
        tagline.setTextAlignment(TextAlignment.CENTER);
        tagline.setWrappingWidth(660);

        
        TitledPane instructions = buildInstructions();

        
        Text chooseText = new Text("Choose Your Side:");
        chooseText.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        chooseText.setFill(Color.web("#FFD700"));

        ToggleGroup group = new ToggleGroup();

        RadioButton scarerBtn  = roleButton("👻  SCARER",  "#AD1457", "#880E4F", group);
        RadioButton laugherBtn = roleButton("😂  LAUGHER", "#00695C", "#004D40", group);
        scarerBtn.setSelected(true);

        HBox roleRow = new HBox(30, scarerBtn, laugherBtn);
        roleRow.setAlignment(Pos.CENTER);

        
        Label scarerDesc  = roleDesc(
            "Scarers collect energy through fear. Land on Scarer Doors to gain energy.");
        Label laugherDesc = roleDesc(
            "Laughers collect energy through laughter. Land on Laugher Doors to gain energy.");

        HBox descRow = new HBox(30, scarerDesc, laugherDesc);
        descRow.setAlignment(Pos.CENTER);

        
        Button startBtn = new Button("▶   START GAME");
        startBtn.setFont(Font.font("Georgia", FontWeight.BOLD, 19));
        startBtn.setStyle("-fx-background-color:#FFD700;-fx-text-fill:#0D0221;" +
                          "-fx-background-radius:30;-fx-padding:14 55;-fx-cursor:hand;");
        startBtn.setOnMouseEntered(e -> startBtn.setStyle(
                "-fx-background-color:#FFC107;-fx-text-fill:#0D0221;" +
                "-fx-background-radius:30;-fx-padding:14 55;-fx-cursor:hand;"));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(
                "-fx-background-color:#FFD700;-fx-text-fill:#0D0221;" +
                "-fx-background-radius:30;-fx-padding:14 55;-fx-cursor:hand;"));

        startBtn.setOnAction(e -> {
            Role selected = scarerBtn.isSelected() ? Role.SCARER : Role.LAUGHER;
            try {
                GameController controller = new GameController(selected, stage);
                GameView gameView = new GameView(controller);
                controller.setView(gameView);
                stage.setScene(gameView.getScene());
            } catch (Exception ex) {
                ErrorPopupView.show("Initialization Error", ex.getMessage());
            }
        });

        root.getChildren().addAll(
            title, sub, tagline,
            instructions,
            chooseText, roleRow, descRow,
            startBtn
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color:#0D0221;");

        scene = new Scene(scroll, 900, 820);
    }

    

    private TitledPane buildInstructions() {
        String text =
            "OBJECTIVE\n" +
            "  Be the first monster to reach cell 99 (Boo's Door) with ≥ 1000 energy.\n\n" +
            "TURN SEQUENCE\n" +
            "  1. Optionally activate your POWERUP (costs 500 energy).\n" +
            "  2. Roll a 6-sided die and move forward.\n" +
            "  3. You cannot land on the opponent's cell — if destination is occupied, reroll.\n\n" +
            "CELL TYPES\n" +
            "  👻 Scarer Door  — land on matching role to gain energy; mismatch loses energy.\n" +
            "  😂 Laugher Door — same rules, opposite role.\n" +
            "  ✖  Exhausted    — door already used, no effect.\n" +
            "  🃏 Card Cell    — draw a random card with a special effect.\n" +
            "  ➡️ Conveyor Belt — transported forward (no cell effect at destination).\n" +
            "  🧦 Contamination Sock — transported backward AND lose 100 energy.\n" +
            "  👾 Monster Cell — same role → free powerup; different role → energy swap if you have less.\n\n" +
            "CARDS\n" +
            "  Position Swap  — swap places with opponent if you're behind.\n" +
            "  Start Over     — sends player or opponent back to cell 0.\n" +
            "  Energy Steal   — steal 50 / 100 / 150 energy from opponent.\n" +
            "  Super Shield   — block the next negative energy effect for your team.\n" +
            "  Confusion      — swap roles of both monsters for 2–3 turns.\n\n" +
            "MONSTER TYPES\n" +
            "  Dasher      — 2× movement speed; powerup: 3× for 3 turns.\n" +
            "  Dynamo      — 2× energy gains AND losses; powerup: freeze opponent 1 turn.\n" +
            "  MultiTasker — ½ movement speed, +200 on all energy changes; powerup: normal speed 2 turns.\n" +
            "  Schemer     — +10 on all energy changes; powerup: steal 10 energy from all others.\n\n" +
            "WIN CONDITION\n" +
            "  Reach cell 99 AND have ≥ 1000 energy.";

        TextArea ta = new TextArea(text);
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefHeight(260);
        ta.setFont(Font.font("Monospaced", 12));
        ta.setStyle("-fx-control-inner-background:#0D1B2A;-fx-text-fill:#CFD8DC;");

        TitledPane tp = new TitledPane("📖  Game Instructions  (click to expand / collapse)", ta);
        tp.setExpanded(false);
        tp.setStyle("-fx-font-family:Georgia;-fx-font-size:14;-fx-text-fill:#000000;");
        tp.setMaxWidth(720);
        return tp;
    }

    private RadioButton roleButton(String label, String bg, String hover, ToggleGroup group) {
        RadioButton rb = new RadioButton(label);
        rb.setToggleGroup(group);
        rb.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        rb.setTextFill(Color.WHITE);
        rb.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:20;" +
                    "-fx-padding:12 35;-fx-cursor:hand;");
        rb.setOnMouseEntered(e -> rb.setStyle(
                "-fx-background-color:" + hover + ";-fx-background-radius:20;" +
                "-fx-padding:12 35;-fx-cursor:hand;"));
        rb.setOnMouseExited(e -> rb.setStyle(
                "-fx-background-color:" + bg + ";-fx-background-radius:20;" +
                "-fx-padding:12 35;-fx-cursor:hand;"));
        return rb;
    }

    private Label roleDesc(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Georgia", 12));
        l.setTextFill(Color.web("#90A4AE"));
        l.setWrapText(true);
        l.setMaxWidth(290);
        l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-text-alignment:center;");
        return l;
    }

    public Scene getScene() { return scene; }
}