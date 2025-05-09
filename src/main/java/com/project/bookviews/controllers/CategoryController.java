package com.project.bookviews.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.CategoryDTO;
import com.project.bookviews.exceptions.CategoryException;
import com.project.bookviews.models.Category;
import com.project.bookviews.responses.CategoryResponse;
import com.project.bookviews.responses.UpdateCategoryResponse;
import com.project.bookviews.services.category.CategoryService;
import com.project.bookviews.services.category.ICategoryService;
import com.project.bookviews.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final ICategoryService categoryService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryDTO categoryDTO,
            BindingResult result) {
        CategoryResponse categoryResponse = new CategoryResponse();

        try {
            // Kiểm tra lỗi xác thực
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());

                categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
                categoryResponse.setErrors(errorMessages);

                return ResponseEntity.badRequest().body(categoryResponse);
            }

            // Tạo danh mục mới
            Category category = categoryService.
                    createCategory(categoryDTO);
            categoryResponse.setCategory(category);
            categoryResponse.setMessage("Tạo danh mục thành công");
            categoryResponse.setErrors(null); // Đảm bảo rằng trường errors không bị null

            return ResponseEntity.ok(categoryResponse);
        } catch (CategoryException e) {
            categoryResponse.setMessage(e.getMessage());
            categoryResponse.setErrors(null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(categoryResponse);
        } catch (Exception e) {
            categoryResponse.setMessage("Tạo danh mục thất bại");
            categoryResponse.setErrors(List.of(e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(categoryResponse);
        }
    }
//    public ResponseEntity<CategoryResponse> createCategory(
//            @Valid @RequestBody CategoryDTO categoryDTO,
//            BindingResult result) {
//        CategoryResponse categoryResponse = new CategoryResponse();
//        if(result.hasErrors()) {
//            List<String> errorMessages = result.getFieldErrors()
//                    .stream()
//                    .map(FieldError::getDefaultMessage)
//                    .toList();
//            categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
//            categoryResponse.setErrors(errorMessages);
//            return ResponseEntity.badRequest().body(categoryResponse);
//        }
//        Category category = categoryService.createCategory(categoryDTO);
//        categoryResponse.setCategory(category);
//        return ResponseEntity.ok(categoryResponse);
//    }

    @GetMapping("")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Category>> getAllCategories( // hàm xem tất cả danh mục
            @RequestParam(value="page", defaultValue ="1") int page, //phân trang
            @RequestParam(value="limit", defaultValue = "10") int limit //phân trang
    ) throws JsonProcessingException {
        int totalPages = 0;
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").descending()
        );
        {
            List<Category> categories = categoryService.getAllCategory(); // gọi service để lấy tất cả danh mục nhấn phím ctrl + click vào sau chữ màu tím
            return ResponseEntity.ok(categories);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(
            @PathVariable("id") Long categoryId
    ) {
        try {
            Category existingCategory = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(existingCategory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UpdateCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ){
            UpdateCategoryResponse updateCategoryResponse = new UpdateCategoryResponse();
            categoryService.updateCategory(id, categoryDTO);
            updateCategoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
            return ResponseEntity.ok(updateCategoryResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok("Xóa danh mục với id " + id + " thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa danh mục thất bại");
        }
    }
}
