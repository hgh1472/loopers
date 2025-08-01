package com.loopers.domain.brand;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandService {
    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public BrandInfo findBy(BrandCommand.Find command) {
        return brandRepository.findBy(command.id())
                .map(BrandInfo::from)
                .orElse(null);
    }
}
