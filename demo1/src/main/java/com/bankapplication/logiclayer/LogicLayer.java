package com.bankapplication.logiclayer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.bankapplication.customer.Customer;
import com.bankapplication.account.Account;
import com.bankapplication.dbconnection.DbConnection;
import com.bankapplication.dbconnection.PersistentLayer;
import com.bankapplication.exception.CustomException;
import com.bankapplication.mapdata.MapData;

public class LogicLayer {
    PersistentLayer persistentLayer  = new DbConnection();
    //Initialisation of which database to choose from properties file.
    public void initPersistentLayer() throws Exception{
//        Properties properties = new Properties();
//        InputStream inputStream = new FileInputStream("C:\\Users\\inc3\\IdeaProjects\\Task3Modified\\src\\db.properties");
//        properties.load(inputStream);
//        Class temp = Class.forName(properties.getProperty("db"));
//        persistentLayer = (PersistentLayer) temp.newInstance();
    	//persistentLayer = new DbConnection();
//        Class tempClass = Class.forName("com.bankapplication.dbconnection.DbConnection");
//        persistentLayer = (PersistentLayer) tempClass.newInstance();
    }
    //creation of tables
    public void createTables(String table1, String table2) throws Exception {
        initPersistentLayer();
        try{
            persistentLayer.createTables(table1,table2);
        }catch (Exception exception){
            throw new CustomException("Enter appropriate table names");
        }
    }
    //getCustomerList is for retrieving the customerList based on the value. value can either be 0 or 1. 0 for failure
    // and 1 for success
    public List<Customer> getCustomerList(Map<Integer,List<List>> tempMap,int value){
        List<List> list = tempMap.get(value);
        List<Customer> customerList = new ArrayList<>();
        for(List tempList : list){
            //As there is a list inside the list, 0th index has the customer object.
            Customer customer = (Customer) tempList.get(0);
            customerList.add(customer);
        }
        return customerList;
    }
    //getAccountList is for retrieving the accountList based on the value. value can either be 0 or 1. 0 for failure
    // and 1 for success
    public List<Account> getAccountList(Map<Integer,List<List>> tempMap,int value){
        List<List> list = tempMap.get(value);
        List<Account> accountList = new ArrayList<>();
        for(List tempList : list){
            //As there is a list inside the list, 1st index has the account object.
            Account account = (Account) tempList.get(1);
            accountList.add(account);
        }
        return accountList;
    }
    //Sending values to database and sending the successful insertion values to the cache map.
    public Map<Integer,List<List>> insertUsers(List<Customer> customerList, List<Account> accountList) {
        Map<Integer,List<List>> tempMap= persistentLayer.insertUsers(customerList,accountList);
        if(tempMap==null){
            return null;
        }
        List<Customer>successCustomerList = getCustomerList(tempMap,1);
        List<Account> successAccountList = getAccountList(tempMap,1);
        MapData.mapData.setCustomers(successCustomerList);
        MapData.mapData.setCustomerBranches(successAccountList);

            System.out.println("from insertusers");
            MapData.mapData.setDbHashValues(successAccountList);

        return tempMap;
    }
    public Map<Integer,Customer> getCustomerMap(){
        return MapData.mapData.getCustomerMap();
    }
    public int insertAccount(Account account){
        int accountNo =  persistentLayer.insertAccount(account);
        if(accountNo==-1)
            return accountNo;
        account.setAccountNo(accountNo);

            System.out.println("from insert accounts");
            MapData.mapData.setDbHashMap(account);

        return accountNo;
    }
    //Retrieving the values from the database.
    public void retrieveUsers() throws Exception {
    	System.out.print("dd");
        List<Account> accountList = persistentLayer.getAccountsList();
        //MapData.mapData.clearDbHashMap();
        System.out.println("from retrieve users "+accountList.size());
        MapData.mapData.setDbHashValues(accountList);
    }
    public List<Customer> getCustomerList() throws Exception{
    	//System.out.println(persistentLayer.getCustomerList().size());
    	return persistentLayer.getCustomerList();
    }
    public void depositAmount(int id,double amount, long accountNo,double balance) throws CustomException {
        try{
            persistentLayer.updateAmount(amount+balance,accountNo);
        }
        catch (Exception exception){
            throw new CustomException("Deposition failed.");
        }
        //if(getDbHashMap()!=null){
            System.out.println("from deposit");
            MapData.mapData.updateAccount(id,accountNo,amount+balance);
        //}
    }
    public void withdrawAmount(int id,double amount, long accountNo,double balance) throws CustomException{
        try {
            persistentLayer.updateAmount(balance - amount, accountNo);
        }
        catch (Exception exception){
            throw new CustomException("Amount Withdraw failed");
        }
        //if(getDbHashMap()!=null){
            System.out.println("from withdraw");
            MapData.mapData.updateAccount(id,accountNo,balance-amount);
        //}
    }
    public void removeAccount(int customerId, long accountNo) throws CustomException{
        try {
            persistentLayer.removeAccount(accountNo);
        }
        catch (Exception exception){
            System.out.println("rm a");
            throw new CustomException("Account deletion failed");
        }
        String branch = getDbHashMap().get(customerId).get(accountNo).getBranch();
        if(getDbHashMap().get(customerId).size()==1){
            try{
                removeCustomer(customerId);
            }catch (Exception exception){
                System.out.println("rm c");
                persistentLayer.rollbackAccount(accountNo);
                throw new CustomException("Account deletion failed");
            }
            MapData.mapData.removeCustomer(customerId,accountNo);
            return;
        }
        MapData.mapData.removeAccount(customerId,accountNo,branch);
    }
    public void removeCustomer(int customerId) throws CustomException{
        try{
            persistentLayer.removeCustomer(customerId);
        }
        catch (Exception exception){
            throw new CustomException(exception.getMessage());
        }
    }
    public void insertUsers(Account account,Customer customer) throws CustomException{
        try{
            persistentLayer.activateCustomer(account.getAccountNo(),customer.getCustomerId());
        }
        catch (Exception exception){
            throw new CustomException(exception.getMessage());
        }
        MapData.mapData.activateCustomer(account,customer);
    }
    public Map<Integer,Map<Long,Account>> getDbHashMap(){
        return MapData.mapData.getDbHashMap();
    }
    public Map<Integer, List<String>> getCustomerBranchMap(){
        return MapData.mapData.getCustomerBranchMap();
    }
    public Map<Integer,Customer> getDeactivatedCustomerMap(){
        return MapData.mapData.getDeactivatedCustomerMap();
    }
    public Map<Integer, Map<Long,Account>> getDeactivatedDbHashMap(){ return  MapData.mapData.getDeactivatedDbHashMap();}
    //Closing the necessary statement from the database
    public void cleanUp() throws Exception{
        persistentLayer.closeStatement();
        persistentLayer.closeConnection();
    }
}
