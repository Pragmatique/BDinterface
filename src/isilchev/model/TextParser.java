package isilchev.model;

import com.mysql.jdbc.StringUtils;

/**
 * Created by User on 28.08.2016.
 */
public class TextParser {
    private String request;
    public String [] expressions;
    public String [] bigexpressions;

    public TextParser(String request){
        this.request=request;
        request=request.trim();

        String [] blocks = request.split("-");
        for (String s:blocks){
            System.out.println(s);
        }

        try {
            if (blocks[0] != null) {
                expressions = blocks[0].toLowerCase().trim().split("[\\\\,\\\\;\\\\.]");
                for (String s : expressions) {
                    System.out.println(s);
                }
            } else {
                System.out.println("blocks0-null");
            }
            if (blocks[1] != null) {
                bigexpressions = blocks[1].toLowerCase().trim().split("[\\\\,\\\\;\\\\.]");
                for (String s : bigexpressions) {
                    System.out.println(s);
                }
            } else {
                System.out.println("blocks1-null");
            }
        }catch (IndexOutOfBoundsException ex){ex.printStackTrace();}
    }



    public boolean textFinder(String text){
        boolean bool = false;
        text=text.toLowerCase();
        if (bigexpressions!=null){
            for (String s:bigexpressions ){
                text.replaceAll(s.toLowerCase(), "абабагаламага");
            }
        }

        if (expressions!=null){
            for (String s:expressions ) {
                if (text.indexOf(s.toLowerCase()) > -1) {
                    bool = true;
                    return bool;
                }
            }
        }

        return bool;
    }

    public String detailedTextFinder(String text){
        String output = "";
        String[] abzaces=text.trim().split("\n");
        for (String abzac:abzaces){
            if (textFinder(text)==true)output=output+"\n"+abzac;
        }
        return output;

    }

    public boolean areExpressions(){
        if (expressions!=null){return true;}else{return false;}
    }


}
