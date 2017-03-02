package com.nexus.nsnik.weather;

/**
 * Created by nsnik on 04-Nov-16.
 */

public class UnitConverter {

    public static String ConvertToFareheit(double ct){
        ct = (ct*1.8)+32;
        return String.valueOf(ct);
    }

    public static String ConvertToKelvin(double ct){
        ct = ct+273.15;
        return String.valueOf(ct);
    }
}
