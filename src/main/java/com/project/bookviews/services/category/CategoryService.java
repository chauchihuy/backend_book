package com.project.bookviews.services.category;

import com.project.bookviews.components.LocalizationUtils;
import com.project.bookviews.dtos.CategoryDTO;
import com.project.bookviews.exceptions.CategoryException;
import com.project.bookviews.models.Category;
import com.project.bookviews.models.Ebook;
import com.project.bookviews.models.EbookCategory;
import com.project.bookviews.repositories.ICategoryRepository;
import com.project.bookviews.repositories.IEbookCategoryRepository;
import com.project.bookviews.repositories.IEbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService
{

    @Autowired

    private final ICategoryRepository iCategoryRepository;
    private final IEbookRepository iEbookRepository;
    private final LocalizationUtils localizationUtils;
    private final IEbookCategoryRepository iEbookCategoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryDTO categoryDTO) {
        if (isCategoryNameExists(categoryDTO.getName())) {
            throw new CategoryException("Category name already exists");
        }
        Category category = new Category();
        category.setName(categoryDTO.getName());
        return iCategoryRepository.save(category);
    }
//    public Category createCategory(CategoryDTO categoryDTO) {
//        Category newCategory = Category
//                .builder()
//                .name(categoryDTO.getName()).build();
//        return ICategoryRepository.save(newCategory);
//    }

    @Override
    public Category getCategoryById(long id) {
        return iCategoryRepository.findById(id).
                orElseThrow(() ->  new RuntimeException("Category not found"));
    }

    @Override
    public List<Category> getAllCategory() {
        return iCategoryRepository.findAll();
    }

    @Override
    @Transactional
    public Category updateCategory(long categoryId, CategoryDTO categoryDTO) {
        // Kiểm tra xem tên danh mục mới đã tồn tại chưa
        if (isCategoryNameExists(categoryDTO.getName())) {
            throw new CategoryException("Category name already exists");
        }

        // Lấy danh mục hiện tại
        Category existingCategory = getCategoryById(categoryId);

        // Cập nhật tên danh mục
        existingCategory.setName(categoryDTO.getName());

        // Lưu danh mục đã cập nhật vào cơ sở dữ liệu
        return iCategoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public Category deleteCategory(long id) throws Exception {
        Category category = iCategoryRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        List<Ebook> products = iEbookCategoryRepository.findAllByCategory(category)
                .stream()
                .map(EbookCategory::getEbook)
                .toList();
        if (!products.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated products");
        } else {
            iCategoryRepository.deleteById(id);
            return  category;
        }
    }

    @Override
    public boolean isCategoryNameExists(String categoryName) {
        return iCategoryRepository.existsByName(categoryName);
    }
}
