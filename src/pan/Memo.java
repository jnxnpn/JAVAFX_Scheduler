package pan;

/**
 * Created by PJX on 6/26/17.
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Memo {
    private String time = "";
    private String description = "";
    private String status = "To Do";
    private Label timeLabel = new Label ();
    private Label despLabel = new Label ();
    Button statusButton = new Button ("T");
    CheckBox check = new CheckBox();
    private Course sup;
    File file;

    public Memo(File f, Course sup) throws IOException {
        this.sup = sup;
        file = f;
        if (!f.exists()){
            f.getParentFile().mkdirs();
            f.createNewFile();
        }else{
            Scanner scan = new Scanner (f);
            if (scan.hasNext()) time = scan.nextLine();
            if (scan.hasNext()) status = scan.nextLine();
            while (scan.hasNext()) {
                description += scan.nextLine() +"\n";
            }
        }
        timeLabel.setText(time);
        despLabel.setText(description);
        statusButton.setText(status.substring(0,1));
        statusButton.setMaxSize(40,20   );
        statusButton.setOnAction(e -> {
                if (sup.triggered) {
                changeWindow();
                sup.returnNomalcy();
                sup.triggered = false;
            }else{
                if (status.equals("To Do")){
                    status = "Done";
                } else if (status.equals("Done")){
                    status = "Review";
                } else {
                    status = "To Do";
                }
                statusButton.setText(status.substring(0,1));
                try {
                    save();
                } catch (IOException e1) {
                    //e1.printStackTrace();
                }
            }
            }
        );


    }

    public ScrollPane getLayout (){
        timeLabel.setText(time);
        despLabel.setText(description);
        ScrollPane sp = new ScrollPane();
        VBox v = new VBox(3);
        sp.setPadding(new Insets(2, 2, 2, 2));
        sp.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,new CornerRadii(3), BorderStroke.DEFAULT_WIDTHS )));
        sp.setMinWidth(180);
        sp.setMinHeight(40);
        sp.setMaxWidth(180);
        sp.setMaxHeight(150);
        sp.fitToWidthProperty().setValue(true);
        HBox h = new HBox(2);
        h.getChildren().addAll(check, statusButton, timeLabel);
        v.getChildren().addAll(h, despLabel);
        sp.setContent(v);
        sp.setStyle("-fx-background-color:transparent;");
        return sp;
    }


    void setFile (File f){
        file = f;
    }

    private void changeWindow(){

        Stage makeChange = new Stage ();
        makeChange.initModality(Modality.APPLICATION_MODAL);
        makeChange.setTitle("Make Change to This Event");
        Label timeLbl = new Label("Due Time: ");
        TextField tfTime = new TextField (this.time);
        Label despLbl = new Label("Description: ");
        TextArea tfDes = new TextArea(this.description);
        Button confirm = new Button ("Confirm");
        confirm.setOnAction(e->{
            description = tfDes.getText();
            time = tfTime.getText();
            despLabel.setText(description);
            timeLabel.setText(time);
            try {
                save();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
            makeChange.close();
        });

        HBox hbox  = new HBox(5);
        hbox.getChildren().addAll(timeLbl,tfTime);

        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(hbox, despLbl, tfDes,confirm);
        vbox.setPadding(new Insets(8,8,8,8));

        makeChange.setScene(new Scene(vbox, 300,300) );
        makeChange.setAlwaysOnTop(true);
        makeChange.show();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    void save () throws IOException {
        BufferedWriter bw = new BufferedWriter (new FileWriter(file));
        bw. write(time + "\n"+status + "\n"+ description);
        bw.close();
    }

}
