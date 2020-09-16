package org.tyaa.demo.springboot.simplespa.junit.service;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.tyaa.demo.springboot.simplespa.dao.CategoryHibernateDAO;
import org.tyaa.demo.springboot.simplespa.entity.Category;
import org.tyaa.demo.springboot.simplespa.model.CategoryModel;
import org.tyaa.demo.springboot.simplespa.model.ResponseModel;
import org.tyaa.demo.springboot.simplespa.service.CategoryService;
import org.tyaa.demo.springboot.simplespa.service.interfaces.ICategoryService;

import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {


    @Mock
    private CategoryHibernateDAO categoryDAO;

    @Mock
    private ICategoryService categoryServiceMock;

    @InjectMocks
    private CategoryService categoryService;

    ArgumentCaptor<Category> categoryArgument =
            ArgumentCaptor.forClass(Category.class);


    @BeforeAll
    static void setup() {
        System.out.println("CategoryService Unit Test Started");
    }

    @BeforeEach
    void init() {
        System.out.println("Test Case Started");
    }

    @Test
    void demoCase() {
        assertTrue(2 * 2 == 4);
    }

    @AfterEach
    void tearDown() {
        System.out.println("Test Case Finished");
    }

    @Test
    void shouldCreateCategory() {
        //проверка создания
        final CategoryModel categoryModel =
                CategoryModel.builder()
                        .name("test category")
                        .build();
        ResponseModel responseModel =
                categoryService.create(categoryModel);
        //проверка что результат не Null
        assertNotNull(responseModel);
        //проверка что возвращен положительный результат
        assertEquals(ResponseModel.SUCCESS_STATUS, responseModel.getStatus());
        //проверка что был вызван метод .save
        verify(categoryDAO, atLeast(1))
                .save(categoryArgument.capture());


    }

    @Test
    void shouldReturnGetAll() {
        doReturn(
                ResponseModel.builder()
                        .status(ResponseModel.SUCCESS_STATUS)
                        .data(Arrays.asList(new CategoryModel(1L, "category1"),
                                new CategoryModel(2L, "category2"),
                                new CategoryModel(3L, "category3"))).build()
        ).when(categoryServiceMock).getAll();
        //вызов из макета сервиса
        ResponseModel responseModel =
                categoryServiceMock.getAll();

        assertNotNull(responseModel);
        assertNotNull(responseModel.getData());
        assertEquals((((List) responseModel.getData()).size()), 3);

    }

    //проверка ошибки максимальной длинны
    @Test
    void shouldThrowConstraintException(){
        final String tooLong = "11111111111111111111111111111111111111111111111111111111111111111111111111111";
        final CategoryModel categoryModel = CategoryModel.builder().name(tooLong).build();

        BDDMockito.given(categoryServiceMock.create(categoryModel))
                .willThrow(new IllegalArgumentException());

        try {
            final CategoryModel categoryModel1 =
                    CategoryModel.builder()
                            .name(tooLong)
                            .build();
            categoryServiceMock.create(categoryModel1);
            fail("Should throw an IllegalArgumentException");
        } catch (IllegalArgumentException exception) {

        }
        then(categoryDAO)
                .should(never())
                .save(categoryArgument.capture());

    }

    @AfterAll
    static void done() {
        System.out.println("CategoryService Unit Test Finished");
    }


}
