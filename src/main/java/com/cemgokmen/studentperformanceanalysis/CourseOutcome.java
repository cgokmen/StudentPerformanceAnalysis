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

public class CourseOutcome {
    private final String name;
    private final String explanation;
    private final List<String> programOutcomes;
    private final List<Question> relevantQuestions;
    private double totalValueInCourse;
    
    private static final Map<String, CourseOutcome> courseOutcomes = new HashMap<String, CourseOutcome>();

    public CourseOutcome(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
        this.programOutcomes = new ArrayList<String>();
        this.relevantQuestions = new ArrayList<Question>();
        this.totalValueInCourse = 0;
    }
    
    public void addProgramOutcome(String po) {
        if (!hasProgramOutcome(po))
            programOutcomes.add(po);
    }
    
    public boolean hasProgramOutcome(String po) {
        return programOutcomes.contains(po);
    }
    
    public String[] getProgramOutcomes() {
        String[] outcomes = new String[programOutcomes.size()];
        programOutcomes.toArray(outcomes);
        return outcomes;
    }
    
    public void addRelevantQuestion(Question q) {
        if (!hasRelevantQuestion(q)) {
            relevantQuestions.add(q);
            totalValueInCourse += q.getValueInCourse();
        }
    }
    
    public boolean hasRelevantQuestion(Question q) {
        return relevantQuestions.contains(q);
    }
    
    public Question[] getRelevantQuestions() {
        Question[] questions = new Question[relevantQuestions.size()];
        relevantQuestions.toArray(questions);
        return questions;
    }
    
    public double calculateQuestionValue(Question q) {
        return q.getValueInCourse() / totalValueInCourse;
    }
    
    public double calculateScoreForQuestion(double score, Question q) {
        return (score / q.getPoints()) * calculateQuestionValue(q);
    }

    public String getName() {
        return name;
    }

    public String getExplanation() {
        return explanation;
    }

    public double getTotalValueInCourse() {
        return totalValueInCourse;
    }
    
    @Override
    public String toString() {
        String output = name + ": " + explanation + "\nProgram Outcomes: ";
        for (String outcome : programOutcomes) {
            output += outcome + ", ";
        }
        
        
        output += "\nRelevant Questions:\n";

        for (Question question : relevantQuestions) {
            output += String.format("    Evaluation %s(%.2f), Question %s(%.2f), Total value in course:(%.2f), Total value in outcome(%.2f)%n",
                    question.getParent().getName(),
                    question.getParent().getPercentage() * 100,
                    question.getName(),
                    question.getPoints() * 100,
                    question.getValueInCourse() * 100,
                    question.getValueInCourseOutcome(this) * 100
            );
        }
        
        return output;
    }
    
    public static void processExcelSheet(Sheet sheet) {
        int startingRow = 2;
        while (true) {
            Row row = sheet.getRow(startingRow);
            if (row == null) break;
            
            Cell name = row.getCell(0);
            if (name == null) break;
            
            Cell explanation = row.getCell(1);
            if (explanation == null) break;
            
            String nameStr = name.getStringCellValue();
            String explanationStr = explanation.getStringCellValue();
            
            CourseOutcome outcome = new CourseOutcome(nameStr, explanationStr);
            
            int startingCol = 2;
            while (true) {
                Cell po = row.getCell(startingCol);
                if (po == null) break;
                
                outcome.addProgramOutcome(po.getStringCellValue());
                startingCol++;
            }
            
            //System.out.println(outcome + "\n");
            startingRow++;
            
            courseOutcomes.put(nameStr, outcome);
        }
    }
    
    public static CourseOutcome get(String str) {
        return courseOutcomes.get(str);
    }
    
    public static CourseOutcome[] getAll() {
        return courseOutcomes.values().toArray(new CourseOutcome[0]);
    }
}
