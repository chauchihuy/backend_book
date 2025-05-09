package com.project.bookviews.models;

import jakarta.persistence.*;
import lombok.*;
import org.apache.kafka.common.protocol.types.Field;

@Entity
@Table(name = "categories")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false) // không được phép null
    private String name;

}
