package com.project.bookviews.services.comment;

import com.project.bookviews.dtos.CommentDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.Comment;
import com.project.bookviews.responses.CommentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentService {
    Comment insertComment(CommentDTO comment);

    void deleteComment(Long commentId);
    void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;

    List<CommentResponse> getCommentsByUserAndEbook(Long userId, Long productId);
    List<CommentResponse> getCommentsByEbook(Long productId);
     CommentResponse findCommentById(Long commentId) throws DataNotFoundException;
     List<CommentResponse> getAllComments(Pageable pageable);
}
