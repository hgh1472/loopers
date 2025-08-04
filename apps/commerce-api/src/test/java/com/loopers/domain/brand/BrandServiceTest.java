package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @Nested
    @DisplayName("브랜드 조회 시,")
    class Find {
        @DisplayName("브랜드가 존재하지 않으면, null을 반환한다.")
        @Test
        void returnNull_whenBrandDoesNotExist() {
            given(brandRepository.findBy(anyLong()))
                    .willReturn(Optional.empty());

            BrandInfo brandInfo = brandService.findBy(new BrandCommand.Find(1L));

            assertThat(brandInfo).isNull();
        }
    }
}
