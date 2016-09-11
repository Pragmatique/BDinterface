package isilchev.exceptions;

/**
 * Created by User on 28.06.2016.
 */
public class UnsupportedFileTypeExceptionforImport extends Exception {

    public UnsupportedFileTypeExceptionforImport() {}

    //Constructor that accepts a message
    public UnsupportedFileTypeExceptionforImport(String message)
    {
        super(message);
    }

}
