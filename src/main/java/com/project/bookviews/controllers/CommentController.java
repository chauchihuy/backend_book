package com.project.bookviews.controllers;

import com.project.bookviews.dtos.CommentDTO;
import com.project.bookviews.models.Role;
import com.project.bookviews.models.User;
import com.project.bookviews.responses.CommentResponse;
import com.project.bookviews.responses.UserResponse;
import com.project.bookviews.services.comment.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("${api.prefix}/comments")
//@Validated
//Dependency Injection
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam("ebook_id") Long ebookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );

        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsByEbook(ebookId);
        } else {
            commentResponses = commentService.getCommentsByUserAndEbook(userId, ebookId);
        }
        return ResponseEntity.ok(commentResponses);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        try {
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
                return ResponseEntity.badRequest().body("You cannot update another user's comment");
            }
            commentService.updateComment(commentId, commentDTO);
            return ResponseEntity.ok("Update comment successfully");
        } catch (Exception e) {
            // Xử lý và ghi lại ngoại lệ
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during comment update.");
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<?> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        try {
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(!loginUser.getId().equals(commentDTO.getUserId())) {
                return ResponseEntity.badRequest().body("You cannot comment as another user");
            }
            commentService.insertComment(commentDTO);
            return ResponseEntity.ok("Insert comment successfully");
        } catch (Exception e) {
            // Log chi tiết ngoại lệ
            e.printStackTrace(); // Hoặc sử dụng logger để ghi lại log
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("An error occurred during comment insertion: " + e.getMessage());
        }
    }
//    @DeleteMapping("/{id}")
////    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//    public ResponseEntity<?> deleteComment(
//            @PathVariable("id") Long commentId
//    ) {
//        try {
//            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//            CommentResponse commentResponse = commentService.findCommentById(commentId);
//            if (loginUser.getId() != commentResponse.getUserResponse().getId()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body("You are not authorized to delete this comment");
//            }
//            commentService.deleteComment(commentId);
//            return ResponseEntity.ok("Delete comment successfully");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred during comment deletion: " + e.getMessage());
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long commentId) {
        try {
            User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CommentResponse commentResponse = commentService.findCommentById(commentId);

            // In ra giá trị role của người dùng
            String roleName = loginUser.getRole().getName();
            System.out.println("User role: " + roleName);

            boolean isAdmin = roleName.equalsIgnoreCase(Role.ADMIN);
            boolean isOwner = loginUser.getId().equals(commentResponse.getUserResponse().getId());

            // Nếu người dùng không phải là admin và không phải là chủ nhân của comment thì trả về lỗi 401
            if (!isAdmin && !isOwner) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("You are not authorized to delete this comment");
            }

            commentService.deleteComment(commentId);
            return ResponseEntity.ok("Delete comment successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred during comment deletion: " + e.getMessage());
        }
    }





    @GetMapping("/all")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<CommentResponse>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int limit

    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );

        Pageable pageable = PageRequest.of(page, limit, Sort.by("id").ascending());

        List<CommentResponse> commentResponses = commentService.getAllComments(pageable);
        return ResponseEntity.ok(commentResponses);
    }
}
