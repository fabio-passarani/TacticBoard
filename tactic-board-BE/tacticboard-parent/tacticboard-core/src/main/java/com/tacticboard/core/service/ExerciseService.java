package com.tacticboard.core.service;

import com.tacticboard.core.model.entity.training.Category;
import com.tacticboard.core.model.entity.training.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseService {
    
    Exercise createExercise(Exercise exercise);
    
    Exercise updateExercise(Exercise exercise);
    
    Optional<Exercise> getExerciseById(Long id);
    
    List<Exercise> getAllExercises();
    
    List<Exercise> getExercisesByCategory(Long categoryId);
    
    List<Exercise> getPublicExercises();
    
    void deleteExercise(Long id);
    
    Category createCategory(Category category);
    
    Category updateCategory(Category category);
    
    Optional<Category> getCategoryById(Long id);
    
    List<Category> getAllCategories();
    
    void deleteCategory(Long id);
}