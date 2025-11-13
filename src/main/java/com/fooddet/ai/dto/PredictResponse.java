package com.fooddet.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PredictResponse(
        String mainCategory,
        String dish,
        Map<String, Double> confidence,
        Topk topk,
        String locale
) {}

