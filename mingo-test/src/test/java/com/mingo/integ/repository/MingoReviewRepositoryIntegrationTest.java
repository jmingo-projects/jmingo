package com.mingo.integ.repository;

import static com.mingo.domain.util.DomainTestBuilder.createDateAndAddYear;
import static com.mingo.domain.util.DomainTestBuilder.createReviews;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Sets;
import com.mingo.domain.Author;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.api.IReviewRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Integration test for {@link com.mingo.repository.impl.MingoReviewRepository}.
 */
public class MingoReviewRepositoryIntegrationTest extends CommonIntegrationTest {

    private static final int REVIEWS_COUNT = 20;
    @Autowired
    @Qualifier(value = "mingoReviewRepository")
    private IReviewRepository mingoReviewRepository;

    @Autowired
    @Qualifier(value = "reviewRepository")
    private IReviewRepository reviewRepository;

    private List<Review> reviews;


    @BeforeClass(groups = "integration")
    public void setUp() {
        mongoTemplate.dropCollection(Review.class);
        reviews = createReviews(REVIEWS_COUNT);
        for (Review review : reviews) {
            mingoReviewRepository.insert(review);
        }
    }

    @Test(groups = "integration")
    public void testFindAll() {
        List<Review> savedReviews = mingoReviewRepository.findAll();
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
        Review savedReview = mingoReviewRepository.findById(review.getId());
        assertNotNull(savedReview);
        assertEquals(review, savedReview);
    }

    @DataProvider(name = "testGetByAuthorDataSource")
    public Object[][] testGetByAuthorDataSource() {
        return new Object[][]{
            new Object[]{new Author("author name 1", "author email 1")},
            new Object[]{new Author(null, "author email 1")},
            new Object[]{new Author("author name 1", null)},
            new Object[]{new Author(null, null)},
        };
    }

    @Test(dataProvider = "testGetByAuthorDataSource", groups = "integration")
    public void testGetByAuthor(Author author) {
        List<Review> repReviews = reviewRepository.getByAuthor(author);
        List<Review> mingoReviews = mingoReviewRepository.getByAuthor(author);
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
        if (author.getEmail() == null && author.getName() == null) {
            assertEquals(mingoReviews.size(), REVIEWS_COUNT);
        }
    }

    @Test(groups = "integration")
    public void testGetCountByTags() {
        Map<String, Integer> count = reviewRepository.getCountByTags(ModerationStatus.STATUS_NOT_MODERATED);
        Map<String, Integer> mingoCount = mingoReviewRepository.getCountByTags(ModerationStatus.STATUS_NOT_MODERATED);
        Map<String, Integer> mingoCountEmptyMs = mingoReviewRepository.getCountByTags(null);
        assertNotNull(count);
        assertNotNull(mingoCount);
        assertNotNull(mingoCountEmptyMs);
        assertEquals(mingoCount, mingoCountEmptyMs);
        assertEquals(count, mingoCount);
    }

    @Test(groups = "integration")
    public void testGetByModerationStatuses() {
        List<Review> repReviews = reviewRepository.getByModerationStatuses(
            Sets.newHashSet(ModerationStatus.STATUS_NOT_MODERATED, ModerationStatus.STATUS_PASSED));
        List<Review> mingoReviews = mingoReviewRepository.getByModerationStatuses(Sets.newHashSet(ModerationStatus.STATUS_NOT_MODERATED, ModerationStatus.STATUS_PASSED));
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
    }

    @Test(groups = "integration")
    public void testGetByCreated() {
        Date created = createDateAndAddYear(10);
        List<Review> repReviews = reviewRepository.getByCreated(created);
        List<Review> mingoReviews = mingoReviewRepository.getByCreated(created);
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), 9);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
    }

    @Test(groups = "integration")
    public void testGetByTags() {
        List<Review> repReviews = reviewRepository.getByTags("software");
        List<Review> mingoReviews = mingoReviewRepository.getByTags("software");
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
    }

    @Test(groups = "integration")
    public void testGetByRating() {
        List<Review> repReviews = reviewRepository.getByRating(5f);
        List<Review> mingoReviews = mingoReviewRepository.getByRating(5f);
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
    }

    @Test(groups = "integration")
    public void getByModerationStatus() {
        List<Review> repReviews = reviewRepository.getByModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        List<Review> mingoReviews = mingoReviewRepository.getByModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
    }

    @Test(groups = "integration")
    public void getByNullModerationStatus() {
        List<Review> mingoReviews = mingoReviewRepository.getByModerationStatus(null);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @Test(groups = "integration")
    public void testGetByNullRating() {
        List<Review> mingoReviews = mingoReviewRepository.getByRating(null);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @Test(groups = "integration")
    public void testGetByNullModerationStatuses() {
        List<Review> mingoReviews = mingoReviewRepository.getByModerationStatuses(null);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @Test(groups = "integration")
    public void testGetByEmptyModerationStatuses() {
        List<Review> mingoReviews = mingoReviewRepository.getByModerationStatuses(Sets.<ModerationStatus>newHashSet());
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @Test(groups = "integration")
    public void testGetByNullTags() {
        List<Review> mingoReviews = mingoReviewRepository.getByTags(null);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @Test(groups = "integration")
    public void testGetByEmptyTags() {
        List<Review> mingoReviews = mingoReviewRepository.getByTags(new String[]{});
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), REVIEWS_COUNT);
    }

    @DataProvider(name = "testGetByMultipleParametersDataSource")
    public Object[][] testGetByMultipleParametersDataSource() {
        return new Object[][]{
            new Object[]{createDateAndAddYear(10), Sets.newHashSet(ModerationStatus.STATUS_NOT_MODERATED, ModerationStatus.STATUS_PASSED)},
            new Object[]{null, Sets.newHashSet(ModerationStatus.STATUS_NOT_MODERATED, ModerationStatus.STATUS_PASSED)},
            new Object[]{createDateAndAddYear(10), null},
            new Object[]{null, null},
        };
    }

    @Test(dataProvider = "testGetByMultipleParametersDataSource", groups = "integration")
    public void testGetByMultipleParameters(Date created, Set<ModerationStatus> moderationStatuses) {

        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("statuses", moderationStatuses);
        parameters.put("created", created);
        List<Review> mingoReviews = mingoReviewRepository.getByMultipleParameters(parameters);
        List<Review> repReviews = reviewRepository.getByMultipleParameters(parameters);
        assertNotNull(repReviews);
        assertNotNull(mingoReviews);
        assertEquals(mingoReviews.size(), repReviews.size());
        assertEquals(Sets.newHashSet(mingoReviews), Sets.newHashSet(repReviews));
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