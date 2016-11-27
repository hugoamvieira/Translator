package io.github.hugoamvieira.picturetranslator;

public class Label {
    private float confidence;
    private String labelName;

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    
    public Label(float confidence, String labelName) {
        setConfidence(confidence);
        setLabelName(labelName);
    }
}
