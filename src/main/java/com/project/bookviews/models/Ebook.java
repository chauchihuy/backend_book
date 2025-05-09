package com.project.bookviews.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "ebooks")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(EbookListener.class)
public class Ebook extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "kindofbook", nullable = false)
    private KindOfBook kindofbook;

    @Column(name = "document")
    private String document;

    @Column(name = "thumbnail")
    private String thumbnail;

//    @Column(name = "audio_url")
//    private String audioUrl;

    @Column(name = "evaluate")
    private Integer evaluate;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "active")
    private boolean active;

    @OneToMany(mappedBy = "ebook",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<EbookImage> ebookImages;

    @OneToMany(mappedBy = "ebook",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<EbookMp3> ebookMp3s;

    @OneToMany(mappedBy = "ebook", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();


    //xóa eboook có trong đơn mua
//    @OneToMany(mappedBy = "ebook", cascade = CascadeType.REMOVE)
//    private Set<OrderDetail> orderDetails;

}
