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

package org.drools.planner.core.heuristic.selector.move.composite;

import java.util.ArrayList;

import org.drools.planner.core.heuristic.selector.SelectorTestUtils;
import org.drools.planner.core.heuristic.selector.move.MoveSelector;
import org.drools.planner.core.move.DummyMove;
import org.drools.planner.core.phase.AbstractSolverPhaseScope;
import org.drools.planner.core.phase.step.AbstractStepScope;
import org.drools.planner.core.solver.scope.DefaultSolverScope;
import org.junit.Test;

import static org.drools.planner.core.testdata.util.PlannerAssert.*;
import static org.mockito.Mockito.*;

public class CartesianProductMoveSelectorTest {

    @Test
    public void originSelection() {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<MoveSelector>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(
                DummyMove.class, new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(
                DummyMove.class,new DummyMove("b1"), new DummyMove("b2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfEndingMoveSelector(moveSelector,
                "a1+b1", "a1+b2",
                "a2+b1", "a2+b2",
                "a3+b1", "a3+b2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifySolverPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }
    @Test
    public void originSelection3ChildMoveSelectors() {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<MoveSelector>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(
                DummyMove.class, new DummyMove("a1"), new DummyMove("a2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(
                DummyMove.class,new DummyMove("b1"), new DummyMove("b2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(
                DummyMove.class,new DummyMove("c1"), new DummyMove("c2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList, false);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertAllCodesOfEndingMoveSelector(moveSelector,
                "a1+b1+c1", "a1+b1+c2", "a1+b2+c1", "a1+b2+c2",
                "a2+b1+c1", "a2+b1+c2", "a2+b2+c1", "a2+b2+c2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifySolverPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    public void randomSelection() {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<MoveSelector>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2"), new DummyMove("a3")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList, true);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 6, "a1+b1", "a2+b2", "a3+b1", "a1+b2", "a2+b1", "a3+b2");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifySolverPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

    @Test
    public void randomSelection3ChildMoveSelectors() {
        ArrayList<MoveSelector> childMoveSelectorList = new ArrayList<MoveSelector>();
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("a1"), new DummyMove("a2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("b1"), new DummyMove("b2")));
        childMoveSelectorList.add(SelectorTestUtils.mockMoveSelector(DummyMove.class,
                new DummyMove("c1"), new DummyMove("c2")));
        CartesianProductMoveSelector moveSelector = new CartesianProductMoveSelector(childMoveSelectorList, true);

        DefaultSolverScope solverScope = mock(DefaultSolverScope.class);
        moveSelector.solvingStarted(solverScope);
        AbstractSolverPhaseScope phaseScopeA = mock(AbstractSolverPhaseScope.class);
        when(phaseScopeA.getSolverScope()).thenReturn(solverScope);
        moveSelector.phaseStarted(phaseScopeA);
        AbstractStepScope stepScopeA1 = mock(AbstractStepScope.class);
        when(stepScopeA1.getPhaseScope()).thenReturn(phaseScopeA);
        moveSelector.stepStarted(stepScopeA1);

        assertCodesOfNeverEndingMoveSelector(moveSelector, 8, "a1+b1+c1", "a2+b2+c2", "a1+b1+c1");

        moveSelector.stepEnded(stepScopeA1);
        moveSelector.phaseEnded(phaseScopeA);
        moveSelector.solvingEnded(solverScope);

        verifySolverPhaseLifecycle(childMoveSelectorList.get(0), 1, 1, 1);
        verifySolverPhaseLifecycle(childMoveSelectorList.get(1), 1, 1, 1);
    }

}
