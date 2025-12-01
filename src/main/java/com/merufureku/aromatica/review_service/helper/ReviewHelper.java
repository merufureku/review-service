package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class ReviewHelper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public Reviews updateReview(Reviews existingReview, PostReviewParam param) {
        setIfNotNull(param::comment, existingReview::setComment);
        setIfNotNull(param::rating, existingReview::setRating);
        existingReview.setUpdatedAt(LocalDate.now());

        logger.info("Review has been updated: {}", existingReview);

        return existingReview;
    }

    private static <T> void setIfNotNull(Supplier<T> source, Consumer<T> target) {
        T value = source.get();
        if (value != null) {
            target.accept(value);
        }
    }

}
