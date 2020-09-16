package org.tyaa.demo.springboot.simplespa.service.interfaces;

import org.tyaa.demo.springboot.simplespa.model.CategoryModel;
import org.tyaa.demo.springboot.simplespa.model.ResponseModel;

public interface ICategoryService {
    ResponseModel create(CategoryModel categoryModel);
    ResponseModel getAll();
    ResponseModel delete(Long id);
}
