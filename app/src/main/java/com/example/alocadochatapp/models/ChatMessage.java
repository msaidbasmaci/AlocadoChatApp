package com.example.alocadochatapp.models;

import java.util.Date;

//mesaj alıp verebilmek için gerekli bilgiler burda saklanmaktadır

public class ChatMessage {
    public String senderId;
    public String receiverId;
    public String message;
    public String dateTime;
    public Date dateObject;
    public String conversionId, conversionName, conversionImage;
}
