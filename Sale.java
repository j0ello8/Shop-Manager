
package GUIApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author BOUJIQUE
 */
public final class Sale {
    int product_id, unitPrice;
    float quantity, total;
    String buyer, product_name;
    Date dateTime;
    public String removeParanthesis(String str){
       str = str.replace(" ", "");
       str = str.replace("(", ",");
       String sArray[] = str.split(",");
       //System.out.println("sArray[0]: " + sArray[0]);
       return sArray[0];
    }
    public Sale(String cartItem){
        cartItem =  cartItem.replace(" | ", "=");
        String strArray[] = cartItem.split("=");
        this.product_id = Integer.parseInt(strArray[0]);
        this.product_name = strArray[1];
        this.quantity = Float.parseFloat(removeParanthesis(strArray[3]));
        this.unitPrice =  Integer.parseInt(strArray[4]);
        this.total =  Float.parseFloat(removeParanthesis(strArray[5]));
        //System.out.println("total" + this.total);
        //set Date time format. Getting today's date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        Date now = new Date();
        this.dateTime = now;
       // this.dateTime = strArray[5];
    }
}
