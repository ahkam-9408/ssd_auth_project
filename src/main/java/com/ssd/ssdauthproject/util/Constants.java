package com.ssd.assignment.oauth.demo.util;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.DriveScopes;

import java.util.Collections;
import java.util.List;


/**
 * Contains all the constants used within the application
 */

public class Constants {

    public static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    public static final String USER_IDENTIFIER_KEY = "MY_TEST_USER";
    public static final String APPLICATION_NAME = "SSD-Assignment2";
    public static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

}
