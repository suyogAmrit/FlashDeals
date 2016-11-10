package com.suyogindia.model;

import java.util.ArrayList;

/**
 * Created by Tanmay on 11/9/2016.
 */

public class QuestionRequest {
    String userId;
    ArrayList<String> answerList;
    ArrayList<Address> addressList;

    public QuestionRequest(String userId) {
        this.userId = userId;
    }

    public void setAnswerList(ArrayList<String> answerList) {
        this.answerList = answerList;
    }

    public void setAddressList(ArrayList<Address> addressList) {
        this.addressList = addressList;
    }
}
