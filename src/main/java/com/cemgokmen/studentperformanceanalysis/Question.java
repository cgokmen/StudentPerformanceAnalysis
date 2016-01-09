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
import java.util.List;

public class Question {
    private final String name;
    private final double points;
    private final List<CourseOutcome> courseOutcomes;
    private final Evaluation parent;

    public Question(String name, double points, Evaluation parent) {
        this.name = name;
        this.points = points;
        this.courseOutcomes = new ArrayList<CourseOutcome>();
        this.parent = parent;
    }
    
    public void addCourseOutcome(CourseOutcome co) {
        if (!hasCourseOutcome(co)) {
            courseOutcomes.add(co);
            co.addRelevantQuestion(this);
        }
    }
    
    public boolean hasCourseOutcome(CourseOutcome co) {
        return courseOutcomes.contains(co);
    }
    
    public CourseOutcome[] getCourseOutcomes() {
        CourseOutcome[] outcomes = new CourseOutcome[courseOutcomes.size()];
        courseOutcomes.toArray(outcomes);
        return outcomes;
    }

    public String getName() {
        return name;
    }

    public double getPoints() {
        return points;
    }

    public Evaluation getParent() {
        return parent;
    }
    
    public double getValueInCourse() {
        return points * getParent().getPercentage();
    }
    
    public double getValueInCourseOutcome(CourseOutcome co) {
        return getValueInCourse() / co.getTotalValueInCourse();
    }
    
    @Override
    public String toString() {
        String output = "Question " + name + ": " + String.format("%.2f", points * 100) + " points. Course Outcomes: ";
        for (CourseOutcome co : courseOutcomes) {
            output += co.getName() + ", ";
        }
        return output;
    }
}
