package com.tacticboard.persistence.service.impl;

import com.tacticboard.core.exception.ResourceNotFoundException;
import com.tacticboard.core.model.entity.training.Category;
import com.tacticboard.core.model.entity.training.Exercise;
import com.tacticboard.core.service.ExerciseService;
import com.tacticboard.persistence.repository.CategoryRepository;
import com.tacticboard.persistence.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, CategoryRepository categoryRepository) {
        this.exerciseRepository = exerciseRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise updateExercise(Exercise exercise) {
        if (!exerciseRepository.existsById(exercise.getId())) {
            throw new ResourceNotFoundException("Exercise", "id", exercise.getId());
        }
        return exerciseRepository.save(exercise);
    }

    @Override
    public Optional<Exercise> getExerciseById(Long id) {
        return exerciseRepository.findById(id);
    }

    @Override
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @Override
    public List<Exercise> getExercisesByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        return exerciseRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<Exercise> getPublicExercises() {
        return exerciseRepository.findByIsPublicTrue();
    }

    @Override
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercise", "id", id);
        }
        exerciseRepository.deleteById(id);
    }

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new ResourceNotFoundException("Category", "id", category.getId());
        }
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        categoryRepository.deleteById(id);
    }
}