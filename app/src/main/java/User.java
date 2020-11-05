public class User {

    private String email;
    private String phoneNumber;
    private String fullName;

    public User(String fullName, String email, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    //private String uID;

    public void setFullName(String fullName){
        this.fullName = fullName;
    }
    public void getPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
    public void getEmail(String email){
        this.email = email;
    }

    public String getName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
