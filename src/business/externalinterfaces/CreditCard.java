package business.externalinterfaces;

public interface CreditCard {

    String getNameOnCard();

    String getExpirationDate();

    String getCardNum();

    String getCardType();

    void setNameOnCard(String name);

    void setExpirationDate(String date);

    void setCardNum(String num);

    void setCardType(String type);

}
