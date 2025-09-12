package com.loopers.application.ranking;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.ranking.RankingCommand.Rankings;
import com.loopers.domain.ranking.RankingInfo;
import com.loopers.domain.ranking.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RankingFacade {
    private final RankingService rankingService;
    private final ProductService productService;
    private final BrandService brandService;

    public PageResponse<RankingResult> getRankProducts(RankingCriteria.Search cri) {
        PageResponse<RankingInfo> rankings = rankingService.getRankings(
                new Rankings(cri.size(), cri.page(), cri.date()));

        return rankings.map(ranking -> {
            ProductInfo product = productService.findProduct(new ProductCommand.Find(ranking.productId()));
            BrandInfo brand = brandService.findBy(new BrandCommand.Find(product.brandId()));
            return new RankingResult(
                    product.id(),
                    brand.name(),
                    product.name(),
                    product.price(),
                    product.status(),
                    ranking.rank()
            );
        });
    }
}
