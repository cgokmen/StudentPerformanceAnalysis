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
import java.util.Arrays;

public abstract class Outcome implements Comparable<Outcome> {    
    public abstract void addRelevantQuestion(Question q);
    
    public abstract boolean hasRelevantQuestion(Question q);
    
    public abstract Question[] getRelevantQuestions();
    
    public abstract Outcome[] getRelatedOutcomes();
        
    public abstract String getName();

    public abstract String getExplanation();

    public abstract double getTotalValueInCourse();
    
    public double calculateAverage() {
        double sum = 0;
        int eligibleStudents = 0;
        for (Student s : Student.getAll()) {
            if (s.doesStudentCount()) {
                sum += s.calculateOutcomeScore(this);
                eligibleStudents++;
            }
        }
        return (eligibleStudents > 0) ? sum / eligibleStudents : -1;
    }
    
    public static Outcome get(String key) {
        Outcome courseOutcome = CourseOutcome.get(key);
        Outcome programOutcome = ProgramOutcome.get(key);
        
        return (courseOutcome == null) ? programOutcome : courseOutcome;
    }
    
    public static Outcome[] getAll() {
        ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
        
        outcomes.addAll(Arrays.asList(ProgramOutcome.getAll()));
        outcomes.addAll(Arrays.asList(CourseOutcome.getAll()));
        
        return outcomes.toArray(new Outcome[0]);
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

        Question[] relevantQuestions = this.getRelevantQuestions();
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

            output += String.format("The average for this outcome is: %.2f%%.%n", this.calculateAverage() * 100);
        } else {
            output += "N/A\n";
            output += "The average for this outcome cannot be calculated due to the lack of data.\n";
        }
        
        return output;
    }
    
    public int compareTo(Outcome o) {
        return (new AlphanumComparator()).compare(this.getName(), o.getName());
    }
}
