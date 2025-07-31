package com.loopers.application.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.domain.count.ProductCount;
import com.loopers.domain.count.ProductCountRepository;
import com.loopers.domain.like.ProductLike;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LikeFacadeIntegrationTest {
    @Autowired
    private LikeFacade likeFacade;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private ProductCountRepository productCountRepository;
    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 좋아요 등록 시,")
    class Like {

        @DisplayName("존재하지 않는 사용자 요청일 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenUserNotExists() {
            LikeCriteria.Product criteria = new LikeCriteria.Product(1L, 1L);

            CoreException thrown = assertThrows(CoreException.class, () -> likeFacade.like(criteria));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }

        @DisplayName("이미 좋아요가 등록된 상품에 대해 좋아요를 다시 등록하면, 좋아요 수는 증가되지 않는다.")
        @Test
        void DoNotIncrementCount_whenKikeAlreadyExists() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));
            ProductCount before = ProductCount.from(product.getId());
            before.incrementLike();
            productCountRepository.save(before);

            LikeResult.Product result = likeFacade.like(new LikeCriteria.Product(product.getId(), user.getId()));

            boolean likeExists = productLikeRepository.existsByProductIdAndUserId(result.productId(), result.userId());
            Optional<ProductCount> findProductCount = productCountRepository.findBy(product.getId());
            assertAll(
                    () -> assertThat(likeExists).isTrue(),
                    () -> assertThat(findProductCount).isPresent(),
                    () -> assertThat(findProductCount.get().getLikeCount()).isEqualTo(before.getLikeCount())
            );
        }

        @DisplayName("좋아요가 등록되고, 상품 좋아요 수가 증가한다.")
        @Test
        void like() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            ProductCount before = productCountRepository.save(ProductCount.from(product.getId()));
            LikeCriteria.Product criteria = new LikeCriteria.Product(product.getId(), user.getId());

            LikeResult.Product result = likeFacade.like(criteria);

            boolean likeExists = productLikeRepository.existsByProductIdAndUserId(result.productId(), result.userId());
            Optional<ProductCount> findProductCount = productCountRepository.findBy(product.getId());
            assertAll(
                    () -> assertThat(likeExists).isTrue(),
                    () -> assertThat(findProductCount).isPresent(),
                    () -> assertThat(findProductCount.get().getLikeCount()).isEqualTo(before.getLikeCount() + 1)
            );
        }
    }

    @Nested
    @DisplayName("상품 좋아요 취소 시,")
    class CancelLike {

        @DisplayName("존재하지 않는 사용자 요청일 경우, NOT_FOUND 예외가 발생한다.")
        @Test
        void throwNotFoundException_whenUserNotExists() {
            LikeCriteria.Product criteria = new LikeCriteria.Product(1L, 1L);

            CoreException thrown = assertThrows(CoreException.class, () -> likeFacade.cancelLike(criteria));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."));
        }

        @DisplayName("좋아요가 등록되지 않은 상품에 대해 좋아요 취소를 요청하면, 좋아요 수는 감소되지 않는다.")
        @Test
        void DoNotDecrementCount_whenLikeNotExists() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            ProductCount before = ProductCount.from(product.getId());
            before.incrementLike();
            productCountRepository.save(before);

            LikeResult.Product result = likeFacade.cancelLike(new LikeCriteria.Product(product.getId(), user.getId()));

            Optional<ProductCount> after = productCountRepository.findBy(product.getId());

            assertAll(
                    () -> assertThat(after).isPresent(),
                    () -> assertThat(after.get().getLikeCount()).isEqualTo(before.getLikeCount())
            );
        }

        @DisplayName("좋아요가 취소되고, 상품 좋아요 수가 감소한다.")
        @Test
        void cancelLike() {
            User user = userRepository.save(User.create(new UserCommand.Join("LoginId", "hgh1472@loopers.im", "1999-06-23", "MALE")));
            Product product = productRepository.save(Product.create(new ProductCommand.Create(1L, "Test Product", new BigDecimal("2000"), "ON_SALE")));
            ProductCount before = ProductCount.from(product.getId());
            before.incrementLike();
            productCountRepository.save(before);
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(product.getId(), user.getId())));

            LikeResult.Product result = likeFacade.cancelLike(new LikeCriteria.Product(product.getId(), user.getId()));

            boolean likeExists = productLikeRepository.existsByProductIdAndUserId(result.productId(), result.userId());
            Optional<ProductCount> after = productCountRepository.findBy(product.getId());
            assertAll(
                    () -> assertThat(after).isPresent(),
                    () -> assertThat(likeExists).isFalse(),
                    () -> assertThat(after.get().getLikeCount()).isEqualTo(before.getLikeCount() - 1)
            );
        }
    }
}
