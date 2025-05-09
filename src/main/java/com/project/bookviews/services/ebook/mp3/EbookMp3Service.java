package com.project.bookviews.services.ebook.mp3;

import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;
import com.project.bookviews.repositories.IEbookImageRepository;
import com.project.bookviews.repositories.IEbookMp3Repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EbookMp3Service implements IEbookMp3Service{
    private final IEbookMp3Repository iEbookMp3Repository;

    @Override
    @Transactional
    public EbookMp3 deleteEbookMp3(Long id) throws DataNotFoundException, IOException {
        Optional<EbookMp3> ebookMp3 = iEbookMp3Repository.findById(id);
        if (ebookMp3.isEmpty()) {
            throw new DataNotFoundException(
                    String.format("Cannot find ebook mp3 with id: %d", id)
            );
        }
        iEbookMp3Repository.deleteById(id);
        return ebookMp3.get();
    }

    @Override
    public EbookMp3 getEbookMp3ById(Long id) throws DataNotFoundException {
        return iEbookMp3Repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ebook mp3 not found"));
    }
}
