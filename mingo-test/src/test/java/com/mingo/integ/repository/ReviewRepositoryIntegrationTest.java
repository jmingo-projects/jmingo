package com.mingo.integ.repository;


import com.mingo.domain.Item;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.impl.ReviewRepository;
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
}
