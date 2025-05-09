package com.project.bookviews.controllers;

import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.EbookDTO;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.services.ebook.EbookService;

import com.project.bookviews.services.ebook.image.IEbookImageService;
import com.project.bookviews.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/ebook_images")
@RequiredArgsConstructor
public class EbookImageController {
    private final IEbookImageService IebookImageService;
    private final EbookService IebookService;
    private  final LocalizationUtils localizationUtils;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteEbookImage(@PathVariable Long id) {
        try {
            EbookImage ebookImage = IebookImageService.deleteEbookImage(id);
            if (ebookImage != null) {
//                IebookService.deleteFile(ebookImage.getImageUrl());
                IebookService.updateEbookThumbnailOnImageDelete(ebookImage.getEbook().getId());
            }
            return ResponseEntity.ok(ebookImage);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

}
