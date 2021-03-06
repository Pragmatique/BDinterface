package isilchev.model;

import isilchev.exceptions.UnsupportedFileTypeExceptionforImport;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.function.BiConsumer;

/**
 * Created by User on 11.08.2016.
 */
public class ImbordToDB {
    private File file;
    private int n;
    private Connection conn = null;
    private static double j=0;
    private Task copyWorker;

    private BiConsumer<Integer, Integer> progressUpdate ;

    public void setProgressUpdate(BiConsumer<Integer, Integer> progressUpdate) {
        this.progressUpdate = progressUpdate ;
    }

    final private double incremental(){
        j=j+0.1;
        return j;
    }


    final String INSERT_SYUGETS = "INSERT INTO syugets (date, channel,start_time,end_time,tittle,description,reach)" +
            " VALUES (?,?,?,?,?,?,?)";
    final String INSERT_PERSONS = "INSERT INTO persons (key_syuget, person_id)" +
            " VALUES (?,?)";
    SimpleDateFormat dateFormatter = new SimpleDateFormat( "dd.mm.yyyy" );
    private PreparedStatement pstmt;


    ProgressBar progressBar;

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws InterruptedException{
                for (double j = 0.0; j < 1.1; j=j+0.1) {
                    Thread.sleep(50);
                    updateProgress(j, 1);
                    System.out.println(progressBar.getProgress());
                }
                return true;
            }
        };
    }

    public ImbordToDB(File file, Connection conn,ProgressBar progressBar)throws UnsupportedFileTypeExceptionforImport,SQLException {
        try {
            if ((file.getCanonicalPath().endsWith("xls"))||(file.getCanonicalPath().endsWith("xlsx"))){
                this.file=file; this.conn=conn;this.progressBar=progressBar;} else throw new UnsupportedFileTypeExceptionforImport();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private java.sql.Date dateParser(XSSFCell cell)throws ParseException{
        java.sql.Date date;

        try{
            date = new java.sql.Date(cell.getDateCellValue().getTime());
        }catch (IllegalStateException illegalStateException){
            date = new java.sql.Date(dateFormatter.parse(cell.getStringCellValue()).getTime());
        }
        return date;

    }


    public void xlsxBrowser()throws FileNotFoundException,IOException,SQLException, ParseException,InterruptedException {

                FileInputStream fis = new FileInputStream(file);
                XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
                XSSFSheet sheet = myWorkBook.getSheetAt(0);
                XSSFRow row;
                XSSFCell cell;
                int rowNumber=sheet.getLastRowNum()+1;
                Iterator itr = sheet.iterator();
                Double j=0.1;
                if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

                conn.setAutoCommit(false);

                while (itr.hasNext()){
                    row=(XSSFRow) itr.next();
                    System.out.println(row.getRowNum());
                    //System.out.println(row.getRowNum());
                    if (row.getRowNum()==0){continue;}
                    else {
                        PreparedStatement pstmt = conn.prepareStatement(INSERT_SYUGETS, java.sql.Statement.RETURN_GENERATED_KEYS);

                        //pstmt.setDate(2, (java.sql.Date) row.getCell(0).getDateCellValue());
                        System.out.println(dateParser(row.getCell(0)));

                        pstmt.setDate(1, dateParser(row.getCell(0)));
                        //System.out.println((java.sql.Date)dateFormatter.parse(row.getCell(0).getStringCellValue()));
                        //pstmt.setDate(1, Date.valueOf(row.getCell(0).getStringCellValue()));
                        //System.out.println(Date.valueOf(row.getCell(0).getStringCellValue()));
                        pstmt.setString(2, row.getCell(1).getStringCellValue());
                        System.out.println(row.getCell(1).getStringCellValue());

                        //pstmt.setTime(4, new Time((long) row.getCell(2).getNumericCellValue()));
                        //pstmt.setTime(5, new Time((long) row.getCell(3).getNumericCellValue()));
                        pstmt.setTime(3, Time.valueOf(row.getCell(2).getStringCellValue()));
                        System.out.println(Time.valueOf(row.getCell(2).getStringCellValue()));
                        pstmt.setTime(4, Time.valueOf(row.getCell(3).getStringCellValue()));
                        System.out.println(Time.valueOf(row.getCell(3).getStringCellValue()));
                        pstmt.setString(5, row.getCell(4).getStringCellValue());
                        System.out.println(row.getCell(4).getStringCellValue());
                        pstmt.setString(6, row.getCell(5).getStringCellValue());
                        System.out.println(row.getCell(5).getStringCellValue());
                        pstmt.setInt(7, (int) row.getCell(69).getNumericCellValue());
                        System.out.println(row.getCell(69).getNumericCellValue());
                        //n=pstmt.executeUpdate();
                        if (pstmt!=null){System.out.println("pstmt not null");}else{System.out.println("pstmt null");}
                        pstmt.executeUpdate();
                        System.out.println("ok1");

                        ResultSet generatedKeys = pstmt.getGeneratedKeys();
                        generatedKeys.next();
                        n=generatedKeys.getInt(1);
                        System.out.println("ok2");


                        for (int i=7;i<70;i++){
                            if ((row.getCell(i-1)!=null)){
                                System.out.println(i);
                                PreparedStatement pstmtperson = conn.prepareStatement(INSERT_PERSONS, java.sql.Statement.RETURN_GENERATED_KEYS);
                                pstmtperson.setInt(1,n);
                                pstmtperson.setInt(2,i);
                                pstmtperson.executeUpdate();
                            }

                        }
                        //System.out.println((double)row.getRowNum()/rowNumber);


                        if (((double)row.getRowNum()/rowNumber)>j-0.01){
                            //updateProgress(j, 1);
                            j=j+0.1;

                            System.out.println(rowNumber);
                            System.out.println(row.getRowNum());
                            System.out.println(j);


                        }


                    }
                    //cell=row.getCell(1);
                }
                conn.setAutoCommit(true);






        /*FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
        XSSFSheet sheet = myWorkBook.getSheetAt(0);
        XSSFRow row;
        XSSFCell cell;
        int rowNumber=sheet.getLastRowNum()+1;
        Iterator itr = sheet.iterator();
        Double j=0.1;
        if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

        new Thread(copyWorker).start();

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());
        while (itr.hasNext()){
            row=(XSSFRow) itr.next();
            //System.out.println(row.getRowNum());
            if (row.getRowNum()==0){continue;}
            else {
                PreparedStatement pstmt = conn.prepareStatement(INSERT_SYUGETS, java.sql.Statement.RETURN_GENERATED_KEYS);

                //pstmt.setDate(2, (java.sql.Date) row.getCell(0).getDateCellValue());

                pstmt.setDate(1, new Date(dateFormatter.parse(row.getCell(0).getStringCellValue()).getTime()));
                //System.out.println(new Date(dateFormatter.parse(row.getCell(0).getStringCellValue()).getTime()));
                pstmt.setString(2, row.getCell(1).getStringCellValue());
                //System.out.println(row.getCell(1).getStringCellValue());

                //pstmt.setTime(4, new Time((long) row.getCell(2).getNumericCellValue()));
                //pstmt.setTime(5, new Time((long) row.getCell(3).getNumericCellValue()));
                pstmt.setTime(3, Time.valueOf(row.getCell(2).getStringCellValue()));
                pstmt.setTime(4, Time.valueOf(row.getCell(3).getStringCellValue()));
                pstmt.setString(5, row.getCell(4).getStringCellValue());
                pstmt.setString(6, row.getCell(5).getStringCellValue());
                pstmt.setInt(7, (int) row.getCell(69).getNumericCellValue());
                //n=pstmt.executeUpdate();
                pstmt.executeUpdate();

                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                generatedKeys.next();
                n=generatedKeys.getInt(1);
                //System.out.println(n);


                for (int i=7;i<70;i++){
                    if ((row.getCell(i-1)!=null)){
                        PreparedStatement pstmtperson = conn.prepareStatement(INSERT_PERSONS, java.sql.Statement.RETURN_GENERATED_KEYS);
                        pstmtperson.setInt(1,n);
                        pstmtperson.setInt(2,i);
                        pstmtperson.executeUpdate();
                    }

                }
                //System.out.println((double)row.getRowNum()/rowNumber);


                if (((double)row.getRowNum()/rowNumber)>=j){




                    System.out.println(rowNumber);
                    System.out.println(row.getRowNum());
                    System.out.println(j);

                    j=j+0.1;
                }






            }
            //cell=row.getCell(1);
        } */


    }

}
