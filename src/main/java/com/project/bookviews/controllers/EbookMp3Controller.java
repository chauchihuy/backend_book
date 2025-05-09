package com.project.bookviews.controllers;


import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;
import com.project.bookviews.services.ebook.EbookService;
import com.project.bookviews.services.ebook.image.IEbookImageService;
import com.project.bookviews.services.ebook.mp3.IEbookMp3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("${api.prefix}/ebook_mp3s")
@RequiredArgsConstructor
public class EbookMp3Controller {

    private final IEbookMp3Service iEbookMp3Service;

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteEbookMp3(@PathVariable Long id) {
        try {
            EbookMp3 ebookMp3 = iEbookMp3Service.deleteEbookMp3(id);
            return ResponseEntity.ok(ebookMp3);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

}
