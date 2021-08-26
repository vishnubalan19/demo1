package com.bankapplication.dbapplication;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.List;

import com.bankapplication.logiclayer.LogicLayer;
import com.bankapplication.account.Account;
import com.bankapplication.customer.Customer;
import com.bankapplication.ruleengine.RuleEngine;

public class DbApplication{
	Scanner scanner = new Scanner(System.in);
	LogicLayer logicLayer = new LogicLayer();
	public Account getCustomerDetails(){
		System.out.println("Enter id");
		String tempId = scanner.nextLine();
		if(!RuleEngine.validateNumber(tempId)){
			System.out.println("Enter appropriate values");
			return null;
		}
		//Receiving the dbHashMap from the logicLayer.
		int id = Integer.parseInt(tempId);
		Map<Integer,Map<Long,Account>>dbHashMap=logicLayer.getDbHashMap();
		Map<Integer,Customer> customerMap=logicLayer.getCustomerMap();
		if(dbHashMap==null || !dbHashMap.containsKey(id) || customerMap==null){
			System.out.println("Enter valid user id");
			return null;
		}
		Map<Long,Account> tempMap = dbHashMap.get(id);
		System.out.println("Enter the Account No");
		String tempAccountNo = scanner.nextLine();
		if(!RuleEngine.validateNumber(tempAccountNo)){
			System.out.println("Enter appropriate values");
			return null;
		}
		long accountNo = Integer.parseInt(tempAccountNo);
		if(!tempMap.containsKey(accountNo)){
			System.out.println("Account is not available or Enter correct Account No.");
			return null;
		}
		return tempMap.get(accountNo);
	}
	public void enterUserChoice()throws Exception{
        Map <Integer,Customer> customerMap;
        Map <Integer,Map<Long,Account>> dbHashMap ;
		boolean choiceFlag = true;
		//choiceFlag is for loop to happen until user wants to exit.
		while(choiceFlag){
			System.out.println("Database Application");
			System.out.println("1. Create User details table and Account table ");
			System.out.println("2. Insert Details");
			System.out.println("3. Get User and Account information");
			System.out.println("4. Deposit Amount");
			System.out.println("5. Withdraw Amount");
			System.out.println("6. Delete Account");
			System.out.println("7. Reactivate User");
			System.out.println("8. Exit");
			String tempChoice = scanner.nextLine();
			int choice ;
			//Below statement will execute if choice is irrelevant data type.
			if(!RuleEngine.validateNumber(tempChoice)){
				System.out.println("Enter appropriate values");
			}
			//If choice input is fine for processing, then the else block will execute.
			else{
				choice = Integer.parseInt(tempChoice);
				switch(choice){
					case 1:
						String table1,table2;
						System.out.println("Enter the table name which contains user id and user name");
						table1=scanner.nextLine();
						//Validating both the table names. If it fails, then it will break the switch.
						if(!RuleEngine.validateName(table1)){
							System.out.println("Enter appropriate table name");
							break;
						}
						System.out.println("Enter the table name which contains account no, account balance and id");
						table2=scanner.nextLine();
						if(!RuleEngine.validateName(table2)){
							System.out.println("Enter appropriate table name");
							break;
						}
						//flag is for acknowledging the user, whether table created successfully or not.
						try{
							logicLayer.createTables(table1,table2);
						}
						catch(Exception exception){
							System.out.println(exception.getMessage());
							break;
						}
						System.out.println("Table created successfully");
						break;
					case 2:
						boolean insertFlag = true;
						//insertFlag is for looping the below blocks of statement, until the user wants to exit.
						while(insertFlag) {
							System.out.println("1. New Users");
							System.out.println("2. Existing users");
							System.out.println("3. Exit");
							System.out.println("Enter your choice");
							String tempInsertChoice = scanner.nextLine();
							//Below statement is for validating the user input.
							if(!RuleEngine.validateNumber(tempInsertChoice)){
								System.out.println("Enter appropriate values");
								break;
							}
							int insertChoice = Integer.parseInt(tempInsertChoice);
							switch (insertChoice) {
								case 1:
									System.out.println("Enter the no. of users");
									String tempNoOfUsers = scanner.nextLine();
									//validation
									if(!RuleEngine.validateNumber(tempNoOfUsers)){
										System.out.println("Enter appropriate values");
										break;
									}
									int noOfUsers = Integer.parseInt(tempNoOfUsers);
									List<Customer> customerList = new ArrayList<>();
									List<Account> accountList = new ArrayList<>();
									//innerFlag is for to exit the switch from the below loop.(break the loop if condition fails and change the
									// flag to terminate the switch)
									boolean innerFlag=true;
									while (noOfUsers-- > 0) {
										//scanner.nextLine();
										//validation
										System.out.println("Enter the user name");
										String name = scanner.nextLine();
										if(!RuleEngine.validateName(name)){
											System.out.println("Enter appropriate Name");
											innerFlag = false;
											break;
										}
										System.out.println("Enter the User MobileNo");
										String mobileNumber = scanner.nextLine();
										if(!RuleEngine.validateMobileNo(mobileNumber)){
											System.out.println("Enter appropriate MobileNo");
											innerFlag = false;
											break;
										}
										long mobileNo = Long.parseLong(mobileNumber);
										System.out.println(mobileNo);
										//Insertion of customer details into its pojo class
										Customer customer = new Customer();
										customer.setName(name);
										customer.setMobileNo(mobileNo);
										customerList.add(customer);

										System.out.println("Enter the branch");
										String branch = scanner.nextLine();
										//validation
										if(!RuleEngine.validateName(branch)){
											System.out.println("Enter the appropriate branch name");
											innerFlag = false;
											break;
										}
										System.out.println("Enter the Initial Deposit");
										String tempBalance = scanner.nextLine();
										if(!RuleEngine.validateDoubleNumber(tempBalance)){
											System.out.println("Enter appropriate balance");
											innerFlag = false;
											break;
										}
										double balance = Double.parseDouble(tempBalance);
										if(!RuleEngine.validateInitialDeposit(balance)){
											System.out.println("Enter the appropriate Initial deposit (Minimum balance should be 500)");
											innerFlag = false;
											break;
										}
										//Insertion of account details into account object.
										Account account = new Account();
										account.setBalance(balance);
										account.setBranch(branch);
										accountList.add(account);
									}
									if(!innerFlag){
										break;
									}
									//If all the input are in the appropriate form, then below statements will execute.
									//tempMap contains two list for success and failure records.
									Map<Integer,List<List>> tempMap = logicLayer.insertUsers(customerList, accountList);
									System.out.println("Total records sent - "+accountList.size());
									//1 in the below statement is for successful records.
									List<Account> tempAccountList = logicLayer.getAccountList(tempMap,1);
									System.out.println("Successful insertion - "+tempAccountList.size());
									System.out.println("AccountNo  CustomerId  InitialDeposit  Branch");
									for(Account account : tempAccountList){
										System.out.println(account);
									}
									//0 in the above statement is for failure records.
									tempAccountList = logicLayer.getAccountList(tempMap,0);
									System.out.println("Failure insertion - "+tempAccountList.size());
									if(tempAccountList.size()==0){
										break;
									}
									System.out.println("AccountNo  CustomerId  InitialDeposit  Branch");
									for(Account account : tempAccountList){
										System.out.println(account);
									}
									break;
								case 2:
									customerMap = logicLayer.getCustomerMap();
									System.out.println("Enter the customer id");
									String tempCustomerId = scanner.nextLine();
									//validation
									if(!RuleEngine.validateNumber(tempCustomerId)){
										System.out.println("Enter appropriate customer id");
										break;
									}
									int customerId = Integer.parseInt(tempCustomerId);
									Customer customer = customerMap.get(customerId);
									if(customer==null){
										System.out.println("Invalid user id");
										break;
									}
									System.out.println("Enter the MobileNo");
									String mobileNumber = scanner.nextLine();
									if(!RuleEngine.validateMobileNo(mobileNumber)){
										System.out.println("Enter appropriate Mobile number");
										break;
									}
									long mobileNo = Long.parseLong(mobileNumber);
									System.out.println("Enter the branch");
									String branch = scanner.nextLine();
									if(!RuleEngine.validateName(branch)){
										System.out.println("Enter appropriate branch");
										break;
									}
									Map<Integer,List<String>>customerBranchMap = logicLayer.getCustomerBranchMap();
									List <String>branchList = customerBranchMap.get(customerId);
									if (branchList==null || branchList.contains(branch) || customer.getMobileNo() != mobileNo) {
										System.out.println("Enter valid MobileNo or Account may be exist in the branch");
										break;
									}
									System.out.println("Enter the initial deposit");
									String tempBalance = scanner.nextLine();
									if(!RuleEngine.validateDoubleNumber(tempBalance)){
										System.out.println("Enter appropriate balance");
										break;
									}
									double balance = Double.parseDouble(tempBalance);
									if(!RuleEngine.validateInitialDeposit(balance)){
										System.out.println("Enter the appropriate Initial deposit (Minimum balance should be 500)");
										break;
									}
									//Setting the account details into account object.
									Account account = new Account();
									account.setCustomerId(customer.getCustomerId());
									account.setBalance(balance);
									account.setBranch(branch);
									int accountNo = logicLayer.insertAccount(account);
									if(accountNo==-1){
										System.out.println("Enter appropriate account details");
										break;
									}
									System.out.println("AccountNo  CustomerId  InitialDeposit  Branch");
									System.out.println(account);
									break;
								case 3:
									insertFlag = false;
									break;
								default:
									System.out.println("Enter appropriate choice");
									break;
							}
						}
						break;
					case 3:
						//retrieveFlag is for looping below statement until the user wants to exit.
						boolean retrieveFlag=true;
						while(retrieveFlag){
							System.out.println("1. Find users");
							System.out.println("2. Exit");
							System.out.println("Enter your choice");
							String tempRetrieveChoice = scanner.nextLine();
							//validation
							if(!RuleEngine.validateNumber(tempRetrieveChoice)){
								System.out.println("Enter appropriate values");
								break;
							}
							int retrieveChoice = Integer.parseInt(tempRetrieveChoice);
							switch(retrieveChoice){
								case 1 :
									if(logicLayer.getDbHashMap()==null){
										System.out.println("no retrieve");
										//logicLayer.retrieveUsers();
									}
									System.out.println("Enter id");
									String tempId = scanner.nextLine();
									if(!RuleEngine.validateNumber(tempId)){
										System.out.println("Enter appropriate values");
										break;
									}
									//Receiving the dbHashMap from the logicLayer.
									int id = Integer.parseInt(tempId);
									dbHashMap=logicLayer.getDbHashMap();
									customerMap=logicLayer.getCustomerMap();
									if(dbHashMap==null || !dbHashMap.containsKey(id) || customerMap==null){
										System.out.println("Enter valid user id");
										break;
									}
									Map<Long,Account> tempMap = dbHashMap.get(id);
									for(Long t : tempMap.keySet()){
										Account account = tempMap.get(t);
										Customer customer = customerMap.get(account.getCustomerId());
										System.out.println("Name  CustomerId  MobileNo");
										System.out.println(customer);
										System.out.println("AccountNo  CustomerId  InitialDeposit  Branch");
										System.out.println(account);
										System.out.println();
									}
									break;
								case 2 :
									retrieveFlag=false;
									break;
								default :
									System.out.println("Enter valid choice");
									break;
							}
						}
						break;
					case 4:
						Account account = getCustomerDetails();
						if(account==null){
							break;
						}
						System.out.println("Enter the amount that you want to deposit");
						String varDepositAmount = scanner.nextLine();
						if(!RuleEngine.validateDoubleNumber(varDepositAmount)){
							System.out.println("Enter appropriate amount");
							break;
						}
						double depositAmount = Double.parseDouble(varDepositAmount);
						try{
							logicLayer.depositAmount(account.getCustomerId(),depositAmount,account.getAccountNo(),account.getBalance());
						}
						catch (Exception exception){
							System.out.println(exception.getMessage());
							break;
						}
						System.out.println("Amount deposited successfully");
						break;
					case 5:
						account = getCustomerDetails();
						if(account==null){
							break;
						}
						System.out.println("Enter the amount that you want to withdraw");
						String varWithdrawAmount = scanner.nextLine();
						if(!RuleEngine.validateDoubleNumber(varWithdrawAmount)){
							System.out.println("Enter appropriate amount");
							break;
						}
						double withdrawAmount = Double.parseDouble(varWithdrawAmount);
						if(!RuleEngine.validateInitialDeposit(account.getBalance()-withdrawAmount)){
							System.out.println("Account should minimum have 500 rupees");
							break;
						}
						try {
							logicLayer.withdrawAmount(account.getCustomerId(),withdrawAmount,account.getAccountNo(),account.getBalance());
						}catch (Exception exception){
							System.out.println(exception.getMessage());
							break;
						}
						System.out.println("Amount withdrawal is successful");
						break;
					case 6:
						account = getCustomerDetails();
						if(account==null){
							break;
						}
						try{
							logicLayer.removeAccount(account.getCustomerId(), account.getAccountNo());
						}catch (Exception exception){
							System.out.println(exception.getMessage());
							break;
						}
						System.out.println("Account deleted successfully");
						break;
					case 7:
						Map<Integer,Map<Long,Account>> tempDbHashMap = logicLayer.getDeactivatedDbHashMap();
						System.out.println("Enter User id");
						String tempId = scanner.nextLine();
						if(!RuleEngine.validateNumber(tempId)){
							System.out.println("Enter appropriate values");
							break;
						}
						int id = Integer.parseInt(tempId);
						if(!tempDbHashMap.containsKey(id)){
							System.out.println("Enter appropriate customer id");
							break;
						}
						System.out.println("Enter the accountNo to activate");
						String tempAccountNo = scanner.nextLine();
						if(!RuleEngine.validateNumber(tempAccountNo)){
							System.out.println("Enter appropriate accountNo");
							break;
						}
						long accountNo = Integer.parseInt(tempAccountNo);
						if(!tempDbHashMap.get(id).containsKey(accountNo))
						{
							System.out.println("Enter appropriate accountNo");
							break;
						}
						Map<Long,Account> tempAccountMap = tempDbHashMap.get(id);
						Customer customer = logicLayer.getDeactivatedCustomerMap().get(id);
						try{
							logicLayer.insertUsers(tempAccountMap.get(accountNo),customer);
						}
						catch (Exception exception){
							System.out.println(exception.getMessage());
							break;
						}
						System.out.println("Account Reactivated Successfully");
//						boolean reactivationFlag = true;
//						while(reactivationFlag){
//							break;
//						}

						break;
					case 8:
						//cleanUp() will close all the statements.
						logicLayer.cleanUp();
						choiceFlag=false;
						break;
					default:
						System.out.println("Enter appropriate choice");
						break;
				}
			}
		}
    }
        
    public static void main(String [] args) throws Exception{
        new DbApplication().enterUserChoice();
    }
}


//package com.bankapplication.dbapplication;
//        import java.util.ArrayList;
//        import java.util.Map;
//
////import java.util.HashMap;
//        import java.util.Scanner;
//        import java.util.List;
////import java.util.ArrayList;
//
//        import com.bankapplication.dbconnection.DbConnection;
//        import com.bankapplication.account.Account;
//        import com.bankapplication.customer.Customer;
//        import com.bankapplication.mapdata.MapData;
//
//public class DbApplication{
//    private static int id = 1;
//    private DbConnection dbConnection = new DbConnection();
//    private Map <Integer,Customer> customerMap;
//    private Map <Integer,Map<Long,Account>> dbHashMap ;
//    private List <Customer> customerList = new ArrayList<>();
//    private List <Account> accountList = new ArrayList<>();
//    public void getUserChoice()throws Exception{
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Database Application");
//        System.out.println("1. Create User details table and Account table ");
//        System.out.println("2. Insert values into User details table and Account table");
//        System.out.println("3. Get User and Account information");
//        System.out.println("4. Exit");
//        int choice = scanner.nextInt();
//        switch(choice){
//            case 1:
//                String table1,table2;
//                System.out.println("Enter the table name which contains user id and user name");
//                table1=scanner.next();
//                System.out.println("Enter the table name which contains account no, account balance and id");
//                table2=scanner.next();
//                //if(dbConnection.createTables(table1,table2))
//                dbConnection.createTables(table1,table2);
//                System.out.println("Tables are created successfully");
//                //else
//                //System.out.println("Enter unique table names");
//                getUserChoice();
//                break;
//            case 2:
//                System.out.println("Enter the no. of users");
//                int noOfUsers=scanner.nextInt();
//                while(noOfUsers-->0){
//                    scanner.nextLine();
//                    System.out.println("Enter the user name");
//                    String name = scanner.nextLine();
//                    //System.out.println(name);
//                    Customer customer = new Customer();
//                    customer.setCustomerId(id);
//                    customer.setName(name);
//                    //customerMap.put(id,customer);
//                    MapData.mapData.setCustomerMap(customer);
//                    customerList.add(customer);
//                    //dbConnection.insertUser(customer);
//                    //scanner.nextLine();
//                    System.out.println("Enter the no. of accounts for user");
//                    //scanner.nextLine();
//                    int noOfAccounts = scanner.nextInt();
//                    while(noOfAccounts-->0){
//                        System.out.println("Enter the accountNo and balance");
//                        long accountNo = scanner.nextLong();
//                        int balance = scanner.nextInt();
//                        Account account = new Account();
//                        account.setAccountNo(accountNo);
//                        account.setCustomerId(id);
//                        account.setBalance(balance);
//                        accountList.add(account);
//                        //dbConnection.insertAccountDetails(account);
//                    }
//                    id++;
//                }
//                dbConnection.insertUser(customerList);
//                dbConnection.insertAccountDetails(accountList);
//                customerList.clear();
//                accountList.clear();
////                boolean status = dbConnection.commitValues();
////                if(status)
////                    System.out.println("Data inserted successfully");
////                else
////                    System.out.println("Data failed to insert");
//                getUserChoice();
//                break;
//            case 3:
//                //dbHashMap = dbConnection.retrieveUsers();
//                dbConnection.retrieveUsers();
//                boolean flag=true;
//                while(flag){
//                    System.out.println("1. Find users");
//                    System.out.println("2. Exit");
//                    System.out.println("Enter your choice");
//                    int curChoice = scanner.nextInt();
//                    switch(curChoice){
//                        case 1 :
//                            System.out.println("Enter id");
//                            int id = scanner.nextInt();
//                            dbHashMap=MapData.mapData.getDbHashMap();
//                            customerMap=MapData.mapData.getCustomerMap();
//                            if(dbHashMap.containsKey(id)){
//                                Map<Long,Account> tempMap = dbHashMap.get(id);
//                                for(Long t : tempMap.keySet()){
//                                    System.out.print(tempMap.get(t).getCustomerId()+" "+customerMap.get(tempMap.get(t).getCustomerId()).getName()+" "+tempMap.get(t).getAccountNo()+" "+tempMap.get(t).getBalance());
//                                    System.out.println();
//                                }
//                            }
//                            else{
//                                System.out.println("Enter valid user id");
//                            }
//                            break;
//                        case 2 :
//                            flag=false;
//                            break;
//                        default :
//                            System.out.println("Enter valid choice");
//                            break;
//                    }
//                }
//                getUserChoice();
//                break;
//            case 4:
//                dbConnection.closeStatement();
//                dbConnection.closeConnection();
//                break;
//            default:
//                System.out.println("Enter appropriate choice");
//                getUserChoice();
//                break;
//        }
//        //System.out.println("");
//    }
//    public static void main(String [] args) throws Exception{
//        new DbApplication().getUserChoice();
//    }
//}
//

