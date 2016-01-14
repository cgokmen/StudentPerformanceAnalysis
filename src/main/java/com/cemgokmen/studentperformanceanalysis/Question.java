/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://github.com/sultanskyman/StudentPerformanceAnalysis/blob/master/LICENSE.md
 */
package com.cemgokmen.studentperformanceanalysis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Question {
    private final String name;
    private final double points;
    private final boolean countQuestion;
    private final List<Outcome> outcomes;
    private final Evaluation parent;
    private final int column;
    
    private static final Map<Integer, Question> questions = new LinkedHashMap<Integer, Question>();

    public Question(String name, double points, boolean countQuestion, Evaluation parent, int column) {
        this.name = name;
        this.points = points;
        this.countQuestion = countQuestion;
        this.outcomes = new ArrayList<Outcome>();
        this.parent = parent;
        this.column = column;
        
        questions.put(column, this);
    }
    
    public void addOutcome(Outcome co) {
        if (!hasOutcome(co)) {
            outcomes.add(co);
            co.addRelevantQuestion(this);
        }
    }
    
    public boolean hasOutcome(Outcome co) {
        return outcomes.contains(co);
    }
    
    public Outcome[] getOutcomes() {
        return outcomes.toArray(new Outcome[0]);
    }

    public String getName() {
        return name;
    }

    public double getPoints() {
        return points;
    }

    public int getColumn() {
        return column;
    }

    public Evaluation getParent() {
        return parent;
    }
    
    public double getValueInCourse() {
        return points * getParent().getPercentage();
    }
    
    public double getValueInOutcome(Outcome co) {
        return getValueInCourse() / co.getTotalValueInCourse();
    }
    
    @Override
    public String toString() {
        String output = "Question " + name + ": " + String.format("%.2f", points * 100) + " points. Outcomes: ";
        for (Outcome co : outcomes) {
            output += co.getName() + ", ";
        }
        output = output.substring(0, output.length() - 2);
        return output;
    }
    
    public static Question get(String str) {
        return Question.get(str);
    }
    
    public static Question[] getAll() {
        return questions.values().toArray(new Question[0]);
    }
}
