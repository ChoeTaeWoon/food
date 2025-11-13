// com.fooddet.ai.InferService.java
package com.fooddet.ai;

import com.fooddet.ai.dto.PredictResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InferService {
    private final WebClient fastapiClient;

    public PredictResponse classify(MultipartFile image, String locale) {
        try {
            String filename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "image.jpg";
            String contentType = image.getContentType() != null ? image.getContentType() : "image/jpeg";

            MultipartBodyBuilder mb = new MultipartBodyBuilder();
            mb.part("image", new ByteArrayResource(image.getBytes()){
                @Override public String getFilename() { return filename; }
            }).contentType(MediaType.parseMediaType(contentType));
            mb.part("locale", locale != null ? locale : "ko");

            Mono<PredictResponse> call = fastapiClient.post()
                    .uri("/predict")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(mb.build()))
                    .retrieve()
                    .onStatus(s -> s.is5xxServerError(), rsp ->
                            Mono.error(new ResponseStatusException(HttpStatus.BAD_GATEWAY, "inference upstream error")))
                    .onStatus(s -> s.value() == 422, rsp ->
                            Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid image or params")))
                    .bodyToMono(PredictResponse.class);

            return call.block(); // MVC니까 블로킹 반환. WebFlux면 Mono 그대로 리턴.
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "inference call failed", e);
        }
    }
}

