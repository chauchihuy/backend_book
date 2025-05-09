package com.project.bookviews.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.EbookImage;
import com.project.bookviews.models.EbookMp3;
import com.project.bookviews.models.KindOfBook;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EbookResponse extends BaseResponse {
    private Long id;
    private String name;
    private String title;
    private KindOfBook kindofbook;
    private String document;
    private double price;
    private String thumbnail;
//    private String audioUrl;
    private Integer evaluate;
    // Thêm trường totalPages
    private int totalPages;
    private boolean active;
    @JsonProperty("ebook_images")
    private List<EbookImage> ebookImages = new ArrayList<>();

    @JsonProperty("ebook_mp3s")
    private List<EbookMp3> ebookMp3s = new ArrayList<>();

    public static EbookResponse fromEbook(Ebook ebook) {
        EbookResponse ebookResponse = EbookResponse.builder()
                .id(ebook.getId())
                .name(ebook.getName())
                .title(ebook.getTitle())
                .kindofbook(ebook.getKindofbook())
                .document(ebook.getDocument())
                .price(ebook.getPrice())
                .thumbnail(ebook.getThumbnail())
//                .audioUrl(ebook.getAudioUrl())
                .evaluate(ebook.getEvaluate())
                .ebookImages(ebook.getEbookImages())
                .ebookMp3s(ebook.getEbookMp3s())
                .active(ebook.isActive())
                .build();
//        ebookResponse.setCreatedAt(ebook.getCreatedAt());
//        ebookResponse.setUpdatedAt(ebook.getUpdatedAt());
        return ebookResponse;
    }
}
