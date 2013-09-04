package com.mingo.integ.repository;

import static com.mingo.domain.util.DomainTestBuilder.createReviews;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.api.IReviewRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Integration test for {@link com.mingo.repository.impl.ReviewRepository}.
 */
public class ReviewRepositoryIntegrationTest extends CommonIntegrationTest {

    private static final int REVIEWS_COUNT = 20;
    @Autowired
    @Qualifier(value = "reviewRepository")
    private IReviewRepository reviewRepository;

    private List<Review> reviews;


    @BeforeClass(groups = "integration")
    public void setUp() {
        mongoTemplate.dropCollection(Review.class);
        reviews = createReviews(REVIEWS_COUNT);
        for (Review review : reviews) {
            reviewRepository.insert(review);
        }
    }

    @Test(groups = "integration")
    public void testFindAll() {
        List<Review> savedReviews = reviewRepository.findAll();
        assertNotNull(savedReviews);
        assertTrue(CollectionUtils.isNotEmpty(savedReviews));
        assertEquals(REVIEWS_COUNT, savedReviews.size());
        for (Review review : savedReviews) {
            Review r = getById(review.getId());
            assertNotNull(r);
            assertEquals(r, review);
        }
    }

    @Test(groups = "integration")
    public void testFindById() {
        Review review = reviews.get(0);
        Review savedReview = reviewRepository.findById(review.getId());
        assertNotNull(savedReview);
        assertEquals(review, savedReview);
    }

    @Test(groups = "integration")
    public void testGetCountByTags() {
        Map<String, Integer> count = reviewRepository.getCountByTags(ModerationStatus.STATUS_NOT_MODERATED);
        Map<String, Integer> countEmptyMs = reviewRepository.getCountByTags(null);
        assertNotNull(count);
        assertNotNull(countEmptyMs);
        assertEquals(count, countEmptyMs);
    }

    private Review getById(String id) {
        Review review = null;
        for (Review r : reviews) {
            if (id.equals(r.getId())) {
                review = r;
                break;
            }
        }
        return review;
    }
}
