// com.fooddet.ai.InferController.java
package com.fooddet.ai;

import com.fooddet.ai.dto.PredictResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class InferController {

    private final InferService inferService;

    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PredictResponse classify(
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "locale", required = false) String locale
    ) {
        return inferService.classify(image, locale);
    }
}

