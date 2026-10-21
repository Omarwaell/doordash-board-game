package game.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ErrorPopupView {

    public static void show(String title, String message) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);

        Text titleTxt = new Text("⚠️  " + title);
        titleTxt.setFont(Font.font("Georgia", FontWeight.BOLD, 16));
        titleTxt.setFill(Color.web("#FFD700"));

        Text msgTxt = new Text(message);
        msgTxt.setFont(Font.font("Georgia", 13));
        msgTxt.setFill(Color.web("#E0E0E0"));
        msgTxt.setWrappingWidth(380);

        Button okBtn = new Button("  OK — Continue Game  ");
        okBtn.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
        okBtn.setStyle("-fx-background-color:#E53935;-fx-text-fill:white;" +
                       "-fx-background-radius:8;-fx-cursor:hand;-fx-padding:8 20;");
        okBtn.setOnAction(e -> popup.close());

        VBox root = new VBox(18, titleTxt, msgTxt, okBtn);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color:#1A2F3A;" +
                      "-fx-border-color:#E53935;-fx-border-width:2;");

        popup.setScene(new Scene(root));
        popup.showAndWait();   
    }
}