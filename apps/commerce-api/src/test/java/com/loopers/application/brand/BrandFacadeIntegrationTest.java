package com.loopers.application.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand.Create;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BrandFacadeIntegrationTest {

    @Autowired
    private BrandFacade brandFacade;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("브랜드 조회 시,")
    class Get {
        @DisplayName("존재하지 않는 브랜드를 조회하면, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenBrandDoesNotExist() {
            Long nonExistentBrandId = 999L;

            CoreException thrown = assertThrows(CoreException.class, () -> brandFacade.getBrand(new BrandCriteria.Get(nonExistentBrandId)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 브랜드입니다."));
        }

        @DisplayName("존재하는 브랜드를 조회하면, 해당 브랜드 정보를 반환한다.")
        @Test
        void returnBrandResult_whenBrandExists() {
            Brand save = brandRepository.save(Brand.create(new Create("브랜드", "브랜드 설명")));

            BrandResult brandResult = brandFacade.getBrand(new BrandCriteria.Get(save.getId()));

            assertAll(
                    () -> assertThat(brandResult.id()).isEqualTo(save.getId()),
                    () -> assertThat(brandResult.name()).isEqualTo(save.getName()),
                    () -> assertThat(brandResult.description()).isEqualTo(save.getDescription())
            );
        }
    }
}
