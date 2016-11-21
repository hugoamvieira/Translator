package io.github.hugoamvieira.picturetranslator;

public class Label {
    private double confidence;
    private String labelName;

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    
    public Label(double confidence, String labelName) {
        setConfidence(confidence);
        setLabelName(labelName);
    }
}
