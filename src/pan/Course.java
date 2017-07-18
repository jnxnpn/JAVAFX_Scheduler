package pan;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.io.File;


/**
 * Created by PJX on 6/26/17.
 */

public class Course {
    private char period;
    private String name;
    private ArrayList<Memo> memos = new ArrayList<Memo>(3);
    private String path;
    private Label lbl = new Label();
    int currentEventNumber = 0; //file name starts with 0.
    Button settingMode = new Button("Edit\nEvent");
    Boolean triggered = false;
    Button eventAddition = new Button("+");
    HBox innerHBox = new HBox(8);
    ;
    Main main;

    public Course(char prd, String name, Main main) throws IOException {
        this.main = main;
        innerHBox.setAlignment(Pos.CENTER_LEFT);
        this.name = name;
        this.period = prd;
        path = Main.baseString+ "save" + Main.pathSign + name + Main.pathSign;
        initializeList();
        settingMode.setOnAction(e -> {
            if (main.editCourseTrigger){
                main.editCourseTrigger = false;
                main.addCourse.setVisible(false);
                main.editCourse.setText("Edit Course...");
                courseEditWindow();

            }else if (!triggered) {
                settingMode.setText("Choose\nEvent");
                //this.main.resolveTriggered = false;
                triggered = true;
                for (Memo m : memos) {
                    m.statusButton.setText("..."); // choice
                }
                innerHBox.getChildren().add(eventAddition);
            } else {
                returnNomalcy();
                triggered = false;
            }

        });

        eventAddition.setOnAction(e -> {
            additionWindow();
            currentEventNumber++;
            this.returnNomalcy();
            this.triggered = false;

        });

    }

    private void additionWindow() {
        Stage makeAddition = new Stage();
        makeAddition.initModality(Modality.APPLICATION_MODAL);
        makeAddition.setTitle("Add New Event to This Course");
        Label timeLbl = new Label("Due Time: ");
        TextField tfTime = new TextField();
        Label despLbl = new Label("Description: ");
        TextArea tfDes = new TextArea();
        Button confirm = new Button("Confirm");

        Memo tMemo = null;
        try {
            tMemo = new Memo(new File(path + (currentEventNumber + 1) + ".memo"), this);
        } catch (IOException e) {
        }
        Memo newMemo = tMemo;
        confirm.setOnAction(e -> {
            newMemo.setDescription(tfDes.getText());
            newMemo.setTime(tfTime.getText());
            memos.add(newMemo);
            try {
                newMemo.save();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            //innerHBox.getChildren().add(newMemo.getLayout());
            makeAddition.close();
            main.updateWindow();
        });

        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(timeLbl, tfTime);

        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(hbox, despLbl, tfDes, confirm);
        vbox.setPadding(new Insets(8, 8, 8, 8));

        makeAddition.setScene(new Scene(vbox, 300, 300));
        makeAddition.setAlwaysOnTop(true);
        makeAddition.show();
    }

    private void courseEditWindow() {
        Stage makeChange = new Stage();
        makeChange.initModality(Modality.APPLICATION_MODAL);
        makeChange.setTitle("Edit This Course");
        Label nameLbl = new Label("Course Name: ");
        TextField tfName = new TextField(name);
        Label prdLbl = new Label("Period: ");
        TextField tfPrd = new TextField(""+period);
        Button confirm = new Button("Confirm");
        Button remove = new Button("Remove");


        remove.setOnAction(e->{
            main.removeCourse(this);
            main.editCourseTrigger = false;
            main.addCourse.setVisible(false);
            main.editCourseTrigger = false;
            main.editCourse.setText("Edit Course...");
            for (Course c: main.courses){
                c.returnNomalcy();
                c.triggered = false;
                c.settingMode.setText("Edit\nEvent");
            }


            makeChange.close();
        });


        confirm.setOnAction(e -> {
            try {
                period = tfPrd.getText().charAt(0);
            }catch(Exception ex){
                period = '-';
            }
            save(tfName.getText());
            name = tfName.getText();
            main.upDateMainFile();
            makeChange.close();
            main.updateWindow();
        });

        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(nameLbl, tfName);
        HBox hbox2 = new HBox(5);
        hbox2.getChildren().addAll(prdLbl, tfPrd);
        HBox hbox3 = new HBox(5);
        hbox3.getChildren().addAll(confirm, remove);
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(hbox, hbox2, hbox3);
        vbox.setPadding(new Insets(8, 8, 8, 8));

        makeChange.setScene(new Scene(vbox, 300, 300));
        makeChange.setAlwaysOnTop(true);
        makeChange.show();
    }



    private void initializeList() throws IOException {
        while (new File(path + currentEventNumber + ".memo").exists()) {
            memos.add(new Memo(new File(path + currentEventNumber + ".memo"), this));
            currentEventNumber++;
        }
        currentEventNumber--;
    }

    protected void returnNomalcy() {
        for (Memo m : memos) {
            m.statusButton.setText(m.getStatus().substring(0, 1)); //emoji: choice
        }
        innerHBox.getChildren().remove(eventAddition);
        settingMode.setText("Edit\nEvent");
    }

    void remove(int seq) {
        memos.get(seq).file.delete();
        for (int i = seq + 1; i < memos.size(); i++) {
            memos.get(i).file.renameTo(memos.get(i - 1).file);
        }
        memos.remove(seq);
        currentEventNumber--;
    }

    public char getPeriod() {
        return period;
    }

    public void setPeriod(char period) {
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Memo> getMemos() {
        return memos;
    }

    public HBox getLayout() {
        ScrollPane sp = new ScrollPane();

        VBox v = new VBox(3);
        v.getChildren().addAll(lbl, settingMode);
        sp.setContent(v);
        sp.setPadding(new Insets(2, 2, 2, 2));
        sp.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(3), new BorderWidths(3))));
        sp.setMinWidth(80);
        sp.setMinHeight(40);
        sp.setMaxWidth(120);
        sp.setMaxHeight(150);
        lbl.setText(this.toString());
        sp.fitToWidthProperty().setValue(true);
        sp.setStyle("-fx-background-color:transparent;");

        //sp.setPickOnBounds(true);
        //v.getChildren().add(sp);
        HBox h = new HBox(8);
        h.getChildren().add(sp);
        ScrollPane sp2 = new ScrollPane();

        HBox tempH = new HBox(8);

        for (Memo m : memos) {
            tempH.getChildren().add(m.getLayout());
        }
        innerHBox = tempH;
        innerHBox.setAlignment(Pos.CENTER_LEFT);

        sp2.setContent(innerHBox);
        sp2.fitToHeightProperty().setValue(true);
        sp2.setStyle("-fx-background-color:transparent;");
        h.getChildren().add(sp2);
        return h;
    }

    void save (String newName){ //use before actually change 'name' (scope) variable
        File tFile = new File (Main.baseString+ "save" + Main.pathSign + name);
        File nFile = new File (Main.baseString+ "save" + Main.pathSign + newName);
        File oldFile = new File (Main.baseString+"save" + Main.pathSign + "Courses" + Main.pathSign + name +".course");
        oldFile.delete();
        File newFile = new File (Main.baseString+"save" + Main.pathSign + "Courses" + Main.pathSign + newName +".course");


        try {
            newFile.createNewFile();
            BufferedWriter bw = new BufferedWriter (new FileWriter(newFile));
            bw.write(period);
            bw.close();
        } catch (IOException e) {
        }





        tFile.renameTo(nFile);
        path = "save" + Main.pathSign + newName + Main.pathSign;
        for (int i = 0; i<memos.size();i++){
            memos.get(i).setFile(new File(path + i + ".memo"));
        }
    }

    @Override
    public String toString() {
        return String.format("%s (%c) \nTo Do:\n%d", name, period == '0' ? ' ' : period, currentEventNumber + 1);
    }
}
