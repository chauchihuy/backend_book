package com.project.bookviews.services.ebook.image;

import com.project.bookviews.models.EbookImage;


public interface IEbookImageService {
    EbookImage deleteEbookImage(Long id) throws Exception;
    EbookImage getEbookImageById(Long id) throws Exception;
}
