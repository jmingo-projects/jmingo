package com.mingo.demo.repository.integration

import com.mingo.demo.domain.ModerationStatus
import com.mingo.demo.domain.Review
import com.mingo.demo.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals
import static org.testng.AssertJUnit.assertNotNull

@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class ReviewRepositoryIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    ReviewRepository reviewRepository;

    @Test
    void testGetTagsCount() {
        //given create
        Review reviewNotModerated = new Review()
        reviewNotModerated.tags = ["scala", "groovy"]

        Review reviewStatusPassed = new Review()
        reviewStatusPassed.moderationStatus = ModerationStatus.STATUS_PASSED;
        reviewStatusPassed.tags = ["java", "groovy"]

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        Map<String, Integer> count = reviewRepository.getTagsCount([ModerationStatus.STATUS_PASSED]);

        assertNotNull(count);
        assertEquals(Integer.valueOf(1), count.get("groovy"));
        assertEquals(null, count.get("scala"));
        assertEquals(Integer.valueOf(1), count.get("java"));
    }
}
