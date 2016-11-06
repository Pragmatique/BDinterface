package isilchev.model;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

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

    private static final String DATABASE_USER = "user";
    private static final String DATABASE_PASSWORD = "password";
    private static final String MYSQL_AUTO_RECONNECT = "autoReconnect";
    private static final String MYSQL_MAX_RECONNECTS = "maxReconnects";

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

    public void connect() throws ClassNotFoundException,SQLException,CommunicationsException {
        String url = "jdbc:mysql://127.0.0.1:3306/tvbase";
        String user = "root";
        String password = "";

            Class.forName("com.mysql.jdbc.Driver");

            java.util.Properties connProperties = new java.util.Properties();

            // set additional connection properties:
            // if connection stales, then make automatically
            // reconnect; make it alive again;
            // if connection stales, then try for reconnection;
            connProperties.put(DATABASE_USER, user);
            connProperties.put(DATABASE_PASSWORD, password);

            connProperties.put(MYSQL_AUTO_RECONNECT, "true");
            connProperties.put(MYSQL_MAX_RECONNECTS, "4");
            conn = DriverManager.getConnection(url, connProperties);

            //conn = DriverManager.;



        if (conn!=null) setIsconnected(true);

    }


    public boolean isIsconnected() {
        return isconnected;
    }

    public void setIsconnected(boolean isconnected) {
        this.isconnected = isconnected;
    }
}
