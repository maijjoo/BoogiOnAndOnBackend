package com.boogionandon.backend.util;

public class DistanceCalculator {

  private static final double EARTH_RADIUS = 6371000; // 지구 반지름 (미터 단위)

  public static double calculateDistance(double startLat, double startLon, double endLat, double endLon) {
    // 위도와 경도를 라디안으로 변환
    double startLatRad = Math.toRadians(startLat);
    double startLonRad = Math.toRadians(startLon);
    double endLatRad = Math.toRadians(endLat);
    double endLonRad = Math.toRadians(endLon);

    // 위도와 경도의 차이 계산
    double deltaLat = endLatRad - startLatRad;
    double deltaLon = endLonRad - startLonRad;

    // Haversine 공식 사용
    double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
        Math.cos(startLatRad) * Math.cos(endLatRad) *
            Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    // 거리 계산 및 소수점 첫째 자리까지 반올림
    double distance = EARTH_RADIUS * c;
    return Math.round(distance * 10.0) / 10.0;
  }

}
