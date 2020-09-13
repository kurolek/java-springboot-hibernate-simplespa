package org.tyaa.demo.springboot.simplespa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tyaa.demo.springboot.simplespa.model.ProductFilterModel;
import org.tyaa.demo.springboot.simplespa.model.ProductModel;
import org.tyaa.demo.springboot.simplespa.model.ResponseModel;
import org.tyaa.demo.springboot.simplespa.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<ResponseModel> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<ResponseModel> create(@RequestBody ProductModel product) {
        return new ResponseEntity<>(service.create(product), HttpStatus.CREATED);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<ResponseModel> deleteProduct(@PathVariable Long id){
        ResponseModel responseModel = service.delete(id);
        System.out.println(responseModel);
        return new ResponseEntity<>(responseModel, HttpStatus.OK);
    }


    @GetMapping("/categories/{categoryIds}/products::orderBy:{orderBy}::sortingDirection:{sortingDirection}")
    public ResponseEntity<ResponseModel> getByCategories(
            @PathVariable List<Long> categoryIds,
            @PathVariable String orderBy,
            @PathVariable Sort.Direction sortingDirection
    ) {
        return new ResponseEntity<>(
            service.getFiltered(
                new ProductFilterModel(categoryIds, orderBy, sortingDirection)
            ),
            HttpStatus.OK
        );
    }
}
