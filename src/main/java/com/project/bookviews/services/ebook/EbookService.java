
package com.project.bookviews.services.ebook;

import com.project.bookviews.dtos.EbookDTO;
import com.project.bookviews.dtos.EbookImageDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.exceptions.InvalidParamException;
import com.project.bookviews.models.*;
import com.project.bookviews.repositories.*;
import com.project.bookviews.responses.EbookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EbookService implements IEbookService {
    private final IEbookRepository IEbookRepository;
    private final ICategoryRepository ICategoryRepository;
    private final IEbookImageRepository IEbookImageRepository;
    private final IEbookMp3Repository iEbookMp3Repository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final IEbookCategoryRepository iEbookCategoryRepository;
    private final IOrderDetailRepository iOrderDetailRepository;
    private static String UPLOADS_FOLDER = "uploads";
    private static String UPLOADS_MP3 = "uploadmp3s";

//    @Override
//    @Transactional
//    public Ebook createEbook(EbookDTO ebookDTO) throws DataNotFoundException {
//        if (ebookDTO.getCategoryId() == null || ebookDTO.getCategoryId().isEmpty()) {
//            throw new IllegalArgumentException("Category IDs cannot be null or empty");
//        }
//
//        List<Category> categories = new ArrayList<>();
//        for (Long cateId : ebookDTO.getCategoryId()) {
//            Category existingCategory = ICategoryRepository
//                    .findById(cateId)
//                    .orElseThrow(() ->
//                            new DataNotFoundException(
//                                    "Cannot find category with id: " + cateId));
//            categories.add(existingCategory);
//        }
//
////        if (ebookDTO.getKindofbook() == KindOfBook.FREE || ebookDTO.getKindofbook() == KindOfBook.MEMBERSHIP) {
////            ebookDTO.setPrice(0);
////        }
////
////        if (ebookDTO.getKindofbook() == KindOfBook.FOR_SALE && ebookDTO.getPrice() <= 0) {
////            throw new IllegalArgumentException("Price must be greater than zero for paid books");
////        }
//        if (ebookDTO.getKindofbook() == KindOfBook.FREE || ebookDTO.getKindofbook() == KindOfBook.MEMBERSHIP) {
//            ebookDTO.setPrice(0);
//        }
//         else if (ebookDTO.getKindofbook() == KindOfBook.FOR_SALE) {
//            // Nếu là sách bán, giá phải lớn hơn 0
//            if (ebookDTO.getPrice() <= 0) {
//                throw new IllegalArgumentException("Ebook bán giá tiền lớn hơn 0");
//            }
//        }
//
//        Ebook newEbook = Ebook.builder()
//                .name(ebookDTO.getName())
//                .title(ebookDTO.getTitle())
//                .price(ebookDTO.getPrice())
//                .thumbnail(ebookDTO.getThumbnail())
//                .document(ebookDTO.getDocument())
//                .kindofbook(ebookDTO.getKindofbook())
////                .audioUrl(ebookDTO.getAudioUrl())
//                .evaluate(ebookDTO.getEvaluate())
//                .build();
//
//        IEbookRepository.save(newEbook);
//        for (Category category : categories) {
//            EbookCategory ebookCategory = EbookCategory.builder()
//                    .ebook(newEbook)
//                    .category(category)
//                    .build();
//            iEbookCategoryRepository.save(ebookCategory);
//        }
//
//        return newEbook;
//    }

    @Override
    @Transactional
    public Ebook createEbook(EbookDTO ebookDTO) throws DataNotFoundException {
        // Kiểm tra danh sách categoryId
        if (ebookDTO.getCategoryId() == null || ebookDTO.getCategoryId().isEmpty()) {
            throw new IllegalArgumentException("Danh mục không được để trống hoặc rỗng");
        }

        List<Category> categories = new ArrayList<>();
        for (Long cateId : ebookDTO.getCategoryId()) {
            Category existingCategory = ICategoryRepository
                    .findById(cateId)
                    .orElseThrow(() ->
                            new DataNotFoundException(
                                    "Cannot find category with id: " + cateId));
            categories.add(existingCategory);
        }

        // Kiểm tra loại sách và giá
        if (ebookDTO.getKindofbook() == KindOfBook.FREE || ebookDTO.getKindofbook() == KindOfBook.MEMBERSHIP) {
            // Đặt giá thành 0 cho sách miễn phí hoặc sách thành viên
            ebookDTO.setPrice(0);
        } if (ebookDTO.getKindofbook() == KindOfBook.FOR_SALE && ebookDTO.getPrice() <= 0) {
            // Nếu là sách bán, giá phải lớn hơn 0
                throw new IllegalArgumentException("Ebook bán giá tiền phải lớn hơn 0");
        }
        // Tạo sách mới
        Ebook newEbook = Ebook.builder()
                .name(ebookDTO.getName())
                .title(ebookDTO.getTitle())
                .price(ebookDTO.getPrice())
                .thumbnail(ebookDTO.getThumbnail())
                .document(ebookDTO.getDocument())
                .kindofbook(ebookDTO.getKindofbook())
                .evaluate(ebookDTO.getEvaluate())
                .build();

        IEbookRepository.save(newEbook);

        // Thêm các thể loại cho sách
        for (Category category : categories) {
            EbookCategory ebookCategory = EbookCategory.builder()
                    .ebook(newEbook)
                    .category(category)
                    .build();
            iEbookCategoryRepository.save(ebookCategory);
        }

        return newEbook;
    }


    @Override
    public Ebook getEbookById(Long id) throws DataNotFoundException {
        return IEbookRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ebook not found with id: " + id));
    }

    @Override
    public List<Ebook> findEbooksByIds(List<Long> ebookIds) {
        return IEbookRepository.findEbooksByIds(ebookIds);
    }


    //ebook cho admin
    @Override
    public Page<EbookResponse> getAllEbooks(String keyword, Long categoryId, PageRequest pageRequest) {
//        Page<Ebook> ebooksPage = IEbookRepository.searchEbooks(categoryId, keyword, pageRequest);
//
//        return ebooksPage.map(EbookResponse::fromEbook);
        if ((keyword == null || keyword.isEmpty()) && (categoryId == null || categoryId == 0)) {
            return IEbookRepository.findAll(pageRequest).map(EbookResponse::fromEbook);
        }

        // Ngược lại, sử dụng phương thức tìm kiếm với các bộ lọc
        Page<Ebook> ebooksPage = IEbookRepository.searchEbooks(categoryId, keyword, pageRequest);
        return ebooksPage.map(EbookResponse::fromEbook);
    }


    // hiển thị ebook cho user - home
    @Override
    public Page<EbookResponse> getAllInactiveEbooksUser(String keyword, Long categoryId, PageRequest pageRequest) {
        if ((keyword == null || keyword.isEmpty()) && (categoryId == null || categoryId == 0)) {
            return IEbookRepository.findAllInactiveEbooksUser(pageRequest).map(EbookResponse::fromEbook);
        } else {
            return IEbookRepository.searchInactiveEbooks(categoryId, keyword, pageRequest).map(EbookResponse::fromEbook);
        }
    }

    @Override
    @Transactional
    public Ebook updateEbook(long id, EbookDTO ebookDTO) throws Exception {
        Ebook existingEbook = getEbookById(id);

        if (ebookDTO.getKindofbook() == KindOfBook.FREE || ebookDTO.getKindofbook() == KindOfBook.MEMBERSHIP) {
            ebookDTO.setPrice(0);
        }

        if (ebookDTO.getKindofbook() == KindOfBook.FOR_SALE && ebookDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero for paid books");
        }
        if (existingEbook != null) {
//            Category existingCategory = ICategoryRepository
//                    .findById(ebookDTO.getCategoryId())
//                    .orElseThrow(() ->
//                            new DataNotFoundException(
//                                    "Cannot find category with id: " + ebookDTO.getCategoryId()));

            if (ebookDTO.getName() != null && !ebookDTO.getName().isEmpty()) {
                existingEbook.setName(ebookDTO.getName());
            }

            if (ebookDTO.getPrice() >= 0) {
                existingEbook.setPrice(ebookDTO.getPrice());
            }
            if (ebookDTO.getTitle() != null && !ebookDTO.getTitle().isEmpty()) {
                existingEbook.setTitle(ebookDTO.getTitle());
            }
            if (ebookDTO.getDocument() != null && !ebookDTO.getDocument().isEmpty()) {
                existingEbook.setDocument(ebookDTO.getDocument());
            }
            if (ebookDTO.getKindofbook() != null) {
                existingEbook.setKindofbook(ebookDTO.getKindofbook());
            }
//            if (ebookDTO.getAudioUrl() != null && !ebookDTO.getAudioUrl().isEmpty()) {
//                existingEbook.setAudioUrl(ebookDTO.getAudioUrl());
//            }
            if (ebookDTO.getEvaluate() != null) {
                existingEbook.setEvaluate(ebookDTO.getEvaluate());
            }
            if (ebookDTO.getThumbnail() != null && !ebookDTO.getThumbnail().isEmpty()) {
                existingEbook.setThumbnail(ebookDTO.getThumbnail());
            }
            // Cập nhật danh mục cho ebook
            if (ebookDTO.getCategoryId() != null && !ebookDTO.getCategoryId().isEmpty()) {

                // Lấy các thể loại mới từ categoryId
                List<Category> categories = new ArrayList<>();
                for (Long cateId : ebookDTO.getCategoryId()) {
                    Category existingCategory = ICategoryRepository
                            .findById(cateId)
                            .orElseThrow(() ->
                                    new DataNotFoundException(
                                            "Không tìm thấy danh mục có id: " + cateId));
                    categories.add(existingCategory);
                }

                // Thêm lại các thể loại cho ebook
                for (Category category : categories) {
                    EbookCategory ebookCategory = EbookCategory.builder()
                            .ebook(existingEbook)
                            .category(category)
                            .build();
                    iEbookCategoryRepository.save(ebookCategory);
                }
            }

            return IEbookRepository.save(existingEbook);
        }
        return null;
    }

//    @Override
//    @Transactional
//    public void deleteEbook(long id) {
//        Optional<Ebook> optionalEbook = IEbookRepository.findById(id);
//        optionalEbook.ifPresent(IEbookRepository::delete);
//    }

//    @Override
//    @Transactional
//    public void deleteEbook(long id) throws Exception {
//        Optional<Ebook> optionalEbook =  IEbookRepository.findById(id);
//        if (optionalEbook.isPresent()) {
//            Ebook ebook = optionalEbook.get();
//            if (iOrderDetailRepository.existsByEbookId(id)) {
//                throw new Exception("Không thể xóa ebook. Ebook đã được mua và không thể xóa.");
//            }
//            IEbookRepository.delete(ebook);
//        } else {
//            throw new Exception("Không tìm thấy Ebook.");
//        }
//    }

    @Override
    public boolean updateActiveStatus(Long id) {
        return IEbookRepository.findById(id).map(ebook -> {
            ebook.setActive(!ebook.isActive()); // Chuyển trạng thái từ active sang inactive và ngược lại
            IEbookRepository.save(ebook);
            return true;
        }).orElse(false); // Trả về false nếu không tìm thấy ebook
    }


    @Override
    public boolean existsByName(String name) {
        return IEbookRepository.existsByName(name);
    }

    @Override
    @Transactional
    public EbookImage createEbookImage(Long ebookId, EbookImageDTO ebookImageDTO) throws Exception {
        Ebook existingEbook = IEbookRepository
                .findById(ebookId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find ebook with id: " + ebookImageDTO.getEbookId()));

        EbookImage newEbookImage = EbookImage.builder()
                .ebook(existingEbook)
                .imageUrl(ebookImageDTO.getImageUrl())
                .build();

        int size = IEbookImageRepository.findByEbookId(ebookId).size();
        if (size >= EbookImage.MAXIMUM_IMAGES_PER_EBOOK) {
            throw new InvalidParamException(
                    "Number of images must be <= " + EbookImage.MAXIMUM_IMAGES_PER_EBOOK);
        }

        if (existingEbook.getThumbnail() == null) {
            existingEbook.setThumbnail(newEbookImage.getImageUrl());
        }
        IEbookRepository.save(existingEbook);
        return IEbookImageRepository.save(newEbookImage);
    }


    public void updateEbookThumbnailOnImageDelete(Long ebookId) throws DataNotFoundException {
        Ebook ebook = IEbookRepository.findById(ebookId)
                .orElseThrow(() -> new DataNotFoundException("Ebook not found"));

        // Lấy ảnh đầu tiên trong danh sách ebookImages (nếu có)
        if (!ebook.getEbookImages().isEmpty()) {
            EbookImage firstImage = ebook.getEbookImages().get(0);
            ebook.setThumbnail(firstImage.getImageUrl());
            IEbookRepository.save(ebook);
        }
    }


    @Override
    @Transactional
    public void deleteFile(String filename) throws IOException {
        java.nio.file.Path uploadDir = Paths.get(UPLOADS_FOLDER);

        java.nio.file.Path filePath = uploadDir.resolve(filename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new FileNotFoundException("File not found: " + filename);
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private boolean isMp3File(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("audio/mpeg");
    }


    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        if (!isImageFile(file) ) {
            throw new IllegalArgumentException("Unsupported file type. Supported types: image, mp3");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = originalFilename;
//        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path uploadDir = Paths.get(UPLOADS_FOLDER);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path destination;
        if (isImageFile(file)) {
            destination = uploadDir.resolve(uniqueFilename);
        }
        else { // Assuming handling for audio files
            Path audioUploadDir = Paths.get(UPLOADS_MP3);
            if (!Files.exists(audioUploadDir)) {
                Files.createDirectories(audioUploadDir);
            }
            destination = audioUploadDir.resolve(uniqueFilename);
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @Override
    @Transactional
    public void uploadMultiFile(List<MultipartFile> files, Long ebookId) throws IOException{
        Ebook existingEbook = IEbookRepository.findById(ebookId).orElse(null);
        if(existingEbook != null){
            Path uploadDir = Paths.get(UPLOADS_MP3);
            // Kiểm tra và tạo thư mục nếu nó không tồn tại
            if(!Files.exists(uploadDir)){
                Files.createDirectories(uploadDir);
            }
            List<String> audios = new ArrayList<>();
            for(MultipartFile file : files){

                String uniqueFileName = generateUniqueFileName(file);
                // Đường dẫn đầy đủ đến file
                Path destination = Paths.get(uploadDir.toString(),uniqueFileName);

                // Sao chép file vào thư mục đích
                Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
                audios.add(uniqueFileName);
            }
            for(String audio : audios){
                iEbookMp3Repository.save(EbookMp3.builder()
                                .ebook(existingEbook)
                                .mp3Url(audio)
                                .build());
            }
        }

    }
    public String generateUniqueFileName(MultipartFile file){
        // Lấy ra file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        // Tạo ra tên file duy nhất randome
        return UUID.randomUUID().toString() + "_" + fileName;
    }
//    @Override
//    public String storeFile(MultipartFile file) throws IOException {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("File is empty or null");
//        }
//
//        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
//
//        Path uploadDir = isImageFile(file) ? Paths.get(UPLOADS_FOLDER) : Paths.get(UPLOADS_MP3);
//
//        if (!Files.exists(uploadDir)) {
//            Files.createDirectories(uploadDir);
//        }
//
//        Path destination = uploadDir.resolve(uniqueFilename);
//        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//
//        return uniqueFilename;
//    }

   // @Override
//    public String storeMp3File(MultipartFile file) throws IOException {
//        if (!isMp3File(file) || file.getOriginalFilename() == null) {
//            throw new IOException("Invalid audio format");
//        }
//        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        // Add UUID before the filename to ensure the filename is unique
//        String uniqueFilename = filename;
////        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
//        // Path to the directory where you want to save the file
//        java.nio.file.Path uploadDir = Paths.get(UPLOADS_MP3);
//        // Check and create directory if it doesn't exist
//        if (!Files.exists(uploadDir)) {
//            Files.createDirectories(uploadDir);
//        }
//        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
//        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//        return uniqueFilename;
//
//    }
//    @Override
//    public String storeMp3File(MultipartFile file) throws IOException {
//        if (!isMp3File(file) || file.getOriginalFilename() == null) {
//            throw new IOException("Invalid audio format or filename");
//        }
//
//        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
//        String uniqueFilename = originalFilename;
//
//        Path uploadDir = Paths.get(UPLOADS_MP3);
//
//        if (!Files.exists(uploadDir)) {
//            try {
//                Files.createDirectories(uploadDir);
//            } catch (IOException e) {
//                throw new IOException("Could not create upload directory", e);
//            }
//        }
//
//        Path destination = uploadDir.resolve(uniqueFilename);
//
//        try {
//            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            throw new IOException("Failed to store file " + uniqueFilename, e);
//        }
//
//        return uniqueFilename;
//    }


    //succes
//   @Override
//   public void storeMp3File(MultipartFile file, String filename) throws IOException {
//       Path uploadDir = Paths.get(UPLOADS_MP3);
//
//       if (!Files.exists(uploadDir)) {
//           try {
//               Files.createDirectories(uploadDir);
//           } catch (IOException e) {
//               throw new IOException("Could not create upload directory", e);
//           }
//       }
//
//       Path destination = uploadDir.resolve(filename);
//
//       try {
//           Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
//       } catch (IOException e) {
//           throw new IOException("Failed to store file " + filename, e);
//       }
//   }

    public void storeMp3File(MultipartFile file, String filename) throws IOException {
        Path path = Paths.get("uploadmp3s/" + filename);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    }


    @Override
    public void updateEbookAudioUrl(Long ebookId, String newAudioUrl) throws DataNotFoundException, IOException {
//        Ebook existingEbook = IEbookRepository.findById(ebookId)
//                .orElseThrow(() -> new DataNotFoundException("Ebook not found with id: " + ebookId));
//
//        String currentAudioUrl = existingEbook.getAudioUrl();
//
//        if (currentAudioUrl != null && !currentAudioUrl.isEmpty()) {
//            // lấy tên file từ url random
//            Path path = Paths.get(currentAudioUrl);
//            String filename = path.getFileName().toString();
//
//            // Delete the current audio file (consider error handling)
//            try {
//                Files.delete(Paths.get(UPLOADS_MP3, filename));
//            } catch (IOException e) {
//                // Log the error or handle it appropriately
//                System.out.println("Error deleting old audio file: " + e.getMessage());
//            }
//        }
//
//        existingEbook.setAudioUrl(newAudioUrl);
//        IEbookRepository.save(existingEbook);
    }

    public List<EbookCategory> getCategoriesByEbookId(Long ebookId) {
        return iEbookCategoryRepository.findByEbookId(ebookId);
    }
}

