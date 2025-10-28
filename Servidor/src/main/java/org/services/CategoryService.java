package org.services;

import org.exceptions.CategoryNotFoundException;
import org.exceptions.CategoryAlreadyExistsException;
import org.model.events.Category;
import org.repositories.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {
    public CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(Category category){
        Optional<Category> optCat = categoryRepository.findById(category.getTitle());
        if(optCat.isPresent()){
            throw new CategoryAlreadyExistsException("La categoria " + category.getTitle() + " ya existe");
        }
        return categoryRepository.save(category);
    }

    public Category findCategory(Category category){
        return categoryRepository.findById(category.getTitle()).orElseThrow(()-> new CategoryNotFoundException("No se encontro la categoria " + category.getTitle()));
    }

    public Page<Category> getCategories(Integer page, Integer limit, String startsWith){

        return startsWith != null ? categoryRepository.findByTitleStartingWith(startsWith, PageRequest.of(page, limit))
                : categoryRepository.findAll(PageRequest.of(page, limit));
    }

    public void deleteCategoryByTitle(String category){
        Optional<Category> optCat = categoryRepository.findById(category);
        if(optCat.isEmpty()){
            throw new CategoryNotFoundException("No se encontro la categoria " + category);
        }
        categoryRepository.deleteById(category);
    }

}
