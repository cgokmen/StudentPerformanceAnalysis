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

/**
 *
 * @author funstein
 */
public abstract class Outcome implements Comparable<Outcome> {    

    /**
     *
     * @param q
     */
    public abstract void addRelevantQuestion(Question q);
    
    /**
     *
     * @param q
     * @return
     */
    public abstract boolean hasRelevantQuestion(Question q);
    
    /**
     *
     * @param onlyDirect
     * @return
     */
    public abstract Question[] getRelevantQuestions(boolean onlyDirect);
    
    /**
     *
     * @return
     */
    public abstract Outcome[] getRelatedOutcomes();
        
    /**
     *
     * @return
     */
    public abstract String getName();

    /**
     *
     * @return
     */
    public abstract String getExplanation();

    /**
     *
     * @return
     */
    public abstract double getTotalValueInCourse();
    
    /**
     *
     * @param onlyDirect
     * @return
     */
    public double calculateAverage(boolean onlyDirect, boolean quantized) {
        double sum = 0;
        int eligibleStudents = 0;
        for (Student s : Student.getAll()) {
            if (s.doesStudentCount()) {
                sum += s.calculateOutcomeScore(this, onlyDirect, quantized);
                eligibleStudents++;
            }
        }
        return (eligibleStudents > 0) ? sum / eligibleStudents : -1;
    }
    
    /**
     *
     * @param key
     * @return
     */
    public static Outcome get(String key) {
        Outcome courseOutcome = CourseOutcome.get(key);
        Outcome programOutcome = ProgramOutcome.get(key);
        
        return (courseOutcome == null) ? programOutcome : courseOutcome;
    }
    
    /**
     *
     * @return
     */
    public static Outcome[] getAll() {
        ArrayList<Outcome> outcomes = new ArrayList<>();
        
        outcomes.addAll(Arrays.asList(ProgramOutcome.getAll()));
        outcomes.addAll(Arrays.asList(CourseOutcome.getAll()));
        
        return outcomes.toArray(new Outcome[0]);
    }
    
    /**
     *
     * @return
     */
    public static Outcome[] getAllWithQuestions() {
        ArrayList<Outcome> outcomes = new ArrayList<>();
        
        outcomes.addAll(Arrays.asList(ProgramOutcome.getAll()));
        outcomes.addAll(Arrays.asList(CourseOutcome.getAll()));
        
        ArrayList<Outcome> results = new ArrayList<>();
        for (Outcome o : outcomes) {
            if (o.getRelevantQuestions(false).length > 0)
                results.add(o);
        }
        
        return results.toArray(new Outcome[0]);
    }
    
    @Override
    public String toString() {
        String output = this.getName() + ": " + this.getExplanation() + "\nRelated Outcomes: ";
        Outcome[] relatedOutcomes = this.getRelatedOutcomes();
        if (relatedOutcomes.length > 0) {
            for (Outcome outcome : relatedOutcomes) {
                output += outcome.getName() + ", ";
            }
            output = output.substring(0, output.length() - 2);
        } else {
            output += "N/A";
        }
        
        output += "\nRelevant Questions: ";

        Question[] relevantQuestions = this.getRelevantQuestions(false);
        if (relevantQuestions.length > 0) {
            output += "\n";
            for (Question question : relevantQuestions) {
                output += String.format("    Evaluation %s(%.2f), Question %s(%.2f), Total value in course:(%.2f), Total value in outcome(%.2f)%n",
                        question.getParent().getName(),
                        question.getParent().getPercentage() * 100,
                        question.getName(),
                        question.getPoints(),
                        question.getValueInCourse() * 100,
                        question.getValueInOutcome(this) * 100
                );
            }

            output += String.format("The average for this outcome is: %.2f%%.%n", this.calculateAverage(false, false) * 100);
        } else {
            output += "N/A\n";
            output += "The average for this outcome cannot be calculated due to the lack of data.\n";
        }
        
        return output;
    }
    
    @Override
    public int compareTo(Outcome o) {
        return (new AlphanumComparator()).compare(this.getName(), o.getName());
    }
}
