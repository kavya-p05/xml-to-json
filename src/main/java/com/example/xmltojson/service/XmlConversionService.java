package com.example.xmltojson.service;

import com.example.xmltojson.exception.XmlConversionException;
import com.example.xmltojson.util.XmlJsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class XmlConversionService {


    private XmlJsonConverter converter;
    @Autowired
    public XmlConversionService(XmlJsonConverter converter) {
        this.converter = converter;
    }

    @Value("${converter.score-tags:Score}")
    private String scoreTagList; // Comma-separated tag names

    public String convertXml(String xml, String matchXPath) {
        List<String> scoreTags = Arrays.asList(scoreTagList.split(","));
        return converter.convert(xml, matchXPath, scoreTags);
    }

}
