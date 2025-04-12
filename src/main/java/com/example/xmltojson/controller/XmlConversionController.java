package com.example.xmltojson.controller;

import com.example.xmltojson.service.XmlConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/convert")
public class XmlConversionController {
    @Autowired
    private XmlConversionService conversionService;

    @PostMapping("/xml")
    public ResponseEntity<String> convertXmlToJson(@RequestBody String xml) {
        String xpath = "/ResultBlock/MatchDetails/Match"; // fixed as per contract
        String resultJson = conversionService.convertXml(xml, xpath);
        return ResponseEntity.ok(resultJson);
    }
}
