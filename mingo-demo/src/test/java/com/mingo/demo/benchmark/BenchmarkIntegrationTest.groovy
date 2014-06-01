package com.mingo.demo.benchmark

import com.mingo.demo.domain.ModerationStatus
import com.mingo.demo.domain.Review
import com.mingo.demo.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class BenchmarkIntegrationTest  extends AbstractTestNGSpringContextTests {

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        reviewRepository.getMingoTemplate().removeAll(Review.class);
    }

    //@Test
    void testGetTagsCount() {
        //given create
        Review reviewNotModerated = new Review()
        reviewNotModerated.tags = ["scala", "groovy"]

        Review reviewStatusPassed = new Review()
        reviewStatusPassed.moderationStatus = ModerationStatus.STATUS_PASSED;
        reviewStatusPassed.tags = ["java", "groovy"]

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        while (true) {
            Map<String, Integer> count = reviewRepository.getTagsCount([ModerationStatus.STATUS_PASSED]);
            Thread.sleep(1000 * randInt(1, 3));
            List<Review> reviews = reviewRepository.getByModerationStatus(ModerationStatus.STATUS_PASSED, 10, 0);
            Thread.sleep(1000 * randInt(1, 4));
        }

    }

    public static int randInt(int min, int max) {

        // Usually this should be a field rather than a method variable so
        // that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
