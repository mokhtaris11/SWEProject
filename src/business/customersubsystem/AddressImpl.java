package business.customersubsystem;

import business.externalinterfaces.Address;

/**
 *  pcorazza
 * @since Nov 4, 2004 Class Description:
 *
 *
 */
class AddressImpl
        implements Address {

    AddressImpl() {
    }

    AddressImpl(String street, String city, String state, String zip) {
        this.street1 = street;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    AddressImpl(String street1, String street2, String city, String state, String zip) {
        this.street1 = street1;
        this.street2 = street2;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }
    private Integer addressId;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;

    @Override
    public Integer getAddressId() {
        return addressId;
    }

    @Override
    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getStreet1() {
        return street1;
    }

    @Override
    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    @Override
    public String getStreet2() {
        return street2;
    }

    @Override
    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    @Override
    public String getZip() {
        return zip;
    }

    @Override
    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String toString() {
        String n = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append("Street1: ").append(street1).append(n);
        sb.append("Street2: ").append(street2).append(n);
        sb.append("City: ").append(city).append(n);
        sb.append("State: ").append(state).append(n);
        sb.append("Zip: ").append(zip).append(n);
        return sb.toString();
    }
}
