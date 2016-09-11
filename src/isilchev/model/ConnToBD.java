package isilchev.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by User on 27.06.2016.
 */
public class ConnToBD {
    public Logger logger = Logger.getLogger(Logger.class.getName());
    public Connection conn = null;
    private boolean isconnected=false;
    final String INSERT_SYUGETS = "INSERT INTO syugets (key_syuget, date, channel,start_time,end_time,tittle,description,reach)" +
            " VALUES (?,?,?,?,?,?,?,?)";

    public ConnToBD(){
        Logger log = Logger.getLogger(ConnToBD.class.getName());
        /*try {
            FileHandler fh = new FileHandler("%tLogApp");
            logger.addHandler(fh);

        } catch (SecurityException e) {
            logger.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за политики безопасности.",
                    e);
        } catch (IOException e) {
            logger.log(Level.SEVERE,
                    "Не удалось создать файл лога из-за ошибки ввода-вывода.",
                    e);
        }*/

        //logger.log(Level.INFO, "Запись лога с уровнем INFO (информационная)");
        //logger.log(Level.WARNING,"Запись лога с уровнем WARNING (Предупреждение)");
        //logger.log(Level.SEVERE, "Запись лога с уровнем SEVERE (серъёзная ошибка)");

    }

    public void connect() {
        String url = "jdbc:mysql://127.0.0.1:3306/tvbase";
        String user = "root";
        String password = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            //conn = DriverManager.;

        }catch (ClassNotFoundException ex) {ex.printStackTrace();}
        catch (SQLException ex) { System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());}
        if (conn!=null) setIsconnected(true);

    }


    public boolean isIsconnected() {
        return isconnected;
    }

    public void setIsconnected(boolean isconnected) {
        this.isconnected = isconnected;
    }
}
