package com.bankapplication.mapdata;

import com.bankapplication.account.Account;
import com.bankapplication.customer.Customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public enum MapData{
    mapData;
    private final Map<Integer, Customer> customerMap = new HashMap<>();
    private Map <Integer,Map<Long, Account>> dbHashMap = null;
    private final Map<Integer,List<String>> customerBranchMap = new HashMap<>();
    private Map<Integer,Customer> deactivatedCustomerMap = new HashMap<>();
    private Map <Integer,Map<Long,Account>> deactivatedDbHashMap = new HashMap<>();
    private List<Account> accountList = new ArrayList<>();
    //Storing the customer data in Map.
    public void setCustomers(List <Customer> customerList){
        for(Customer customer : customerList){
            setCustomerMap(customer);
        }
    }
    public void setCustomerMap(Customer customer){
        customerMap.put(customer.getCustomerId(),customer);
    }
    public Map<Integer,Customer> getCustomerMap(){
        return customerMap;
    }
    //Storing the customer and their branch relation in Map.
    public void setCustomerBranches( List<Account> accountList){
        if(accountList==null){
            return;
        }
        for(Account account : accountList){
            setCustomerBranchMap(account.getCustomerId(),account.getBranch());
        }
    }
    public void setCustomerBranchMap(int customerId,String branch){
        List<String > branchList = customerBranchMap.getOrDefault(customerId,new ArrayList<>());
        branchList.add(branch);
        customerBranchMap.put(customerId,branchList);
    }
    public void setAccountList(List<Account> accountList) {
    	this.accountList = accountList;
    }
    public List<Account> getAccountList(){
    	return this.accountList;
    }
    public Map<Integer,List<String>> getCustomerBranchMap(){
        return customerBranchMap;
    }
    public void clearDbHashMap(){
        dbHashMap.clear();
    }
    //setDbHashValues is for setting the dbHashMap.
	public void setDbHashValues(List<Account> accountList){
        if(accountList==null){
            return;
        }
        if(accountList.size()==0){
            return;
        }
        if(getDbHashMap()==null){
            this.dbHashMap = new HashMap<>();
        }
        setAccountList(accountList);
        for(Account account : accountList){
            setDbHashMap(account);
        }
    }
    public void setDbHashMap(Account account){
        setDetailsMap(dbHashMap,account);
    }
    public void setDetailsMap(Map<Integer,Map<Long,Account>> dbHashMap,Account account){
        Map<Long,Account> userMap = dbHashMap.getOrDefault(account.getCustomerId(), new HashMap<>());
        userMap.put(account.getAccountNo(),account);
        dbHashMap.put(account.getCustomerId(),userMap);
    }
    public void activateCustomer(Account account,Customer customer){
        setDetailsMap(dbHashMap,account);
        setCustomerMap(customer);
        setCustomerBranchMap(customer.getCustomerId(),account.getBranch());
        deactivatedDbHashMap.remove(customer.getCustomerId());
        deactivatedCustomerMap.remove(customer);
    }
    public void setDeactivatedDbHashMap(Account account){
        setDetailsMap(deactivatedDbHashMap,account);
    }
    public void updateAccount(int customerId,long accountNo, double balance){
        dbHashMap.get(customerId).get(accountNo).setBalance(balance);
    }
    public Map<Integer,Map<Long,Account>> getDbHashMap(){
        return dbHashMap;
    }
    public void removeAccount(int customerId,long accountNo,String branch){
        setDeactivatedDbHashMap(getDbHashMap().get(customerId).remove(accountNo));
        getCustomerBranchMap().get(customerId).remove(branch);
    }
    public void removeCustomer(int customerId, long accountNo){
        setDeactivatedDbHashMap(getDbHashMap().get(customerId).remove(accountNo));
        getDbHashMap().remove(customerId);
        setDeactivatedCustomerMap(customerId,getCustomerMap().get(customerId));
        getCustomerMap().remove(customerId);
        getCustomerBranchMap().remove(customerId);
    }

    public void setDeactivatedCustomerMap(int customerId, Customer customer) {
        this.deactivatedCustomerMap.put(customerId,customer);
    }

    public Map<Integer,Customer> getDeactivatedCustomerMap() {
        return deactivatedCustomerMap;
    }

    public Map<Integer, Map<Long, Account>> getDeactivatedDbHashMap() {
        return deactivatedDbHashMap;
    }
}


//package com.bankapplication.mapdata;
//
//        import com.bankapplication.account.Account;
//        import com.bankapplication.customer.Customer;
//
//        import java.util.HashMap;
//        import java.util.Map;
//
///*public class MapData {
//    private MapData(){
//
//    }
//    private static MapData mapData=null;
//    public static MapData getInstance(){
//        if(mapData==null)
//            mapData=new MapData();
//        return mapData;
//    }
//    private Map<Integer, Customer> customerMap = new HashMap<>();
//    private Map <Integer,Map<Long, Account>> dbHashMap = new HashMap<>();
//    public void setCustomerMap(Customer customer){
//        customerMap.put(customer.getId(),customer);
//    }
//    public Map<Integer,Customer> getCustomerMap(){
//        return customerMap;
//    }
//
//    public void setDbHashMap(Account account){
//        Map<Long,Account> userMap = dbHashMap.getOrDefault(account.getId(), new HashMap<>());
//        //System.out.println("1");
//        userMap.put(account.getAccountNo(),account);
//        dbHashMap.put(account.getId(),userMap);
//    }
//    public Map<Integer,Map<Long,Account>> getDbHashMap(){
//        return dbHashMap;
//    }
//}*/
//public enum MapData{
//    mapData;
//    private Map<Integer, Customer> customerMap = new HashMap<>();
//    private Map <Integer,Map<Long, Account>> dbHashMap = new HashMap<>();
//    public void setCustomerMap(Customer customer){
//        customerMap.put(customer.getCustomerId(),customer);
//    }
//    public Map<Integer,Customer> getCustomerMap(){
//        return customerMap;
//    }
//
//    public void setDbHashMap(Account account){
//        Map<Long,Account> userMap = dbHashMap.getOrDefault(account.getCustomerId(), new HashMap<>());
//        //System.out.println("1");
//        if(!userMap.containsKey(account.getAccountNo())){
//            System.out.println("hi from md");
//            userMap.put(account.getAccountNo(),account);
//            dbHashMap.put(account.getCustomerId(),userMap);
//        }
//    }
//    public Map<Integer,Map<Long,Account>> getDbHashMap(){
//        return dbHashMap;
//    }
//}