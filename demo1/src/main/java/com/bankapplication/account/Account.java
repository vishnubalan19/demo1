package com.bankapplication.account;
//Account is the pojo class
public class Account{
    private int customerId;
    private double balance;
    private long accountNo;
	private String branch;
    public void setAccountNo(long accountNo){
        this.accountNo = accountNo;
    }
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    public void setBalance(double balance){
        this.balance = balance;
    }
	public void setBranch(String branch){
		this.branch = branch;
	}
    public long getAccountNo(){
        return this.accountNo;
    }
    public int getCustomerId(){
        return this.customerId;
    }
    public double getBalance(){
        return this.balance;
    }
	public String getBranch(){
		return this.branch;
	}
	@Override
	public String toString(){
		return this.getAccountNo() +" "+this.getCustomerId()+" "+this.getBalance()+" "+this.getBranch();
	}
}