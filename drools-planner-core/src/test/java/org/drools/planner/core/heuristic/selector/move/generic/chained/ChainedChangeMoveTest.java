package org.drools.planner.core.heuristic.selector.move.generic.chained;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.move.Move;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.testdata.domain.TestdataChainedAnchor;
import org.drools.planner.core.testdata.domain.TestdataChainedEntity;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ChainedChangeMoveTest {

    @Test
    public void noTrailing() {
        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);

        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, b1});

        ChainedChangeMove move = new ChainedChangeMove(a3, variableDescriptor, b1);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a2);
        SelectorTestUtils.assertChain(b0, b1, a3);

        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

    @Test
    public void oldAndNewTrailing() {

        PlanningEntityDescriptor entityDescriptor = TestdataChainedEntity.buildEntityDescriptor();
        PlanningVariableDescriptor variableDescriptor = entityDescriptor.getPlanningVariableDescriptor("chainedObject");
        ScoreDirector scoreDirector = mock(ScoreDirector.class);
        TestdataChainedAnchor a0 = new TestdataChainedAnchor("a0");
        TestdataChainedEntity a1 = new TestdataChainedEntity("a1", a0);
        TestdataChainedEntity a2 = new TestdataChainedEntity("a2", a1);
        TestdataChainedEntity a3 = new TestdataChainedEntity("a3", a2);

        TestdataChainedAnchor b0 = new TestdataChainedAnchor("b0");
        TestdataChainedEntity b1 = new TestdataChainedEntity("b1", b0);

        SelectorTestUtils.mockMethodGetTrailingEntity(scoreDirector, variableDescriptor,
                new TestdataChainedEntity[]{a1, a2, a3, b1});

        ChainedChangeMove move = new ChainedChangeMove(a2, variableDescriptor, b0);
        Move undoMove = move.createUndoMove(scoreDirector);
        move.doMove(scoreDirector);

        SelectorTestUtils.assertChain(a0, a1, a3);
        SelectorTestUtils.assertChain(b0, a2, b1);

        verify(scoreDirector).beforeVariableChanged(a2, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a2, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(a3, "chainedObject");
        verify(scoreDirector).afterVariableChanged(a3, "chainedObject");
        verify(scoreDirector).beforeVariableChanged(b1, "chainedObject");
        verify(scoreDirector).afterVariableChanged(b1, "chainedObject");

        undoMove.doMove(scoreDirector);
        SelectorTestUtils.assertChain(a0, a1, a2, a3);
        SelectorTestUtils.assertChain(b0, b1);
    }

}
