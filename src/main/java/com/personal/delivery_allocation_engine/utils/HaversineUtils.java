package com.personal.delivery_allocation_engine.utils;

import lombok.experimental.UtilityClass;

/**
 * @author Soumyajit Podder created on 24/07/25
 */
@UtilityClass
public class HaversineUtils {

  private static final double EARTH_RADIUS_KM = 6371.0;
  private static final double METERS_IN_KM = 1000.0;

  public static double getDistanceInMetres(double lat1, double lon1, double lat2, double lon2) {
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    double latSin = Math.sin(latDistance / 2);
    double latCos = Math.cos(Math.toRadians(lat1));
    double longSin = Math.sin(lonDistance / 2);
    double longCos = Math.cos(Math.toRadians(lat2));

    double a = latSin * latSin + latCos * longCos * longSin * longSin;

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return EARTH_RADIUS_KM * c * METERS_IN_KM;
  }
}
