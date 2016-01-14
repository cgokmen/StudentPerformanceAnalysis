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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Student {
    private final int id;
    private final String name;
    private final boolean countStudent;
    private final String letterGrade;
    private final Map<Question, Double> scores;
    
    private static final Map<Integer, Student> students = new LinkedHashMap<Integer, Student>();

    public Student(int id, String name, boolean countStudent, String letterGrade) {
        this.id = id;
        this.name = name;
        this.countStudent = countStudent;
        this.letterGrade = letterGrade;
        this.scores = new LinkedHashMap<Question, Double>();
    }
    
    public void addScore(Question q, double score) {
        scores.put(q, score);
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
    
    public double calculateOutcomeScore(Outcome co) {
        double score = 0;
        
        for (Question q : co.getRelevantQuestions()) {
            double s = 0;
            if (scores.get(q) != null) s = scores.get(q);
            score += (s / q.getPoints()) * q.getValueInOutcome(co);
        }
        
        return score;
    }
    
    @Override
    public String toString() {
        return id + "";
    }
    
    public static void processExcelSheet(Sheet sheet) {
        int startingRow = 11;
        while (true) {
            Row row = sheet.getRow(startingRow);
            if (row == null) break;
            
            Cell idCell = row.getCell(0);
            if (idCell == null) break;
            
            Cell nameCell = row.getCell(1);
            if (nameCell == null) break;
            
            Cell countCell = row.getCell(3);
            if (countCell == null) break;
            
            Cell letterGradeCell = row.getCell(4);
            if (letterGradeCell == null) break;
            
            int id = (int) idCell.getNumericCellValue();
            String name = nameCell.getStringCellValue();
            boolean count = countCell.getBooleanCellValue();
            String letterGrade = letterGradeCell.getStringCellValue();
            
            Student student = new Student(id, name, count, letterGrade);
            
            // Get all the questions and map them to their results
            for (Question q : Question.getAll()) {
                Cell scoreCell = row.getCell(q.getColumn());
                double score = scoreCell.getNumericCellValue();
                student.addScore(q, score);
            }
            
            //System.out.println(outcome + "\n");
            startingRow++;
            
            students.put(id, student);
        }
    }
    
    public static Student get(int id) {
        return students.get(id);
    }
    
    public static Student[] getAll() {
        return students.values().toArray(new Student[0]);
    }
}
