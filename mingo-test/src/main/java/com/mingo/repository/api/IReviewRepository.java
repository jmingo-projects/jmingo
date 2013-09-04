package com.mingo.repository.api;

import com.mingo.domain.Author;
import com.mingo.domain.ModerationStatus;
import com.mingo.domain.Review;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public interface IReviewRepository extends IBaseRepository<String, Review> {

    Map<String, Integer> getCountByTags(ModerationStatus moderationStatus);

    List<Review> getByTags(String... tags);

    List<Review> getByRating(Float rating);

    List<Review> getByModerationStatuses(Set<ModerationStatus> moderationStatuses);

    List<Review> getByModerationStatus(ModerationStatus moderationStatus);

    List<Review> getByCreated(Date created);

    List<Review> getByMultipleParameters(Map<String, Object> parameters);

    List<Review> getByAuthor(Author author);

}
