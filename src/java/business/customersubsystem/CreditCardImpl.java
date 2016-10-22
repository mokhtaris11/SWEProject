package business.customersubsystem;

import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CreditCardForTest;

class CreditCardImpl
        implements CreditCard, CreditCardForTest {

    String nameOnCard;
    String expirationDate;
    String cardNum;
    String cardType;

    CreditCardImpl() {

    }

    CreditCardImpl(String nameOnCard, String expirationDate,
            String cardNum, String cardType) {
        this.nameOnCard = nameOnCard;
        this.expirationDate = expirationDate;
        this.cardNum = cardNum;
        this.cardType = cardType;
    }

    @Override
    public String getNameOnCard() {
        return nameOnCard;
    }

    @Override
    public String getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String getCardNum() {
        return cardNum;
    }

    @Override
    public String getCardType() {
        return cardType;
    }

    @Override
    public void setNameOnCard(String name) {
        nameOnCard = name;
    }

    @Override
    public void setExpirationDate(String date) {
        expirationDate = date;
    }

    @Override
    public void setCardNum(String num) {
        cardNum = num;
    }

    @Override
    public void setCardType(String type) {
        cardType = type;
    }

}
