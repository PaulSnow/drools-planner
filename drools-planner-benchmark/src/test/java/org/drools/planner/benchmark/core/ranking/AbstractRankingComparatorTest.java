/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.benchmark.core.ranking;

import java.util.List;

import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.measurement.ScoreDifferencePercentage;
import org.drools.planner.core.score.buildin.simple.DefaultSimpleScore;

public abstract class AbstractRankingComparatorTest {

    protected void addSingleBenchmark(List<SingleBenchmark> singleBenchmarkList, int score, int bestScore, int worstScore) {
        SingleBenchmark singleBenchmark = new SingleBenchmark(null, null);
        DefaultSimpleScore scoreObject = DefaultSimpleScore.valueOf(score);
        DefaultSimpleScore bestScoreObject = DefaultSimpleScore.valueOf(bestScore);
        DefaultSimpleScore worstScoreObject = DefaultSimpleScore.valueOf(worstScore);
        singleBenchmark.setScore(scoreObject);
        singleBenchmark.setWinningScoreDifference(scoreObject.subtract(bestScoreObject));
        singleBenchmark.setWorstScoreDifferencePercentage(
                ScoreDifferencePercentage.calculateScoreDifferencePercentage(worstScoreObject, scoreObject));
        singleBenchmarkList.add(singleBenchmark);
    }

}
