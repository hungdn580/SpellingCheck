package com.example.dao;

import com.example.model.Customer;
import com.example.model.Suggest;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 19/09/2016.
 */
public interface CustomerDao {
    List<Customer> getAllCustomer();
    Customer getCustommerById(long id);
    Customer getCustomerByLastName(String name);
    void createCustomer(Customer customer);
    ArrayList spellingCheck(String content);
    String positionMistake(String content);
    String findMistakePosition(String url);
    ArrayList typingCheck(String content);
    ArrayList<Suggest> checkParagraph(String content);
}
