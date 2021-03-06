package org.drools.planner.core.heuristic.selector.value.decorator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleBridge;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheLifecycleListener;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.value.AbstractValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.solver.scope.DefaultSolverScope;

public abstract class AbstractCachingValueSelector extends AbstractValueSelector implements SelectionCacheLifecycleListener {

    protected final ValueSelector childValueSelector;
    protected final SelectionCacheType cacheType;

    protected List<Object> cachedValueList = null;

    public AbstractCachingValueSelector(ValueSelector childValueSelector, SelectionCacheType cacheType) {
        this.childValueSelector = childValueSelector;
        this.cacheType = cacheType;
        if (childValueSelector.isNeverEnding()) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with neverEnding (" + childValueSelector.isNeverEnding() + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(childValueSelector);
        if (cacheType.isNotCached()) {
            throw new IllegalArgumentException("The selector (" + this
                    + ") does not support the cacheType (" + cacheType + ").");
        }
        solverPhaseLifecycleSupport.addEventListener(new SelectionCacheLifecycleBridge(cacheType, this));
    }

    public ValueSelector getChildValueSelector() {
        return childValueSelector;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void constructCache(DefaultSolverScope solverScope) {
        long childSize = childValueSelector.getSize();
        if (childSize > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("The selector (" + this
                    + ") has a childValueSelector (" + childValueSelector
                    + ") with childSize (" + childSize
                    + ") which is higher than Integer.MAX_VALUE.");
        }
        cachedValueList = new ArrayList<Object>((int) childSize);
        CollectionUtils.addAll(cachedValueList, childValueSelector.iterator());
        logger.trace("    Created cachedValueList with size ({}) in valueSelector({}).",
                cachedValueList.size(), this);
    }

    public void disposeCache(DefaultSolverScope solverScope) {
        cachedValueList = null;
    }

    public PlanningVariableDescriptor getVariableDescriptor() {
        return childValueSelector.getVariableDescriptor();
    }

    public boolean isContinuous() {
        return false;
    }

    public long getSize() {
        return cachedValueList.size();
    }

}
