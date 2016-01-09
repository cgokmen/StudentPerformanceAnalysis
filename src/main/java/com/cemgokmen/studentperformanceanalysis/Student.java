/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://github.com/sultanskyman/StudentPerformanceAnalysis/blob/master/LICENSE.md
 */
package com.cemgokmen.studentperformanceanalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Student {
    private final int id;
    private final Map<Question, Double> scores;
    
    private static final Map<Integer, Student> students = new HashMap<Integer, Student>();

    public Student(int id) {
        this.id = id;
        this.scores = new HashMap<Question, Double>();
    }

    public int getId() {
        return id;
    }
    
    public double getQuestionScore(Question q) {
        return scores.get(q);
    }
    
    public double calculateCourseScore() {
        double score = 0;
        
        for (Entry<Question, Double> e : scores.entrySet()) {
            score += (e.getValue() / e.getKey().getPoints()) * e.getKey().getValueInCourse();
        }
        
        return score;
    }
    
    public double calculateCourseOutcomeScore(CourseOutcome co) {
        double score = 0;
        
        for (Question q : co.getRelevantQuestions()) {
            double s = 0;
            if (scores.get(q) != null) s = scores.get(q);
            score += (s / q.getPoints()) * q.getValueInCourseOutcome(co);
        }
        
        return score;
    }
    
    @Override
    public String toString() {
        return id + "";
    }
    
    public static void processExcelSheet(Sheet sheet) {

    }
    
    public static Student get(int id) {
        return students.get(id);
    }
    
    public static Student[] getAll() {
        return students.values().toArray(new Student[0]);
    }
}
