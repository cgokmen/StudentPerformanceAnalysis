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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class CourseOutcome extends Outcome {
    private final String name;
    private final String explanation;
    private final List<ProgramOutcome> programOutcomes;
    private final Set<Question> relevantQuestions;
    private double totalValueInCourse;
    
    private static final Map<String, CourseOutcome> courseOutcomes = new LinkedHashMap<String, CourseOutcome>();

    public CourseOutcome(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
        this.programOutcomes = new ArrayList<ProgramOutcome>();
        this.relevantQuestions = new LinkedHashSet<Question>();
        this.totalValueInCourse = 0;
    }
    
    public void addProgramOutcome(ProgramOutcome po) {
        if (!hasProgramOutcome(po)) {
            programOutcomes.add(po);
            po.addCourseOutcome(this);
        }
    }
    
    public boolean hasProgramOutcome(ProgramOutcome po) {
        return programOutcomes.contains(po);
    }
    
    public ProgramOutcome[] getProgramOutcomes() {
        return programOutcomes.toArray(new ProgramOutcome[0]);
    }
    
    public void addRelevantQuestion(Question q) {
        if (!hasRelevantQuestion(q)) {
            relevantQuestions.add(q);
            recalculateTotalValueInCourse();
            for (ProgramOutcome po : programOutcomes) {
                po.recalculateTotalValueInCourse();
            }
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
    
    public void recalculateTotalValueInCourse() {
        totalValueInCourse = 0;
        for (Question q : relevantQuestions) {
            totalValueInCourse += q.getValueInCourse();
        }
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
        for (ProgramOutcome outcome : programOutcomes) {
            output += outcome.getName() + ", ";
        }
        output = output.substring(0, output.length() - 2);
        
        output += "\nRelevant Questions:\n";

        for (Question question : relevantQuestions) {
            output += String.format("    Evaluation %s(%.2f), Question %s(%.2f), Total value in course:(%.2f), Total value in outcome(%.2f)%n",
                    question.getParent().getName(),
                    question.getParent().getPercentage() * 100,
                    question.getName(),
                    question.getPoints() * 100,
                    question.getValueInCourse() * 100,
                    question.getValueInOutcome(this) * 100
            );
        }
        
        output += String.format("The average for this outcome is: %.2f%%.%n", calculateAverageForOutcome(this) * 100);
        
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
            
            Cell po = row.getCell(2);
            if (po == null) break;
            
            String[] programOutcomes = po.getStringCellValue().split(",");
            for (String out : programOutcomes) {
                ProgramOutcome pOutcome = ProgramOutcome.get(out.trim());
                if (pOutcome != null)
                    outcome.addProgramOutcome(pOutcome);
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
