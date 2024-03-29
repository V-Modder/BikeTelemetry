package com.biketelemetry.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class Telemetry implements Parcelable {

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
    private int hdop;
    private int roll;
    private int pitch;
    private int temperature;

    Telemetry() { }

    Telemetry(double latitude, double longitude, double altitude, double distance, double speed, int year, int month, int day, int hour, int minute, int second, int millisecond, int satellites, int hdop, int roll, int pitch, int temperature) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.distance = distance;
        this.speed = speed;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.millisecond = millisecond;
        this.satellites = satellites;
        this.hdop = hdop;
        this.roll = roll;
        this.pitch = pitch;
        this.temperature = temperature;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.of(year, month, day, hour, minute, second, millisecond * 1000);
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
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

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getHdop() {
        return hdop;
    }

    public void setHdop(int hdop) {
        this.hdop = hdop;
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

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
        dest.writeDouble(distance);
        dest.writeDouble(speed);
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(second);
        dest.writeInt(millisecond);
        dest.writeInt(satellites);
        dest.writeInt(hdop);
        dest.writeInt(roll);
        dest.writeInt(pitch);
        dest.writeInt(temperature);
    }

    public static final Parcelable.Creator<Telemetry> CREATOR
            = new Parcelable.Creator<Telemetry>() {
        public Telemetry createFromParcel(Parcel in) {
            return new Telemetry(in);
        }

        public Telemetry[] newArray(int size) {
            return new Telemetry[size];
        }
    };

    private Telemetry(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readDouble();
        distance = in.readDouble();
        speed = in.readDouble();
        year = in.readInt();
        month = in.readInt();
        day = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        second = in.readInt();
        millisecond = in.readInt();
        satellites = in.readInt();
        hdop = in.readInt();
        roll = in.readInt();
        pitch = in.readInt();
        temperature = in.readInt();
    }
}
