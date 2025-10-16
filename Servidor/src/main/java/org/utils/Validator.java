package org.utils;


import java.net.URI;
import java.net.URISyntaxException;

public class Validator {
    public static boolean isValidUrlNonLocalhost(String urlStr) {
        try {
            URI uri = new URI(urlStr);
            String schema = uri.getScheme();
            String host = uri.getHost();
            return schema != null && host != null && !host.equalsIgnoreCase("localhost") && !host.equals("127.0.0.1") && !host.equals("::1");
        } catch (Exception e){
            return false;
        }
    }
}
