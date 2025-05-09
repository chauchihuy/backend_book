package com.project.bookviews.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.EbookDTO;
import com.project.bookviews.dtos.EbookImageDTO;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.EbookCategory;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;
import com.project.bookviews.responses.EbookListResponse;
import com.project.bookviews.responses.EbookResponse;
import com.project.bookviews.services.ebook.IEbookService;
import com.project.bookviews.services.ebook.image.IEbookImageService;
import com.project.bookviews.utils.MessageKeys;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/ebooks")
@RequiredArgsConstructor
public class EbookController {
    private static final Logger logger = LoggerFactory.getLogger(EbookController.class);
    private final IEbookService ebookService;
    private final IEbookImageService iEbookImageService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createEbook(
            @Valid @RequestBody EbookDTO ebookDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Ebook newEbook = ebookService.createEbook(ebookDTO);
            return ResponseEntity.ok(newEbook);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


//    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> uploadImages(
//            @PathVariable("id") Long ebookId,
//            @ModelAttribute("files") List<MultipartFile> files
//    ) {
//        try {
//            Ebook existingEbook = ebookService.getEbookById(ebookId);
//            files = files == null ? new ArrayList<MultipartFile>() : files;
//            if (files.size() > EbookImage.MAXIMUM_IMAGES_PER_EBOOK)
//            {
//                return ResponseEntity.badRequest().body(localizationUtils
//                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
//            }
//            List<EbookImage> ebookImages = new ArrayList<>();
//            for (MultipartFile file : files) {
//                if (file.getSize() == 0) {
//                    continue;
//                }
//                // Kiểm tra kích thước file và định dạng
//                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
//                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
//                            .body(localizationUtils
//                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
//                }
//                String contentType = file.getContentType();
//                if (contentType == null || !contentType.startsWith("image/")) {
//                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
//                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
//                }
//                // Lưu file và cập nhật thumbnail trong DTO
//                String filename = ebookService.storeFile(file); // Thay thế hàm này với code của bạn để lưu file
//                //lưu vào đối tượng product trong DB
//                EbookImage ebookImage = ebookService.createEbookImage(
//                        existingEbook.getId(),
//                        EbookImageDTO.builder()
//                                .imageUrl(filename)
//                                .build()
//                );
//                ebookImages.add(ebookImage);
//
//            }
//            if (!ebookImages.isEmpty())
//            {
//                    existingEbook.setThumbnail(ebookImages.get(0).getImageUrl()); // Lấy ảnh từ vị trí đầu tiên
//                    ebookService.updateEbook(existingEbook.getId(), EbookDTO.builder()
//                            .thumbnail(existingEbook.getThumbnail()).build());
//            }
//            return ResponseEntity.ok().body(ebookImages);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long ebookId,
            @ModelAttribute("files") List<MultipartFile> files
    ) {
        try {
            Ebook existingEbook = ebookService.getEbookById(ebookId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > EbookImage.MAXIMUM_IMAGES_PER_EBOOK)
            {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
            }
            List<EbookImage> ebookImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = ebookService.storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                //lưu vào đối tượng product trong DB
                EbookImage ebookImage = ebookService.createEbookImage(
                        existingEbook.getId(),
                        EbookImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                ebookImages.add(ebookImage);

            }
           // Update the thumbnail of the ebook with the first image
            if (!ebookImages.isEmpty()) {
                existingEbook.setThumbnail(ebookImages.get(0).getImageUrl()); // Set the first image as thumbnail
                ebookService.updateEbook(existingEbook.getId(), EbookDTO.builder()
                        .thumbnail(existingEbook.getThumbnail())
                                .price(existingEbook.getPrice())
                        .build());
            }

            return ResponseEntity.ok().body(ebookImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



//    @PostMapping(value = "/{id}/upload-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> uploadAudio(
//            @PathVariable("id") Long ebookId,
//            @RequestParam("audio") MultipartFile audioFile
//    ) {
//        try {
//            // Ensure the ebook exists
//            Ebook existingEbook = ebookService.getEbookById(ebookId);
//
//            // Store the audio file and get the path
//            String audioFilePath = ebookService.storeMp3File(audioFile);
//
//            // Update the Ebook entity with the audio file path
//            existingEbook.setAudioUrl(audioFilePath);
//            Ebook updatedEbook = ebookService.updateEbookAudioUrl(existingEbook);
//
//            return ResponseEntity.ok(EbookResponse.fromEbook(updatedEbook));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

//@PostMapping("/uploadmp3s/{ebookId}")
//public ResponseEntity<String> uploadEbookAudio(@PathVariable Long ebookId, @RequestParam("audio") MultipartFile audioFile) {
//    try {
//        Ebook ebook = ebookService.getEbookById(ebookId);
//        String fileName = ebookService.storeMp3File(audioFile);
//        ebook.setAudioUrl(fileName);
//        ebookService.updateEbookAudioUrl(ebook);
//        return ResponseEntity.ok("Audio uploaded successfully");
//    } catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading audio: " + e.getMessage());
//    }
//}
    @PostMapping(value = "/uploadmp3s/{ebookId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> uploadEbookAudio(@PathVariable Long ebookId,
                                                   @RequestParam("audio") List<MultipartFile> audioFiles) {
        try {
            Ebook ebook = ebookService.getEbookById(ebookId);

            audioFiles = audioFiles == null ? new ArrayList<MultipartFile>() : audioFiles;
            if (audioFiles.size() > EbookMp3.MAXIMUM_VIDE_PER_EBOOK)
            {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPLOAD_MP3S_MAX_5));
            }
            ebookService.uploadMultiFile(audioFiles, ebookId);
//
            return ResponseEntity.ok("Audio uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading audio: " + e.getMessage());
        }
    }

    private boolean validateAudio(MultipartFile file) throws IOException {
        // Check content type
        if (file.getContentType() != null && file.getContentType().equals("audio/mpeg")) {
            return true;
        }
        return false;
    }
    private String generateUniqueFilename(String originalFilename) {

        return UUID.randomUUID().toString() + "-" + originalFilename;
    }


    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/default-image.jpg").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/audios/{audioName}")
    public ResponseEntity<?> viewAudio(@PathVariable String audioName) {
        try {
            java.nio.file.Path audioPath = Paths.get("uploadmp3s/" + audioName);
            UrlResource resource = new UrlResource(audioPath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("audio/mpeg"))
                        .body(new UrlResource(Paths.get("uploads/default-audio.mp3").toUri()));
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("")
//    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<EbookListResponse> getEbooks(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) throws JsonProcessingException {
        int totalPages = 0;
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").descending()
        );
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                keyword, categoryId, page, limit));

        Page<EbookResponse> ebookPage = ebookService
                .getAllEbooks(keyword, categoryId, pageRequest);
        // Lấy tổng số trang
        totalPages = ebookPage.getTotalPages();
        List<EbookResponse> ebookResponses = ebookPage.getContent();
        // Bổ sung totalPages vào các đối tượng ProductResponse
        for (EbookResponse ebook : ebookResponses) {
            ebook.setTotalPages(totalPages);
        }

        return ResponseEntity.ok(EbookListResponse
                .builder()
                .ebooks(ebookResponses)
                .totalPages(totalPages)
                .build());
    }


    @GetMapping("/user")

    public ResponseEntity<EbookListResponse> getEbooksUser(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    )
    {
        int totalPages;
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").descending()
        );

        Page<EbookResponse> ebookPage = ebookService.getAllInactiveEbooksUser(keyword, categoryId, pageRequest);
        totalPages = ebookPage.getTotalPages();
        List<EbookResponse> ebookResponses = ebookPage.getContent();

        for (EbookResponse ebook : ebookResponses) {
            ebook.setTotalPages(totalPages);
        }

        return ResponseEntity.ok(EbookListResponse
                .builder()
                .ebooks(ebookResponses)
                .totalPages(totalPages)
                .build());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getEbookById(@PathVariable("id") Long ebookId) {
        try {
            Ebook existingEbook = ebookService.getEbookById(ebookId);
            return ResponseEntity.ok(EbookResponse.fromEbook(existingEbook));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/by-ids")
    public ResponseEntity<?> getEbooksByIds(@RequestParam("ids") String ids) {

        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> ebookIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Ebook> ebooks = ebookService.findEbooksByIds(ebookIds);
            return ResponseEntity.ok(ebooks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//xóa
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public ResponseEntity<?> deleteEbook(@PathVariable Long id) {
//        try {
//            ebookService.deleteEbook(id);
//            return ResponseEntity.ok("Ebook deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//
//    }

 //khóa
        @PutMapping("/{id}/status")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        public ResponseEntity<Map<String, String>> updateActiveStatus(@PathVariable Long id) {
            boolean updated = ebookService.updateActiveStatus(id);
            Map<String, String> response = new HashMap<>();
            if (updated) {
                response.put("message", "Status updated successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Ebook not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        }


//update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    //@SecurityRequirement(name="bearer-key")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestBody EbookDTO ebookDTO) {
        try {
            Ebook updatedProduct = ebookService.updateEbook(id, ebookDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/ebookcategories/{ebookId}")
    public ResponseEntity<?> viewEbkCategory(@PathVariable Long ebookId) {
        List<EbookCategory> ebookCategories = ebookService.getCategoriesByEbookId(ebookId);
        return ResponseEntity.ok(ebookCategories);
    }
}
