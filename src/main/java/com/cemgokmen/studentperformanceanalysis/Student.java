/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
 */
package com.cemgokmen.studentperformanceanalysis;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author funstein
 */
public class Student implements Comparable<Student> {
    private final int id;
    private final String name;
    private final boolean countStudent;
    private final String letterGrade;
    private final Map<Question, Double> scores;
    
    private static final Map<Integer, Student> students = new LinkedHashMap<>();

    /**
     *
     * @param id
     * @param name
     * @param countStudent
     * @param letterGrade
     */
    public Student(int id, String name, boolean countStudent, String letterGrade) {
        this.id = id;
        this.name = name;
        this.countStudent = countStudent;
        this.letterGrade = letterGrade;
        this.scores = new LinkedHashMap<>();
    }
    
    /**
     *
     * @param q
     * @param score
     */
    public void addScore(Question q, double score) {
        scores.put(q, score);
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public boolean doesStudentCount() {
        if (!countStudent) return false;
        return letterGrade.toUpperCase().charAt(0) != 'F';
    }
    
    /**
     *
     * @param q
     * @return
     */
    public double getQuestionScore(Question q, boolean quantized) {
        return (quantized) ? StudentPerformanceAnalysis.quantize(scores.get(q) / q.getPoints()) * q.getPoints() : scores.get(q);
    }
    
    /**
     *
     * @param q
     * @return
     */
    public double getQuestionPercentage(Question q, boolean quantized) {
        double score = (quantized) ? StudentPerformanceAnalysis.quantize(scores.get(q) / q.getPoints()) * q.getPoints() : scores.get(q);
        return score / q.getPoints();
    }
    
    /**
     *
     * @return
     */
    public double calculateCourseScore(boolean quantized) {
        double score = 0;
        
        for (Entry<Question, Double> e : scores.entrySet()) {
            double value = e.getValue();
            Question q = e.getKey();
            if (quantized) value = StudentPerformanceAnalysis.quantize(value / q.getPoints()) * q.getPoints();
            score += (value / q.getPoints()) * e.getKey().getValueInCourse();
        }
        
        return score;
    }
    
    /**
     *
     * @param co
     * @param onlyDirect
     * @return
     */
    public double calculateOutcomeScore(Outcome co, boolean onlyDirect, boolean quantized) {
        double score = 0;
        
        for (Question q : co.getRelevantQuestions(onlyDirect)) {
            double s = 0;
            if (scores.get(q) != null) s = scores.get(q);
            if (quantized) s = StudentPerformanceAnalysis.quantize(s / q.getPoints()) * q.getPoints();
            score += (s / q.getPoints()) * q.getValueInOutcome(co);
        }
        
        return score;
    }
    
    @Override
    public String toString() {
        return id + "";
    }
    
    /**
     *
     * @param sheet
     */
    public static void processExcelSheet(Sheet sheet) {
        int startingRow = 9;
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
    
    /**
     *
     * @param id
     * @return
     */
    public static Student get(int id) {
        return students.get(id);
    }
    
    /**
     *
     * @return
     */
    public static Student[] getAll() {
        Student[] studentArray = students.values().toArray(new Student[0]);
        Arrays.sort(studentArray);
        return studentArray;
    }

    @Override
    public int compareTo(Student o) {
        return ((Integer) this.getId()).compareTo((Integer) o.getId());
    }
}
