package com.jcorpse.beauty.util;

import lombok.extern.slf4j.Slf4j;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

@Slf4j
public class ConfigUtil {
    private static ResourceBundle serverResource;

    static {
        serverResource = ResourceBundle.getBundle("config");
    }

    public static String getStringResourceByKey(String key) {
        try {
            return serverResource.getString(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public static Integer getIntResourceByKey(String key) {
        try {
            return Integer.valueOf(serverResource.getString(key));
        } catch (MissingResourceException | NumberFormatException e) {
            return null;
        }
    }

    public static Long getLongResourceByKey(String key) {
        try {
            return Long.valueOf(serverResource.getString(key));
        } catch (MissingResourceException | NumberFormatException e) {
            return null;
        }
    }

    public static Boolean getBooleanResourceByKey(String key) {
        try {
            return Boolean.valueOf(serverResource.getString(key));
        } catch (MissingResourceException e) {
            return null;
        }
    }

    public static Double getDoubleResourceByKey(String key) {
        try {
            return Double.valueOf(serverResource.getString(key));
        } catch (MissingResourceException | NumberFormatException e) {
            return null;
        }
    }
}
