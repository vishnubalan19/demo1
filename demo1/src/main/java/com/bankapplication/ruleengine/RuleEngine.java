package com.bankapplication.ruleengine;


public class RuleEngine {
    //validateFloatNumber is for validating the balance.
    public static boolean validateDoubleNumber(String number){
        try{
            Double.parseDouble(number);
        }catch (Exception e){
            return false;
        }
        return true;
    }
    //validateNumber is for validating the number.
    public static boolean validateNumber(String number){
        try{
            if(number.contains(" ")){
                return false;
            }
            Integer.parseInt(number);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
    //validating the string like userName,tableName and branchName.
    public static boolean validateName(String name){
       return (name!=null)&& !name.equals("")&&name.matches("^[a-zA-Z\\s]*$");
    }
    //validating the mobileNo with required length and size.
    public static boolean validateMobileNo(String mobileNo){
        return mobileNo.length()==10 && mobileNo.matches("^[0-9]*$");
    }
    //validating the initial deposit.
    public static boolean validateInitialDeposit(double balance){
        return balance>=500;
    }
}
