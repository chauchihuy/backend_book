package com.project.bookviews.services.comment;

import com.project.bookviews.dtos.CommentDTO;
import com.project.bookviews.exceptions.DataNotFoundException;
import com.project.bookviews.models.Comment;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.User;
import com.project.bookviews.repositories.ICommentRepository;
import com.project.bookviews.repositories.IEbookRepository;
import com.project.bookviews.repositories.IUserRepository;
import com.project.bookviews.responses.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {
    private final ICommentRepository iCommentRepository;
    private final IUserRepository iUserRepository;
    private final IEbookRepository iEbookRepository;


    @Override
    @Transactional
    public Comment insertComment(CommentDTO commentDTO) {
        User user = iUserRepository.findById(commentDTO.getUserId()).orElse(null);
        Ebook ebook = iEbookRepository.findById(commentDTO.getEbookId()).orElse(null);
        if (user == null || ebook == null) {
            throw new IllegalArgumentException("User or Ebook not found");
        }
        Comment newComment = Comment.builder()
                .user(user)
                .ebook(ebook)
                .content(commentDTO.getContent())
                .build();
        return iCommentRepository.save(newComment);
    }


    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        iCommentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        Comment existingComment = iCommentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        iCommentRepository.save(existingComment);
    }


    @Override
    public List<CommentResponse> getCommentsByUserAndEbook(Long userId, Long ebookId) {
        List<Comment> comments = iCommentRepository.findByUserIdAndEbookId(userId, ebookId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentResponse> getCommentsByEbook(Long ebookId) {
        List<Comment> comments = iCommentRepository.findByEbookId(ebookId);
        return comments.stream()
                .map(comment -> CommentResponse.fromComment(comment))
                .collect(Collectors.toList());
    }
    @Override
    public CommentResponse findCommentById(Long commentId) throws DataNotFoundException {
        Comment comment = iCommentRepository.findById(commentId)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        return CommentResponse.fromComment(comment);
    }

    @Override
    public List<CommentResponse> getAllComments(Pageable pageable) {
        List<Comment> comments = iCommentRepository.findAll();
        return comments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }

}
