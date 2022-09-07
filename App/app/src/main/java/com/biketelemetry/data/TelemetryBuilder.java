package com.biketelemetry.data;

public class TelemetryBuilder {
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

    public TelemetryBuilder withLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public TelemetryBuilder withLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public TelemetryBuilder withAltitude(double altitude) {
        this.altitude = altitude;
        return this;
    }

    public TelemetryBuilder withDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public TelemetryBuilder withSpeed(double speed) {
        this.speed = speed;
        return this;
    }

    public TelemetryBuilder withYear(int year) {
        this.year = year;
        return this;
    }

    public TelemetryBuilder withMonth(int month) {
        this.month = month;
        return this;
    }

    public TelemetryBuilder withDay(int day) {
        this.day = day;
        return this;
    }

    public TelemetryBuilder withHour(int hour) {
        this.hour = hour;
        return this;
    }

    public TelemetryBuilder withMinute(int minute) {
        this.minute = minute;
        return this;
    }

    public TelemetryBuilder withSecond(int second) {
        this.second = second;
        return this;
    }

    public TelemetryBuilder withMillisecond(int millisecond) {
        this.millisecond = millisecond;
        return this;
    }

    public TelemetryBuilder withSatellites(int satellites) {
        this.satellites = satellites;
        return this;
    }

    public TelemetryBuilder withHdop(int hdop) {
        this.hdop = hdop;
        return this;
    }

    public TelemetryBuilder withRoll(int roll) {
        this.roll = roll;
        return this;
    }

    public TelemetryBuilder withPitch(int pitch) {
        this.pitch = pitch;
        return this;
    }

    public TelemetryBuilder withTemperature(int temperature) {
        this.temperature = temperature;
        return this;
    }

    public Telemetry createTelemetry() {
        return new Telemetry(latitude, longitude, altitude, distance, speed, year, month, day, hour, minute, second, millisecond, satellites, hdop, roll, pitch, temperature);
    }
}