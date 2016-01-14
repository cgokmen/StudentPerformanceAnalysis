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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProgramOutcome extends Outcome {
    private final String name;
    private final String explanation;
    private final List<CourseOutcome> courseOutcomes;
    private final Set<Question> directlyRelevantQuestions;
    private double totalValueInCourse;
    
    private static final Map<String, ProgramOutcome> programOutcomes = new LinkedHashMap<String, ProgramOutcome>();

    public ProgramOutcome(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
        this.courseOutcomes = new ArrayList<CourseOutcome>();
        this.directlyRelevantQuestions = new LinkedHashSet<Question>();
        this.totalValueInCourse = 0;
    }
    
    public void addCourseOutcome(CourseOutcome co) {
        if (!hasCourseOutcome(co))
            courseOutcomes.add(co);
    }
    
    public boolean hasCourseOutcome(CourseOutcome co) {
        return courseOutcomes.contains(co);
    }
    
    public Outcome[] getRelatedOutcomes() {
        Outcome[] outcomes = courseOutcomes.toArray(new Outcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
    
    public void addRelevantQuestion(Question q) {
        directlyRelevantQuestions.add(q);
        recalculateTotalValueInCourse();
    }
    
    public boolean hasRelevantQuestion(Question q) {
        if (directlyRelevantQuestions.contains(q))
            return true;
        
        for (CourseOutcome co : courseOutcomes) {
            if (co.hasRelevantQuestion(q))
                return true;
        }
        
        return false;
    }
    
    public boolean hasDirectlyRelevantQuestion(Question q) {
        return directlyRelevantQuestions.contains(q);
    }
    
    public Question[] getRelevantQuestions() {
        Set<Question> qs = new LinkedHashSet<Question>();
        qs.addAll(directlyRelevantQuestions);
        for (CourseOutcome co : courseOutcomes) {
            qs.addAll(Arrays.asList(co.getRelevantQuestions()));
        }

        Question[] questions = qs.toArray(new Question[0]);
        Arrays.sort(questions);
        return questions;
    }
    
    public Question[] getDirectlyRelevantQuestions() {
        Question[] questions = directlyRelevantQuestions.toArray(new Question[0]);
        Arrays.sort(questions);
        return questions;
    }
    
    public void recalculateTotalValueInCourse() {
        totalValueInCourse = 0;
        for (Question q : getRelevantQuestions()) {
            if (q.doesQuestionCount())
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
            
            ProgramOutcome outcome = new ProgramOutcome(nameStr, explanationStr);
            
            //System.out.println(outcome + "\n");
            startingRow++;
            
            programOutcomes.put(nameStr, outcome);
        }
    }
    
    public static ProgramOutcome get(String str) {
        return programOutcomes.get(str);
    }
    
    public static ProgramOutcome[] getAll() {
        ProgramOutcome[] outcomes = programOutcomes.values().toArray(new ProgramOutcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
}
