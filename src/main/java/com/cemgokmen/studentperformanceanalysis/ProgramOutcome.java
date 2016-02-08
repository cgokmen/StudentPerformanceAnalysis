/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
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

/**
 *
 * @author funstein
 */
public class ProgramOutcome extends Outcome {
    private final String name;
    private final String explanation;
    private final List<CourseOutcome> courseOutcomes;
    private final Set<Question> directlyRelevantQuestions;
    private double totalValueInCourse;
    
    private static final Map<String, ProgramOutcome> programOutcomes = new LinkedHashMap<>();

    /**
     *
     * @param name
     * @param explanation
     */
    public ProgramOutcome(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
        this.courseOutcomes = new ArrayList<>();
        this.directlyRelevantQuestions = new LinkedHashSet<>();
        this.totalValueInCourse = 0;
    }
    
    /**
     *
     * @param co
     */
    public void addCourseOutcome(CourseOutcome co) {
        if (!hasCourseOutcome(co))
            courseOutcomes.add(co);
    }
    
    /**
     *
     * @param co
     * @return
     */
    public boolean hasCourseOutcome(CourseOutcome co) {
        return courseOutcomes.contains(co);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Outcome[] getRelatedOutcomes() {
        Outcome[] outcomes = courseOutcomes.toArray(new Outcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
    
    /**
     *
     * @param q
     */
    @Override
    public void addRelevantQuestion(Question q) {
        directlyRelevantQuestions.add(q);
        recalculateTotalValueInCourse();
    }
    
    /**
     *
     * @param q
     * @return
     */
    @Override
    public boolean hasRelevantQuestion(Question q) {
        if (directlyRelevantQuestions.contains(q))
            return true;
        
        for (CourseOutcome co : courseOutcomes) {
            if (co.hasRelevantQuestion(q))
                return true;
        }
        
        return false;
    }
    
    /**
     *
     * @param q
     * @return
     */
    public boolean hasDirectlyRelevantQuestion(Question q) {
        return directlyRelevantQuestions.contains(q);
    }
    
    /**
     *
     * @param onlyDirect
     * @return
     */
    @Override
    public Question[] getRelevantQuestions(boolean onlyDirect) {
        if (onlyDirect)
            return getDirectlyRelevantQuestions();
        
        Set<Question> qs = new LinkedHashSet<>();
        qs.addAll(directlyRelevantQuestions);
        for (CourseOutcome co : courseOutcomes) {
            qs.addAll(Arrays.asList(co.getRelevantQuestions(onlyDirect)));
        }

        Question[] questions = qs.toArray(new Question[0]);
        Arrays.sort(questions);
        return questions;
    }
    
    /**
     *
     * @return
     */
    public Question[] getDirectlyRelevantQuestions() {
        Question[] questions = directlyRelevantQuestions.toArray(new Question[0]);
        Arrays.sort(questions);
        return questions;
    }
    
    /**
     *
     */
    public void recalculateTotalValueInCourse() {
        totalValueInCourse = 0;
        
        // For the set-based approach, comment out this part
        List<Question> qs = new ArrayList<>();
        qs.addAll(directlyRelevantQuestions);
        for (CourseOutcome co : courseOutcomes) {
            qs.addAll(Arrays.asList(co.getRelevantQuestions(false)));
        }
        // Comment out until here
        
        // Uncomment this line:
        // Set<Question> qs = new HashSet<>();
        
        for (Question q : qs) {
            if (q.doesQuestionCount())
                totalValueInCourse += q.getValueInCourse();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public String getExplanation() {
        return explanation;
    }

    /**
     *
     * @return
     */
    @Override
    public double getTotalValueInCourse() {
        return totalValueInCourse;
    }
    
    /**
     *
     * @param sheet
     */
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
    
    /**
     *
     * @param str
     * @return
     */
    public static ProgramOutcome get(String str) {
        return programOutcomes.get(str);
    }
    
    /**
     *
     * @return
     */
    public static ProgramOutcome[] getAll() {
        ProgramOutcome[] outcomes = programOutcomes.values().toArray(new ProgramOutcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
}
