package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandCommand.Create;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand.Join;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductFacadeTest {

    @Autowired
    private ProductFacade productFacade;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 조회 시,")
    class Get {
        @DisplayName("존재하지 않는 상품을 조회하면, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenProductDoesNotExist() {
            Long productId = 999L;
            Long userId = 1L;

            CoreException thrown = assertThrows(CoreException.class, () ->
                    productFacade.getProduct(new ProductCriteria.Get(productId, userId))
            );

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }

        @DisplayName("유저 ID와 함께 존재하는 상품을 조회하면, 상품 좋아요 여부를 포함한 상세 정보를 반환한다.")
        @Test
        void returnProductInfo_whenProductExistsWithUser() {
            User user = userRepository.save(User.create(new Join("login", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId() + 1)));

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), user.getId()));

            assertAll(
                    () -> assertThat(productResult.id()).isEqualTo(product.getId()),
                    () -> assertThat(productResult.brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(productResult.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productResult.price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(productResult.status()).isEqualTo(product.getStatus().name()),
                    () -> assertThat(productResult.quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(productResult.likeCount()).isEqualTo(2L),
                    () -> assertThat(productResult.isLiked()).isTrue()
            );
        }

        @DisplayName("유저 ID 없이 존재하는 상품을 조회하면, 좋아요 여부를 제외한 상세 정보를 반환한다.")
        @Test
        void returnProductInfo_whenProductExistsWithoutUser() {
            User user = userRepository.save(User.create(new Join("login", "hgh1472@loopers.com", "1999-06-23", "MALE")));
            Product init = Product.create(new ProductCommand.Create(1L, "제품", new BigDecimal(1000L), "ON_SALE"));
            Product product = productRepository.findById(productRepository.save(init).getId()).get();
            Brand brand = brandRepository.save(Brand.create(new Create("브랜드", "브랜드 설명")));
            Stock stock = stockRepository.save(Stock.create(new StockCommand.Create(product.getId(), 100L)));
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId() + 1)));

            ProductResult productResult = productFacade.getProduct(new ProductCriteria.Get(product.getId(), 1L));

            assertAll(
                    () -> assertThat(productResult.id()).isEqualTo(product.getId()),
                    () -> assertThat(productResult.brandName()).isEqualTo(brand.getName()),
                    () -> assertThat(productResult.productName()).isEqualTo(product.getName()),
                    () -> assertThat(productResult.price()).isEqualTo(product.getPrice().getValue()),
                    () -> assertThat(productResult.status()).isEqualTo(product.getStatus().name()),
                    () -> assertThat(productResult.quantity()).isEqualTo(stock.getQuantity().getValue()),
                    () -> assertThat(productResult.likeCount()).isEqualTo(2L),
                    () -> assertThat(productResult.isLiked()).isFalse()
            );
        }
    }
}
