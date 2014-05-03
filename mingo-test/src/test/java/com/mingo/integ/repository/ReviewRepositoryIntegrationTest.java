package com.mingo.integ.repository;


import com.google.common.collect.Sets;
import com.mingo.domain.Item;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.impl.ReviewRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ReviewRepositoryIntegrationTest extends CommonIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeMethod(groups = "integration")
    public void setUp() throws Exception {
        reviewRepository.getMingoTemplate().dropCollection(Review.class);

    }

    @Test(groups = "integration")
    public void testGetCountByTags() {
        //given create
        Review reviewNotModerated = new Review();
        reviewNotModerated.addTag("scala");
        reviewNotModerated.addTag("groovy");

        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        reviewStatusPassed.addTag("java");
        reviewStatusPassed.addTag("groovy");

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        Map<String, Integer> count = reviewRepository.getTagsCount();

        assertNotNull(count);
        assertEquals(Integer.valueOf(2), count.get("groovy"));
        assertEquals(Integer.valueOf(1), count.get("scala"));
        assertEquals(Integer.valueOf(1), count.get("java"));

    }

    @Test(groups = "integration")
    public void testGetCountByTagsStatusPassed() {
        //given create
        Review reviewNotModerated = new Review();
        reviewNotModerated.addTag("scala");
        reviewNotModerated.addTag("groovy");

        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        reviewStatusPassed.addTag("java");
        reviewStatusPassed.addTag("groovy");

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        Map<String, Integer> count = reviewRepository.getTagsCount(ModerationStatus.STATUS_PASSED);

        assertNotNull(count);
        assertEquals(Integer.valueOf(1), count.get("groovy"));
        assertEquals(null, count.get("scala"));
        assertEquals(Integer.valueOf(1), count.get("java"));

    }
    @Test(groups = "integration")
    public void testFindByModerationStatuses(){
        //given
        Review reviewNotModerated1 = new Review();
        reviewNotModerated1.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewNotModerated2 = new Review();
        reviewNotModerated2.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        Review reviewStatusRejected = new Review();
        reviewStatusRejected.setModerationStatus(ModerationStatus.STATUS_REJECTED);
        reviewRepository.insert(reviewNotModerated1, reviewNotModerated2,reviewStatusPassed, reviewStatusRejected );

        //then
        List<Review> reviews = reviewRepository.findByModerationStatuses(
            Sets.newHashSet(ModerationStatus.STATUS_PASSED, ModerationStatus.STATUS_NOT_MODERATED));
        assertNotNull(reviews);
        assertEquals(3, reviews.size());
    }
}
