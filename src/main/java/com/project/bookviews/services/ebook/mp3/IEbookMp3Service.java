package com.project.bookviews.services.ebook.mp3;

import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;

import java.io.IOException;

public interface IEbookMp3Service {
    EbookMp3 deleteEbookMp3(Long id) throws DataNotFoundException, IOException;
    EbookMp3 getEbookMp3ById(Long id) throws DataNotFoundException;
}
