package com.saurabh.homepage;

import android.os.Build;

import java.util.ArrayList;
import java.util.List;

class Detection {
    float xCenter, yCenter, width, height, confidence;
    int classId;
    float classScore;

    public Detection(float xCenter, float yCenter, float width, float height, float confidence, int classId, float classScore) {
        this.xCenter = xCenter;
        this.yCenter = yCenter;
        this.width = width;
        this.height = height;
        this.confidence = confidence;
        this.classId = classId;
        this.classScore = classScore;
    }


}




