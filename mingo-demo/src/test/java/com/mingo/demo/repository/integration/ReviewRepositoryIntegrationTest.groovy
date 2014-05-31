package com.mingo.demo.repository.integration

import com.mingo.demo.domain.Author
import com.mingo.demo.domain.Comment
import com.mingo.demo.domain.ModerationStatus
import com.mingo.demo.domain.Rating
import com.mingo.demo.domain.Review
import com.mingo.demo.repository.ReviewRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

import static org.testng.Assert.assertEquals
import static org.testng.Assert.assertNotNull

@ContextConfiguration(locations = "classpath:META-INF/spring/applicationContext.xml")
public class ReviewRepositoryIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    ReviewRepository reviewRepository;

    @BeforeMethod
    public void setUp() throws Exception {
        reviewRepository.getMingoTemplate().removeAll(Review.class);
    }

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
        assertEquals(count.get("groovy"), Integer.valueOf(1));
        assertEquals(count.get("scala"), null);
        assertEquals(count.get("java"), Integer.valueOf(1));
    }

    @Test
    public void testGetCountByTagsEmptyStatuses() {
        //given create
        Review reviewNotModerated = new Review();
        reviewNotModerated.tags = ['scala', 'groovy']


        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        reviewStatusPassed.tags = ['java', 'groovy']

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        Map<String, Integer> count = reviewRepository.getTagsCount([]);

        assertNotNull(count);
        assertEquals(count.get("groovy"), Integer.valueOf(2));
        assertEquals(count.get("scala"), Integer.valueOf(1));
        assertEquals(count.get("java"), Integer.valueOf(1));
    }

    @Test
    public void testGetByAuthor() {
        // given
        Review review1 = new Review();
        review1.author = new Author("name1", "mail_1@mail.com");

        Review review2 = new Review();
        review2.author = new Author("name1", "mail_1@mail.com");

        // then
        reviewRepository.insert(review1);
        reviewRepository.insert(review2);
        // when
        def reviews = reviewRepository.getByAuthor("name1", "mail_1@mail.com");
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    void testGetByModerationStatusWithEmptyComments() {
        Review review1 = new Review()
        review1.author = new Author("name1", "mail_1@mail.com")
        review1.ratings = [new Rating(type: "overall", value: 10f)]
        review1.commentsCount = 0
        review1.moderationStatus = ModerationStatus.STATUS_PASSED
        review1.text = 'review text'
        review1.title = 'review title'
        review1.tags = ['java', 'groovy']
        reviewRepository.insert(review1);
        def reviews = reviewRepository.getByModerationStatus(ModerationStatus.STATUS_PASSED, 10, 0);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        def actual = reviews.get(0);
        assertEquals(0, actual.commentsCount);
        assertReviewEquals(actual, review1)
    }

    @Test
    void testGetByModerationStatusWithComments() {
        Review review1 = new Review()
        review1.author = new Author("name1", "mail_1@mail.com")
        review1.ratings = [new Rating(type: "overall", value: 10f)]
        review1.comments = [new Comment("comment 1", ModerationStatus.STATUS_PASSED), new Comment("comment 2",
                ModerationStatus.STATUS_NOT_MODERATED),
                            new Comment("comment 3", ModerationStatus.STATUS_REJECTED)]
        review1.moderationStatus = ModerationStatus.STATUS_PASSED
        review1.text = 'review text'
        review1.title = 'review title'
        review1.tags = ['java', 'groovy']
        reviewRepository.insert(review1);
        def reviews = reviewRepository.getByModerationStatus(ModerationStatus.STATUS_PASSED, 10, 0);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        def actual = reviews.get(0);
        assertEquals(2, actual.commentsCount);
        assertEquals(actual.id, review1.id);
        assertEquals(actual.text, review1.text);
        assertEquals(actual.title, review1.title);
        assertEquals(actual.moderationStatus, review1.moderationStatus);
        assertEquals(actual.comments.size(), review1.comments.size());
        assertEquals(actual.ratings.size(), review1.ratings.size());
        assertEquals(actual.tags.size(), review1.tags.size());
    }

    @Test
    void testGetByEmptyModerationStatus() {
        Review review1 = new Review()
        review1.author = new Author("name1", "mail_1@mail.com")
        review1.ratings = [new Rating(type: "overall", value: 10f)]
        review1.comments = [new Comment("comment 1", ModerationStatus.STATUS_PASSED), new Comment("comment 2",
                ModerationStatus.STATUS_NOT_MODERATED),
                            new Comment("comment 3", ModerationStatus.STATUS_REJECTED)]
        review1.moderationStatus = ModerationStatus.STATUS_PASSED
        review1.text = 'review text'
        review1.title = 'review title'
        review1.tags = ['java', 'groovy']
        reviewRepository.insert(review1);
        def reviews = reviewRepository.getByModerationStatus(null, 10, 0);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        def actual = reviews.get(0);
        assertEquals(2, actual.commentsCount);
        assertEquals(actual.id, review1.id);
        assertEquals(actual.text, review1.text);
        assertEquals(actual.title, review1.title);
        assertEquals(actual.moderationStatus, review1.moderationStatus);
        assertEquals(actual.comments.size(), review1.comments.size());
        assertEquals(actual.ratings.size(), review1.ratings.size());
        assertEquals(actual.tags.size(), review1.tags.size());
    }

    private void assertReviewEquals(Review actual, Review expected) {
        assertEquals(actual.id, expected.id);
        assertEquals(actual.text, expected.text);
        assertEquals(actual.title, expected.title);
        assertEquals(actual.moderationStatus, expected.moderationStatus);
    }

}
