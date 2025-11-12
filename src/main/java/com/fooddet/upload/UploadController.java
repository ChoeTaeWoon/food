package com.fooddet.upload;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping
public class UploadController {

    private final Path baseDir;

    public UploadController(@Value("${app.upload.dir:./uploads}") String dir) {
        this.baseDir = Paths.get(dir).toAbsolutePath().normalize();
    }

    @PostMapping(path = "/classify", consumes = "multipart/form-data")
    public ResponseEntity<?> classify(@RequestPart("image") MultipartFile image,
                                      @RequestParam(defaultValue = "ko") String locale) throws Exception {
        if (image.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message","image is empty"));
        String ext = getExt(image.getOriginalFilename());
        if (!List.of("jpg","jpeg","png","webp").contains(ext)) {
            return ResponseEntity.badRequest().body(Map.of("message","unsupported file type"));
        }

        // yyyy/MM/dd 구조로 저장
        LocalDate today = LocalDate.now();
        Path saveDir = baseDir.resolve(Paths.get(
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth())
        ));
        Files.createDirectories(saveDir);

        String filename = UUID.randomUUID() + "." + ext;
        Path savePath = saveDir.resolve(filename);
        image.transferTo(savePath.toFile());

        // 정적 경로(/files/**)로 접근 가능한 URL 생성
        Path rel = baseDir.relativize(savePath);
        String imageUrl = "/files/" + rel.toString().replace(File.separatorChar, '/');

        // 더미 분류 결과(나중에 AI로 교체)
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("label", "food:dummy");
        result.put("confidence", 0.99);
        result.put("imageUrl", imageUrl);
        result.put("locale", locale);
        result.put("recipe", Map.of(
                "title", locale.equals("ko") ? "더미 레시피" : "Dummy Recipe",
                "steps", List.of("1) 재료 손질", "2) 가열", "3) 접시 담기")
        ));

        return ResponseEntity.ok(result);
    }

    private String getExt(String name) {
        if (!StringUtils.hasText(name) || !name.contains(".")) return "jpg";
        return name.substring(name.lastIndexOf('.')+1).toLowerCase(Locale.ROOT);
    }
}
