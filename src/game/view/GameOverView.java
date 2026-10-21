package game.view;

import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


public class GameOverView {

    public static void show(Stage stage, Monster player, Monster opponent, Monster winner) {
        boolean playerWon = winner == player;

        VBox root = new VBox(22);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60, 80, 60, 80));
        String gradStart = playerWon ? "#0A2A0A" : "#2A0A0A";
        String gradEnd   = playerWon ? "#1B5E20" : "#B71C1C";
        root.setStyle("-fx-background-color:linear-gradient(to bottom,"
                + gradStart + "," + gradEnd + ");");

  
        Text headline = new Text(playerWon ? "🏆  VICTORY!" : "💀  GAME OVER");
        headline.setFont(Font.font("Georgia", FontWeight.BOLD, 62));
        headline.setFill(playerWon ? Color.web("#FFD700") : Color.web("#FF5252"));
        DropShadow glow = new DropShadow(35,
                playerWon ? Color.web("#FFD700") : Color.web("#FF5252"));
        headline.setEffect(glow);

        
        Object origRoleObj = ReflectionHelper.getObj(winner, "originalRole");
        String origRole    = origRoleObj != null ? origRoleObj.toString()
                                                 : winner.getRole().toString();

        Text winnerName = new Text("🥇  Winner: " + winner.getName());
        winnerName.setFont(Font.font("Georgia", FontWeight.BOLD, 28));
        winnerName.setFill(Color.WHITE);

        Text winnerRole = new Text("Role: " + origRole
                + ("SCARER".equals(origRole) ? "  👻" : "  😂"));
        winnerRole.setFont(Font.font("Georgia", FontPosture.ITALIC, 20));
        winnerRole.setFill(Color.web("#FFD700"));

        Text winnerType = new Text("Type: " + winner.getClass().getSimpleName());
        winnerType.setFont(Font.font("Georgia", 16));
        winnerType.setFill(Color.web("#90CAF9"));

        
        Text energyHdr = new Text("── Final Energies ──");
        energyHdr.setFont(Font.font("Georgia", FontWeight.BOLD, 18));
        energyHdr.setFill(Color.web("#90CAF9"));

        Text playerEnergy = new Text(
            "👤 " + player.getName() + "   ⚡ " + player.getEnergy()
            + (player.getEnergy() >= 1000 ? "  ✅" : "  ✗"));
        playerEnergy.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        playerEnergy.setFill(Color.web(player.getEnergy() >= 1000 ? "#A5D6A7" : "#EF9A9A"));

        Text opponentEnergy = new Text(
            "🤖 " + opponent.getName() + "   ⚡ " + opponent.getEnergy()
            + (opponent.getEnergy() >= 1000 ? "  ✅" : "  ✗"));
        opponentEnergy.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        opponentEnergy.setFill(Color.web(opponent.getEnergy() >= 1000 ? "#A5D6A7" : "#EF9A9A"));

        
        Text playerPos   = new Text("👤 " + player.getName()   + " — Cell " + player.getPosition());
        Text opponentPos = new Text("🤖 " + opponent.getName() + " — Cell " + opponent.getPosition());
        for (Text t : new Text[]{playerPos, opponentPos}) {
            t.setFont(Font.font("Georgia", 14));
            t.setFill(Color.web("#B0BEC5"));
        }

        
        Button returnBtn = new Button("↩   Return to Main Menu");
        returnBtn.setFont(Font.font("Georgia", FontWeight.BOLD, 17));
        returnBtn.setStyle("-fx-background-color:#FFD700;-fx-text-fill:#0D0221;" +
                           "-fx-background-radius:30;-fx-padding:13 45;-fx-cursor:hand;");
        returnBtn.setOnMouseEntered(e -> returnBtn.setStyle(
                "-fx-background-color:#FFC107;-fx-text-fill:#0D0221;" +
                "-fx-background-radius:30;-fx-padding:13 45;-fx-cursor:hand;"));
        returnBtn.setOnMouseExited(e -> returnBtn.setStyle(
                "-fx-background-color:#FFD700;-fx-text-fill:#0D0221;" +
                "-fx-background-radius:30;-fx-padding:13 45;-fx-cursor:hand;"));
        returnBtn.setOnAction(e -> GameApplication.showStartView());

        root.getChildren().addAll(
            headline,
            winnerName, winnerRole, winnerType,
            energyHdr, playerEnergy, opponentEnergy,
            playerPos, opponentPos,
            returnBtn
        );

        stage.setScene(new Scene(root, 900, 660));
    }
}