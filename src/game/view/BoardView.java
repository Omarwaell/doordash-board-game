package game.view;

import game.engine.Board;
import game.engine.Constants;
import game.engine.Role;
import game.engine.cells.*;
import game.engine.monsters.Monster;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;


public class BoardView extends GridPane {

    private static final int CELL_W = 58;
    private static final int CELL_H = 58;

    public BoardView() {
        setHgap(1);
        setVgap(1);
        setPadding(new Insets(4));
        setStyle("-fx-background-color:#1C2833;");
    }

    public void render(Board board, Monster player, Monster opponent) {
        getChildren().clear();
        Cell[][] cells = board.getBoardCells();

        for (int idx = 0; idx < Constants.BOARD_SIZE; idx++) {
            int[] rc    = toRowCol(idx);
            int row     = rc[0];
            int col     = rc[1];
            Cell cell   = cells[row][col];
            int dispRow = Constants.BOARD_ROWS - 1 - row;   // flip: 0 at bottom

            add(buildCell(idx, cell, player, opponent), col, dispRow);
        }
    }

   
    private VBox buildCell(int idx, Cell cell,
                           Monster player, Monster opponent) {
        VBox box = new VBox(1);
        box.setAlignment(Pos.CENTER);
        box.setPrefSize(CELL_W, CELL_H);
        box.setMaxSize(CELL_W, CELL_H);
        box.setMinSize(CELL_W, CELL_H);
        box.setPadding(new Insets(2));

        String bg = bgColor(cell, idx);
        box.setStyle("-fx-background-color:" + bg + ";" +
                     "-fx-border-color:#37474F;-fx-border-width:0.5;");

        
        Label idxLbl = miniLabel(String.valueOf(idx), "#90A4AE");

       
        Label iconLbl = iconLabel(cell, idx);

        
        if (cell instanceof DoorCell) {
            int en = safeIntMethod(cell, "getEnergy");
            Label eLbl = miniLabel("E:" + en, "#FFD700");
            box.getChildren().addAll(idxLbl, iconLbl, eLbl);
        } else {
            box.getChildren().addAll(idxLbl, iconLbl);
        }

        
        if (cell instanceof MonsterCell) {
            Object cm = ReflectionHelper.getObj(cell, "cellMonster");
            if (cm instanceof Monster) {
                String shortName = ((Monster) cm).getName().split(" ")[0];
                Label ml = miniLabel(shortName, "#CE93D8");
                ml.setMaxWidth(CELL_W - 4);
                ml.setWrapText(true);
                box.getChildren().add(ml);
            }
        }

       
        boolean pHere = player   != null && player.getPosition()   == idx;
        boolean oHere = opponent != null && opponent.getPosition() == idx;
        if (pHere || oHere) {
            StringBuilder mText = new StringBuilder();
            if (pHere) mText.append("👤");
            if (oHere) mText.append("🤖");
            Label mLbl = new Label(mText.toString());
            mLbl.setFont(Font.font("Arial", 14));
            box.getChildren().add(mLbl);
            
            box.setStyle(box.getStyle() +
                    "-fx-border-color:#FFD700;-fx-border-width:2;");
        }

        
        Tooltip tip = new Tooltip(buildTooltip(idx, cell, player, opponent));
        tip.setStyle("-fx-font-size:11;");
        Tooltip.install(box, tip);

        return box;
    }

   

    private String bgColor(Cell cell, int idx) {
        if (cell instanceof MonsterCell)       return "#0D47A1";  // deep blue
        if (cell instanceof CardCell)          return "#B71C1C";  // deep red
        if (cell instanceof ConveyorBelt)      return "#1B5E20";  // deep green
        if (cell instanceof ContaminationSock) return "#E65100";  // deep orange
        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            Role r = (Role) safeObjMethod(dc, "getRole");
            boolean activated = ReflectionHelper.getBool(dc, "activated");
            if (r == Role.SCARER)
                return activated ? "#3E0000" : "#AD1457";   // exhausted darker
            else
                return activated ? "#003300" : "#00695C";   // exhausted darker
        }
        if (idx == 0)  return "#4A148C";   // start — purple
        if (idx == 99) return "#F57F17";   // end / Boo's Door — amber
        return "#1E3040";                  // normal cell
    }

    

    private Label iconLabel(Cell cell, int idx) {
        String icon;
        if (cell instanceof MonsterCell)       { icon = "👾"; }
        else if (cell instanceof CardCell)     { icon = "🃏"; }
        else if (cell instanceof ConveyorBelt) { icon = "➡️"; }
        else if (cell instanceof ContaminationSock) { icon = "🧦"; }
        else if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            boolean activated = ReflectionHelper.getBool(dc, "activated");
            Role r = (Role) safeObjMethod(dc, "getRole");
            if (activated) icon = "✖";
            else icon = (r == Role.SCARER) ? "👻" : "😂";
        }
        else if (idx == 0)  icon = "🚀";
        else if (idx == 99) icon = "🚪";
        else                icon = "";

        Label lbl = new Label(icon);
        lbl.setFont(Font.font("Arial", 12));
        lbl.setTextFill(Color.WHITE);
        lbl.setMaxWidth(CELL_W - 4);
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }

   

    private String buildTooltip(int idx, Cell cell,
                                Monster player, Monster opponent) {
        StringBuilder sb = new StringBuilder("Cell " + idx + "\n");
        if (cell instanceof DoorCell) {
            DoorCell dc = (DoorCell) cell;
            Role r = (Role) safeObjMethod(dc, "getRole");
            boolean act = ReflectionHelper.getBool(dc, "activated");
            int en = safeIntMethod(dc, "getEnergy");
            sb.append("Type: ").append(r).append(" Door\n");
            sb.append("Energy: ").append(en).append("\n");
            sb.append(act ? "Status: EXHAUSTED (used)" : "Status: Active");
        } else if (cell instanceof MonsterCell) {
            Object cm = ReflectionHelper.getObj(cell, "cellMonster");
            sb.append("Type: Monster Cell\n");
            if (cm instanceof Monster)
                sb.append("Stationed: ").append(((Monster) cm).getName());
        } else if (cell instanceof ConveyorBelt)
            sb.append("Type: Conveyor Belt (moves forward)");
        else if (cell instanceof ContaminationSock)
            sb.append("Type: Contamination Sock (moves back, -100 energy)");
        else if (cell instanceof CardCell)
            sb.append("Type: Card Cell (draw a card)");
        else if (idx == 99)
            sb.append("BOO'S DOOR — Win here with ≥1000 energy!");
        else if (idx == 0)
            sb.append("Start cell");
        else
            sb.append("Normal cell");

        if (player   != null && player.getPosition()   == idx) sb.append("\n👤 YOUR monster here");
        if (opponent != null && opponent.getPosition() == idx) sb.append("\n🤖 Opponent here");
        return sb.toString();
    }

    

    private Label miniLabel(String text, String color) {
        Label l = new Label(text);
        l.setFont(Font.font("Monospaced", FontWeight.BOLD, 8));
        l.setTextFill(Color.web(color));
        return l;
    }

    private int safeIntMethod(Object obj, String name) {
        try {
            java.lang.reflect.Method m = obj.getClass().getDeclaredMethod(name);
            m.setAccessible(true);
            Object r = m.invoke(obj);
            return r instanceof Integer ? (Integer) r : 0;
        } catch (Exception e) { return 0; }
    }

    private Object safeObjMethod(Object obj, String name) {
        try {
            java.lang.reflect.Method m = obj.getClass().getDeclaredMethod(name);
            m.setAccessible(true);
            return m.invoke(obj);
        } catch (Exception e) { return null; }
    }

    
    private int[] toRowCol(int index) {
        int cols = Constants.BOARD_COLS;
        int row  = index / cols;
        int col  = index % cols;
        if (row % 2 == 1) col = cols - 1 - col;
        return new int[]{row, col};
    }
}