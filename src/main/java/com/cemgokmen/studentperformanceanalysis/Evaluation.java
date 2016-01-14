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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class Evaluation {
    private final String name;
    private final double percentage;
    private final List<Question> questions;
    
    private static final Map<String, Evaluation> evaluations = new LinkedHashMap<String, Evaluation>();

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
        Row exams = sheet.getRow(1);
        if (exams == null) return;
        
        Row examValues = sheet.getRow(2);
        if (examValues == null) return;
        
        Row questions = sheet.getRow(3);
        if (questions == null) return;
        
        Row questionValues = sheet.getRow(4);
        if (questionValues == null) return;
        
        Row questionOutcomes = sheet.getRow(5);
        if (questionOutcomes == null) return;
        
        Row countQuestions = sheet.getRow(6);
        if (countQuestions == null) return;
        
        int startingCol = 5;
        evaluations: while (true) {            
            Cell name = exams.getCell(startingCol);
            if (name == null) break;
            
            Cell percentage = examValues.getCell(startingCol);
            if (percentage == null) break;
            
            String nameStr = name.getStringCellValue();
            double percentageValue = percentage.getNumericCellValue() / 100;
            
            Evaluation evaluation = new Evaluation(nameStr, percentageValue);
            
            questions: while (true) {
                Cell qName = questions.getCell(startingCol);
                if (qName == null) break;
                
                Cell qPoints = questionValues.getCell(startingCol);
                if (qPoints == null) break;
                
                Cell qCount = countQuestions.getCell(startingCol);
                if (qCount == null) break;
                
                String qNameStr = (qName.getCellType() == Cell.CELL_TYPE_STRING) ? qName.getStringCellValue() : ((int) qName.getNumericCellValue()) + "";
                double qPointsValue = qPoints.getNumericCellValue() / 100;
                
                Question question = new Question(qNameStr, qPointsValue, qCount.getBooleanCellValue(), evaluation, startingCol);
                
                Cell qOutcomes = questionOutcomes.getCell(startingCol);
                String[] outcomes = qOutcomes.getStringCellValue().split(",");
                for (String out : outcomes) {
                    Outcome outcome = Outcome.get(out.trim());
                    if (outcome != null)
                        question.addOutcome(outcome);
                }
                
                evaluation.addQuestion(question);
                startingCol++;
                
                // Check if new row has new quiz
                Cell testName = exams.getCell(startingCol);
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
