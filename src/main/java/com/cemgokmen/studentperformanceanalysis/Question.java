/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
 */
package com.cemgokmen.studentperformanceanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Question implements Comparable<Question> {
    private final String name;
    private final double points;
    private final boolean countQuestion;
    private final List<Outcome> outcomes;
    private final Evaluation parent;
    private final int column;
    
    private static final Map<Integer, Question> questions = new LinkedHashMap<>();

    public Question(String name, double points, boolean countQuestion, Evaluation parent, int column) {
        this.name = name;
        this.points = points;
        this.countQuestion = countQuestion;
        this.outcomes = new ArrayList<>();
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
        Outcome[] outcomeArray = outcomes.toArray(new Outcome[0]);
        Arrays.sort(outcomeArray);
        return outcomeArray;
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

    public boolean doesQuestionCount() {
        return countQuestion;
    }
    
    public double getValueInCourse() {
        return (points / 100.0) * getParent().getPercentage();
    }
    
    public double getValueInOutcome(Outcome co) {
        return (countQuestion) ? getValueInCourse() / co.getTotalValueInCourse() : 0;
    }
    
    @Override
    public String toString() {
        String output = "Question " + name + ": " + String.format("%.2f", points) + " points. Outcomes: ";
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
        Question[] questionArray = questions.values().toArray(new Question[0]);
        Arrays.sort(questionArray);
        return questionArray;
    }

    @Override
    public int compareTo(Question o) {
        String thisName = this.getParent().getName() + "." + this.getName();
        String oName = o.getParent().getName() + "." + this.getName();
        return (new AlphanumComparator()).compare(thisName, oName);
    }
}
