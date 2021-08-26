package com.bankapplication.customer;
//Customer is the pojo class.
public class Customer{
    private int customerId;
    private String name;
    private long mobileNo;
    public void setCustomerId(int customerId){
        this.customerId = customerId;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setMobileNo(long mobileNo){
        this.mobileNo = mobileNo;
    }
    public int getCustomerId(){
        return customerId;
    }
    public String getName(){
        return name;
    }

    public long getMobileNo() {
        return mobileNo;
    }

    @Override
	public String toString(){
		return this.getName()+" "+this.getCustomerId()+" "+this.getMobileNo();
	}
}