package isilchev.model;

import isilchev.Controller;
import isilchev.exceptions.UnsupportedFileTypeExceptionforImport;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import org.apache.poi.ss.format.CellFormatType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by User on 10.08.2016.
 */
public class ExportFromDB {
    private File folder;
    private final String fileName="DataFromDB.xlsx";
    //private int i=1;
    private Connection conn = null;
    private static double j=0;
    private Date dt1;
    private Date dt2;
    private TextParser tp;


    final String SELECT_ALL_SYUGETS = "SELECT date, channel,start_time,end_time,tittle,description,reach FROM syugets";
    final String SELECT_SYUGETS_DATE = "SELECT * FROM syugets " +
            "WHERE ((date >= ?) AND (date <= ?))";
    final String GET_PERSONS_NAME = "SELECT person_id, person FROM personsID";
    final String GET_PERSONS = "SELECT person_id FROM persons WHERE key_syuget=?";
    final String JOIN_FLATTEN_PERSONS = "SELECT p.person_id,s.date, s.channel,s.start_time,s.end_time,s.tittle,s.description,s.reach " +
            "FROM persons p "+
            "INNER JOIN syugets s "+"ON p.key_syuget=s.key_syuget";
    final String JOIN_FLATTEN_PERSONS_DATEFILTER = "SELECT p.person_id,s.date, s.channel,s.start_time,s.end_time,s.tittle,s.description,s.reach " +
            "FROM persons p "+
            "INNER JOIN syugets s "+"ON p.key_syuget=s.key_syuget "+
            "WHERE ((s.date >= ?) AND (s.date <= ?))";

    Map<Integer, String> personsMap = new HashMap<Integer, String>();
    SimpleDateFormat dateFormatter = new SimpleDateFormat( "dd.MM.yyyy" );


    private PreparedStatement pstmt;
    private PreparedStatement pstmt2;


    ProgressBar progressBar;


    public ExportFromDB(File folder,Connection conn,ProgressBar progressBar,LocalDate ld1,LocalDate ld2,String request) {

        this.folder=folder;
        this.conn=conn;
        this.progressBar=progressBar;
        this.tp=new TextParser(request);

        System.out.println("Start");
        if (ld1!=null){dt1 = dt1.valueOf(ld1);}else {dt1=Date.valueOf("2006-01-01");}
        if (ld2!=null){dt2 = dt2.valueOf(ld2);}else {dt2=Date.valueOf("2036-01-01");}

        try {
            PreparedStatement pstmt1 = conn.prepareStatement(GET_PERSONS_NAME, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs1 = pstmt1.executeQuery();
            while (rs1.next()){
                personsMap.put(new Integer(rs1.getInt(1)),rs1.getString(2));
                System.out.println(new Integer(rs1.getInt(1))+" "+rs1.getString(2));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Finish");

    }

    public ExportFromDB(File folder,Connection conn,ProgressBar progressBar) {
        this.folder=folder;
        this.conn=conn;
        this.progressBar=progressBar;
        System.out.println("Start");
        try {
            PreparedStatement pstmt1 = conn.prepareStatement(GET_PERSONS_NAME,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs1 = pstmt1.executeQuery();
            while (rs1.next()){
                personsMap.put(new Integer(rs1.getInt(1)),rs1.getString(2));
                System.out.println(new Integer(rs1.getInt(1))+" "+rs1.getString(2));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Finish");

    }

    public void exporterAll()throws FileNotFoundException,IOException,SQLException, ParseException,InterruptedException {
        Task taskExport = new Task<Void>() {

            @Override protected Void call() throws SQLException, FileNotFoundException, IOException{

                int i=1;
                XSSFWorkbook myWorkBook = new XSSFWorkbook ();
                CreationHelper createHelper = myWorkBook.getCreationHelper();
                XSSFSheet sheet = myWorkBook.createSheet("dataMassive");
                CellStyle cellStyleDate = myWorkBook.createCellStyle();
                cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));


                pstmt = conn.prepareStatement("SELECT * FROM syugets",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = pstmt.executeQuery();

                rs.last();
                int rowNumber=rs.getRow();
                rs.beforeFirst();
                System.out.println(rowNumber);
                System.out.println(i);
                //Iterator itr = sheet.iterator();
                Double j=0.1;

                if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

                XSSFRow rowFringe = sheet.createRow((short) 0);
                rowFringe.createCell(0).setCellValue("date");
                rowFringe.createCell(1).setCellValue("channel");
                rowFringe.createCell(2).setCellValue("start_time");
                rowFringe.createCell(3).setCellValue("end_time");
                rowFringe.createCell(4).setCellValue("tittle");
                rowFringe.createCell(5).setCellValue("description");
                rowFringe.createCell(6).setCellValue("reach");

                for (int m=7;m<personsMap.size()+7;m++){
                    rowFringe.createCell(m).setCellValue(personsMap.get(m));
                }


                while (rs.next()){
                    System.out.println(i);
                    XSSFRow row = sheet.createRow((short) i);
                    //System.out.println(row.getRowNum());
                    row.createCell(0).setCellValue(rs.getDate(2));row.getCell(0).setCellStyle(cellStyleDate);
                    row.createCell(1).setCellValue(rs.getString(3));
                    row.createCell(2).setCellValue(rs.getString(4));
                    row.createCell(3).setCellValue(rs.getString(5));
                    row.createCell(4).setCellValue(rs.getString(6));
                    row.createCell(5).setCellValue(rs.getString(7));
                    row.createCell(6).setCellValue(rs.getInt(8));

                    pstmt2 = conn.prepareStatement(GET_PERSONS,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                    pstmt2.setInt(1, rs.getInt(1));
                    ResultSet rs2 = pstmt2.executeQuery();
                    while (rs2.next()){
                        row.createCell(rs2.getInt(1)).setCellValue(1);
                    }




                        if ((double)i/rowNumber>j-0.01){
                            updateProgress(j, 1);
                            j=j+0.1;

                            System.out.println(i);
                            System.out.println(rowNumber);
                            System.out.println(row.getRowNum());
                            System.out.println(j);


                        }
                    i++;

                    //cell=row.getCell(1);
                }
                FileOutputStream fileOut = new FileOutputStream(folder.getCanonicalPath()+"\\"+fileName);
                myWorkBook.write(fileOut);
                fileOut.flush();
                fileOut.close();

                return null;
            }


        };
        progressBar.progressProperty().bind(taskExport.progressProperty());
        new Thread(taskExport).start();
        System.out.println(folder.getCanonicalPath()+fileName);
        //FileOutputStream fileOut = new FileOutputStream(folder+fileName);



    }

    public void flattenexporterAll()throws FileNotFoundException,IOException,SQLException, ParseException,InterruptedException {
        Task taskFlattenExport = new Task<Void>() {

            @Override protected Void call() throws SQLException, FileNotFoundException, IOException{

                int i=1;
                XSSFWorkbook myWorkBook = new XSSFWorkbook ();
                CreationHelper createHelper = myWorkBook.getCreationHelper();
                XSSFSheet sheet = myWorkBook.createSheet("dataMassive");
                CellStyle cellStyleDate = myWorkBook.createCellStyle();
                cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));


                pstmt = conn.prepareStatement(JOIN_FLATTEN_PERSONS,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = pstmt.executeQuery();

                rs.last();
                int rowNumber=rs.getRow();
                rs.beforeFirst();
                System.out.println(rowNumber);
                System.out.println(i);
                //Iterator itr = sheet.iterator();
                Double j=0.1;

                if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

                XSSFRow rowFringe = sheet.createRow((short) 0);
                rowFringe.createCell(0).setCellValue("Person");
                rowFringe.createCell(1).setCellValue("Date");
                rowFringe.createCell(2).setCellValue("Channel");
                rowFringe.createCell(3).setCellValue("start_time");
                rowFringe.createCell(4).setCellValue("end_time");
                rowFringe.createCell(5).setCellValue("tittle");
                rowFringe.createCell(6).setCellValue("description");
                rowFringe.createCell(7).setCellValue("reach");


                while (rs.next()){
                    System.out.println(i);
                    XSSFRow row = sheet.createRow((short) i);
                    //System.out.println(row.getRowNum());
                    row.createCell(0).setCellValue(personsMap.get(rs.getInt(1)));
                    row.createCell(1).setCellValue(rs.getDate(2));row.getCell(1).setCellStyle(cellStyleDate);
                    row.createCell(2).setCellValue(rs.getString(3));
                    row.createCell(3).setCellValue(rs.getString(4));
                    row.createCell(4).setCellValue(rs.getString(5));
                    row.createCell(5).setCellValue(rs.getString(6));
                    row.createCell(6).setCellValue(rs.getString(7));
                    row.createCell(7).setCellValue(rs.getInt(8));


                    if ((double)i/rowNumber>j-0.01){

                        updateProgress(j,1.0);
                        j=j+0.1;

                        System.out.println(i);
                        System.out.println(rowNumber);
                        System.out.println(row.getRowNum());
                        System.out.println(Controller.dbl.get());

                    }
                    i++;

                    //cell=row.getCell(1);
                }
                FileOutputStream fileOut = new FileOutputStream(folder.getCanonicalPath()+"\\"+fileName);
                myWorkBook.write(fileOut);
                fileOut.flush();
                fileOut.close();

                return null;
            }


        };
        progressBar.progressProperty().bind(taskFlattenExport.progressProperty());
        new Thread(taskFlattenExport).start();
        System.out.println(folder.getCanonicalPath()+fileName);
        //FileOutputStream fileOut = new FileOutputStream(folder+fileName);



    }

    public void flattenexportWithFilters()throws FileNotFoundException,IOException,SQLException, ParseException,InterruptedException {
        Task taskFlattenExport = new Task<Void>() {

            @Override protected Void call() throws SQLException, FileNotFoundException, IOException{

                int i=1;
                XSSFWorkbook myWorkBook = new XSSFWorkbook ();
                CreationHelper createHelper = myWorkBook.getCreationHelper();
                XSSFSheet sheet = myWorkBook.createSheet("dataMassive");
                CellStyle cellStyleDate = myWorkBook.createCellStyle();
                cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));


                pstmt = conn.prepareStatement(JOIN_FLATTEN_PERSONS_DATEFILTER,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setDate(1,dt1);
                pstmt.setDate(2,dt2);
                ResultSet rs = pstmt.executeQuery();

                rs.last();
                int rowNumber=rs.getRow();
                rs.beforeFirst();
                System.out.println(rowNumber);
                System.out.println(i);
                //Iterator itr = sheet.iterator();
                Double j=0.1;

                if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

                XSSFRow rowFringe = sheet.createRow((short) 0);
                rowFringe.createCell(0).setCellValue("Person");
                rowFringe.createCell(1).setCellValue("Date");
                rowFringe.createCell(2).setCellValue("Channel");
                rowFringe.createCell(3).setCellValue("start_time");
                rowFringe.createCell(4).setCellValue("end_time");
                rowFringe.createCell(5).setCellValue("tittle");
                rowFringe.createCell(6).setCellValue("description");
                rowFringe.createCell(7).setCellValue("reach");


                while (rs.next()){
                    System.out.println(i);
                    XSSFRow row = sheet.createRow((short) i);
                    //System.out.println(row.getRowNum());
                    row.createCell(0).setCellValue(personsMap.get(rs.getInt(1)));
                    row.createCell(1).setCellValue(rs.getDate(2));row.getCell(1).setCellStyle(cellStyleDate);
                    row.createCell(2).setCellValue(rs.getString(3));
                    row.createCell(3).setCellValue(rs.getString(4));
                    row.createCell(4).setCellValue(rs.getString(5));
                    row.createCell(5).setCellValue(rs.getString(6));
                    row.createCell(6).setCellValue(rs.getString(7));
                    row.createCell(7).setCellValue(rs.getInt(8));


                    if ((double)i/rowNumber>j-0.01){

                        updateProgress(j,1.0);
                        j=j+0.1;

                        System.out.println(i);
                        System.out.println(rowNumber);
                        System.out.println(row.getRowNum());
                        System.out.println(Controller.dbl.get());


                    }
                    i++;

                    //cell=row.getCell(1);
                }
                FileOutputStream fileOut = new FileOutputStream(folder.getCanonicalPath()+"\\"+fileName);
                myWorkBook.write(fileOut);
                fileOut.flush();
                fileOut.close();

                return null;
            }


        };
        progressBar.progressProperty().bind(taskFlattenExport.progressProperty());
        new Thread(taskFlattenExport).start();
        System.out.println(folder.getCanonicalPath()+fileName);
        //FileOutputStream fileOut = new FileOutputStream(folder+fileName);



    }

    public void exporterWithFilters()throws FileNotFoundException,IOException,SQLException, ParseException,InterruptedException {
        Task taskExport = new Task<Void>() {

            @Override protected Void call() throws SQLException, FileNotFoundException, IOException{

                int i=1;
                XSSFWorkbook myWorkBook = new XSSFWorkbook ();
                CreationHelper createHelper = myWorkBook.getCreationHelper();
                XSSFSheet sheet = myWorkBook.createSheet("dataMassive");
                CellStyle cellStyleDate = myWorkBook.createCellStyle();
                cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("dd.mm.yyyy"));


                pstmt = conn.prepareStatement(SELECT_SYUGETS_DATE,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                pstmt.setDate(1,dt1);
                pstmt.setDate(2,dt2);
                ResultSet rs = pstmt.executeQuery();

                rs.last();
                int rowNumber=rs.getRow();
                rs.beforeFirst();
                System.out.println(rowNumber);
                System.out.println(i);
                //Iterator itr = sheet.iterator();
                Double j=0.1;

                if (progressBar!=null){System.out.println(progressBar.toString());}else{System.out.println("null");}

                XSSFRow rowFringe = sheet.createRow((short) 0);
                rowFringe.createCell(0).setCellValue("date");
                rowFringe.createCell(1).setCellValue("channel");
                rowFringe.createCell(2).setCellValue("start_time");
                rowFringe.createCell(3).setCellValue("end_time");
                rowFringe.createCell(4).setCellValue("tittle");
                rowFringe.createCell(5).setCellValue("description");
                rowFringe.createCell(6).setCellValue("reach");

                for (int m=7;m<personsMap.size()+7;m++){
                    rowFringe.createCell(m).setCellValue(personsMap.get(m));
                }

                System.out.println(tp.expressions);
                System.out.println(tp.bigexpressions);
                System.out.println(tp.areExpressions());


                if (tp.areExpressions()){
                    while (rs.next()){
                        if (tp.textFinder(rs.getString(7))) {
                            System.out.println(i);
                            System.out.println(tp.textFinder(rs.getString(7)));
                            XSSFRow row = sheet.createRow((short) i);
                            //System.out.println(row.getRowNum());
                            row.createCell(0).setCellValue(rs.getDate(2));
                            row.getCell(0).setCellStyle(cellStyleDate);
                            row.createCell(1).setCellValue(rs.getString(3));
                            row.createCell(2).setCellValue(rs.getString(4));
                            row.createCell(3).setCellValue(rs.getString(5));
                            row.createCell(4).setCellValue(rs.getString(6));
                            row.createCell(5).setCellValue(rs.getString(7));
                            row.createCell(6).setCellValue(rs.getInt(8));

                            pstmt2 = conn.prepareStatement(GET_PERSONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                            pstmt2.setInt(1, rs.getInt(1));
                            ResultSet rs2 = pstmt2.executeQuery();
                            while (rs2.next()) {
                                row.createCell(rs2.getInt(1)).setCellValue(1);
                            }


                            if ((double) i / rowNumber > j - 0.01) {

                                j = j + 0.05;
                                updateProgress(j,1);


                                System.out.println(i);
                                System.out.println(rowNumber);
                                System.out.println(row.getRowNum());


                            }

                        }i++;
                    }

                }else{
                    while (rs.next()){
                        System.out.println(i);
                        System.out.println(tp.textFinder(rs.getString(7)));
                        XSSFRow row = sheet.createRow((short) i);
                        //System.out.println(row.getRowNum());
                        row.createCell(0).setCellValue(rs.getDate(2));
                        row.getCell(0).setCellStyle(cellStyleDate);
                        row.createCell(1).setCellValue(rs.getString(3));
                        row.createCell(2).setCellValue(rs.getString(4));
                        row.createCell(3).setCellValue(rs.getString(5));
                        row.createCell(4).setCellValue(rs.getString(6));
                        row.createCell(5).setCellValue(rs.getString(7));
                        row.createCell(6).setCellValue(rs.getInt(8));

                        pstmt2 = conn.prepareStatement(GET_PERSONS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        pstmt2.setInt(1, rs.getInt(1));
                        ResultSet rs2 = pstmt2.executeQuery();
                        while (rs2.next()) {
                            row.createCell(rs2.getInt(1)).setCellValue(1);
                        }


                        if ((double) i / rowNumber > j - 0.01) {

                            j = j + 0.05;
                            updateProgress(j,1);


                            System.out.println(i);
                            System.out.println(rowNumber);
                            System.out.println(row.getRowNum());



                        }
                        i++;
                    }
                }
                FileOutputStream fileOut = new FileOutputStream(folder.getCanonicalPath()+"\\"+fileName);
                myWorkBook.write(fileOut);
                fileOut.flush();
                fileOut.close();

                return null;
            }


        };
        progressBar.progressProperty().bind(taskExport.progressProperty());
        new Thread(taskExport).start();
        System.out.println(folder.getCanonicalPath()+fileName);
        //FileOutputStream fileOut = new FileOutputStream(folder+fileName);

    }



}
