package com.project.bookviews.services.ebook;

import com.project.bookviews.dtos.EbookImageDTO;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.EbookCategory;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.dtos.EbookDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.responses.EbookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IEbookService {
    Ebook createEbook(EbookDTO ebookDTO) throws Exception;
    Ebook getEbookById(Long id) throws DataNotFoundException;
    Page<EbookResponse> getAllEbooks(String keyword, Long categoryId, PageRequest pageRequest);
    Page<EbookResponse> getAllInactiveEbooksUser(String keyword, Long categoryId, PageRequest pageRequest);
    Ebook updateEbook(long id, EbookDTO ebookDTO) throws Exception;
//    void deleteEbook(long id);
// void deleteEbook(long id) throws Exception;
public boolean updateActiveStatus(Long id);
    boolean existsByName(String name);
    EbookImage createEbookImage(
            Long ebookId,
            EbookImageDTO ebookImageDTO) throws Exception;

    List<Ebook> findEbooksByIds(List<Long> ebookIds);
    String storeFile(MultipartFile file) throws IOException;
    void storeMp3File (MultipartFile file, String filename) throws IOException;
     void updateEbookAudioUrl(Long ebookId, String newAudioUrl) throws DataNotFoundException, IOException;
    void deleteFile(String filename) throws IOException;
    void uploadMultiFile(List<MultipartFile> files, Long ebookId) throws IOException;
    List<EbookCategory> getCategoriesByEbookId(Long ebookId);
}
