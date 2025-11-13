// com.fooddet.ai.dto.PredictDto.java
package com.fooddet.ai.dto;

import java.util.List;
import java.util.Map;

public record PredictResponse(
        String mainCategory,
        String dish,
        Map<String, Double> confidence,
        Topk topk,
        String locale
) {}

public record Topk(
        List<LabelScore> main,
        List<LabelScore> dish
) {}

public record LabelScore(
        String label,
        double p
) {}

