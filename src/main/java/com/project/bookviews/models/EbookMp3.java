package com.project.bookviews.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ebook_mp3s")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EbookMp3 {

    public static final int MAXIMUM_VIDE_PER_EBOOK = 6;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ebook_id")
    @JsonIgnore
    private Ebook ebook;

    @Column(name = "mp3_url", length = 300)
    @JsonProperty("mp3_url")
    private String mp3Url;
}
