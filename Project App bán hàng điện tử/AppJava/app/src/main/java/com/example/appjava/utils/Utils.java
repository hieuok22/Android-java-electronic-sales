package com.example.appjava.utils;

import com.example.appjava.model.GioHang;
import com.example.appjava.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
//    public static final String BASE_URL="http://192.168.1.6/banhang/";
    public static final String BASE_URL="http://192.168.41.151/banhang/";
//    public static final String BASE_URL="https://hieuok22.000webhostapp.com/banhang/";
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();

    public static String ID_RECEIVED;
    public static  final String SENDID = "idsend";
    public static  final String RECEIVEDID = "idreceived";
    public static  final String MESS = "message";
    public static  final String DATETIME = "datetime";
    public static  final String PATH_CHAT = "chat";


}
