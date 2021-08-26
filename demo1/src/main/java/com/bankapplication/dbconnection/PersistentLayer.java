package com.bankapplication.dbconnection;

import com.bankapplication.account.Account;
import com.bankapplication.customer.Customer;
import com.bankapplication.exception.CustomException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public interface PersistentLayer {
    Connection getConnection() throws Exception;
    void closeConnection() throws Exception;
    PreparedStatement getStatement(String sql,PreparedStatement preparedStatement) throws Exception;
    void closePreparedStatement(PreparedStatement preparedStatement) throws Exception;
    void closeStatement()throws Exception;
    void createTables(String table1, String table2) throws CustomException;
    void createTable(String sql) throws Exception;
    Map<Integer,List<List>> insertUsers( List<Customer>customerList,List<Account>accountList);
    int insertUser(Customer customer) throws Exception;
    int insertAccount(Account account);
    void deleteCustomer(int customerId) throws Exception;
    List<Account> getAccountsList() throws Exception;
    void updateAmount(double amount, long accountNo) throws CustomException;
    void removeAccount(long accountNo) throws CustomException;
    void removeCustomer(int customerId) throws CustomException;
    void updateDb(String sql) throws CustomException;
    void rollbackAccount(long accountNo) throws CustomException;
    void activateCustomer(long accountNo, int customerId) throws CustomException;
    List<Customer> getCustomerList() throws Exception;
}
