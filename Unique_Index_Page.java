
package GUIApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author BOUJIQUE
 */
public class Unique_Index_Page extends javax.swing.JFrame {
    
    //Declaring connection variables. 
    static Connection myConn = null;
    static Statement myStat = null;
    static ResultSet myRs = null;
    float cartTotal = 0;
    String role;
    
    //Creating a listModel for the JLists.
    static DefaultListModel<String> listModel1 = new DefaultListModel<>();
    static DefaultListModel<String> listModel2 = new DefaultListModel<>();
    static DefaultListModel<String> listModel3 = new DefaultListModel<>();
    
    //Initialising a new Table Model.
    DefaultTableModel tableModel1 = new DefaultTableModel();
    DefaultTableModel tableModel2 = new DefaultTableModel();
    DefaultTableModel tableModel3 = new DefaultTableModel();
    DefaultTableModel tableModel4 = new DefaultTableModel();
    DefaultTableModel tableModel5 = new DefaultTableModel();
        
    //Function to print a product list the screen. 
    public void displayProducts(DefaultListModel listModel, Product P,  float quantity, int unitPrice, float total){
        String output;
                output = "";
                output = output + String.valueOf(P.product_id) + " | " + P.product_name + " | "
                    + P.product_category + " | " + String.valueOf(quantity) + "( " + P.scale + " )" + " | "
                    + String.valueOf(unitPrice) + " | " + String.valueOf(total) + "(Total amount)";
                  
                listModel.addElement(output);   // Add a string of product details to the list Model. 
    }
    // Function to clear textFields.
    public void clearTextFields(){
        pnameField.setText("");
        pcategoryField.setText("");
        //scaleField.setText("");
        initQuanField.setText("");
        dateEntryField.setText("");
        unitCostField.setText("");
        unitSellField.setText("");
        supplierField.setText("");
    }
    //Function to read the selected product from the jtable. 
    public String getSelectedProduct(String table){
        String selectedProduct = "";
        if(table.equals("table1")){
           int row = table1.getSelectedRow();
           for(int column = 0; column<10; column++){
            selectedProduct = selectedProduct + table1.getModel().getValueAt(row, column).toString() + " | ";
            }
        }
        else if(table.equals("table2")){
            int row = table2.getSelectedRow();
            for(int column = 0; column<10; column++){
            selectedProduct = selectedProduct + table2.getModel().getValueAt(row, column).toString() + " | ";
            }
        }
        //System.out.println("Selected Product: " + selectedProduct);
        return selectedProduct;
    }
    //Function to create a new sale
    public void createSale(String selectedSale){
        Sale sale = new Sale(selectedSale);
        float rem_quantity = 0;
        try{
        String sqlQuery = " INSERT INTO sales(dateTime, product_id, quantity, unitPrice, total)"
                + " VALUES(?,?,?,?,?)";
        PreparedStatement prepStmt = NamedPreparedStatement.prepareStatement(myConn, sqlQuery);
        //inserting sale data into the sales table.
        prepStmt.setString(1, sale.dateTime.toString());
        prepStmt.setString(2, String.valueOf(sale.product_id));
        prepStmt.setString(3, String.valueOf(sale.quantity));
        prepStmt.setString(4, String.valueOf(sale.unitPrice));
        prepStmt.setString(5, String.valueOf(sale.total));
        //prepStmt.setString(6, String.valueOf(sale.buyer));
        prepStmt.executeUpdate();
        prepStmt.close();
        }catch(Exception e){
           e.printStackTrace();
        } 
        //UPdating the product table.  
        try{
        myRs = myStat.executeQuery("SELECT rem_quantity FROM products WHERE product_id = " + sale.product_id);
        while(myRs.next()){
            rem_quantity = myRs.getFloat("rem_quantity");
        }
        String sqlQuery = "UPDATE products SET rem_quantity = ? WHERE product_id = ?";
        PreparedStatement prepStmt = NamedPreparedStatement.prepareStatement(myConn, sqlQuery);
        rem_quantity = rem_quantity - sale.quantity;
        prepStmt.setFloat(1, rem_quantity);
        prepStmt.setInt(2, sale.product_id);
        
        prepStmt.executeUpdate();
        prepStmt.close();
        }catch(Exception e){
           e.printStackTrace();
        } 
        //return sale.total;
    }
    
    public Unique_Index_Page(){
        initComponents();
    }
    
    //Function to print product content to the jTable.
    public void printProducts(String table, String query){
        DefaultTableModel tableModel = new DefaultTableModel();
        //clearing the jTable before writing new content to it. 
        DefaultTableModel dm = (DefaultTableModel)table1.getModel();
        dm.getDataVector().removeAllElements();
        dm.fireTableDataChanged(); // notifies the jTable that the data has changed.
        
        DefaultTableModel dm1 = (DefaultTableModel)table2.getModel();
        dm1.getDataVector().removeAllElements();
        dm1.fireTableDataChanged(); // notifies the jTable that the data has changed.
        
        float count = 0, quantity;
        int unitPrice;
        
        tableModel.setColumnIdentifiers(new Object[]{"ID","Name","Category","scale","Initial Quantity","Remaining Quantity",
        "Cost Price","Selling Price","Date_entry","supplier"});
        
        Object[] row = new Object[10];
        try {
            // Read product Info from database.
            myRs = myStat.executeQuery(query);
            //Populating the row and column data into the table. 
            while(myRs.next()){
                 row[0] = myRs.getInt("product_id"); 
                 row[1] = myRs.getString("product_name");
                 row[2] = myRs.getString("product_category");
                 row[3] = myRs.getString("scale");
                 row[4] = myRs.getFloat("init_quantity_in_scale");
                 quantity = myRs.getFloat("init_quantity_in_scale");
                 row[5] = myRs.getFloat("rem_quantity");
                 row[6] = myRs.getInt("unit_cost_price");
                 unitPrice = myRs.getInt("unit_cost_price");
                 count = count + (quantity * unitPrice);
                 row[7] = myRs.getInt("unit_sell_price");
                 row[8] = myRs.getString("date_entry");
                 row[9] = myRs.getString("supplier");
                 
                 tableModel.addRow(row);
             }
           } catch (SQLException ex) {
            Logger.getLogger(Unique_Index_Page.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(table.equals("table1")){
            table1.setModel(tableModel);
        }
        else if(table.equals("table2")){
            table2.setModel(tableModel);
        }
    }    
     
    //Function to search product items from database.
    public void findProduct(String table, String criteria){
        String query = "SELECT * FROM products WHERE product_name LIKE \"%" + criteria + "%\" OR product_category LIKE \"%" + criteria + "%\"";
        printProducts("table1", query);
    }
       
    // Function to print sales record on the screen. 
    public DefaultTableModel printSales(String period, String criteria){
        float salesTotal = 0;
        DefaultTableModel tableModel = new DefaultTableModel();
        String query = "";
        if(criteria.equals("") ){
            if(period.contains(",")){
            String date[] = period.split(",");
            //System.out.println("dates: "+ date[0]+ " " + date[1] + " "+ date[2]);
                query = "SELECT sales.sales_id, sales.dateTime, products.product_name,"
            + " products.product_category, sales.quantity, products.scale, sales.unitPrice, sales.total,"
            + " sales.buyer FROM sales, products WHERE sales.product_id = products.product_id "
            + "AND sales.dateTime LIKE \"%" + date[0] + "%\" OR sales.dateTime LIKE \"%" + date[1] + "%\""
            + "OR sales.dateTime LIKE \"%" + date[2] + "%\" OR sales.dateTime LIKE \"%" + date[3] + "%\" "
            + "OR sales.dateTime LIKE \"%" + date[4] + "%\" OR sales.dateTime LIKE \"%" + date[5] + "%\" "
            + "OR sales.dateTime LIKE \"%" + date[6] + "%\" ";
            }
            else{
               query = "SELECT sales.sales_id, sales.dateTime, products.product_name,"
            + " products.product_category, sales.quantity, products.scale, sales.unitPrice, sales.total,"
            + " sales.buyer FROM sales, products WHERE sales.product_id = products.product_id "
            + "AND sales.dateTime LIKE \"%" + period + "%\" "; 
            }
        }else{
            
            query = "SELECT sales.sales_id, sales.dateTime, products.product_name,"
            + " products.product_category, sales.quantity, products.scale, sales.unitPrice, sales.total,"
            + " sales.buyer FROM sales, products WHERE sales.product_id = products.product_id "
            + "AND sales.dateTime LIKE \"%" + period + "%\" AND products.product_name LIKE \"%" + criteria + "%\"";
        }
        
        tableModel.setColumnIdentifiers(new Object[]{"Sale_id","dateTime","product_name","quantity_sold","scale","unit_Price","total", "buyer"});
        Object[] row = new Object[8];
        try {
            myRs = myStat.executeQuery(query);  // Read sales info Info from database.
            //Populating the row and column data into the table. 
            while(myRs.next()){
                 row[0] = myRs.getInt("sales_id"); 
                 row[1] = myRs.getString("dateTime");
                 row[2] = myRs.getString("product_name");
                 row[3] = myRs.getFloat("quantity");
                 row[4] = myRs.getString("scale");
                 row[5] = myRs.getInt("unitPrice");
                 row[6] = myRs.getFloat("total");
                 salesTotal = salesTotal + myRs.getFloat("total");
                 row[7] = myRs.getString("buyer");
                 
                 tableModel.addRow(row);
             }
           } catch (SQLException ex) {
            Logger.getLogger(Unique_Index_Page.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(period.equals(getDate("today"))){
            totalSalesField.setText(String.valueOf(salesTotal));
        }
        //else if(period.equals(getWeek("this week"))){
            //totalSalesField1.setText(String.valueOf(salesTotal));
        //}
        else if(period.equals(getDate("this month"))){
            totalSalesField2.setText(String.valueOf(salesTotal));
        }
        else if(period.equals(getDate("this year"))){
            totalSalesField3.setText(String.valueOf(salesTotal));
        }
        
        return tableModel;
    }
    //function to compute the weekly dates.
    public String getWeek(String todayString){
        
        String weekdays[] = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
        String month[] = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

        String dateComponents[] = todayString.split(" ");
        String dayOfWeek = dateComponents[0];
        String monthString = dateComponents[1];
        String output = "";
        int dayNumber = Integer.parseInt(dateComponents[2]);
        ArrayList<Integer> dayNumbers = new ArrayList();
        ArrayList<String> months = new ArrayList();
        int tmpday = dayNumber;
        int monthNumber = 0;
        int dayOfWeekNumber= 0;
        
        for(int i = 0; i<7; i++){
            if(dayOfWeek.equals(weekdays[i])){
                dayOfWeekNumber = i;
            }
        }
        int tmp1 = dayOfWeekNumber;
        for(int i = 0; i<12; i++){
            if(monthString.equals(month[i])){
                monthNumber = i;
            }
        }
        
       String monthsWith30Days = "Sep" + "Apr"+ "Jun"+"Nov";
        int monthEnd = 0;
        if(monthsWith30Days.contains(monthString)){
             monthEnd = 30;
        }
        else if(monthString.equals("Feb")){
             monthEnd = 28;
        }else {
            monthEnd = 31;
        }
        
        String tmpMonthString = monthString;
        while(dayOfWeekNumber<=6){
            dayNumbers.add(dayNumber);
            dayOfWeekNumber++;
            months.add(monthString);
            dayNumber++;
            if(dayNumber>monthEnd){
               monthString = month[monthNumber + 1];
               dayNumber = 1;
            }  
        }
        dayNumber = tmpday-1;
        dayOfWeekNumber = tmp1;
        monthString = tmpMonthString;
        while(dayOfWeekNumber>0){
            if(dayNumber<1){
               monthString = month[monthNumber - 1];
               dayNumber = monthEnd;
            }  
           dayNumbers.add(dayNumber);
           months.add(monthString);
           dayNumber--;
           dayOfWeekNumber--;    
       }
        
        for(int i = 0; i<7; i++){
          output = output + months.get(i) + " " + dayNumbers.get(i) + ", ";
        }
        return output;
    }
    
    //function to display sales on the various tables. 
    public void displaySales(String criteria){
        String today = getDate("today");
        String thisWeek = getWeek(today);
        System.out.println("This Week: " + thisWeek);
        String thisMonth = getDate("this month");
        String thisYear = getDate("this year");
        
        //Display today's sales.
        tableModel2 = printSales(today,criteria);
        salesTable1.setModel(tableModel2);
        
        //Display this month's sales.
        tableModel4 = printSales(thisMonth,criteria);
        salesTable3.setModel(tableModel4);
        
        //Display this years's sales.
        tableModel5 = printSales(thisYear,criteria);
        salesTable4.setModel(tableModel5);
        
        //Display this week's sales.
        tableModel3 = printSales(thisWeek,criteria);
        salesTable2.setModel(tableModel3);
    }
    
    //function to extract the date of today 
    public String getDate(String moment){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
        Date now = new Date();
        String dateString = now.toString();
        //System.out.println("now: "  + dateString);
        String date[] = dateString.split(" ");
        String today = date[0] + " " + date[1] + " " + date[2];
        if(moment.equals("today")){
            return today;
        }
        else if(moment.equals("this month")){
          return date[1];  
        }
        else if(moment.equals("this year")){
          return date[5];
        }
        else return null;
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signupFrame = new javax.swing.JFrame();
        jPanel5 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        phoneField = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cancelSignuplbtn = new javax.swing.JButton();
        signupbtn2 = new javax.swing.JButton();
        emailField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        confirmPassField = new javax.swing.JPasswordField();
        jLabel19 = new javax.swing.JLabel();
        homePageFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        productsbtn = new javax.swing.JButton();
        customersbtn = new javax.swing.JButton();
        salesbtn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Logoutbtn = new javax.swing.JButton();
        Statisticsbtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        sellItemFrame = new javax.swing.JFrame();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        Logoutbtn2 = new javax.swing.JButton();
        Backbtn3 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        printReceiptbtn = new javax.swing.JButton();
        confirmSalebtn = new javax.swing.JButton();
        removeCartbtn = new javax.swing.JButton();
        clearbtn = new javax.swing.JButton();
        jLabel59 = new javax.swing.JLabel();
        cartTotalField = new javax.swing.JTextField();
        productFrame = new javax.swing.JFrame();
        jPanel7 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        removeProductbtn = new javax.swing.JButton();
        updateProductbtn2 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        Logoutbtn1 = new javax.swing.JButton();
        Accountbtn1 = new javax.swing.JButton();
        Cartbtn = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        FindProductbtn = new javax.swing.JButton();
        refreshProductbtn = new javax.swing.JButton();
        findProductField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        sellItembtn = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        addProductFrame = new javax.swing.JFrame();
        jPanel11 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        pnameField = new javax.swing.JTextField();
        pcategoryField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        initQuanField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        dateEntryField = new javax.swing.JTextField();
        addProductbtn = new javax.swing.JButton();
        addProductCancelbtn = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        unitCostField = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        unitSellField = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        supplierField = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        scaleCombo1 = new javax.swing.JComboBox<>();
        addItemFrame = new javax.swing.JFrame();
        jPanel12 = new javax.swing.JPanel();
        qtyField = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        unitPriceField = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        displayScaleField = new javax.swing.JTextField();
        displayScaleField1 = new javax.swing.JTextField();
        itemDetailsCancelbtn = new javax.swing.JButton();
        addtoCartbtn = new javax.swing.JButton();
        selectedProductField = new javax.swing.JTextField();
        makeSalebtn = new javax.swing.JButton();
        salesFrame = new javax.swing.JFrame();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        salesTable1 = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        totalSalesField = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        salesTable2 = new javax.swing.JTable();
        jLabel61 = new javax.swing.JLabel();
        totalSalesField1 = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        salesTable3 = new javax.swing.JTable();
        jLabel62 = new javax.swing.JLabel();
        totalSalesField2 = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        salesTable4 = new javax.swing.JTable();
        jLabel63 = new javax.swing.JLabel();
        totalSalesField3 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        salesSearchField = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        Accountbtn2 = new javax.swing.JButton();
        salesRefreshbtn = new javax.swing.JButton();
        salesFindbtn = new javax.swing.JButton();
        updateProductFrame = new javax.swing.JFrame();
        jPanel13 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        productIDField = new javax.swing.JTextField();
        pCategoryField1 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        scaleField1 = new javax.swing.JTextField();
        initQuanField1 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        dateEntryField1 = new javax.swing.JTextField();
        updateProductbtn = new javax.swing.JButton();
        updateProductCancelbtn = new javax.swing.JButton();
        jLabel47 = new javax.swing.JLabel();
        unitCostField1 = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        unitSellField1 = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        supplierField1 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        remQuantityField = new javax.swing.JTextField();
        pNameField1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        buyerNameDialog = new javax.swing.JDialog();
        jPanel18 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        buyerNameField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        productFrameUser = new javax.swing.JFrame();
        jPanel19 = new javax.swing.JPanel();
        jPanel20 = new javax.swing.JPanel();
        jLabel64 = new javax.swing.JLabel();
        LogoutbtnUser = new javax.swing.JButton();
        BackbtnUser = new javax.swing.JButton();
        CartbtnUser = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        FindProductUser = new javax.swing.JButton();
        refreshProductbtnUser = new javax.swing.JButton();
        findProductField1 = new javax.swing.JTextField();
        sellItembtnUser = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        updateProduct = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        loginbtn1 = new javax.swing.JButton();
        signupbtn1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        usernameField1 = new javax.swing.JTextField();
        passwordField1 = new javax.swing.JPasswordField();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel54 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();

        signupFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        signupFrame.setAlwaysOnTop(true);
        signupFrame.setResizable(false);
        signupFrame.setSize(new java.awt.Dimension(467, 480));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setPreferredSize(new java.awt.Dimension(500, 480));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel11.setText("Sign Up Form");
        jPanel5.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 34, 158, 48));
        jPanel5.add(usernameField, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 101, 286, 31));

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel14.setText("Username:");
        jPanel5.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 83, 31));
        jPanel5.add(phoneField, new org.netbeans.lib.awtextra.AbsoluteConstraints(98, 202, 258, 31));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel16.setText("Phone:");
        jPanel5.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 201, -1, 31));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel17.setText("Password: ");
        jPanel5.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(38, 252, -1, 31));

        cancelSignuplbtn.setBackground(new java.awt.Color(255, 255, 255));
        cancelSignuplbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        cancelSignuplbtn.setText("Cancel");
        cancelSignuplbtn.setBorder(null);
        cancelSignuplbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelSignuplbtnActionPerformed(evt);
            }
        });
        jPanel5.add(cancelSignuplbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 373, 61, 31));

        signupbtn2.setBackground(new java.awt.Color(255, 255, 255));
        signupbtn2.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        signupbtn2.setText("SignUp");
        signupbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupbtn2ActionPerformed(evt);
            }
        });
        jPanel5.add(signupbtn2, new org.netbeans.lib.awtextra.AbsoluteConstraints(261, 373, -1, 31));
        jPanel5.add(emailField, new org.netbeans.lib.awtextra.AbsoluteConstraints(98, 151, 258, 31));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel18.setText("Email: ");
        jPanel5.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, -1, 31));
        jPanel5.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(129, 253, 227, 31));
        jPanel5.add(confirmPassField, new org.netbeans.lib.awtextra.AbsoluteConstraints(182, 307, 229, 31));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel19.setText("Confirm Password: ");
        jPanel5.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(26, 306, -1, 31));

        javax.swing.GroupLayout signupFrameLayout = new javax.swing.GroupLayout(signupFrame.getContentPane());
        signupFrame.getContentPane().setLayout(signupFrameLayout);
        signupFrameLayout.setHorizontalGroup(
            signupFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 467, Short.MAX_VALUE)
            .addGroup(signupFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
        );
        signupFrameLayout.setVerticalGroup(
            signupFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 480, Short.MAX_VALUE)
            .addGroup(signupFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        homePageFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        homePageFrame.setTitle("Your Home Page");
        homePageFrame.setResizable(false);
        homePageFrame.setSize(new java.awt.Dimension(882, 558));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSeparator1.setBackground(new java.awt.Color(0, 102, 102));
        jSeparator1.setForeground(new java.awt.Color(0, 102, 102));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 129, -1, 350));

        productsbtn.setBackground(new java.awt.Color(0, 102, 102));
        productsbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        productsbtn.setForeground(new java.awt.Color(255, 255, 255));
        productsbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productsbtnActionPerformed(evt);
            }
        });
        jPanel2.add(productsbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 102, 70));

        customersbtn.setBackground(new java.awt.Color(0, 102, 102));
        customersbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        customersbtn.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(customersbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 102, 71));

        salesbtn.setBackground(new java.awt.Color(0, 102, 102));
        salesbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesbtn.setForeground(new java.awt.Color(255, 255, 255));
        salesbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesbtnActionPerformed(evt);
            }
        });
        jPanel2.add(salesbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 390, 102, 63));

        jPanel6.setBackground(new java.awt.Color(0, 102, 102));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("HOME - MY UNIQUE-SHOP");

        Logoutbtn.setBackground(new java.awt.Color(0, 102, 102));
        Logoutbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Logoutbtn.setForeground(new java.awt.Color(255, 255, 255));
        Logoutbtn.setText("Logout");
        Logoutbtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Logoutbtn.setBorderPainted(false);
        Logoutbtn.setOpaque(false);
        Logoutbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutbtnActionPerformed(evt);
            }
        });

        Statisticsbtn.setBackground(new java.awt.Color(0, 102, 102));
        Statisticsbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Statisticsbtn.setForeground(new java.awt.Color(255, 255, 255));
        Statisticsbtn.setText("Statistics");
        Statisticsbtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Statisticsbtn.setBorderPainted(false);
        Statisticsbtn.setOpaque(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 280, Short.MAX_VALUE)
                .addComponent(Statisticsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Logoutbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Logoutbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Statisticsbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(33, Short.MAX_VALUE))))
        );

        jPanel2.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText("PRODUCTS");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 105, 26));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setText("CUSTOMERS");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, -1, 26));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setText("SALES REVIEW");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 410, -1, 26));

        javax.swing.GroupLayout homePageFrameLayout = new javax.swing.GroupLayout(homePageFrame.getContentPane());
        homePageFrame.getContentPane().setLayout(homePageFrameLayout);
        homePageFrameLayout.setHorizontalGroup(
            homePageFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        homePageFrameLayout.setVerticalGroup(
            homePageFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );

        sellItemFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        sellItemFrame.setResizable(false);
        sellItemFrame.setSize(new java.awt.Dimension(900, 697));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jPanel10.setBackground(new java.awt.Color(0, 102, 102));

        jLabel23.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 255));
        jLabel23.setText("Search Product: ");

        jTextField1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        jLabel24.setBackground(new java.awt.Color(255, 255, 255));
        jLabel24.setFont(new java.awt.Font("Book Antiqua", 0, 36)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setText("Make a Sale!");

        jLabel25.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Sell Something Fresh Right Now!");

        Logoutbtn2.setBackground(new java.awt.Color(0, 102, 102));
        Logoutbtn2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Logoutbtn2.setForeground(new java.awt.Color(255, 255, 255));
        Logoutbtn2.setText("Logout");
        Logoutbtn2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Logoutbtn2.setBorderPainted(false);
        Logoutbtn2.setOpaque(false);
        Logoutbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Logoutbtn2ActionPerformed(evt);
            }
        });

        Backbtn3.setBackground(new java.awt.Color(0, 102, 102));
        Backbtn3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Backbtn3.setForeground(new java.awt.Color(255, 255, 255));
        Backbtn3.setText("Back");
        Backbtn3.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Backbtn3.setBorderPainted(false);
        Backbtn3.setOpaque(false);
        Backbtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Backbtn3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(27, 27, 27)))
                .addGap(107, 107, 107)
                .addComponent(Backbtn3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Logoutbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Logoutbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Backbtn3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        jList3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cart", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Times New Roman", 0, 24), new java.awt.Color(0, 102, 102))); // NOI18N
        jList3.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        jList3.setModel(listModel3);
        jScrollPane3.setViewportView(jList3);

        printReceiptbtn.setBackground(new java.awt.Color(0, 102, 102));
        printReceiptbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        printReceiptbtn.setForeground(new java.awt.Color(255, 255, 255));
        printReceiptbtn.setText("Print Receipt");
        printReceiptbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printReceiptbtnActionPerformed(evt);
            }
        });

        confirmSalebtn.setBackground(new java.awt.Color(0, 102, 102));
        confirmSalebtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        confirmSalebtn.setForeground(new java.awt.Color(255, 255, 255));
        confirmSalebtn.setText("Confirm Sale");
        confirmSalebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmSalebtnActionPerformed(evt);
            }
        });

        removeCartbtn.setBackground(new java.awt.Color(0, 102, 102));
        removeCartbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        removeCartbtn.setForeground(new java.awt.Color(255, 255, 255));
        removeCartbtn.setText("Remove Item");
        removeCartbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCartbtnActionPerformed(evt);
            }
        });

        clearbtn.setBackground(new java.awt.Color(0, 102, 102));
        clearbtn.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        clearbtn.setForeground(new java.awt.Color(255, 255, 255));
        clearbtn.setText("Clear");
        clearbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearbtnActionPerformed(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(0, 102, 102));
        jLabel59.setText("Total: ");

        cartTotalField.setEditable(false);
        cartTotalField.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        cartTotalField.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel59)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cartTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 698, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(printReceiptbtn)
                            .addComponent(removeCartbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(clearbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(confirmSalebtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(16, 16, 16))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(printReceiptbtn)
                        .addGap(19, 19, 19)
                        .addComponent(removeCartbtn)
                        .addGap(19, 19, 19)
                        .addComponent(confirmSalebtn)
                        .addGap(19, 19, 19)
                        .addComponent(clearbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cartTotalField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout sellItemFrameLayout = new javax.swing.GroupLayout(sellItemFrame.getContentPane());
        sellItemFrame.getContentPane().setLayout(sellItemFrameLayout);
        sellItemFrameLayout.setHorizontalGroup(
            sellItemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sellItemFrameLayout.setVerticalGroup(
            sellItemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        productFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        productFrame.setTitle("PRODUCTS-UNIQUE");
        productFrame.setResizable(false);
        productFrame.setSize(new java.awt.Dimension(1010, 655));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jButton5.setBackground(new java.awt.Color(0, 102, 102));
        jButton5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        removeProductbtn.setBackground(new java.awt.Color(0, 102, 102));
        removeProductbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        removeProductbtn.setForeground(new java.awt.Color(255, 255, 255));
        removeProductbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeProductbtnActionPerformed(evt);
            }
        });

        updateProductbtn2.setBackground(new java.awt.Color(0, 102, 102));
        updateProductbtn2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        updateProductbtn2.setForeground(new java.awt.Color(255, 255, 255));
        updateProductbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProductbtn2ActionPerformed(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(0, 102, 102));
        jPanel8.setForeground(new java.awt.Color(255, 255, 255));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("PRODUCTS - MY UNIQUE-SHOP");

        Logoutbtn1.setBackground(new java.awt.Color(0, 102, 102));
        Logoutbtn1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Logoutbtn1.setForeground(new java.awt.Color(255, 255, 255));
        Logoutbtn1.setText("Logout");
        Logoutbtn1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Logoutbtn1.setBorderPainted(false);
        Logoutbtn1.setOpaque(false);
        Logoutbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Logoutbtn1ActionPerformed(evt);
            }
        });

        Accountbtn1.setBackground(new java.awt.Color(0, 102, 102));
        Accountbtn1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Accountbtn1.setForeground(new java.awt.Color(255, 255, 255));
        Accountbtn1.setText("Back");
        Accountbtn1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Accountbtn1.setBorderPainted(false);
        Accountbtn1.setOpaque(false);
        Accountbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Accountbtn1ActionPerformed(evt);
            }
        });

        Cartbtn.setBackground(new java.awt.Color(0, 102, 102));
        Cartbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Cartbtn.setForeground(new java.awt.Color(255, 255, 255));
        Cartbtn.setText("Cart");
        Cartbtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Cartbtn.setBorderPainted(false);
        Cartbtn.setOpaque(false);
        Cartbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CartbtnActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Find Products:");

        FindProductbtn.setBackground(new java.awt.Color(0, 102, 102));
        FindProductbtn.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        FindProductbtn.setForeground(new java.awt.Color(255, 255, 255));
        FindProductbtn.setText("Find");
        FindProductbtn.setBorder(null);
        FindProductbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindProductbtnActionPerformed(evt);
            }
        });

        refreshProductbtn.setBackground(new java.awt.Color(0, 102, 102));
        refreshProductbtn.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        refreshProductbtn.setForeground(new java.awt.Color(255, 255, 255));
        refreshProductbtn.setText("Refresh");
        refreshProductbtn.setBorder(null);
        refreshProductbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshProductbtnActionPerformed(evt);
            }
        });

        findProductField.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel40)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findProductField, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FindProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(113, 113, 113)
                .addComponent(Cartbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Accountbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Logoutbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Logoutbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Accountbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Cartbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findProductField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FindProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel15.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel15.setText("ADD PRODUCTS");

        jLabel20.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel20.setText("REMOVE PROD.");

        jLabel21.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel21.setText("UPDATE PROD.");

        jLabel22.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel22.setText("SELL PROD.");

        sellItembtn.setBackground(new java.awt.Color(0, 102, 102));
        sellItembtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        sellItembtn.setForeground(new java.awt.Color(255, 255, 255));
        sellItembtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellItembtnActionPerformed(evt);
            }
        });

        table1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        table1.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "product_id", "product_name", "product_category", "Scale", "Initial Quantity", "Remaining Quantity", "Unit Cost Price", "Unit Sell Price", "Date Entry", "Supplier"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table1.setGridColor(new java.awt.Color(255, 255, 255));
        table1.setOpaque(false);
        jScrollPane4.setViewportView(table1);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21)
                            .addComponent(jLabel15)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(sellItembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(updateProductbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(removeProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGap(2, 2, 2)
                                        .addComponent(jLabel22))))))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 831, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(sellItembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel22)
                        .addGap(11, 11, 11)
                        .addComponent(removeProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateProductbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout productFrameLayout = new javax.swing.GroupLayout(productFrame.getContentPane());
        productFrame.getContentPane().setLayout(productFrameLayout);
        productFrameLayout.setHorizontalGroup(
            productFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        productFrameLayout.setVerticalGroup(
            productFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        addProductFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addProductFrame.setResizable(false);
        addProductFrame.setSize(new java.awt.Dimension(490, 547));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setBackground(new java.awt.Color(255, 255, 255));
        jLabel26.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 102, 102));
        jLabel26.setText("ADD PRODUCT");

        jLabel27.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel27.setText("Product Name: ");

        jLabel28.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel28.setText("Product Category: ");

        jLabel29.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel29.setText("Scale: ");

        jLabel30.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel30.setText("Initial Quantity: ");

        jLabel36.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel36.setText("Date Entry:");

        addProductbtn.setBackground(new java.awt.Color(0, 102, 102));
        addProductbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        addProductbtn.setForeground(new java.awt.Color(255, 255, 255));
        addProductbtn.setText("ADD");
        addProductbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductbtnActionPerformed(evt);
            }
        });

        addProductCancelbtn.setBackground(new java.awt.Color(0, 102, 102));
        addProductCancelbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        addProductCancelbtn.setForeground(new java.awt.Color(255, 255, 255));
        addProductCancelbtn.setText("Cancel");
        addProductCancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductCancelbtnActionPerformed(evt);
            }
        });

        jLabel41.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel41.setText("Unit Cost Price: ");

        jLabel42.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel42.setText("Unit Selling Price: ");

        jLabel43.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel43.setText("Supplier Name: ");

        jLabel31.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel31.setText("FCFA");

        jLabel32.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel32.setText("FCFA");

        scaleCombo1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "units", "kg", "cartons", "packets", "bags", "buckets" }));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(addProductbtn)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addProductCancelbtn)
                                .addGap(82, 82, 82))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(151, 151, 151))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel28)
                                        .addComponent(jLabel29)
                                        .addComponent(jLabel30)
                                        .addComponent(jLabel27)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(initQuanField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(unitCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(jLabel31))
                                    .addComponent(scaleCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pcategoryField, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel43)
                                    .addComponent(jLabel42)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(unitSellField, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel32)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(supplierField)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(dateEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(82, 82, 82))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pnameField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pcategoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scaleCombo1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initQuanField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel31))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitSellField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addComponent(supplierField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateEntryField, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addProductCancelbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53))
        );

        javax.swing.GroupLayout addProductFrameLayout = new javax.swing.GroupLayout(addProductFrame.getContentPane());
        addProductFrame.getContentPane().setLayout(addProductFrameLayout);
        addProductFrameLayout.setHorizontalGroup(
            addProductFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        addProductFrameLayout.setVerticalGroup(
            addProductFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        addItemFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addItemFrame.setSize(new java.awt.Dimension(441, 325));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        qtyField.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel12.add(qtyField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 230, 29));

        jLabel38.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel38.setText("Quantity: ");
        jPanel12.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 130, -1, 32));

        unitPriceField.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jPanel12.add(unitPriceField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 170, 230, 29));

        jLabel39.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel39.setText("Unit Price: ");
        jPanel12.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 32));

        jLabel37.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 102, 102));
        jLabel37.setText("Give me some details!");
        jPanel12.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 40, -1, 32));

        displayScaleField.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        displayScaleField.setBorder(null);
        jPanel12.add(displayScaleField, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 56, 32));

        displayScaleField1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        displayScaleField1.setText("FCFA");
        displayScaleField1.setBorder(null);
        jPanel12.add(displayScaleField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 170, 43, 32));

        itemDetailsCancelbtn.setBackground(new java.awt.Color(0, 102, 102));
        itemDetailsCancelbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        itemDetailsCancelbtn.setForeground(new java.awt.Color(255, 255, 255));
        itemDetailsCancelbtn.setText("Cancel");
        itemDetailsCancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDetailsCancelbtnActionPerformed(evt);
            }
        });
        jPanel12.add(itemDetailsCancelbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, -1, -1));

        addtoCartbtn.setBackground(new java.awt.Color(0, 102, 102));
        addtoCartbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        addtoCartbtn.setForeground(new java.awt.Color(255, 255, 255));
        addtoCartbtn.setText("Add to cart");
        addtoCartbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addtoCartbtnActionPerformed(evt);
            }
        });
        jPanel12.add(addtoCartbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, -1, 40));

        selectedProductField.setEditable(false);
        selectedProductField.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        selectedProductField.setBorder(null);
        jPanel12.add(selectedProductField, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 300, 40));

        makeSalebtn.setBackground(new java.awt.Color(0, 102, 102));
        makeSalebtn.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        makeSalebtn.setForeground(new java.awt.Color(255, 255, 255));
        makeSalebtn.setText("Sell");
        makeSalebtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        makeSalebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                makeSalebtnActionPerformed(evt);
            }
        });
        jPanel12.add(makeSalebtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, 90, 40));

        javax.swing.GroupLayout addItemFrameLayout = new javax.swing.GroupLayout(addItemFrame.getContentPane());
        addItemFrame.getContentPane().setLayout(addItemFrameLayout);
        addItemFrameLayout.setHorizontalGroup(
            addItemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addItemFrameLayout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        addItemFrameLayout.setVerticalGroup(
            addItemFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
        );

        salesFrame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        salesFrame.setResizable(false);
        salesFrame.setSize(new java.awt.Dimension(958, 704));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        salesTable1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesTable1.setForeground(new java.awt.Color(0, 102, 102));
        salesTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane5.setViewportView(salesTable1);

        jLabel60.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(0, 102, 102));
        jLabel60.setText("Total Sales :");

        totalSalesField.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        totalSalesField.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalSalesField, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalSalesField))
                .addGap(0, 49, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Today", jPanel4);

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        salesTable2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesTable2.setForeground(new java.awt.Color(0, 102, 102));
        salesTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sales_ID", "Date-Time", "Product Name", "Product Category", "Quantity Sold", "Total", "Buyer's name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane9.setViewportView(salesTable2);

        jLabel61.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(0, 102, 102));
        jLabel61.setText("Total Sales :");

        totalSalesField1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        totalSalesField1.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 992, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalSalesField1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalSalesField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 59, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("This Week", jPanel15);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        salesTable3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesTable3.setForeground(new java.awt.Color(0, 102, 102));
        salesTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sales_ID", "Date-Time", "Product Name", "Product Category", "Quantity Sold", "Total", "Buyer's name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane10.setViewportView(salesTable3);

        jLabel62.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(0, 102, 102));
        jLabel62.setText("Total Sales :");

        totalSalesField2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        totalSalesField2.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 992, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalSalesField2, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel62, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalSalesField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 57, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("This month", jPanel16);

        jPanel17.setBackground(new java.awt.Color(255, 255, 255));

        salesTable4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesTable4.setForeground(new java.awt.Color(0, 102, 102));
        salesTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sales_ID", "Date-Time", "Product Name", "Product Category", "Quantity Sold", "Total", "Buyer's name"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane11.setViewportView(salesTable4);

        jLabel63.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(0, 102, 102));
        jLabel63.setText("Total Sales :");

        totalSalesField3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        totalSalesField3.setForeground(new java.awt.Color(0, 102, 102));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 992, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jLabel63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalSalesField3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel63, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(totalSalesField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 63, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("This year", jPanel17);

        jPanel14.setBackground(new java.awt.Color(0, 102, 102));

        salesSearchField.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N

        jLabel57.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setText("Search sale:");

        jLabel58.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setText("SALES REVIEW");

        Accountbtn2.setBackground(new java.awt.Color(0, 102, 102));
        Accountbtn2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        Accountbtn2.setForeground(new java.awt.Color(255, 255, 255));
        Accountbtn2.setText("Back");
        Accountbtn2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Accountbtn2.setBorderPainted(false);
        Accountbtn2.setOpaque(false);
        Accountbtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Accountbtn2ActionPerformed(evt);
            }
        });

        salesRefreshbtn.setBackground(new java.awt.Color(0, 102, 102));
        salesRefreshbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesRefreshbtn.setForeground(new java.awt.Color(255, 255, 255));
        salesRefreshbtn.setText("Refresh");
        salesRefreshbtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        salesRefreshbtn.setBorderPainted(false);
        salesRefreshbtn.setOpaque(false);
        salesRefreshbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesRefreshbtnActionPerformed(evt);
            }
        });

        salesFindbtn.setBackground(new java.awt.Color(0, 102, 102));
        salesFindbtn.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        salesFindbtn.setForeground(new java.awt.Color(255, 255, 255));
        salesFindbtn.setText("Find");
        salesFindbtn.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        salesFindbtn.setBorderPainted(false);
        salesFindbtn.setOpaque(false);
        salesFindbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesFindbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(215, 215, 215)
                .addComponent(Accountbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel57)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(salesFindbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(salesRefreshbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(Accountbtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(salesSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57)
                    .addComponent(salesRefreshbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(salesFindbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout salesFrameLayout = new javax.swing.GroupLayout(salesFrame.getContentPane());
        salesFrame.getContentPane().setLayout(salesFrameLayout);
        salesFrameLayout.setHorizontalGroup(
            salesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        salesFrameLayout.setVerticalGroup(
            salesFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        updateProductFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        updateProductFrame.setResizable(false);
        updateProductFrame.setSize(new java.awt.Dimension(517, 650));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));

        jLabel33.setBackground(new java.awt.Color(255, 255, 255));
        jLabel33.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 102, 102));
        jLabel33.setText("UPDATE PRODUCT");

        jLabel34.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel34.setText("Product ID: ");

        jLabel35.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel35.setText("Product Category: ");

        jLabel44.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel44.setText("Scale: ");

        jLabel45.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel45.setText("Quantity purchased:");

        jLabel46.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel46.setText("Date Entry:");

        updateProductbtn.setBackground(new java.awt.Color(0, 102, 102));
        updateProductbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        updateProductbtn.setForeground(new java.awt.Color(255, 255, 255));
        updateProductbtn.setText("UPDATE");
        updateProductbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProductbtnActionPerformed(evt);
            }
        });

        updateProductCancelbtn.setBackground(new java.awt.Color(0, 102, 102));
        updateProductCancelbtn.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        updateProductCancelbtn.setForeground(new java.awt.Color(255, 255, 255));
        updateProductCancelbtn.setText("Cancel");
        updateProductCancelbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateProductCancelbtnActionPerformed(evt);
            }
        });

        jLabel47.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel47.setText("Unit Cost Price: ");

        jLabel48.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel48.setText("Unit Selling Price: ");

        jLabel49.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel49.setText("Supplier Name: ");

        jLabel50.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel50.setText("FCFA");

        jLabel51.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel51.setText("FCFA");

        jLabel52.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel52.setText("Qty remaining: ");

        remQuantityField.setEditable(false);

        jLabel53.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel53.setText("Product Name: ");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(247, 247, 247)
                .addComponent(updateProductbtn)
                .addGap(18, 18, 18)
                .addComponent(updateProductCancelbtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel45)
                            .addComponent(jLabel47)
                            .addComponent(jLabel44)
                            .addComponent(jLabel35)
                            .addComponent(jLabel34)
                            .addComponent(jLabel52)
                            .addComponent(jLabel53))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scaleField1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pCategoryField1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(productIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(initQuanField1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(remQuantityField, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(jLabel33)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(unitCostField1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel50, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(38, 38, 38))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(dateEntryField1))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel49)
                                    .addComponent(jLabel48))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(supplierField1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(unitSellField1))))
                        .addGap(92, 92, 92))))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(productIDField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pNameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel53, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pCategoryField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scaleField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initQuanField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(remQuantityField, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(unitCostField1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel50))))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(unitSellField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supplierField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateEntryField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateProductbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updateProductCancelbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(101, 101, 101))
        );

        javax.swing.GroupLayout updateProductFrameLayout = new javax.swing.GroupLayout(updateProductFrame.getContentPane());
        updateProductFrame.getContentPane().setLayout(updateProductFrameLayout);
        updateProductFrameLayout.setHorizontalGroup(
            updateProductFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        updateProductFrameLayout.setVerticalGroup(
            updateProductFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateProductFrameLayout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        jLabel12.setText("Buyer's Name: ");

        buyerNameField.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buyerNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyerNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(75, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout buyerNameDialogLayout = new javax.swing.GroupLayout(buyerNameDialog.getContentPane());
        buyerNameDialog.getContentPane().setLayout(buyerNameDialogLayout);
        buyerNameDialogLayout.setHorizontalGroup(
            buyerNameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buyerNameDialogLayout.setVerticalGroup(
            buyerNameDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jButton1.setText("jButton1");

        productFrameUser.setSize(new java.awt.Dimension(996, 588));

        jPanel19.setBackground(new java.awt.Color(255, 255, 255));

        jPanel20.setBackground(new java.awt.Color(0, 102, 102));
        jPanel20.setForeground(new java.awt.Color(255, 255, 255));

        jLabel64.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(255, 255, 255));
        jLabel64.setText("PRODUCTS - MY UNIQUE-SHOP");

        LogoutbtnUser.setBackground(new java.awt.Color(0, 102, 102));
        LogoutbtnUser.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        LogoutbtnUser.setForeground(new java.awt.Color(255, 255, 255));
        LogoutbtnUser.setText("Logout");
        LogoutbtnUser.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        LogoutbtnUser.setBorderPainted(false);
        LogoutbtnUser.setOpaque(false);
        LogoutbtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LogoutbtnUserActionPerformed(evt);
            }
        });

        BackbtnUser.setBackground(new java.awt.Color(0, 102, 102));
        BackbtnUser.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        BackbtnUser.setForeground(new java.awt.Color(255, 255, 255));
        BackbtnUser.setText("Back");
        BackbtnUser.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        BackbtnUser.setBorderPainted(false);
        BackbtnUser.setOpaque(false);
        BackbtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BackbtnUserActionPerformed(evt);
            }
        });

        CartbtnUser.setBackground(new java.awt.Color(0, 102, 102));
        CartbtnUser.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        CartbtnUser.setForeground(new java.awt.Color(255, 255, 255));
        CartbtnUser.setText("Cart");
        CartbtnUser.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        CartbtnUser.setBorderPainted(false);
        CartbtnUser.setOpaque(false);
        CartbtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CartbtnUserActionPerformed(evt);
            }
        });

        jLabel65.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(255, 255, 255));
        jLabel65.setText("Find Products:");

        FindProductUser.setBackground(new java.awt.Color(0, 102, 102));
        FindProductUser.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        FindProductUser.setForeground(new java.awt.Color(255, 255, 255));
        FindProductUser.setText("Find");
        FindProductUser.setBorder(null);
        FindProductUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindProductUserActionPerformed(evt);
            }
        });

        refreshProductbtnUser.setBackground(new java.awt.Color(0, 102, 102));
        refreshProductbtnUser.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        refreshProductbtnUser.setForeground(new java.awt.Color(255, 255, 255));
        refreshProductbtnUser.setText("Refresh");
        refreshProductbtnUser.setBorder(null);
        refreshProductbtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshProductbtnUserActionPerformed(evt);
            }
        });

        findProductField1.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(findProductField1, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(FindProductUser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(refreshProductbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(299, Short.MAX_VALUE)
                .addComponent(jLabel64)
                .addGap(113, 113, 113)
                .addComponent(CartbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BackbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LogoutbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LogoutbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BackbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CartbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(findProductField1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FindProductUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshProductbtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        sellItembtnUser.setBackground(new java.awt.Color(0, 102, 102));
        sellItembtnUser.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        sellItembtnUser.setForeground(new java.awt.Color(255, 255, 255));
        sellItembtnUser.setText("Sell Item");
        sellItembtnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellItembtnUserActionPerformed(evt);
            }
        });

        table2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        table2.setFont(new java.awt.Font("Times New Roman", 2, 18)); // NOI18N
        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "product_id", "product_name", "product_category", "Scale", "Initial Quantity", "Remaining Quantity", "Unit Cost Price", "Unit Sell Price", "Date Entry", "Supplier"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Float.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table2.setGridColor(new java.awt.Color(255, 255, 255));
        table2.setOpaque(false);
        jScrollPane6.setViewportView(table2);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(sellItembtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6)
                .addContainerGap())
            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(sellItembtnUser, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout productFrameUserLayout = new javax.swing.GroupLayout(productFrameUser.getContentPane());
        productFrameUser.getContentPane().setLayout(productFrameUserLayout);
        productFrameUserLayout.setHorizontalGroup(
            productFrameUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        productFrameUserLayout.setVerticalGroup(
            productFrameUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Uniqu-Vendor-Index");
        setResizable(false);
        setSize(new java.awt.Dimension(791, 535));

        updateProduct.setBackground(new java.awt.Color(0, 102, 102));
        updateProduct.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        updateProduct.setForeground(new java.awt.Color(240, 240, 240));
        updateProduct.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("UNIQUE-VENDOR");
        updateProduct.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 228, -1));

        jPanel3.setBackground(new java.awt.Color(0, 51, 51));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Sign in");

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Username: ");

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Password:");

        loginbtn1.setBackground(new java.awt.Color(0, 102, 102));
        loginbtn1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        loginbtn1.setForeground(new java.awt.Color(255, 255, 255));
        loginbtn1.setText("Sign in");
        loginbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginbtn1ActionPerformed(evt);
            }
        });

        signupbtn1.setBackground(new java.awt.Color(0, 102, 102));
        signupbtn1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        signupbtn1.setForeground(new java.awt.Color(255, 255, 255));
        signupbtn1.setText("sign up");
        signupbtn1.setBorder(null);
        signupbtn1.setBorderPainted(false);
        signupbtn1.setFocusPainted(false);
        signupbtn1.setFocusable(false);
        signupbtn1.setOpaque(false);
        signupbtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signupbtn1ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Do not have an account? ");

        usernameField1.setFont(new java.awt.Font("Times New Roman", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 47, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(signupbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(loginbtn1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(passwordField1)
                                    .addComponent(usernameField1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)))
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(28, 28, 28))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(98, 98, 98))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(usernameField1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addComponent(loginbtn1)
                        .addGap(22, 22, 22))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(passwordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(signupbtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(77, 77, 77))
        );

        updateProduct.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 108, -1, -1));

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        updateProduct.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(348, 99, -1, 400));

        jLabel54.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setText("Welcome to Unique-Vendor");
        updateProduct.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 167, 299, 46));

        jLabel10.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Manage your store, track your sales and expenses.");
        updateProduct.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(406, 225, -1, 22));

        jLabel55.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setText("Keep track of your customers and employees");
        updateProduct.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(416, 253, -1, -1));

        jLabel56.setFont(new java.awt.Font("Georgia", 0, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setText("All in one place,  Here!");
        updateProduct.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(482, 276, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(updateProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 791, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(updateProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginbtn1ActionPerformed
        String username = usernameField1.getText();
        String newPassword = passwordField1.getText();
        String password = "";
        
        //checking that no input fields are empty
        if(username.equals("") || newPassword.equals("")){
            JOptionPane.showMessageDialog(null,"Invalid Login details","Login Error",JOptionPane.ERROR_MESSAGE);
        }
        //Extracting the original password from the database to perform a comparison with the input password.
        else{
        try{
            myStat = myConn.createStatement();
            myRs = myStat.executeQuery("SELECT password, role FROM users WHERE username = '" + username + "'");
            while(myRs.next()){
                password = myRs.getString("password");
                role = myRs.getString("role");
            }
        }catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,"Invalid username","Login Error",JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Unique_Index_Page.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("role: " + role);
        //comparing input password to the original password extracted from the database.
        UserLogin login = new UserLogin(username, password);
        if(login.verify(username, newPassword)){
            JOptionPane.showMessageDialog(null,"Login Successful","Success",JOptionPane.INFORMATION_MESSAGE);
            this.setVisible(false);
            homePageFrame.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null,"Invalid Login Details","Login Error",JOptionPane.ERROR_MESSAGE);
        }
       }
        usernameField1.setText(null);
        passwordField1.setText(null);
    }//GEN-LAST:event_loginbtn1ActionPerformed

    private void signupbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupbtn1ActionPerformed
        //Open the signup Frame.
        signupFrame.setVisible(true);
    }//GEN-LAST:event_signupbtn1ActionPerformed

    private void cancelSignuplbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelSignuplbtnActionPerformed
       //close signup Frame.  
       signupFrame.setVisible(false);
    }//GEN-LAST:event_cancelSignuplbtnActionPerformed

    private void signupbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signupbtn2ActionPerformed
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String confirmPassword = confirmPassField.getText();
        
        //Checking that user leaves no input field empty.
        if(username.equals("")||email.equals("")||password.equals("")||phone.equals("")){
            JOptionPane.showMessageDialog(null,"Please provide all entries","Invalid Input",JOptionPane.WARNING_MESSAGE);
        }
        // Checking that the two passwords provided do match (password and confirm password). 
        else if(confirmPassword.equals(password) == false){
            JOptionPane.showMessageDialog(null,"Passwords do not match!","Invalid Input",JOptionPane.WARNING_MESSAGE);
            passwordField.setText("");
            confirmPassField.setText("");
        }
        else{
        //creating a new user, adding info to database.
        //Using prepared mysql statements to insert the new users' info into the database.
        try{
        String sqlQuery = " INSERT INTO users(username, email, password, phone)"
                + " VALUES(?,?,?,?)";
        PreparedStatement prepStmt = NamedPreparedStatement.prepareStatement(myConn, sqlQuery);
        //prepStmt.setLong("id", 1);
        prepStmt.setString(1, username);
        prepStmt.setString(2, email);
        prepStmt.setString(3, password);
        prepStmt.setString(4, phone);
        
        prepStmt.executeUpdate();
        prepStmt.close();
        }catch(Exception e){
           e.printStackTrace();
        }
        //JOptionPane.showMessageDialog(null, "User added!");
        //Clearing the contents of the text fields.
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        phoneField.setText("");
        
       JOptionPane.showMessageDialog(null,"Successful signup","Success",JOptionPane.INFORMATION_MESSAGE);
       //Returning to the signup Page.
       signupFrame.setVisible(false);
       this.setVisible(true);
     }
    }//GEN-LAST:event_signupbtn2ActionPerformed

    private void LogoutbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutbtnActionPerformed
         //Logging a user out, by simply closing the current page and returning the signin page.
        JOptionPane.showMessageDialog(null,"You're Logging out!","Logging Out",JOptionPane.INFORMATION_MESSAGE);
        homePageFrame.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_LogoutbtnActionPerformed

    private void Logoutbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Logoutbtn1ActionPerformed
        //Logging a user out, by simply closing the current page and returning the signin page.
        JOptionPane.showMessageDialog(null,"You're Logging out!","Logging Out",JOptionPane.INFORMATION_MESSAGE);
        productFrame.setVisible(false);
        this.setVisible(true);    
    }//GEN-LAST:event_Logoutbtn1ActionPerformed

    private void Accountbtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Accountbtn1ActionPerformed
        //returning to the home page.
        productFrame.setVisible(false);
        homePageFrame.setVisible(true);
    }//GEN-LAST:event_Accountbtn1ActionPerformed

    private void productsbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productsbtnActionPerformed
         //Calling displayProducts function to print the products to listModel1.
        //displayProducts(listModel1);
        String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        //Opening the products display page for admin (table 1).
        homePageFrame.setVisible(false);
        if(role.equals("admin")){
            productFrame.setVisible(true);
            printProducts("table1", query);
        }
        //Opening the products display page for a normal user (table 2).
        else if(role.equals("sales")){
            productFrameUser.setVisible(true); 
            printProducts("table2", query);
        }

    }//GEN-LAST:event_productsbtnActionPerformed

    private void Logoutbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Logoutbtn2ActionPerformed
        //Logging a user out, by simply closing the current page and returning the signin page.
        JOptionPane.showMessageDialog(null,"You're Logging out!","Logging Out",JOptionPane.INFORMATION_MESSAGE);
        sellItemFrame.setVisible(false);
        productFrame.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_Logoutbtn2ActionPerformed

    private void Backbtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Backbtn3ActionPerformed
       //returning to the home page.
        sellItemFrame.setVisible(false);
        if(role.equals("admin")){
            productFrame.setVisible(true);
        }else if(role.equals("sales")){
            productFrameUser.setVisible(true);
        }
    }//GEN-LAST:event_Backbtn3ActionPerformed

    private void addProductbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductbtnActionPerformed
        //reading product parameters from the user.
        String productName = pnameField.getText();     
        String productCateg = pcategoryField.getText();
        String productScale = (scaleCombo1.getSelectedItem()).toString();
        float initQuantity =  Float.parseFloat(initQuanField.getText());
        String dateEntry = dateEntryField.getText();
        int unitCostPrice = Integer.parseInt(unitCostField.getText());
        int unitSellPrice = Integer.parseInt(unitSellField.getText());
        String supplierName = supplierField.getText();
        
        //adding a new product to the database, using prepared statements to avoid jsql injection. 
        //Inserting product parameters into the database.
        try{
            String sqlQuery = "INSERT INTO products(product_name,product_category,scale,init_quantity_in_scale,unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry)"
            + " VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement prepStmt = NamedPreparedStatement.prepareStatement(myConn, sqlQuery);
            //prepStmt.setLong("id", 1);
            prepStmt.setString(1, productName);
            prepStmt.setString(2,productCateg);
            prepStmt.setString(3, productScale);
            prepStmt.setFloat(4,initQuantity);
            prepStmt.setInt(5,unitCostPrice);
            prepStmt.setInt(6,unitSellPrice);
            prepStmt.setString(7,supplierName);
            prepStmt.setFloat(8,initQuantity); //Remaining-quantity is set to the initial quanity, at the time of product registration.
            prepStmt.setString(9,dateEntry);
 
            prepStmt.executeUpdate();
            prepStmt.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null,"Product Successfully added!","Success",JOptionPane.INFORMATION_MESSAGE);
        //clearing the text fields.
        clearTextFields();
         String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        printProducts("table1", query);
    }//GEN-LAST:event_addProductbtnActionPerformed

    private void addProductCancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductCancelbtnActionPerformed
        //returning to the Product Page
        addProductFrame.setVisible(false);
        productFrame.setVisible(true);
    }//GEN-LAST:event_addProductCancelbtnActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // Opening the addProduct Frame.
        supplierField1.setText("x");
        dateEntryField1.setText("01-01-2021");
        addProductFrame.setVisible(true);
        productFrame.setVisible(false);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void updateProductbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductbtnActionPerformed
        // Updating Product info
        int product_id = Integer.parseInt(productIDField.getText());
        String productName1 = pNameField1.getText();
        String productCateg1 = pCategoryField1.getText();
        String productScale1 = scaleField1.getText();
        float initQuantity1 =  Float.parseFloat(initQuanField1.getText());
        float remQuantity   =  Float.parseFloat(remQuantityField.getText());
        String dateEntry1 = dateEntryField1.getText();
        int unitCostPrice1 = Integer.parseInt(unitCostField1.getText());
        int unitSellPrice1 = Integer.parseInt(unitSellField1.getText());
        String supplierName1 = supplierField1.getText();
        
        remQuantity = remQuantity + initQuantity1; // Updating the value of the remaining quantity.
        initQuantity1 = remQuantity;
         
        //adding a new product to the database, using prepared statements to avoid jsql injection. 
        //Inserting product parameters into the database.
        try{
            String sqlQuery = "UPDATE products SET product_name = ?, product_category = ?, scale = ?, init_quantity_in_scale = ?, unit_cost_price = ?, "
                + "unit_sell_price = ?, supplier = ?, rem_quantity = ?, date_entry = ? WHERE product_id = ?";
            PreparedStatement prepStmt = NamedPreparedStatement.prepareStatement(myConn, sqlQuery);
            //prepStmt.setLong("id", 1);
            prepStmt.setString(1, productName1);
            prepStmt.setString(2,productCateg1);
            prepStmt.setString(3, productScale1);
            prepStmt.setFloat(4,initQuantity1);
            prepStmt.setInt(5,unitCostPrice1);
            prepStmt.setInt(6,unitSellPrice1);
            prepStmt.setString(7,supplierName1);
            prepStmt.setFloat(8,remQuantity); //Remaining-quantity is set to the initial quanity, at the time of product registration.
            prepStmt.setString(9,dateEntry1);
            prepStmt.setInt(10, product_id);
 
            prepStmt.executeUpdate();
            prepStmt.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null,"Product Successfully Updated!","Success",JOptionPane.INFORMATION_MESSAGE);
        //clearing the text fields.
        clearTextFields();
        updateProductFrame.setVisible(false);
        //displayProducts(listModel1);
         String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        printProducts("table1",query);
    }//GEN-LAST:event_updateProductbtnActionPerformed

    private void updateProductCancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductCancelbtnActionPerformed
        // Closing the Update ProductFrame, and reopening the ProductFrame.
        updateProductFrame.setVisible(false);
        productFrame.setVisible(true);
    }//GEN-LAST:event_updateProductCancelbtnActionPerformed

    private void updateProductbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateProductbtn2ActionPerformed
         productFrame.setVisible(false);
         //Retrieving the selected product, which is to be updated.
       if(role.equals("admin")){
         try{
         String selectedProduct = getSelectedProduct("table1");
         Product P = new Product(selectedProduct); // Creating a new product.
        //Populating text fields using values from the selected product.
        //System.out.println("product_scale: " + P.scale);
        updateProductFrame.setVisible(true);
        productIDField.setText(String.valueOf(P.product_id));
        pNameField1.setText(P.product_name);
        pCategoryField1.setText(P.product_category);
        scaleField1.setText(P.scale);
        initQuanField1.setText(String.valueOf(P.init_quantity_in_scale));
        dateEntryField1.setText(P.date_entry);
        unitCostField1.setText(String.valueOf(P.unit_cost_price));
        unitSellField1.setText(String.valueOf(P.unit_sell_price));
        supplierField1.setText(String.valueOf(P.supplier));
        remQuantityField.setText(String.valueOf(P.rem_quantity));
        
        }catch(Exception e){
           JOptionPane.showMessageDialog(null,"Please Select a Product!","Warning",JOptionPane.WARNING_MESSAGE); 
        } 
      }
       else{
          JOptionPane.showMessageDialog(null,"You do not have the priviledges to make this action! \n Contact the admin.","Warning",JOptionPane.WARNING_MESSAGE);  
       }
    }//GEN-LAST:event_updateProductbtn2ActionPerformed

    private void sellItembtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellItembtnActionPerformed
        // Opening the Sell Item page.
        addItemFrame.setVisible(true);
        String selectedProduct = getSelectedProduct("table1");  //Reading the values for the selected product, from the jTable.
        //System.out.println("Selected Product: " + selectedProduct);
        Product P = new Product(selectedProduct);   //Creating a new product object P.
        displayScaleField.setText(P.scale);
        selectedProductField.setText(P.product_name);
        unitPriceField.setText(String.valueOf(P.unit_sell_price));
    }//GEN-LAST:event_sellItembtnActionPerformed

    private void Accountbtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Accountbtn2ActionPerformed
        // Closing the Sales Review Page.
        salesFrame.setVisible(false);
        homePageFrame.setVisible(true);
    }//GEN-LAST:event_Accountbtn2ActionPerformed

    private void salesbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesbtnActionPerformed
        // Opening the Sales Review Page.
        salesFrame.setVisible(true);
        homePageFrame.setVisible(false);
        displaySales("");
    }//GEN-LAST:event_salesbtnActionPerformed

    private void salesRefreshbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesRefreshbtnActionPerformed
        displaySales("");
    }//GEN-LAST:event_salesRefreshbtnActionPerformed

    private void removeCartbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCartbtnActionPerformed
        JOptionPane.showMessageDialog(null,"Item Deleted!","Warning",JOptionPane.WARNING_MESSAGE);
        String toBeRemoved = jList3.getSelectedValue();
        listModel3.removeElement(toBeRemoved);
        Sale sale = new Sale(toBeRemoved);
        cartTotal = cartTotal - sale.total;
        cartTotalField.setText(String.valueOf(cartTotal));
    }//GEN-LAST:event_removeCartbtnActionPerformed

    private void confirmSalebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmSalebtnActionPerformed
        //Creating a new Sale!
        for(int i = 0; i<listModel3.getSize(); i++){
            createSale(listModel3.getElementAt(i));
        }
        JOptionPane.showMessageDialog(null,"Sale Completed!","Success",JOptionPane.INFORMATION_MESSAGE);
        sellItemFrame.setVisible(false);
        // Refresh the product table. 
        String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        if(role.equals("admin")){
            productFrame.setVisible(true);
            printProducts("table1", query);
        }else if(role.equals("sales")){
            productFrameUser.setVisible(true);
            printProducts("table2", query);
        }
        
    }//GEN-LAST:event_confirmSalebtnActionPerformed

    private void printReceiptbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printReceiptbtnActionPerformed
        // Opening the dialogue box to read the buyer's name.
        buyerNameDialog.setVisible(true);    
    }//GEN-LAST:event_printReceiptbtnActionPerformed

    private void addtoCartbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addtoCartbtnActionPerformed
        //Adding the selected Product to cart.
         String selectedProduct = "";
        if(role.equals("admin")){
          selectedProduct = getSelectedProduct("table1");  //Reading the values for the selected product, from the jTable, table 1")){ 
        }
        else if(role.equals("sales")){
           selectedProduct = getSelectedProduct("table2");  //Reading the values for the selected product, from the jTable, table 2")){ 
        }
        //System.out.println("Selected Product: " + selectedProduct);
        Product P = new Product(selectedProduct);   //Creating a new product object P.
        //System.out.println("Product_id: " + P.product_id);
        int unitPrice = Integer.parseInt(unitPriceField.getText());
        float quantity = Float.parseFloat(qtyField.getText());
        float total = unitPrice * quantity;
        // Add product to the cart listModel and printing to screen
        displayProducts(listModel3, P, quantity, unitPrice, total);
        JOptionPane.showMessageDialog(null,"Product Added to Cart!","Success",JOptionPane.INFORMATION_MESSAGE);
        //sellItemFrame.setVisible(true);
        qtyField.setText("");
        addItemFrame.setVisible(false);
        cartTotal = cartTotal + total;
        cartTotalField.setText(String.valueOf(cartTotal));

    }//GEN-LAST:event_addtoCartbtnActionPerformed

    private void itemDetailsCancelbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDetailsCancelbtnActionPerformed
        //Closing the addItem frame and opening the product Frame.
        qtyField.setText("");
        unitPriceField.setText("");
        addItemFrame.setVisible(false);
        if(role.equals("admin")){
            productFrame.setVisible(true);
        }else if(role.equals("sales")){
            productFrameUser.setVisible(true);
        }
    }//GEN-LAST:event_itemDetailsCancelbtnActionPerformed

    private void CartbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CartbtnActionPerformed
        //Opening the sellItem Frame (cart).
        sellItemFrame.setVisible(true);
        productFrame.setVisible(false);
        
    }//GEN-LAST:event_CartbtnActionPerformed

    private void clearbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearbtnActionPerformed
        // Clearing the contents of the listModel3 (cart)
        listModel3.removeAllElements();
        cartTotal = 0;
    }//GEN-LAST:event_clearbtnActionPerformed

    private void FindProductbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindProductbtnActionPerformed
        // Finding a searched product from database.
        String criteria = findProductField.getText();
        findProduct("table1", criteria);
    }//GEN-LAST:event_FindProductbtnActionPerformed

    private void refreshProductbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshProductbtnActionPerformed
        // Refresh the product table. 
        String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        printProducts("table1", query);
    }//GEN-LAST:event_refreshProductbtnActionPerformed

    private void salesFindbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesFindbtnActionPerformed
        // Display sales based on a search term
        String criteria = salesSearchField.getText();
        displaySales(criteria);
    }//GEN-LAST:event_salesFindbtnActionPerformed

    private void removeProductbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeProductbtnActionPerformed
     Object[] options = {"Yes, please", "No sorry!"};
      int n = JOptionPane.showOptionDialog(productFrame,
        "Are you sure you want to delete this product?",
        "Action cannot be undone!",
         JOptionPane.YES_NO_OPTION,
         JOptionPane.QUESTION_MESSAGE,
         null,
         options,
         options[0]);
         if (n == JOptionPane.YES_OPTION) {        
            try {
                //removing product.
                String selectedProduct = getSelectedProduct("table1");  //Reading the values for the selected product, from the jTable.
                //System.out.println("Selected Product: " + selectedProduct);
                Product P = new Product(selectedProduct);   //Creating a new product object P.
                String query = "DELETE FROM `products` WHERE product_id = " + P.product_id ;
                myStat.execute(query);
            } catch (SQLException ex) {
            Logger.getLogger(Unique_Index_Page.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Calling displayProducts function to print the products to listModel1.
            //displayProducts(listModel1);
            String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
            //Opening the products display page for admin (table 1).
            homePageFrame.setVisible(false);
            if(role.equals("admin")){
            productFrame.setVisible(true);
            printProducts("table1", query);
            }
            //Opening the products display page for a normal user (table 2).
            else if(role.equals("sales")){
            productFrameUser.setVisible(true); 
            printProducts("table2", query);
            }
       } else if (n == JOptionPane.NO_OPTION) {
            
       } else {

        }
    }//GEN-LAST:event_removeProductbtnActionPerformed

    private void LogoutbtnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LogoutbtnUserActionPerformed
         //Logging a user out, by simply closing the current page and returning the signin page.
        JOptionPane.showMessageDialog(null,"You're Logging out!","Logging Out",JOptionPane.INFORMATION_MESSAGE);
        productFrameUser.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_LogoutbtnUserActionPerformed

    private void BackbtnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BackbtnUserActionPerformed
        productFrameUser.setVisible(false);
        homePageFrame.setVisible(true);
    }//GEN-LAST:event_BackbtnUserActionPerformed

    private void CartbtnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CartbtnUserActionPerformed
        //Displaying the cart frame.
        sellItemFrame.setVisible(true);
        productFrameUser.setVisible(false);
    }//GEN-LAST:event_CartbtnUserActionPerformed

    private void FindProductUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindProductUserActionPerformed
        // Finding a searched product from database.
        String criteria = findProductField.getText();
        findProduct("table2", criteria);
    }//GEN-LAST:event_FindProductUserActionPerformed

    private void refreshProductbtnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshProductbtnUserActionPerformed
        // Refresh the product table. 
        String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        printProducts("table2", query);
    }//GEN-LAST:event_refreshProductbtnUserActionPerformed

    private void sellItembtnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellItembtnUserActionPerformed
        System.out.println("I am here");
        String selectedProduct = getSelectedProduct("table2");  //Reading the values for the selected product, from the jTable, table2.
        System.out.println("Selected Product: " + selectedProduct);
        // Opening the Sell Item page.
        addItemFrame.setVisible(true);
        Product P = new Product(selectedProduct);   //Creating a new product object P.
        displayScaleField.setText(P.scale);
        selectedProductField.setText(P.product_name);
        unitPriceField.setText(String.valueOf(P.unit_sell_price));
    }//GEN-LAST:event_sellItembtnUserActionPerformed

    private void makeSalebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_makeSalebtnActionPerformed
        //Creating a new Sale!
        for(int i = 0; i<listModel3.getSize(); i++){
            createSale(listModel3.getElementAt(i));
        }
        //creating a new product object from the selected product.
        String selectedProduct = "";
        if(role.equals("admin")){
          selectedProduct = getSelectedProduct("table1");  //Reading the values for the selected product, from the jTable.
        }
        else if(role.equals("sales")){
          selectedProduct = getSelectedProduct("table2");  //Reading the values for the selected product, from the jTable.
        }
        //System.out.println("Selected Product: " + selectedProduct);
        Product P = new Product(selectedProduct);   //Creating a new product object P.
        //System.out.println("Product_id: " + P.product_id);
        int unitPrice = Integer.parseInt(unitPriceField.getText());
        float quantity = Float.parseFloat(qtyField.getText());
        float total = unitPrice * quantity;

        //creating a sale string from the just created product object.
        String output = String.valueOf(P.product_id) + " | " + P.product_name + " | "
                    + P.product_category + " | " + String.valueOf(quantity) + "( " + P.scale + " )" + " | "
                    + String.valueOf(unitPrice) + " | " + String.valueOf(total) + "(Total amount)";
        JOptionPane.showMessageDialog(null,"Sale Completed!","Success",JOptionPane.INFORMATION_MESSAGE);
        createSale(output);
        qtyField.setText("");
        sellItemFrame.setVisible(false);
       // Refresh the product table. 
        String query = "SELECT product_id, product_name,product_category,scale,init_quantity_in_scale,"
                    + "unit_cost_price,unit_sell_price,supplier,rem_quantity,date_entry FROM products";
        if(role.equals("admin")){
            printProducts("table1", query);
            addItemFrame.setVisible(false);
            productFrame.setVisible(true);
        }
        else if (role.equals("sales")){
            printProducts("table2", query);
            addItemFrame.setVisible(false);
            productFrameUser.setVisible(true);
        }
    }//GEN-LAST:event_makeSalebtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws SQLException {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Unique_Index_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Unique_Index_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Unique_Index_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Unique_Index_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        try{
            //1.Get connection to database
            Class.forName("com.mysql.jdbc.Driver");
            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/stockmanager", "root","");
            System.out.println("Database connection successful");
            
            //2. Create a statement
            myStat = myConn.createStatement();
            
            //Ensure that database connection is successful. 
            }catch(ClassNotFoundException | SQLException e){
           e.printStackTrace();
           JOptionPane.showMessageDialog(null,"Failure Connecting to Database","Error",JOptionPane.ERROR_MESSAGE);
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Unique_Index_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Accountbtn1;
    private javax.swing.JButton Accountbtn2;
    private javax.swing.JButton Backbtn3;
    private javax.swing.JButton BackbtnUser;
    private javax.swing.JButton Cartbtn;
    private javax.swing.JButton CartbtnUser;
    private javax.swing.JButton FindProductUser;
    private javax.swing.JButton FindProductbtn;
    private javax.swing.JButton Logoutbtn;
    private javax.swing.JButton Logoutbtn1;
    private javax.swing.JButton Logoutbtn2;
    private javax.swing.JButton LogoutbtnUser;
    private javax.swing.JButton Statisticsbtn;
    private javax.swing.JFrame addItemFrame;
    private javax.swing.JButton addProductCancelbtn;
    private javax.swing.JFrame addProductFrame;
    private javax.swing.JButton addProductbtn;
    private javax.swing.JButton addtoCartbtn;
    private javax.swing.JDialog buyerNameDialog;
    private javax.swing.JTextField buyerNameField;
    private javax.swing.JButton cancelSignuplbtn;
    private javax.swing.JTextField cartTotalField;
    private javax.swing.JButton clearbtn;
    private javax.swing.JPasswordField confirmPassField;
    private javax.swing.JButton confirmSalebtn;
    private javax.swing.JButton customersbtn;
    private javax.swing.JTextField dateEntryField;
    private javax.swing.JTextField dateEntryField1;
    private javax.swing.JTextField displayScaleField;
    private javax.swing.JTextField displayScaleField1;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField findProductField;
    private javax.swing.JTextField findProductField1;
    private javax.swing.JFrame homePageFrame;
    private javax.swing.JTextField initQuanField;
    private javax.swing.JTextField initQuanField1;
    private javax.swing.JButton itemDetailsCancelbtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton loginbtn1;
    private javax.swing.JButton makeSalebtn;
    private javax.swing.JTextField pCategoryField1;
    private javax.swing.JTextField pNameField1;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JPasswordField passwordField1;
    private javax.swing.JTextField pcategoryField;
    private javax.swing.JTextField phoneField;
    private javax.swing.JTextField pnameField;
    private javax.swing.JButton printReceiptbtn;
    private javax.swing.JFrame productFrame;
    private javax.swing.JFrame productFrameUser;
    private javax.swing.JTextField productIDField;
    private javax.swing.JButton productsbtn;
    private javax.swing.JTextField qtyField;
    private javax.swing.JButton refreshProductbtn;
    private javax.swing.JButton refreshProductbtnUser;
    private javax.swing.JTextField remQuantityField;
    private javax.swing.JButton removeCartbtn;
    private javax.swing.JButton removeProductbtn;
    private javax.swing.JButton salesFindbtn;
    private javax.swing.JFrame salesFrame;
    private javax.swing.JButton salesRefreshbtn;
    private javax.swing.JTextField salesSearchField;
    private javax.swing.JTable salesTable1;
    private javax.swing.JTable salesTable2;
    private javax.swing.JTable salesTable3;
    private javax.swing.JTable salesTable4;
    private javax.swing.JButton salesbtn;
    private javax.swing.JComboBox<String> scaleCombo1;
    private javax.swing.JTextField scaleField1;
    private javax.swing.JTextField selectedProductField;
    private javax.swing.JFrame sellItemFrame;
    private javax.swing.JButton sellItembtn;
    private javax.swing.JButton sellItembtnUser;
    private javax.swing.JFrame signupFrame;
    private javax.swing.JButton signupbtn1;
    private javax.swing.JButton signupbtn2;
    private javax.swing.JTextField supplierField;
    private javax.swing.JTextField supplierField1;
    private javax.swing.JTable table1;
    private javax.swing.JTable table2;
    private javax.swing.JTextField totalSalesField;
    private javax.swing.JTextField totalSalesField1;
    private javax.swing.JTextField totalSalesField2;
    private javax.swing.JTextField totalSalesField3;
    private javax.swing.JTextField unitCostField;
    private javax.swing.JTextField unitCostField1;
    private javax.swing.JTextField unitPriceField;
    private javax.swing.JTextField unitSellField;
    private javax.swing.JTextField unitSellField1;
    private javax.swing.JPanel updateProduct;
    private javax.swing.JButton updateProductCancelbtn;
    private javax.swing.JFrame updateProductFrame;
    private javax.swing.JButton updateProductbtn;
    private javax.swing.JButton updateProductbtn2;
    private javax.swing.JTextField usernameField;
    private javax.swing.JTextField usernameField1;
    // End of variables declaration//GEN-END:variables
}
