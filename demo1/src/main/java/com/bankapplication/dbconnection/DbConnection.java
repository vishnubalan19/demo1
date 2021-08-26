package com.bankapplication.dbconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bankapplication.account.Account;
import com.bankapplication.customer.Customer;
import com.bankapplication.exception.CustomException;

public class DbConnection implements PersistentLayer{
    private final String url = "jdbc:mysql://127.0.0.1:3306/app";
    private final String login = "root";
    private final String pass = "Vishnu@007";
    private final String createTable = "create table if not exists ";
    private String table1="user",table2="account" ;
    private Connection con = null;
    private PreparedStatement ps1 = null, ps2=null;
    //Getting the connection
    @Override
    public Connection getConnection()throws ClassNotFoundException, SQLException{
        if(con==null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, login, pass);
        }
        return con;
    }
    //Closing the connection
    @Override
    public void closeConnection() throws SQLException{
        if(con!=null) {
            con.close();
        }
    }
    //Getting the statement
    @Override
    public PreparedStatement getStatement(String sql,PreparedStatement preparedStatement) throws SQLException, ClassNotFoundException{
        if(preparedStatement == null){
            preparedStatement = getConnection().prepareStatement(sql,preparedStatement.RETURN_GENERATED_KEYS);
        }
        return preparedStatement;
    }
    //Closing the statement
    @Override
    public void closePreparedStatement(PreparedStatement preparedStatement) throws SQLException{
        if(preparedStatement!=null){
            preparedStatement.close();
        }
    }
    @Override
    public void closeStatement() throws SQLException{
        closePreparedStatement(ps1);
        closePreparedStatement(ps2);
    }
    //createTables is for getting the table names and table creation.
    @Override
    public void createTables(String table1, String table2) throws CustomException{
        try{
            this.table1=table1;
            this.table2=table2;
            String sql1 = createTable+table1+" (customerId INTEGER not null AUTO_INCREMENT, name VARCHAR(255) not null,mobileNo BIGINT unsigned not null,status INTEGER not null,PRIMARY KEY(customerId))";
            String sql2 = createTable+table2+" ( accountNo BIGINT unsigned not null AUTO_INCREMENT, balance DOUBLE not null,branch VARCHAR(255) not null, customerId INTEGER not null, status INTEGER not null ,PRIMARY KEY(accountNo), FOREIGN KEY (customerId) REFERENCES "+this.table1+" (customerId) )";
            createTable(sql1);
            createTable(sql2);
        }
        catch (Exception exception){
            throw new CustomException(exception.getMessage());
        }
    }
    @Override
    public void createTable(String sql) throws Exception {
        try(Statement statement = getConnection().createStatement()){
            statement.executeUpdate(sql);
        }
    }
    @Override
    public Map<Integer,List<List>> insertUsers(List <Customer> customerList,List<Account> accountList){
        if(customerList == null) {
            return null;
        }
        //tempMap is for storing the successful customer and account list and failure customer and account list.
        Map<Integer,List<List>> tempMap = new HashMap<>();
        List<List> successList = new ArrayList<>();
        List<List> failureList = new ArrayList<>();
        tempMap.put(0,failureList);
        tempMap.put(1,successList);
		try{
            for(int i=0; i<customerList.size();i++){
                Customer customer = customerList.get(i);
                Account account = accountList.get(i);
                int customerId =insertUser(customer);
                List tempList = new ArrayList();
                tempList.add(customer);
                tempList.add(account);
                if(customerId==-1){
                    failureList.add(tempList);
                }
                else{
                    customer.setCustomerId(customerId);
                    account.setCustomerId(customer.getCustomerId());
                    int accountNo = insertAccount(account);
                    if(accountNo==-1){
                        deleteCustomer(customer.getCustomerId());
                        failureList.add(tempList);
                    }
                    else {
                        account.setAccountNo(accountNo);
                        successList.add(tempList);
                    }
                }
            }
        }
		catch (Exception e){
		    System.out.println(e.getMessage());
        }
		return tempMap;
    }
    @Override
	public int insertUser(Customer customer) {
		if(customer == null){
			return -1;
		}
		try{
            String sql = "insert into "+this.table1+"(name,mobileNo,status) values(?,?,?)";
            ps1 = getStatement(sql,ps1);
            ps1.setString(1,customer.getName());
//            if(customer.getName().equals("vishnu")){
//                throw new Exception();
//            }
            ps1.setLong(2,customer.getMobileNo());
            ps1.setInt(3,1);
            ps1.executeUpdate();
            try(ResultSet rs = ps1.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
		catch (Exception e){
            System.out.println(e.getMessage());
        }
		return -1;
	}
    @Override
	public int insertAccount(Account account){
		if(account==null){
			return -1;
		}
		try{
            String sql = "insert into "+this.table2+"(balance,customerId,branch,status) values(?,?,?,?)";
            ps2 = getStatement(sql,ps2);
            ps2.setDouble(1,account.getBalance());
            ps2.setInt(2,account.getCustomerId());
            ps2.setString(3,account.getBranch());
//            if(account.getBranch().equals("nagai")){
//                throw new Exception();
//            }
            ps2.setInt(4,1);
            ps2.executeUpdate();
            try(ResultSet rs = ps2.getGeneratedKeys()){
                if(rs.next()){
                    return rs.getInt(1);
                }
            }
        }
		catch (Exception e){
		    System.out.println(e.getMessage());
        }
		return -1;
	}
	//When the customer details are added but the account details are failed to add, then deletion can be performed using deleteCustomer.
    @Override
	public void deleteCustomer(int customerId) throws Exception{
        String sql = "Delete from "+this.table1+" where customerId ="+customerId;
        try(Statement statement = getConnection().createStatement()){
            statement.executeUpdate(sql);
        }
    }
    //getAccountsList is for getting all the accounts in the database.
    @Override
    public List<Account> getAccountsList()throws Exception{
        List<Account> accountList = new ArrayList<>();
        String sql = "select customerId,accountNo,balance,branch from "+table2+" where status = 1";
        try(Statement statement = getConnection().createStatement();ResultSet rs = statement.executeQuery(sql)){
            while(rs.next()){
                Account account = new Account();
                account.setCustomerId(rs.getInt("customerId"));
                account.setAccountNo(rs.getLong("accountNo"));
                account.setBalance(rs.getDouble("balance"));
                account.setBranch(rs.getString("branch"));
                accountList.add(account);
            }
        }
        return accountList;
    }
    
    @Override
    public List<Customer> getCustomerList()throws Exception{
        List<Customer> customerList = new ArrayList<>();
        String sql = "select customerId,name,mobileNo from "+table1+" where status = 1";
        try(Statement statement = getConnection().createStatement();ResultSet rs = statement.executeQuery(sql)){
            while(rs.next()){
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customerId"));
                customer.setName(rs.getString("name"));
                customer.setMobileNo(rs.getLong("mobileNo"));
                customerList.add(customer);
            }
        }
        return customerList;
    }

    @Override
    public void removeAccount(long accountNo) throws CustomException{
        String sql = "Update "+this.table2+" set status = 0 where accountNo = "+accountNo;
        updateDb(sql);
    }
    @Override
    public void updateDb(String sql) throws CustomException {
        try (Statement statement = getConnection().createStatement()) {
            statement.executeUpdate(sql);
        } catch (Exception exception) {
            throw new CustomException(exception.getMessage());
        }
    }

    @Override
    public void removeCustomer(int customerId) throws CustomException{
        String sql = "Update "+this.table1+" set status = 0 where customerId = "+customerId;
        updateDb(sql);
    }
    @Override
    public void updateAmount(double amount, long accountNo) throws CustomException{
        String sql = "Update "+this.table2+" set balance = "+amount+" where accountNo = "+accountNo+" and status = 1";
        updateDb(sql);
    }
    @Override
    public void rollbackAccount(long accountNo) throws CustomException{
        String sql = "Update "+this.table2+" set status = 1 where accountNo = "+accountNo;
        updateDb(sql);
    }

    @Override
    public void activateCustomer(long accountNo, int customerId) throws CustomException{
        String sql = "Update "+this.table1+" set status = 1 where customerId = "+customerId;
        updateDb(sql);
        rollbackAccount(accountNo);
    }
}

//package com.bankapplication.dbconnection;
//
//        import java.sql.Connection;
//        import java.sql.DriverManager;
//        import java.sql.PreparedStatement;
//        import java.sql.Statement;
//        import java.sql.ResultSet;
//        import java.util.ArrayList;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.Map;
//
//        import com.bankapplication.account.Account;
//        import com.bankapplication.customer.Customer;
//
//public class DbConnection implements PersistentLayer{
//    private final String url = "jdbc:mysql://127.0.0.1:3306/app";
//    private final String login = "root";
//    private final String pass = "Vishnu@007";
//    private final String createTable = "create table if not exists ";
//    private String table1,table2 ;
//    private Connection con = null;
//    private PreparedStatement ps1 = null, ps2=null;
//    //Getting the connection
//    @Override
//    public Connection getConnection()throws Exception{
//        if(con==null) {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            con = DriverManager.getConnection(url, login, pass);
//        }
//        return con;
//    }
//    //Closing the connection
//    @Override
//    public void closeConnection() throws Exception{
//        if(con!=null) {
//            con.close();
//        }
//    }
//    //Getting the statement
//    @Override
//    public PreparedStatement getStatement(String sql,PreparedStatement preparedStatement) throws Exception{
//        if(preparedStatement == null){
//            preparedStatement = getConnection().prepareStatement(sql,preparedStatement.RETURN_GENERATED_KEYS);
//        }
//        return preparedStatement;
//    }
//    //Closing the statement
//    @Override
//    public void closePreparedStatement(PreparedStatement preparedStatement) throws Exception{
//        if(preparedStatement!=null){
//            preparedStatement.close();
//        }
//    }
//    @Override
//    public void closeStatement() throws Exception{
//        closePreparedStatement(ps1);
//        closePreparedStatement(ps2);
//    }
//    //createTables is for getting the table names and table creation.
//    @Override
//    public boolean createTables(String table1,String table2){
//        try{
//            this.table1=table1;
//            this.table2=table2;
//            String sql1 = createTable+table1+" (customerId INTEGER not null AUTO_INCREMENT, name VARCHAR(255) not null,mobileNo BIGINT unsigned not null,status INTEGER not null,PRIMARY KEY(customerId))";
//            String sql2 = createTable+table2+" ( accountNo BIGINT unsigned not null AUTO_INCREMENT, balance DOUBLE not null,branch VARCHAR(255) not null, customerId INTEGER not null, status INTEGER not null ,PRIMARY KEY(accountNo), FOREIGN KEY (customerId) REFERENCES "+this.table1+" (customerId) )";
//            createTable(sql1);
//            createTable(sql2);
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//            return false;
//        }
//        return true;
//    }
//    @Override
//    public void createTable(String sql) throws Exception{
//        try(Statement statement = getConnection().createStatement()){
//            statement.executeUpdate(sql);
//        }
//    }
//    @Override
//    public Map<Integer,List<List>> insertUsers(List <Customer> customerList,List<Account> accountList){
//        if(customerList == null) {
//            return null;
//        }
//        //tempMap is for storing the successful customer and account list and failure customer and account list.
//        Map<Integer,List<List>> tempMap = new HashMap<>();
//        List<List> successList = new ArrayList<>();
//        List<List> failureList = new ArrayList<>();
//        tempMap.put(0,failureList);
//        tempMap.put(1,successList);
//        try{
//            for(int i=0; i<customerList.size();i++){
//                Customer customer = customerList.get(i);
//                Account account = accountList.get(i);
//                int customerId =insertUser(customer);
//                List tempList = new ArrayList();
//                tempList.add(customer);
//                tempList.add(account);
//                if(customerId==-1){
//                    failureList.add(tempList);
//                }
//                else{
//                    customer.setCustomerId(customerId);
//                    account.setCustomerId(customer.getCustomerId());
//                    int accountNo = insertAccount(account);
//                    if(accountNo==-1){
//                        deleteCustomer(customer.getCustomerId());
//                        failureList.add(tempList);
//                    }
//                    else {
//                        account.setAccountNo(accountNo);
//                        successList.add(tempList);
//                    }
//                }
//            }
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//        return tempMap;
//    }
//    @Override
//    public int insertUser(Customer customer) throws Exception{
//        if(customer == null){
//            return -1;
//        }
//        try{
//            String sql = "insert into "+this.table1+"(name,mobileNo,status) values(?,?,?)";
//            ps1 = getStatement(sql,ps1);
//            ps1.setString(1,customer.getName());
////            if(customer.getName().equals("vishnu")){
////                throw new Exception();
////            }
//            ps1.setLong(2,customer.getMobileNo());
//            ps1.setInt(3,1);
//            ps1.executeUpdate();
//            try(ResultSet rs = ps1.getGeneratedKeys()){
//                if(rs.next()){
//                    return rs.getInt(1);
//                }
//            }
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//        return -1;
//    }
//    @Override
//    public int insertAccount(Account account){
//        if(account==null){
//            return -1;
//        }
//        try{
//            String sql = "insert into "+this.table2+"(balance,customerId,branch,status) values(?,?,?,?)";
//            ps2 = getStatement(sql,ps2);
//            ps2.setDouble(1,account.getBalance());
//            ps2.setInt(2,account.getCustomerId());
//            ps2.setString(3,account.getBranch());
////            if(account.getBranch().equals("nagai")){
////                throw new Exception();
////            }
//            ps2.setInt(4,1);
//            ps2.executeUpdate();
//            try(ResultSet rs = ps2.getGeneratedKeys()){
//                if(rs.next()){
//                    return rs.getInt(1);
//                }
//            }
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//        return -1;
//    }
//    //When the customer details are added but the account details are failed to add, then deletion can be performed using deleteCustomer.
//    @Override
//    public void deleteCustomer(int customerId) throws Exception{
//        String sql = "Delete from "+this.table1+" where customerId ="+customerId;
//        try(Statement statement = getConnection().createStatement()){
//            statement.executeUpdate(sql);
//        }
//    }
//    //getAccountsList is for getting all the accounts in the database.
//    @Override
//    public List<Account> getAccountsList()throws Exception{
//        List<Account> accountList = new ArrayList<>();
//        String sql = "select customerId,accountNo,balance,branch from "+table2+" where status = 1";
//        try(Statement statement = getConnection().createStatement();ResultSet rs = statement.executeQuery(sql)){
//            while(rs.next()){
//                Account account = new Account();
//                account.setCustomerId(rs.getInt("customerId"));
//                account.setAccountNo(rs.getLong("accountNo"));
//                account.setBalance(rs.getDouble("balance"));
//                account.setBranch(rs.getString("branch"));
//                accountList.add(account);
//            }
//        }
//        return accountList;
//    }
//
//    @Override
//    public boolean removeAccount(long accountNo) {
//        String sql = "Update "+this.table2+" set status = 0 where accountNo = "+accountNo;
//        return updateDb(sql);
//    }
//    @Override
//    public boolean updateDb(String sql) {
//        try (Statement statement = getConnection().createStatement()) {
//            statement.executeUpdate(sql);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean removeCustomer(int customerId) {
//        String sql = "Update "+this.table1+" set status = 0 where customerId = "+customerId;
//        return updateDb(sql);
//    }
//    @Override
//    public boolean updateAmount(double amount, long accountNo) {
//        String sql = "Update "+this.table2+" set balance = "+amount+" where accountNo = "+accountNo+" and status = 1";
//        return updateDb(sql);
//    }
//    @Override
//    public void rollbackAccount(long accountNo){
//        String sql = "Update "+this.table2+" set status = 1 where accountNo = "+accountNo;
//        updateDb(sql);
//    }
//}
//package com.bankapplication.dbconnection;
//
//        import java.sql.Connection;
//        import java.sql.DriverManager;
//        import java.sql.PreparedStatement;
//        import java.sql.Statement;
//        import java.sql.ResultSet;
//        import java.util.List;
//
//
//        import com.bankapplication.account.Account;
//        import com.bankapplication.customer.Customer;
//        import com.bankapplication.mapdata.MapData;
//
//public class DbConnection{
//    private final String url = "jdbc:mysql://127.0.0.1:3306/app";
//    private final String login = "root";
//    private final String pass = "Vishnu@007";
//    private String table1,table2 ;
//    private Connection con = null;
//    private PreparedStatement ps1 = null, ps2=null;
//    //private Map <Integer,Map<Long,Account>> dbHashMap = new HashMap<>();
//    private static boolean flag = false;
//    public Connection getConnection()throws Exception{
//        if(con==null) {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            con = DriverManager.getConnection(url, login, pass);
//        }
//        return con;
//    }
//    public void closeConnection() throws Exception{
//        if(con!=null)
//            con.close();
//        //con=null;
//    }
//    public PreparedStatement getStatement(String sql,PreparedStatement preparedStatement) throws Exception{
//        /*if(ps1==null)
//            //System.out.println("hi");
//            ps1 = con.prepareStatement("insert into "+this.table1+" values(?,?)");
//        if(ps2==null)
//            ps2 = con.prepareStatement("insert into "+this.table2+" values(?,?,?)");*/
//        if(preparedStatement == null){
//            preparedStatement = getConnection().prepareStatement(sql);
//        }
//        return preparedStatement;
//    }
//    public void closeStatement() throws Exception{
//        if(ps1!=null&&ps2!=null){
//            ps1.close();
//            ps2.close();
//        }
//    }
//    public void createTables(String table1,String table2) throws Exception{
//		/*DatabaseMetaData dbm = con.getMetaData();
//		ResultSet tables = dbm.getTables(null, null,table1, null);
//		if(tables.next())
//			return false;
//		tables = dbm.getTables(null, null,table2, null);
//		if(tables.next())
//			return false;
//		tables.close();*/
//        this.table1=table1;
//        this.table2=table2;
//        try(Statement statement = getConnection().createStatement()){
//            String sql1 = "create table if not exists "+this.table1+" (id INTEGER not null, name VARCHAR(255) not null,PRIMARY KEY(id))";
//            statement.executeUpdate(sql1);
//            String sql2 = "create table if not exists "+this.table2+" ( accountNo BIGINT unsigned not null, balance INTEGER not null, id INTEGER not null,PRIMARY KEY(accountNo), FOREIGN KEY (id) REFERENCES "+this.table1+" (id) )";
//            statement.executeUpdate(sql2);
//        }
//        //return true;
//    }
//    public void insertUser(List <Customer> customerList) throws Exception{
//        if(customerList == null)
//            return ;
//		/*try(Statement statement = con.createStatement()){
//			String sql="insert into "+table1+" values("+customer.getId()+",'"+customer.getName()+"')";
//			statement.executeUpdate(sql);
//		}*/
//        //con.setAutoCommit(false);
//        String sql = "insert into "+this.table1+" values(?,?)";
//        ps1 = getStatement(sql,ps1);
//        for(Customer customer : customerList){
//            ps1.setInt(1,customer.getId());
//            ps1.setString(2,customer.getName());
//            //ps1.executeUpdate();
//            ps1.addBatch();
//        }
//        ps1.executeBatch();
//        System.out.println("Customer details inserted successfully");
//        flag = true;
//    }
//    public void insertAccountDetails(List <Account> accountList) throws Exception{
//        if(accountList==null)
//            return ;
//		/*try(Statement statement = con.createStatement()){
//			String sql = "insert into "+table2+" values("+account.getAccountNo()+","+account.getBalance()+","+account.getId()+")";
//			statement.executeUpdate(sql);
//		}*/
//        String sql = "insert into "+this.table2+" values(?,?,?)";
//        ps2 = getStatement(sql,ps2);
//        for (Account account : accountList){
//            ps2.setLong(1,account.getAccountNo());
//            ps2.setInt(2,account.getBalance());
//            ps2.setInt(3,account.getId());
//            //ps2.executeUpdate();
//            ps2.addBatch();
//        }
//        ps2.executeBatch();
//        System.out.println("Account details inserted successfully");
//    }
//    //    public boolean commitValues() throws SQLException {
////        try{
////            con.commit();
////        }
////        catch (Exception exception){
////            con.rollback();
////            return false;
////        }
////        return true;
////    }
//    public /*Map<Integer,Map<Long,Account>>*/ void  retrieveUsers()throws Exception{
//        //if(con==null)
//        //System.out.println("bb");
//        if(flag){
//            System.out.println("hi");
//            getConnection();
//            try(Statement statement = con.createStatement();ResultSet rs = statement.executeQuery("select id,accountNo,balance from "+table2)){
//                while(rs.next()){
//				/*System.out.print(rs.getInt("id")+" "+rs.getString("name")+" "+rs.getLong("no")+" "+rs.getInt("balance"));
//				System.out.println();*/
//                    Account account = new Account();
//                    account.setId(rs.getInt("id"));
//                    account.setAccountNo(rs.getLong("accountNo"));
//                    account.setBalance(rs.getInt("balance"));
//                    MapData.mapData.setDbHashMap(account);
//                /*Map<Long,Account> userMap = dbHashMap.getOrDefault(rs.getInt("id"),new HashMap<Long,Account>());
//                userMap.put(rs.getLong("accountNo"),account);
//                dbHashMap.put(rs.getInt("id"),userMap);*/
//
//                }
//			/*for(int k : hm.keySet()){
//				System.out.println(k);
//				HashMap<Long,Account> temp = hm.get(k);
//				for(Long t : temp.keySet()){
//					System.out.print(t+" "+temp.get(t).getName()+" "+temp.get(t).getBalance()+" ");
//				}
//				System.out.println();
//			}*/
//            }
//            flag=false;
//        }
//
//        //return dbHashMap;
//    }
//}