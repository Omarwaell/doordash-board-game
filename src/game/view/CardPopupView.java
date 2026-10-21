package game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class CardPopupView {

    public static void show(String cardName, String cardEffect) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        
        Text header = new Text("🃏  CARD DRAWN!");
        header.setFont(Font.font("Georgia", FontWeight.BOLD, 22));
        header.setFill(Color.web("#FFD700"));

        
        VBox nameBox = new VBox(4);
        nameBox.setAlignment(Pos.CENTER);
        nameBox.setPadding(new Insets(12, 20, 12, 20));
        nameBox.setStyle("-fx-background-color:#2C1654;-fx-background-radius:10;" +
                         "-fx-border-color:#9C27B0;-fx-border-width:2;-fx-border-radius:10;");

        Text nameTxt = new Text(cardName);
        nameTxt.setFont(Font.font("Georgia", FontWeight.BOLD, 20));
        nameTxt.setFill(Color.web("#CE93D8"));

        nameBox.getChildren().add(nameTxt);

       
        Text effectLabel = new Text("Effect:");
        effectLabel.setFont(Font.font("Georgia", FontWeight.BOLD, 14));
        effectLabel.setFill(Color.web("#90CAF9"));

        Text effectTxt = new Text(cardEffect);
        effectTxt.setFont(Font.font("Georgia", 14));
        effectTxt.setFill(Color.web("#E0E0E0"));
        effectTxt.setWrappingWidth(360);
        effectTxt.setTextAlignment(TextAlignment.CENTER);

        Button okBtn = new Button("  OK — Apply Effect  ");
        okBtn.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        okBtn.setStyle("-fx-background-color:#9C27B0;-fx-text-fill:white;" +
                       "-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;");
        okBtn.setOnAction(e -> popup.close());

        VBox root = new VBox(16, header, nameBox, effectLabel, effectTxt, okBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color:#1A0B2E;" +
                      "-fx-border-color:#9C27B0;-fx-border-width:2;");

        popup.setScene(new Scene(root, 440, 340));
        popup.showAndWait();
    }
}