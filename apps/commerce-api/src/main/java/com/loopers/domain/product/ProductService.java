package com.loopers.domain.product;

import com.loopers.domain.PageResponse;
import com.loopers.domain.product.ProductCommand.Purchasable;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCountProcessor productCountProcessor;

    @Transactional(readOnly = true)
    public ProductInfo findProduct(ProductCommand.Find command) {
        return productRepository.findById(command.productId())
                .map(ProductInfo::from)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<ProductInfo> getPurchasableProducts(Purchasable command) {
        if (command.productIds() == null || command.productIds().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID 목록이 비어 있습니다.");
        }

        List<Product> products = productRepository.findByIds(command.productIds());

        return products.stream()
                .filter(Product::isPurchasable)
                .map(ProductInfo::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductInfo.Search> search(ProductCommand.Page command) {
        int page = command.page() != null ? command.page() : 0;
        int size = (command.size() == null || command.size() > 30) ? 10 : command.size();
        ProductParams.Sort sort = ProductParams.Sort.from(command.sort());

        Slice<ProductInfo.Search> views = productRepository.search(new ProductParams.Search(command.brandId(), page, size, sort))
                .map(ProductInfo.Search::from);

        Long count = productCountProcessor.getProductCount(command.brandId());

        return PageResponse.of(views, count);
    }

    @Transactional(readOnly = true)
    public List<ProductInfo.Search> searchProducts(ProductCommand.Search command) {
        List<ProductSearchView> productSearchViews = productRepository.searchProducts(command.productIds());
        return productSearchViews.stream()
                .map(ProductInfo.Search::from)
                .toList();
    }
}
