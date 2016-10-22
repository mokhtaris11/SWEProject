package business.customersubsystem;

import business.externalinterfaces.CustomerProfile;

class CustomerProfileImpl
        implements CustomerProfile {

    private String firstName;
    private String lastName;
    private Integer custId;

    //CustomerProfile(){}

    CustomerProfileImpl(Integer custid, String firstName, String lastName) {
        this.custId = custid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public Integer getCustId() {
        return custId;
    }

    @Override
    public void setCustId(Integer id) {
        custId = id;
    }
}
