package com.biketelemetry.data;

import java.time.LocalDateTime;

public class Telemetry {

    private double latitude;
    private double longitude;
    private double altitude;
    private double distance;
    private double speed;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int millisecond;
    private int satellites;
    private int roll;
    private int pitch;

    public Telemetry() { }

    public Telemetry(double latitude, double longitude, double distance, int month, int day, int year, int hour, int minute, int second, int millisecond, double speed, double altitude, int roll, int pitch, int satelites) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        this.month = month;
        this.day = day;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
        this.speed = speed;
        this.altitude = altitude;
        this.roll = roll;
        this.pitch = pitch;
        this.satellites = satelites;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public double getXg() {
        return Xg;
    }

    public void setXg(double xg) {
        Xg = xg;
    }

    public double getYg() {
        return Yg;
    }

    public void setYg(double yg) {
        Yg = yg;
    }

    public double getZg() {
        return Zg;
    }

    public void setZg(double zg) {
        Zg = zg;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.of(year, month, day, hour, minute, second, millisecond * 1000);
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }
}
