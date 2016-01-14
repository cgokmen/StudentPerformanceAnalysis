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

public abstract class Outcome {    
    public abstract void addRelevantQuestion(Question q);
    
    public abstract boolean hasRelevantQuestion(Question q);
    
    public abstract Question[] getRelevantQuestions();
        
    public abstract String getName();

    public abstract String getExplanation();

    public abstract double getTotalValueInCourse();
        
    @Override
    public abstract String toString();
    
    public static double calculateAverageForOutcome(Outcome o) {
        double sum = 0;
        Student[] students = Student.getAll();
        for (Student s : students) {
            sum += s.calculateOutcomeScore(o);
        }
        return sum / students.length;
    }
    
    public static Outcome get(String key) {
        Outcome courseOutcome = CourseOutcome.get(key);
        Outcome programOutcome = ProgramOutcome.get(key);
        
        return (courseOutcome == null) ? programOutcome : courseOutcome;
    }
    
    public static Outcome[] getAll() {
        ArrayList<Outcome> outcomes = new ArrayList<Outcome>();
        outcomes.addAll(Arrays.asList(CourseOutcome.getAll()));
        outcomes.addAll(Arrays.asList(ProgramOutcome.getAll()));
        
        return outcomes.toArray(new Outcome[0]);
    }
}
