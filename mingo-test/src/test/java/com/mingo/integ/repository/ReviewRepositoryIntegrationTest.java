package com.mingo.integ.repository;


import com.google.common.collect.Sets;
import com.mingo.domain.Author;
import com.mingo.domain.Item;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;
import com.mingo.repository.impl.ReviewRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Random;

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
    public void testBenchmark() throws InterruptedException {
        //given
        Review reviewNotModerated1 = new Review();
        reviewNotModerated1.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewNotModerated2 = new Review();
        reviewNotModerated2.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        Review reviewStatusRejected = new Review();
        reviewStatusRejected.setModerationStatus(ModerationStatus.STATUS_REJECTED);
        reviewRepository.insert(reviewNotModerated1, reviewNotModerated2, reviewStatusPassed, reviewStatusRejected);
        //given create
        Review reviewNotModerated = new Review();
        reviewNotModerated.addTag("scala");
        reviewNotModerated.addTag("groovy");

         reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        reviewStatusPassed.addTag("java");
        reviewStatusPassed.addTag("groovy");

        reviewRepository.insert(reviewNotModerated);
        reviewRepository.insert(reviewStatusPassed);
        while (true) {
            Map<String, Integer> count = reviewRepository.getTagsCount(ModerationStatus.STATUS_PASSED);
            Thread.sleep(1000 * randInt(1, 3));
            List<Review> reviews = reviewRepository.findByModerationStatuses(
                    Sets.newHashSet(ModerationStatus.STATUS_PASSED, ModerationStatus.STATUS_NOT_MODERATED));
            Thread.sleep(1000 * randInt(1, 4));
        }

    }

    @Test(groups = "integration")
    public void testFindByModerationStatuses() {
        //given
        Review reviewNotModerated1 = new Review();
        reviewNotModerated1.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewNotModerated2 = new Review();
        reviewNotModerated2.setModerationStatus(ModerationStatus.STATUS_NOT_MODERATED);
        Review reviewStatusPassed = new Review();
        reviewStatusPassed.setModerationStatus(ModerationStatus.STATUS_PASSED);
        Review reviewStatusRejected = new Review();
        reviewStatusRejected.setModerationStatus(ModerationStatus.STATUS_REJECTED);
        reviewRepository.insert(reviewNotModerated1, reviewNotModerated2, reviewStatusPassed, reviewStatusRejected);

        //then
        List<Review> reviews = reviewRepository.findByModerationStatuses(
                Sets.newHashSet(ModerationStatus.STATUS_PASSED, ModerationStatus.STATUS_NOT_MODERATED));
        //when
        assertNotNull(reviews);
        assertEquals(3, reviews.size());
    }

    @Test(groups = "integration")
    public void testGetByAuthor() {
        // given
        Review review = new Review();
        review.setAuthor(new Author("jeff", "mail@gmail.com"));

        Review review2 = new Review();
        review2.setAuthor(new Author("jeff", "mail@gmail.com"));

        // then
        reviewRepository.insert(review);
        reviewRepository.insert(review2);
        // when
        List<Review> reviews = reviewRepository.getByAuthor("jeff", "mail@gmail.com");
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test(groups = "integration")
    public void testGetByIdentifier() {
        Review review = new Review();
        reviewRepository.insert(review, new Review(), new Review());
        Review saved = reviewRepository.getByIdentifier(review.getIdentifier());
        assertNotNull(saved);
        assertEquals(review.getId(), saved.getId());
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
