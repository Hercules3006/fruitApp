package com.saurabh.homepage;

public class DiseaseInformation {
    String name;
    String conditional;
    String prevent;
    String curve;
    int image;

    // Constructor
    public DiseaseInformation(String name, String conditional, String prevent, String curve, int image) {
        this.name = name;
        this.conditional = conditional;
        this.prevent = prevent;
        this.curve = curve;
        this.image = image;
    }

    // Getters and Setters (optional)
    public String getName() {
        return name;
    }

    public String getConditional() {
        return conditional;
    }

    public String getPrevent() {
        return prevent;
    }

    public String getCurve() {
        return curve;
    }

}

