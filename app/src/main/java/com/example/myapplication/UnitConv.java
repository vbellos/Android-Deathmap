package com.example.myapplication;

import java.math.BigDecimal;

public class UnitConv {

    float speed;
    float accel;
    private float speedKMH;
    private float speedMS;
    private float accMS2;
    private float accG;

    public UnitConv(float speed, float accel)
    {
        this.speed = speed;
        this.accel=accel;

    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public float getSpeedKMH() {
        return round((float) (speed*3.6),0);
    }

    public float getSpeedMS() {
        return round(speed,1);
    }

    public float getAccMS2() {
        return round(accel,1);
    }

    public float getAccG() {
        return round((float)(accel/9.81),1);
    }
}
