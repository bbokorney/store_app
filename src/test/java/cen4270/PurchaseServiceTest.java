package cen4270;

import cen4270.fakes.AccountFake;
import cen4270.fakes.BankFake;
import cen4270.fakes.InventoryFake;
import cen4270.fakes.NotificationFake;
import cen4270.models.*;
import cen4270.services.PurchaseService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Date;

@RunWith(JUnit4.class)
public class PurchaseServiceTest {
    @Test
    public void purchaseItem_ItemAndUserExist_CreditCardCharged() throws Exception {
        // Arrange
        BankFake bankMock = getBank();

        PurchaseService purchaseService = new PurchaseService(
                getAccount(),
                bankMock,
                getInventory(),
                getNotification()
        );

        // Act
        purchaseService.purchaseItem(getUser().getEmail(), getItem().getID(), 1);

        // Assert
        Assert.assertEquals(getCreditCard(), bankMock.getCreditCard());
        Assert.assertEquals(10, bankMock.getAmount());
    }

    @Test
    public void purchaseItem_ItemAndUserExist_EmailSent() throws Exception {
        // Arrange
        NotificationFake notificationMock = getNotification();

        PurchaseService purchaseService = new PurchaseService(
                getAccount(),
                getBank(),
                getInventory(),
                notificationMock
        );

        // Act
        purchaseService.purchaseItem(getUser().getEmail(), getItem().getID(), 1);

        // Assert
        Assert.assertEquals(getUser().getEmail(), notificationMock.getEmail().getTo());
    }

    private CreditCard getCreditCard() {
        return new CreditCard("1234567891234567", new Date(1602271292000l));
    }

    private User getUser() {
        return new User("Baker", "Bokorney", "jpava@fiu.edu", getCreditCard(), Region.East, true);
    }

    private AccountFake getAccount() {
        return new AccountFake(getUser());
    }

    private Item getItem() {
        return new Item("id", "book", 5, 1);
    }

    private InventoryFake getInventory() {
        return new InventoryFake(getItem());
    }

    private NotificationFake getNotification() {
        return new NotificationFake();
    }

    private BankFake getBank() {
        return new BankFake();
    }
}
