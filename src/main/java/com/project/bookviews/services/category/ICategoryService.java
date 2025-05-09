package com.project.bookviews.services.category;

import com.project.bookviews.dtos.CategoryDTO;
import com.project.bookviews.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO category);
    Category getCategoryById(long id);
    List<Category> getAllCategory();
    Category updateCategory(long categoryid, CategoryDTO category);
    Category deleteCategory(long id) throws Exception;
    boolean isCategoryNameExists(String categoryName); // KT TRÙNG TÊN
}
