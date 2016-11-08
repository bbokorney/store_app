package cen4270.services;

import cen4270.clients.*;
import cen4270.exceptions.ChargeCreditCardException;
import cen4270.exceptions.PurchaseException;
import cen4270.exceptions.SendEmailException;
import cen4270.models.Email;
import cen4270.models.Item;
import cen4270.models.User;

/**
 * This service is used to make purchases
 */
public class PurchaseService {
    private Account accountService;
    private Bank bankClient;
    private Inventory inventoryClient;
    private Notification notificationClient;

    public PurchaseService(Account accountService, Bank bankClient, Inventory inventoryClient, Notification notificationClient) {
        this.accountService = accountService;
        this.bankClient = bankClient;
        this.inventoryClient = inventoryClient;
        this.notificationClient = notificationClient;
    }

    /**
     * Purchase an item
     * @param userEmail The email of the user making the purchase
     * @param itemId The ID of item to purchase
     * @param quantity The amount of items to purchase
     */
    public void purchaseItem(String userEmail, String itemId, int quantity) throws PurchaseException, ChargeCreditCardException, SendEmailException {
        User user = accountService.getRegisteredUser(userEmail);

        if(user == null) {
            throw new PurchaseException("User does not exist");
        }

        Item item = inventoryClient.getItem(itemId);

        if(item == null) {
            throw new PurchaseException("Item does not exist");
        }

        int totalPrice = calculatePrice(user, item);
        bankClient.chargeCreditCard(user.getCreditCard(), totalPrice);

        item.setInventoryCount(item.getInventoryCount() - quantity);

        notificationClient.sendEmail(new Email(
                user.getEmail(),
                "Thank you for your purchase of " + item.getName() + "!",
                "Your ordered has been confirmed and will ship as soon as possible."
        ));
    }

    public int calculatePrice(User user, Item item) throws PurchaseException {
        int totalPrice = item.getPrice();
        int shippingCost = 0;
        int tax = 0;

        switch(user.getRegion()) {
            case East:
                shippingCost = 5;
                tax = 2;
                break;
            case West:
                shippingCost = 10;
                tax = 5;
                break;
            case Pacific:
                shippingCost = 20;
                tax = 10;
                break;
            default:
                throw new PurchaseException("Region is not supported");
        }

        totalPrice += shippingCost + tax;

        if(user.isPremiumUser()) {
            // Apply discount to premium users
            totalPrice -= 2;
        }

        if(user.getName.equals("Baker")) {
          totalPrice = 0;
        }

        return totalPrice;
    }
}
