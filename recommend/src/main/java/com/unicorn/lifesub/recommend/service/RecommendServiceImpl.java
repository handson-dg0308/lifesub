package com.unicorn.lifesub.recommend.service;

import com.unicorn.lifesub.common.exception.BusinessException;
import com.unicorn.lifesub.common.exception.ErrorCode;
import com.unicorn.lifesub.recommend.domain.RecommendedCategory;
import com.unicorn.lifesub.recommend.domain.SpendingCategory;
import com.unicorn.lifesub.recommend.dto.RecommendCategoryDTO;
import com.unicorn.lifesub.recommend.repository.jpa.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {
    private final RecommendRepository recommendRepository;
    private final SpendingAnalyzer spendingAnalyzer;

    @Override
    @Transactional(readOnly = true)
    public RecommendCategoryDTO getRecommendedCategory(String userId) {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        
        SpendingCategory topSpending = spendingAnalyzer.analyzeSpending(
            recommendRepository.findSpendingsByUserIdAndDateAfter(userId, startDate)
        );

        if (topSpending == null) {
            throw new BusinessException(ErrorCode.NO_SPENDING_DATA);
        }

        RecommendedCategory recommendedCategory = recommendRepository
            .findBySpendingCategory(topSpending.getCategory())
            .orElseThrow(() -> new BusinessException(ErrorCode.NO_SPENDING_DATA))
            .toDomain();

        return RecommendCategoryDTO.builder()
                .categoryName(recommendedCategory.getRecommendCategory())
                .imagePath(getCategoryImagePath(recommendedCategory.getRecommendCategory()))
                .baseDate(recommendedCategory.getBaseDate())
                .build();
    }

    private String getCategoryImagePath(String category) {
        return "/images/categories/" + category.toLowerCase() + ".png";
    }
}
