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
public class CourseOutcome extends Outcome {
    private final String name;
    private final String explanation;
    private final List<ProgramOutcome> programOutcomes;
    private final Set<Question> relevantQuestions;
    private double totalValueInCourse;
    
    private static final Map<String, CourseOutcome> courseOutcomes = new LinkedHashMap<>();

    /**
     *
     * @param name
     * @param explanation
     */
    public CourseOutcome(String name, String explanation) {
        this.name = name;
        this.explanation = explanation;
        this.programOutcomes = new ArrayList<>();
        this.relevantQuestions = new LinkedHashSet<>();
        this.totalValueInCourse = 0;
    }
    
    /**
     *
     * @param po
     */
    public void addProgramOutcome(ProgramOutcome po) {
        if (!hasProgramOutcome(po)) {
            programOutcomes.add(po);
            po.addCourseOutcome(this);
        }
    }
    
    /**
     *
     * @param po
     * @return
     */
    public boolean hasProgramOutcome(ProgramOutcome po) {
        return programOutcomes.contains(po);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Outcome[] getRelatedOutcomes() {
        Outcome[] outcomes = programOutcomes.toArray(new Outcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
    
    /**
     *
     * @param q
     */
    @Override
    public void addRelevantQuestion(Question q) {
        if (!hasRelevantQuestion(q)) {
            relevantQuestions.add(q);
            recalculateTotalValueInCourse();
            for (ProgramOutcome po : programOutcomes) {
                po.recalculateTotalValueInCourse();
            }
        }
    }
    
    /**
     *
     * @param q
     * @return
     */
    @Override
    public boolean hasRelevantQuestion(Question q) {
        return relevantQuestions.contains(q);
    }
    
    /**
     *
     * @param idc
     * @return
     */
    @Override
    public Question[] getRelevantQuestions(boolean idc) {
        Question[] questions = relevantQuestions.toArray(new Question[0]);
        Arrays.sort(questions);
        return questions;
    }
    
    /**
     *
     */
    public void recalculateTotalValueInCourse() {
        totalValueInCourse = 0;
        for (Question q : relevantQuestions) {
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
        int startingRow = 6;
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
    
    /**
     *
     * @param str
     * @return
     */
    public static CourseOutcome get(String str) {
        return courseOutcomes.get(str);
    }
    
    /**
     *
     * @return
     */
    public static CourseOutcome[] getAll() {
        CourseOutcome[] outcomes = courseOutcomes.values().toArray(new CourseOutcome[0]);
        Arrays.sort(outcomes);
        return outcomes;
    }
}
