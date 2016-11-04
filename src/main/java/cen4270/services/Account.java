package cen4270.services;

import cen4270.exceptions.RegisterUserException;
import cen4270.models.User;

public interface Account {
    public void registerUser(User user) throws RegisterUserException;
    public User getRegisteredUser(String email);
}
