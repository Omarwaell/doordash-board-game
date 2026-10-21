package game.view;

import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class MonsterInfoView extends VBox {

    private final boolean isPlayer;

    private final Text nameRow     = row("", 15, FontWeight.BOLD,   "#FFFFFF");
    private final Text typeRow     = row("", 13, FontWeight.NORMAL, "#90CAF9");
    private final Text origRoleRow = row("", 13, FontWeight.NORMAL, "#B0BEC5");
    private final Text curRoleRow  = row("", 13, FontWeight.BOLD,   "#FFD700");
    private final Text energyRow   = row("", 15, FontWeight.BOLD,   "#A5D6A7");
    private final Text posRow      = row("", 13, FontWeight.NORMAL, "#CE93D8");
    private final Text shieldRow   = row("", 12, FontWeight.NORMAL, "#81D4FA");
    private final Text confRow     = row("", 12, FontWeight.NORMAL, "#FF8A65");
    private final Text frozenRow   = row("", 12, FontWeight.NORMAL, "#80DEEA");
    private final Text momentumRow = row("", 12, FontWeight.NORMAL, "#FFCC80");
    private final Text focusRow    = row("", 12, FontWeight.NORMAL, "#C5E1A5");
    private final Text energyDelta = row("", 13, FontWeight.BOLD,   "#FFFFFF");

    public MonsterInfoView(boolean isPlayer) {
        this.isPlayer = isPlayer;
        setSpacing(4);
        setPadding(new Insets(12));
        setMinWidth(210);
        setPrefWidth(215);
        setAlignment(Pos.TOP_LEFT);
        applyBorder(false);

        String hdrText  = isPlayer ? "👤  YOUR MONSTER" : "🤖  OPPONENT";
        String hdrColor = isPlayer ? "#66BB6A" : "#EF5350";
        Text header = row(hdrText, 13, FontWeight.BOLD, hdrColor);

        Separator sep = new Separator();

        getChildren().addAll(
            header, nameRow, typeRow, origRoleRow, curRoleRow,
            new Separator(),
            energyRow, posRow,
            new Separator(),
            shieldRow, frozenRow, confRow, momentumRow, focusRow,
            new Separator(),
            energyDelta
        );
    }

    
    public void update(Monster m, boolean isCurrent, int energyChange) {
        if (m == null) return;
        applyBorder(isCurrent);

        
        nameRow.setText("🎭 " + m.getName());

       
        typeRow.setText("⚙️  Type: " + m.getClass().getSimpleName());

       
        Object orig = ReflectionHelper.getObj(m, "originalRole");
        String origStr = orig != null ? orig.toString() : m.getRole().toString();
        origRoleRow.setText("🏷️  Original: " + origStr);

        
        String curStr = m.getRole() != null ? m.getRole().toString() : "?";
        int confTurns = ReflectionHelper.getInt(m, "confusionTurns");
        boolean confused = confTurns > 0;

        if (confused && !curStr.equals(origStr)) {
            curRoleRow.setText("🔄 Current: " + curStr + " (CONFUSED!)");
            curRoleRow.setFill(Color.web("#FF7043"));
        } else {
            String icon = "SCARER".equals(curStr) ? "👻" : "😂";
            curRoleRow.setText(icon + " Current: " + curStr);
            curRoleRow.setFill(Color.web("#FFD700"));
        }

        
        int energy = m.getEnergy();
        String eColor = energy >= 700 ? "#A5D6A7" : energy >= 300 ? "#FFF176" : "#EF9A9A";
        energyRow.setFill(Color.web(eColor));
        energyRow.setText("⚡ Energy: " + energy + (energy >= 1000 ? "  ✅ WIN READY!" : ""));

        
        posRow.setText("📍 Position: Cell " + m.getPosition());

        
        boolean shielded = ReflectionHelper.getBool(m, "shielded");
        boolean frozen   = ReflectionHelper.getBool(m, "frozen");
        int momentum     = ReflectionHelper.getInt(m, "momentumTurns");
        int focus        = ReflectionHelper.getInt(m, "normalSpeedTurns");

        shieldRow.setText(shielded  ? "🛡️  SHIELDED (next -energy blocked)" : "");
        frozenRow.setText(frozen    ? "❄️  FROZEN — will skip next turn"     : "");
        confRow.setText  (confTurns > 0 ? "🌀  CONFUSED for " + confTurns + " turn(s)" : "");
        momentumRow.setText(momentum > 0 ? "💨  MOMENTUM RUSH: " + momentum + " turn(s) left" : "");
        focusRow.setText   (focus > 0    ? "🎯  FOCUS MODE: "    + focus    + " turn(s) left" : "");

        
        if (energyChange != 0) {
            String sign = energyChange > 0 ? "+" : "";
            String dColor = energyChange > 0 ? "#A5D6A7" : "#EF9A9A";
            energyDelta.setText("Δ Energy this turn: " + sign + energyChange);
            energyDelta.setFill(Color.web(dColor));
        } else {
            energyDelta.setText("");
        }
    }

    private void applyBorder(boolean isCurrent) {
        String border = isCurrent ? (isPlayer ? "#66BB6A" : "#EF5350") : "#37474F";
        String bg     = isCurrent ? "#1A2F3A" : "#0D1B2A";
        setStyle("-fx-background-color:" + bg + ";" +
                 "-fx-border-color:" + border + ";" +
                 "-fx-border-width:2;-fx-border-radius:8;-fx-background-radius:8;");
    }

    private static Text row(String s, int size, FontWeight w, String color) {
        Text t = new Text(s);
        t.setFont(Font.font("Georgia", w, size));
        t.setFill(Color.web(color));
        t.setWrappingWidth(200);
        return t;
    }


    private static class Separator extends javafx.scene.control.Separator {
        Separator() {
            setStyle("-fx-background-color:#37474F;");
            setMaxWidth(200);
            setPadding(new Insets(1, 0, 1, 0));
        }
    }
}