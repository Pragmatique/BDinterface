package isilchev;


import com.mysql.jdbc.CommunicationsException;
import isilchev.exceptions.UnsupportedFileTypeExceptionforImport;
import isilchev.model.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jxl.write.WriteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Observable;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    static String directoryforexport = "Unknown directory";
    static String msg ="";
    static boolean isConnectedtoDB = false;
    static String filename;
    Connection conn;
    public static DoubleProperty dbl = new SimpleDoubleProperty(0);
    public double doub;
    static String request;





    @FXML
    Label MyLabel0;

    @FXML
    ScrollPane MyScrollPane0;

    @FXML
    TextFlow MyTextFlow0;

    @FXML
    ProgressBar progressBar;

    @FXML
    private GridPane gridPane;

    @FXML
    DatePicker datePicker1;

    @FXML
    DatePicker datePicker2;

    @FXML
    TextField textField;

    @FXML
    MenuItem exportWithFilters;


    private Task<Void> worker = new Task<Void>() {
        double oldVal = 0.0;
        @Override
        protected Void call() throws Exception {
            System.out.println("Start worker");

            dbl.addListener(new ChangeListener()  {

                /*@Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    dbl.set((Double)newValue);
                    System.out.println("New val"+dbl.get());
                    updateProgress((Double)newValue, 1.0);
                    System.out.println(progressBar.getProgress());

                }*/


                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    System.out.println("New valic"+dbl.get());
                    updateProgress((Double)newValue, 1.0);
                }
            });

            System.out.println(progressBar.getProgress());
            while (oldVal<2.0) {
                if (dbl.get() != oldVal) {

                    System.out.println("New val" + dbl);
                    //progressBar.setProgress(dbl.get());
                    updateProgress(dbl.doubleValue(),1.0);
                    oldVal = dbl.get();
                }
                Thread.sleep(100);
            }

            return null;
        }
    };

    @FXML
    public void progressProperty(){


        Thread th = new Thread() {
            double oldVal = 0.0;

            public void run() {

                try {
                    if (dbl.get() != oldVal) {

                        System.out.println("New val" + dbl.get());
                        progressBar.setProgress(dbl.get());
                        oldVal = dbl.get();
                    }
                    sleep(100);
                }catch (InterruptedException ex){ex.printStackTrace();}


            }

        };

        th.start();
        th.run();

    }


    @Override
    @FXML
    public void initialize(URL url, ResourceBundle resource){
        try {
            ConnToBD connToDB = new ConnToBD();
            connToDB.connect();
            this.conn = connToDB.conn;
            if (progressBar != null) {
                System.out.println("not null");
            } else {
                System.out.println("null");
            }
        /*progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(worker.progressProperty());
        Thread t = new Thread(worker);
        t.setDaemon(true);
        t.start();*/
            if (connToDB.isIsconnected()) {
                MyLabel0.setText("Connected");
                MyLabel0.setStyle("-fx-background-color: #00ff14");
                MyTextFlow0.getChildren().add(new Text("Connected to DB"));
            }
        }catch (Exception ex){ex.printStackTrace(); exceptionPrinter(ex);}

    }

    /*@FXML
    private void exceptionPrinter(Exception e)
    {
        //StackTraceElement[] ste = e.getStackTrace();
        Text t1 = new Text();
        t1.setText("\n"+e.toString());
        t1.setStyle("-fx-fill: RED");

        MyTextFlow0.getChildren().add(t1);
    }*/

    private void exceptionPrinter(Exception e)
    {
        System.out.println("exception");
        Thread th = new Thread(){

            public void run() {
                Text t1 = new Text();
                t1.setText("\n" + e.toString());
                t1.setStyle("-fx-fill: RED");
                MyTextFlow0.getChildren().add(t1);
                System.out.println("exception2");
            }
        };

        //Thread th = new Thread(printer);
        //th.setDaemon(true);
        th.start();
        th.run();
    }


    @FXML
         private void chooseFileForImport(ActionEvent event)
    {
        //String output="";

        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose file");
            File file = fileChooser.showOpenDialog(null);
            filename = file.getCanonicalPath();
        }catch (IOException ex){ex.printStackTrace();}

    }

    @FXML
    private void chooseFolderForExport(ActionEvent event)
    {
        //String output="";

        try {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Choose directory");
            File file = dirChooser.showDialog(null);
            directoryforexport = file.getCanonicalPath();
        }catch (IOException ex){ex.printStackTrace();}

    }

    @FXML
    private void importToDB(ActionEvent event){

        /*Service<Void> myService = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        try {
                            ImportToDB importToDB=new ImportToDB(new File(filename),conn);
                            importToDB.setProgressUpdate((workDone, totalWork) ->
                                    updateProgress(workDone, totalWork));

                            importToDB.xlsxBrowser();

                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }

                        return null;
                    }
                };
            }
        };*/

        try {
            ImportToDB importToDB=new ImportToDB(new File(filename),conn,progressBar);
            importToDB.xlsxBrowser();


        } catch (UnsupportedFileTypeExceptionforImport unsupportedFileTypeExceptionforImport) {
            unsupportedFileTypeExceptionforImport.printStackTrace();exceptionPrinter(unsupportedFileTypeExceptionforImport);
        } catch (SQLException e) {
            e.printStackTrace();
            StackTraceElement[] ste = e.getStackTrace();
            Text t1 = new Text();
            t1.setText(ste[0].toString());
            t1.setStyle("-fx-fill: RED");
            //MyTextArea0.appendText(t1.toString());

        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();exceptionPrinter(fileNotFoundException);
        }catch (IOException ex) {
            ex.printStackTrace();exceptionPrinter(ex);
        }catch (ParseException parseException) {
            parseException.printStackTrace();exceptionPrinter(parseException);
        }catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();exceptionPrinter(interruptedException);
        }




    }

    @FXML
    private void exportFromDB(ActionEvent event){

        try {
            ExportFromDB exportFromDB=new ExportFromDB(new File(directoryforexport),conn,progressBar);
            exportFromDB.exporterAll();
            /*dbl.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    dbl.set((Double)newValue);
                    System.out.println("New val"+dbl.get());

                }
            });*/

            //progressProperty();

        }catch (SQLException e) {
            e.printStackTrace();exceptionPrinter(e);
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();exceptionPrinter(fileNotFoundException);
        }catch (IOException ex) {
            ex.printStackTrace();exceptionPrinter(ex);
        }catch (ParseException parseException) {
            parseException.printStackTrace();exceptionPrinter(parseException);
        }catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();exceptionPrinter(interruptedException);
        }

    }

    @FXML
    private void exportFromDBWithFilters(ActionEvent event){


                request = textField.getText();
                System.out.println("Request "+request);


        try {
            ExportFromDB exportFromDB=new ExportFromDB(new File(directoryforexport),conn,progressBar,
                    datePicker1.getValue(),datePicker2.getValue(),request);
            exportFromDB.exporterWithFilters();
            /*dbl.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    dbl.set((Double)newValue);
                    System.out.println("New val"+dbl.get());

                }
            });*/

        }catch (SQLException e) {
            e.printStackTrace();exceptionPrinter(e);
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();exceptionPrinter(fileNotFoundException);
        }catch (IOException ex) {
            ex.printStackTrace();exceptionPrinter(ex);
        }catch (ParseException parseException) {
            parseException.printStackTrace();exceptionPrinter(parseException);
        }catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();exceptionPrinter(interruptedException);
        }


    }

    @FXML
    private void flattenexportFromDBAll(ActionEvent event){

        try {

            ExportFromDB exportFromDB=new ExportFromDB(new File(directoryforexport),conn,progressBar);
            exportFromDB.flattenexporterAll();


        }catch (SQLException e) {
            e.printStackTrace();exceptionPrinter(e);
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();exceptionPrinter(fileNotFoundException);
        }catch (IOException ex) {
            ex.printStackTrace();exceptionPrinter(ex);
        }catch (ParseException parseException) {
            parseException.printStackTrace();exceptionPrinter(parseException);
        }catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();exceptionPrinter(interruptedException);
        }






    }

    @FXML
    private void flattenexportWithFilters(ActionEvent event){

        request = textField.getText();
        System.out.println("Request "+request);


        try {

            ExportFromDB exportFromDB=new ExportFromDB(new File(directoryforexport),conn,progressBar,
                    datePicker1.getValue(),datePicker2.getValue(),request);
            exportFromDB.flattenexportWithFilters();


        }catch (SQLException e) {
            e.printStackTrace();exceptionPrinter(e);
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();exceptionPrinter(fileNotFoundException);
        }catch (IOException ex) {
            ex.printStackTrace();exceptionPrinter(ex);
        }catch (ParseException parseException) {
            parseException.printStackTrace();exceptionPrinter(parseException);
        }catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();exceptionPrinter(interruptedException);
        }


    }



}
