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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Evaluation {
    private final String name;
    private final double percentage;
    private final List<Question> questions;
    
    private static final Map<String, Evaluation> evaluations = new HashMap<String, Evaluation>();

    public Evaluation(String name, double percentage) {
        this.name = name;
        this.percentage = percentage;
        this.questions = new ArrayList<Question>();
    }
    
    public void addQuestion(Question question) {
        if (!hasQuestion(question))
            questions.add(question);
    }
    
    public boolean hasQuestion(Question question) {
        return questions.contains(question);
    }
    
    public Question[] getQuestions() {
        Question[] questionArray = new Question[questions.size()];
        questions.toArray(questionArray);
        return questionArray;
    }

    public String getName() {
        return name;
    }

    public double getPercentage() {
        return percentage;
    }
    
    @Override
    public String toString() {
        String output = name + ": " + String.format("%.2f", percentage * 100) + " points\nQuestions:\n";
        for (Question q: questions) {
            output += "    " + q.toString() + "\n";
        }
        
        return output;
    }
    
    public static void processExcelSheet(Sheet sheet) {
        int startingRow = 2;
        evaluations: while (true) {
            Row row = sheet.getRow(startingRow);
            if (row == null) break;
            
            Cell name = row.getCell(0);
            if (name == null) break;
            
            Cell percentage = row.getCell(1);
            if (percentage == null) break;
            
            String nameStr = name.getStringCellValue();
            double percentageValue = percentage.getNumericCellValue() / 100;
            
            Evaluation evaluation = new Evaluation(nameStr, percentageValue);
            
            questions: while (true) {
                Row qRow = sheet.getRow(startingRow);
                if (qRow == null) break;

                Cell qName = qRow.getCell(2);
                if (qName == null) break;
                
                Cell qPoints = qRow.getCell(3);
                if (qPoints == null) break;
                
                String qNameStr = qName.getStringCellValue();
                double qPointsValue = qPoints.getNumericCellValue() / 100;
                
                Question question = new Question(qNameStr, qPointsValue, evaluation);
                int startingCol = 4;
                outcomes: while (true) {
                    Cell co = qRow.getCell(startingCol);
                    if (co == null) break;

                    CourseOutcome outcome = CourseOutcome.get(co.getStringCellValue());
                    if (outcome != null)
                        question.addCourseOutcome(outcome);
                    
                    startingCol++;
                }
                
                evaluation.addQuestion(question);
                startingRow++;
                
                // Check if new row has new quiz
                Row testRow = sheet.getRow(startingRow);
                if (testRow == null) break evaluations;
                Cell testName = testRow.getCell(0);
                if (testName != null && testName.getStringCellValue().trim().length() > 0) {
                    break;
                }
            }
            
            evaluations.put(nameStr, evaluation);
        }
    }
    
    public static Evaluation get(String str) {
        return evaluations.get(str);
    }
    
    public static Evaluation[] getAll() {
        return evaluations.values().toArray(new Evaluation[0]);
    }
}
