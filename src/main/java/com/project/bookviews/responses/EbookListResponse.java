package com.project.bookviews.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class EbookListResponse {
    private List<EbookResponse> ebooks;
    private int totalPages;
}
