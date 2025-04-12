package com.example.xmltojson.util;

import com.example.xmltojson.exception.XmlConversionException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Component

public class XmlJsonConverter {
    private static final Logger logger = LoggerFactory.getLogger(XmlJsonConverter.class);

    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private BigInteger maxScoreLimit;

    public XmlJsonConverter() {

    }
    @Autowired
    public XmlJsonConverter(@Value("${converter.max-score-limit}") String maxScoreLimitValue) {
        System.out.println("${converter.max-score-limit} "+maxScoreLimitValue);
        this.maxScoreLimit = new BigInteger(maxScoreLimitValue);
        logger.info("Max score limit configured as: {}", this.maxScoreLimit);
    }


    public String convert(String xml, String matchXPath, List<String> possibleScoreTags) {
        logger.info("Starting XML to JSON conversion");
        logger.debug("Input XPath: {}, Score Field: {}", matchXPath);

        try {
            JsonNode root = xmlMapper.readTree(xml);


            // Extract Match nodes dynamically
            JsonNode matchNodes = root.at(matchXPath);

            BigInteger totalScore = BigInteger.ZERO;

            if (matchNodes.isMissingNode()) {
                logger.warn("No nodes found at path: {}", matchXPath);
            } else if (matchNodes.isArray()) {
                for (JsonNode match : matchNodes) {
                    BigInteger score = extractScoreWithFallback(match, possibleScoreTags);
                    totalScore = totalScore.add(score);
                    logger.debug("Score extracted: {} → Running Total: {}", score, totalScore);

                    if (totalScore.compareTo(maxScoreLimit) > 0) {
                        throw new XmlConversionException("Total score exceeds max configured limit: " + maxScoreLimit);
                    }
                }
            } else {
                BigInteger score = extractScoreWithFallback(matchNodes, possibleScoreTags);
                totalScore = totalScore.add(score);
                logger.debug("Single match score: {}", score);
            }

            // Add summary
            ((ObjectNode) root.path("ResultBlock"))
                    .putObject("MatchSummary")
                    .put("TotalMatchScore", totalScore);

            logger.info("Conversion complete. Total Match Score: {}", totalScore);
            return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
        } catch (Exception e) {
            logger.error("Error during XML conversion", e);
            throw new XmlConversionException("Failed to convert XML to JSON", e);
        }
    }

    private BigInteger extractScoreWithFallback(JsonNode matchNode, List<String> tagNames) {
        for (String tag : tagNames) {
            JsonNode scoreNode = matchNode.path(tag);
            if (scoreNode != null && !scoreNode.isMissingNode() && !scoreNode.isNull()) {
                try {
                    String rawValue = scoreNode.asText().replaceAll("^\"|\"$", ""); // Remove surrounding quotes
                    long value = Long.parseLong(rawValue);
                    return BigInteger.valueOf(value);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid number format for tag '{}': {}", tag, scoreNode.asText());
                }
            }
        }
        logger.warn("No valid score tag found in match: {}", matchNode.toPrettyString());
        return BigInteger.ZERO;
    }
}