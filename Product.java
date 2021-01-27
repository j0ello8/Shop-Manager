
package GUIApplication;

/**
 *
 * @author BOUJIQUE
 */
public class Product {
    
    int product_id, unit_cost_price, unit_sell_price; 
    float init_quantity_in_scale, rem_quantity;
    String product_name, product_category, scale, supplier, date_entry;
    
    public Product(String productString){
        productString = productString.replace(" | ", "=");
        String strArray[] = productString.split("=");
        //System.out.println("strArray[1]: " + strArray[1]);
        this.product_id = Integer.parseInt(strArray[0]);
        this.product_name = strArray[1];
        this.product_category = strArray[2];
        this.scale = strArray[3];
        this.init_quantity_in_scale = Float.parseFloat(strArray[4]);
        this.rem_quantity = Float.parseFloat(strArray[5]);
        this.unit_cost_price =  Integer.parseInt(strArray[6]);
        this.unit_sell_price =  Integer.parseInt(strArray[7]);
        this.date_entry = strArray[8];
        this.supplier = strArray[9];
        
    }
}
