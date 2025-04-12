package com.example.xmltojson;

import com.example.xmltojson.exception.XmlConversionException;
import com.example.xmltojson.util.XmlJsonConverter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class XmlJsonConverterTest {

    private XmlJsonConverter converter;

    @BeforeEach
    void setUp() {
        converter = new XmlJsonConverter("1000"); // max limit for tests
    }


    @Test
    void testSuccessfulConversionWithScore() {
        String xml = """
                <Response>
                    <ResultBlock>
                        <MatchDetails>
                            <Match><Score>40</Score></Match>
                            <Match><Score>50</Score></Match>
                        </MatchDetails>
                    </ResultBlock>
                </Response>
                """;

        String result = converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score", "Points"));
        assertTrue(result.contains("\"TotalMatchScore\" : 90"));
    }

    @Test
    void testSuccessfulConversionWithPointsTag() {
        String xml = """
                <Response>
                    <ResultBlock>
                        <MatchDetails>
                            <Match><Points>25</Points></Match>
                            <Match><Points>35</Points></Match>
                        </MatchDetails>
                    </ResultBlock>
                </Response>
                """;

        String result = converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score", "Points"));
        assertTrue(result.contains("\"TotalMatchScore\" : 60"));
    }

    @Test
    void testMissingScoreFieldDefaultsToZero() {
        String xml = """
                <Response>
                    <ResultBlock>
                        <MatchDetails>
                            <Match><SomethingElse>99</SomethingElse></Match>
                        </MatchDetails>
                    </ResultBlock>
                </Response>
                """;

        String result = converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score", "Points"));
        assertTrue(result.contains("\"TotalMatchScore\" : 0"));
    }

    @Test
    void testInvalidScoreValueIgnored() {
        String xml = """
                <Response>
                    <ResultBlock>
                        <MatchDetails>
                            <Match><Score>abc</Score></Match>
                            <Match><Score>50</Score></Match>
                        </MatchDetails>
                    </ResultBlock>
                </Response>
                """;

        String result = converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score", "Points"));
        assertTrue(result.contains("\"TotalMatchScore\" : 50"));
    }

    @Test
    void testEmptyMatchListReturnsZeroScore() {
        String xml = """
                <Response>
                    <ResultBlock>
                        <MatchDetails></MatchDetails>
                    </ResultBlock>
                </Response>
                """;

        String result = converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score", "Points"));
        assertTrue(result.contains("\"TotalMatchScore\" : 0"));
    }

    @Test
    void testMalformedXmlThrowsException() {
        String xml = "<Response><ResultBlock><MatchDetails><Match><Score>30</Score></Match>"; // Missing closing tags

        XmlConversionException exception = assertThrows(XmlConversionException.class, () -> {
            converter.convert(xml, "/ResultBlock/MatchDetails/Match", List.of("Score"));
        });

        assertTrue(exception.getMessage().contains("Failed to convert XML to JSON"));
    }
}

