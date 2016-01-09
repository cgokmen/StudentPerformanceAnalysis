/*
 * StudentPerformancAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://github.com/sultanskyman/StudentPerformanceAnalysis/blob/master/LICENSE.md
 */
package com.cemgokmen.studentperformanceanalysis;

import java.io.File;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class StudentPerformanceAnalysis  {
    public static void main( String[] args ) throws IOException, InvalidFormatException {
        System.out.println( "Hello!\n" );
        
        Workbook wb = WorkbookFactory.create(new File("grades.xlsx"));

        // Process Module1: Read course outcomes
        Sheet courseOutcomeSheet = wb.getSheet("Module1");
        CourseOutcome.processExcelSheet(courseOutcomeSheet);

        // Process Module2: Read evaluations
        Sheet evaluationSheet = wb.getSheet("Module2");
        Evaluation.processExcelSheet(evaluationSheet);
        
        // Process Module3: Read students
        Sheet studentSheet = wb.getSheet("Module3");
        Student.processExcelSheet(studentSheet);
        
        // Let's output
        for (CourseOutcome outcome : CourseOutcome.getAll()) {
            System.out.println(outcome);            
        }
    }
}
