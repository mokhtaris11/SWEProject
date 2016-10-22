package business.rulesbeans;

import business.externalinterfaces.Address;
import business.externalinterfaces.DynamicBean;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AddressBean
        implements DynamicBean {

    public AddressBean(Address addr) {
        this.addr = addr;
    }

    //////////// bean interface for address
    public String getCity() {
        return addr.getCity();
    }

    public String getState() {
        return addr.getState();
    }

    public String getStreet() {
        return addr.getStreet1();
    }

    public String getZip() {
        return addr.getZip();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }

    private final Address addr;
    ///////////property change listener code
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

}
