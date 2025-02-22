package com.sms.student_management.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SearchStrategyFactory {
    private final Map<String, SearchStrategy> strategies;
    
    public SearchStrategyFactory(List<SearchStrategy> searchStrategies) {
        strategies = new HashMap<>();
        searchStrategies.forEach(strategy -> 
            strategies.put(strategy.getClass().getSimpleName(), strategy));
    }
    
    public SearchStrategy getStrategy(String strategyName) {
        SearchStrategy strategy = strategies.get(strategyName + "SearchStrategy");
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid search strategy: " + strategyName);
        }
        return strategy;
    }
}
