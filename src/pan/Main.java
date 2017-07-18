package pan;

import javafx.application.Application;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main extends Application {

    private Stage mainWindow;
    //private String[] courseNames;
    public static final String pathSign = getOsPathSign();
    ArrayList<Course> courses = new ArrayList<Course>(6);
    // boolean resolveTriggered = false;
    public static final String baseString = System.getProperty("user.home")+pathSign+"Documents"+pathSign;
    //if you just want to save the file at wherever the jar file is, just change the baseString to "". Or if you want the file else where, just change accordingly.

    @Override
    public void start(Stage primaryStage) throws Exception {
        //basic setup of the main window
        mainWindow = primaryStage;
        mainWindow.setTitle("Your Schedule");

        //check and read file. create if necessary

        System.out.print(System.getProperty("user.dir"));

        File basic = new File(baseString+"save" + Main.pathSign + "basicInfo.pjx");
        if (new File (baseString+"save").exists() == false)
            new File (baseString+"save").mkdirs();
        if (!basic.exists()) {
            basic.getParentFile().mkdirs();
            basic.createNewFile();
        } else {
            //reads and stores names of classes/courses. calls readFileError() when format is wrong
            Scanner scan = new Scanner(basic);
            try {
                upDateMainFile();
            } catch (Exception e) {
                readBasicFileError();
            }
            int counter = 0;
            while (scan.hasNextLine()) {
                String thisCourseName = scan.nextLine();
                //courseNames[counter] = scan.nextLine();
                //System.out.print(courseNames[counter]);
                courses.add(new Course(getPrd(thisCourseName), thisCourseName,this));
                counter++;
            }
        }

        //organize grid layout

        //mainLayout.getChildren().addAll(courses.get(0).getMemos().get(0).getLayout(),courses.get(0).getLayout());
        updateWindow(500, 400);


    }

    private void readBasicFileError() {
        //called when file format is wrong.

        Stage fileFormatError = new Stage();
        VBox v = new VBox(25);
        v.setAlignment(Pos.CENTER);
        v.getChildren().add(new Label("Your Data Has Been Damaged.\nPlease Fix Them and Retry"));
        Button close = new Button("Close");
        close.setOnAction(e -> {
            fileFormatError.close();
            mainWindow.close();
        });
        fileFormatError.setOnCloseRequest(e -> mainWindow.close());
        v.getChildren().add(close);
        fileFormatError.setScene(new Scene(v, 288, 150));
        fileFormatError.show();
        fileFormatError.setAlwaysOnTop(true);
    }

    //returns period if valid info found. else creates a file under appropriate directory and return '0'
    private char getPrd(String course) throws Exception {
        File fii = new File(baseString+ "save" + pathSign + "Courses");
        if (!fii.exists()) fii.mkdirs();
        File fi = new File(baseString+ "save" + pathSign + "Courses" + pathSign + course + ".course");
        if (fi.exists()) {
            Scanner scan = new Scanner(fi);
            try {
                char tmp = scan.next().charAt(0);
                if (tmp >= 'A' && tmp <= 'Z' || tmp == '-')
                    return tmp;
            } catch (Exception e) {
                return '-';
            }
        } else {
            fi.getParentFile().mkdirs();
            fi.createNewFile();
            return '-';
        }
        return '-';
    }

    public static String getOsPathSign() {
        try {
            if (System.getProperty("os.name").startsWith("Windows"))
                return "\\";
            else return "/";
        } catch (Exception e) {
            return "/";
        }
    }

    public static void main(String[] args) {


        launch(args);
    }

    private final byte Vlocation_offset = 0;
    private final byte Hlocation_offset = 0;
    boolean editCourseTrigger = false;
    Button addCourse;
    Button editCourse;

    void updateWindow(){
        updateWindow(mainWindow.getWidth(), mainWindow.getScene().getHeight());
    }

    private void updateWindow(double sizeX, double sizeY) {
        GridPane mainLayout = new GridPane();
        mainLayout.setPadding(new Insets(8, 8, 8, 8));
        mainLayout.setHgap(8);
        mainLayout.setVgap(8);
        for (int i = 0; i < courses.size(); i++) {
            mainLayout.add(courses.get(i).getLayout(), 0 + Hlocation_offset, i + Vlocation_offset);
            /*for (int j = 0 ; j < courses.get(i).getMemos().size(); j++){
                mainLayout.add(courses.get(i).getMemos().get(j).getLayout(),j+1+Hlocation_offset,i+ Vlocation_offset);
            }*/
        }
        editCourse = new Button("Edit Course...");


        addCourse = new Button("Add Course...");


        Button resolveEvents = new Button("Resolve Events...");

        resolveEvents.setOnAction(e -> {
            for (Course c : courses) {
                for (int m = 0; m < c.getMemos().size(); m++) {
                    if(c.getMemos().get(m).check.isSelected()){
                        c.remove (m);
                        m--;
                    }
                }
                if (c.triggered) {
                    c.triggered = false;
                    c.returnNomalcy();
                }
            }
            addCourse.setVisible(false);
            editCourseTrigger = false;
            editCourse.setText("Edit Course...");
            for (Course c: courses){
                ///c.returnNomalcy();
                c.triggered = false;
                c.settingMode.setText("Edit\nEvent");
            }

            updateWindow(mainWindow.getWidth(), mainWindow.getScene().getHeight());
        });

        editCourse.setOnAction(e->{
            if (editCourseTrigger){
                addCourse.setVisible(false);
                editCourseTrigger = false;
                editCourse.setText("Edit Course...");
                for (Course c: courses){
                    c.returnNomalcy();
                    c.triggered = false;
                    c.settingMode.setText("Edit\nEvent");
                }


            }else{
                editCourseTrigger = true;
                editCourse.setText("Choose Course...");
                addCourse.setVisible(true);
                for (Course c: courses){
                    c.returnNomalcy();
                    c.triggered = false;
                    c.settingMode.setText("Edit\nThis");
                }
            }



        });

        addCourse.setOnAction(e -> {

                    Stage addCourseWindow = new Stage ();
                    addCourseWindow.initModality(Modality.APPLICATION_MODAL);
                    addCourseWindow.setTitle("Add A New Course");
                    Label nameLbl = new Label("Course Name: ");
                    TextField tfName = new TextField ();
                    Label prdLbl = new Label("Period: ");
                    TextField tfPrd = new TextField();
                    Button confirm = new Button ("Confirm");
                    confirm.setOnAction(ee->{
                        addCourse(tfName.getText(), tfPrd.getText().charAt(0));
                        addCourseWindow.close();
                    });

                    HBox hbox1  = new HBox(5);
                    hbox1.getChildren().addAll(nameLbl,tfName);
                    HBox hbox2  = new HBox(5);
                    hbox2.getChildren().addAll(prdLbl,tfPrd);

                    VBox vbox = new VBox(5);
                    vbox.getChildren().addAll(hbox1,hbox2,confirm);
                    vbox.setPadding(new Insets(8,8,8,8));
                    addCourseWindow.setScene(new Scene(vbox, 300,300) );
                    addCourseWindow.setAlwaysOnTop(true);
                    addCourseWindow.show();
        }
        );



        HBox h = new HBox(8);
        h.getChildren().addAll(editCourse, resolveEvents,addCourse);
        addCourse.setVisible(false);

        mainLayout.add(h, 0, courses.size() + Vlocation_offset);
        //mainLayout.add(resolveEvents, 1, courses.size()+Vlocation_offset);

        mainWindow.setScene(new Scene(mainLayout, sizeX, sizeY));
        mainWindow.show();

    }

    void upDateMainFile (){ //in accordance with the folders
        File[] files = new File (baseString+ "save").listFiles();
        int size = files.length;
        int counter = 0;
        for (File f : files) {
            if (f.getName().equals("Courses") && !f.getName().equals("basicInfo.pjx") && f.getName().charAt(0) != '.') {
                size--;
            }
        }
        String fnl = "";
        for (File f : files){
            if (!f.getName().equals("Courses")&&!f.getName().equals("basicInfo.pjx")&&f.getName().charAt(0)!='.'){
                fnl+=f.getName();
                counter++;
                    if (counter != size)fnl+="\n";
            }
        }
        File basic = new File(baseString+ "save" + Main.pathSign + "basicInfo.pjx");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(basic));
            bw.write(fnl);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            //System.out.println(children[1]);
            for (int i=0; i<children.length; i++)
                return deleteDir(new File( dir.getPath()+Main.pathSign+children[i]));
        }
        // The directory is now empty or this is a file so delete it
        return dir.delete();
    }

    void removeCourse (Course c){
        File f1 = new File(baseString+ "save" + Main.pathSign + c.getName());
        deleteDir(f1);
        boolean a = f1.delete();
        File f2 = new File(baseString+ "save" + Main.pathSign + "Courses"+ Main.pathSign +c.getName() +".course");
        boolean b = f2.delete();
        System.out.print(a+" "+b);
        courses.remove(c);
        upDateMainFile();
        updateWindow();

    }

    void addCourse  (String name, char period){
        File f1 = new File(baseString+ "save" + Main.pathSign + name);
        f1.mkdirs();
        File f2 = new File(baseString+ "save" + Main.pathSign + "Courses"+ Main.pathSign + name +".course");
        try {
            f2.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(f2));
            bw.write(period);
            bw.close();
            Course c = new Course(period , name, this);
            courses.add(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editCourseTrigger = false;
        addCourse.setVisible(false);
        editCourseTrigger = false;
        editCourse.setText("Edit Course...");
        for (Course c: courses){
            c.returnNomalcy();
            c.triggered = false;
            c.settingMode.setText("Edit\nEvent");
        }
        upDateMainFile();
        updateWindow();

    }
}
