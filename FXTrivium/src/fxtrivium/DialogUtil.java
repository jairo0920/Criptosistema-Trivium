package fxtrivium;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

/**
 *
 * @author srey
 */
public final class DialogUtil {
    public static void showWarning(String title, String message) {
        Alert warning= new Alert(Alert.AlertType.WARNING, 
                            message, 
                            ButtonType.OK);

        warning.initModality(Modality.WINDOW_MODAL);
        warning.setHeaderText(title);
        warning.setResizable(true);
        warning.showAndWait();
    }
    
    public  static void showInfoMessage(String title, String message) {
        Alert info= new Alert(Alert.AlertType.INFORMATION, 
                            message, 
                            ButtonType.OK);
                    
        info.initModality(Modality.WINDOW_MODAL);
        info.setHeaderText(title);
        info.setResizable(true);
        info.showAndWait();
    }
    
    public static void showException(String title, Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(title);
        alert.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        
        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.setMaxHeight(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }
}