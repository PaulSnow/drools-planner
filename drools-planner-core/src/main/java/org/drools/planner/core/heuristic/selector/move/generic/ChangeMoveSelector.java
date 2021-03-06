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

package org.drools.planner.core.heuristic.selector.move.generic;

import java.util.Iterator;

import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.iterator.UpcomingSelectionIterator;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.move.generic.chained.ChainedChangeMove;
import org.drools.planner.core.heuristic.selector.value.iterator.ValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.move.Move;

public class ChangeMoveSelector extends GenericMoveSelector {

    protected final EntitySelector entitySelector;
    protected final ValueSelector valueSelector;
    protected final boolean randomSelection;

    protected final boolean chained;

    public ChangeMoveSelector(EntitySelector entitySelector, ValueSelector valueSelector,
            boolean randomSelection) {
        this.entitySelector = entitySelector;
        this.valueSelector = valueSelector;
        this.randomSelection = randomSelection;
        PlanningVariableDescriptor variableDescriptor = valueSelector.getVariableDescriptor();
        chained = variableDescriptor.isChained();
        solverPhaseLifecycleSupport.addEventListener(entitySelector);
        solverPhaseLifecycleSupport.addEventListener(valueSelector);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isContinuous() {
        return entitySelector.isContinuous() || valueSelector.isContinuous();
    }

    public boolean isNeverEnding() {
        return randomSelection || entitySelector.isNeverEnding() || valueSelector.isNeverEnding();
    }

    public long getSize() {
        return entitySelector.getSize() * valueSelector.getSize();
    }

    public Iterator<Move> iterator() {
        if (!randomSelection) {
            return new OriginalChangeMoveIterator();
        } else {
            return new RandomChangeMoveIterator();
        }
    }

    private class OriginalChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<Object> entityIterator;
        private ValueIterator valueIterator;

        private Object upcomingEntity;

        private OriginalChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any entity parameter
            if (!entityIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingSelection = null;
            } else {
                upcomingEntity = entityIterator.next();
                createUpcomingSelection();
            }
        }

        @Override
        protected void createUpcomingSelection() {
            while (!valueIterator.hasNext(upcomingEntity)) {
                if (!entityIterator.hasNext()) {
                    upcomingSelection = null;
                    return;
                }
                upcomingEntity = entityIterator.next();
                valueIterator = valueSelector.iterator();
            }
            Object toValue = valueIterator.next(upcomingEntity);
            upcomingSelection = chained
                    ? new ChainedChangeMove(upcomingEntity, valueSelector.getVariableDescriptor(), toValue)
                    : new ChangeMove(upcomingEntity, valueSelector.getVariableDescriptor(), toValue);
        }

    }

    private class RandomChangeMoveIterator extends UpcomingSelectionIterator<Move> {

        private Iterator<Object> entityIterator;
        private ValueIterator valueIterator;

        private RandomChangeMoveIterator() {
            entityIterator = entitySelector.iterator();
            valueIterator = valueSelector.iterator();
            // valueIterator.hasNext() returns true if there is a next for any entity parameter
            if (!entityIterator.hasNext() || !valueIterator.hasNext()) {
                upcomingSelection = null;
            } else {
                createUpcomingSelection();
            }
        }

        @Override
        protected void createUpcomingSelection() {
            // Ideally, this code should have read:
            //     Object entity = entityIterator.next();
            //     Object toValue = valueIterator.next(entity);
            // But empty selectors and ending selectors (such as non-random or shuffled) make it more complex
            if (!entityIterator.hasNext()) {
                entityIterator = entitySelector.iterator();
            }
            Object entity = entityIterator.next();
            int entityIteratorCreationCount = 0;
            // This loop is mostly only relevant when the entityIterator or valueIterator is non-random or shuffled
            while (!valueIterator.hasNext(entity)) {
                // First try to reset the valueIterator to get a next value
                valueIterator = valueSelector.iterator();
                // If that's not sufficient (that entity has an empty value list), then use the next entity
                if (!valueIterator.hasNext(entity)) {
                    if (!entityIterator.hasNext()) {
                        entityIterator = entitySelector.iterator();
                        entityIteratorCreationCount++;
                        if (entityIteratorCreationCount >= 2) {
                            // All entity-value combinations have been tried (some even more than once)
                            upcomingSelection = null;
                            return;
                        }
                    }
                    entity = entityIterator.next();
                }
            }
            Object toValue = valueIterator.next(entity);
            upcomingSelection = chained
                    ? new ChainedChangeMove(entity, valueSelector.getVariableDescriptor(), toValue)
                    : new ChangeMove(entity, valueSelector.getVariableDescriptor(), toValue);
        }

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + entitySelector + ", " + valueSelector + ")";
    }

}
