package com.project.bookviews.services.ebook.image;

import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.repositories.IEbookImageRepository;
import com.project.bookviews.repositories.IEbookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EbookImageService implements IEbookImageService {
    private final IEbookImageRepository IEbookImageRepository;


    @Override
    @Transactional
    public EbookImage deleteEbookImage(Long id) throws Exception {
        Optional<EbookImage> ebookImage = IEbookImageRepository.findById(id);
        if (ebookImage.isEmpty()) {
            throw new DataNotFoundException(
                    String.format("Cannot find ebook image with id: %d", id)
            );
        }
        IEbookImageRepository.deleteById(id);
        return ebookImage.get();
    }
    public EbookImage getEbookImageById(Long id) throws DataNotFoundException {
        return IEbookImageRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ebook image not found"));
    }

}
