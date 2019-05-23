package com.androidproject.sudiet.object;

public class PredictionData extends GlucoseData {

    public enum Result {
        OK,
        ERROR_NO_NFC,
        ERROR_NFC_READ
    }

    public double trend = -1;
    public double confidence = -1;
    public Result errorCode;
    public int attempt;

    public PredictionData() {}

}
