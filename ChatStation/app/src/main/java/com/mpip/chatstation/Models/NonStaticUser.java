package com.mpip.chatstation.Models;

public class NonStaticUser {

    private String username;
    private String firstName;
    private String lastName;

    public NonStaticUser(String username, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        return firstName + " " + lastName;
    }

    public String getCharsForPic(){
        String result = "";
        if(firstName.length()>0){
            result += firstName.charAt(0) + "";
        }
        if(lastName.length()>0){
            result += lastName.charAt(0) + "";
        }
        if(result.trim().length()<1){
            //result += username.charAt(0) + "" + username.charAt(1) + "";
            //error dava so 2 karaktera od usernamot ako usernamot ni e 1 karakter, ova moze da se sredi ako napravime min 3 karaktera da bide userot
            result += username.charAt(0) + "";
        }
        return result;
    }
}
