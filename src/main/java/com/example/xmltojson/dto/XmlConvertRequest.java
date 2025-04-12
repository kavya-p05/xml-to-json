package com.example.xmltojson.dto;

public class XmlConvertRequest {
    private String xml;
    private String scoreXPath;
    private String scoreFieldName;

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getScoreXPath() {
        return scoreXPath;
    }

    public void setScoreXPath(String scoreXPath) {
        this.scoreXPath = scoreXPath;
    }

    public String getScoreFieldName() {
        return scoreFieldName;
    }

    public void setScoreFieldName(String scoreFieldName) {
        this.scoreFieldName = scoreFieldName;
    }
}


