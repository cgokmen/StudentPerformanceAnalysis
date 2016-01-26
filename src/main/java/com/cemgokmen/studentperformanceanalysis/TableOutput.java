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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TableOutput {
    public static void individualOutcomes (Sheet sheet) {
        Outcome[] allOutcomes = Outcome.getAllWithQuestions();
        Row headers = sheet.createRow(0);
        headers.createCell(0).setCellValue("ID");
        headers.createCell(1).setCellValue("Name");
        headers.createCell(2).setCellValue("Average");
        for (int i = 0; i < allOutcomes.length; i++) {
            Outcome o = allOutcomes[i];
            Cell cell = headers.createCell(i + 3);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            cell.setCellValue(o.getName());
        }
        
        DataFormat format = sheet.getWorkbook().createDataFormat();
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setDataFormat(format.getFormat("0.00"));
        
        XSSFCellStyle cancelledStyle = ((XSSFWorkbook) sheet.getWorkbook()).createCellStyle();
        cancelledStyle.cloneStyleFrom(style);
        cancelledStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cancelledStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        
        Student[] students = Student.getAll();
        //System.out.println(students.length);
        int j;
        double sumAllScores = 0;
        int studentCount = 0;
        for (j = 0; j < students.length; j++) {
            Student student = students[j];
            Row row = sheet.createRow(j+1);
            row.createCell(0).setCellValue(student.getId());
            
            Cell nameCell = row.createCell(1);
            nameCell.setCellType(Cell.CELL_TYPE_STRING);
            nameCell.setCellValue(student.getName());
            if (!student.doesStudentCount())
                nameCell.setCellStyle(cancelledStyle);
            
            Cell averageCell = row.createCell(2);
            
            double sumScore = 0;
            int scoreCount = 0;
            for (int k = 0; k < allOutcomes.length; k++) {
                Outcome o = allOutcomes[k];
                double score = student.calculateOutcomeScore(o, false) * 100;
                Cell c = row.createCell(k+3);
                c.setCellType(Cell.CELL_TYPE_NUMERIC);
                c.setCellValue(score);
                c.setCellStyle(style);
                
                if (o.getRelevantQuestions(false).length > 0) {
                    sumScore += score;
                    scoreCount++;
                }
            }
            
            double average = sumScore / scoreCount;
            averageCell.setCellValue(average);
            averageCell.setCellStyle(style);
            if (!student.doesStudentCount()) 
                averageCell.setCellStyle(cancelledStyle);
            
            if (student.doesStudentCount()) {
                sumAllScores += average;
                studentCount++;
            }
        }
        Row averageRow = sheet.createRow(j + 1);
        averageRow.createCell(1).setCellValue("Course averages:");
        Cell averageCell = averageRow.createCell(2);
        
        for (int i = 0; i < allOutcomes.length; i++) {
            Cell cell = averageRow.createCell(i + 3);
            double score = allOutcomes[i].calculateAverage(false) * 100;
            cell.setCellValue(score);
            cell.setCellStyle(style);
        }
        averageCell.setCellValue(sumAllScores / studentCount);
        averageCell.setCellStyle(style);
    }
    
    public static void treeResults (Sheet sheet) {
        Row poHeaders = sheet.createRow(0);
        Row coHeaders = sheet.createRow(1);
        
        DataFormat format = sheet.getWorkbook().createDataFormat();
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setDataFormat(format.getFormat("0.00"));
        
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        
        
        // Eliminate non-passing students
        Student[] students = Student.getAll();
        ArrayList<Student> validStudents = new ArrayList<>(Arrays.asList(students));
        for (Student s: students) {
            if (!s.doesStudentCount())
                validStudents.remove(s);
        }
        students = validStudents.toArray(new Student[0]);
        
        // Create enough rows for students
        int j;
        for (j = 0; j < students.length; j++) {
            Row row = sheet.createRow(j + 2);
            row.createCell(0).setCellValue(students[j].getId());
        }
        Row coAvgRow = sheet.createRow(j + 2);
        coAvgRow.createCell(0).setCellValue("Average of measurement tools:");
        Row poAvgRow = sheet.createRow(j + 3);
        poAvgRow.createCell(0).setCellValue("Program Outcome Average:");

        int startCol = 1;
        ProgramOutcome[] programOutcomes = ProgramOutcome.getAll();
        for (ProgramOutcome po : programOutcomes) {            
            int countCells = 0;
            
            Outcome[] relatedOutcomes = po.getRelatedOutcomes();
            countCells += relatedOutcomes.length;
            
            if (po.getDirectlyRelevantQuestions().length > 0)
                countCells++; // Direct question column
            
            if (countCells > 1) {
                countCells++; // Average column
            }
            
            if (countCells > 0) {
                Cell poHeader = poHeaders.createCell(startCol);
                poHeader.setCellValue(po.getName());
                poHeader.setCellStyle(headerStyle);
                
                if (countCells > 1)
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, startCol, startCol + countCells - 1));
                
                // First course objectives
                for (Outcome co : relatedOutcomes) {
                    // Insert header
                    coHeaders.createCell(startCol).setCellValue(co.getName());
                    
                    // Loop the lower rows
                    for (int i = 0; i < students.length; i++) {
                        Student s = students[i];
                        double score = s.calculateOutcomeScore(co, true);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }
                    
                    double avg = co.calculateAverage(true) * 5;
                    Cell avgCell = coAvgRow.createCell(startCol);
                    avgCell.setCellValue(avg);
                    avgCell.setCellStyle(style);
                                        
                    if (countCells == 0) {
                        Cell poAvgCell = poAvgRow.createCell(startCol);
                        poAvgCell.setCellValue(avg);
                        poAvgCell.setCellStyle(style);
                    }
                    
                    startCol++;
                }
                
                // Direct results
                if (po.getDirectlyRelevantQuestions().length > 0) {
                    coHeaders.createCell(startCol).setCellValue("Direct");
                    
                    // Loop the lower rows
                    for (int i = 0; i < students.length; i++) {
                        Student s = students[i];
                        double score = s.calculateOutcomeScore(po, true);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }
                    
                    double avg = po.calculateAverage(true) * 5;
                    Cell avgCell = coAvgRow.createCell(startCol);
                    avgCell.setCellValue(avg);
                    avgCell.setCellStyle(style);
                    
                    if (countCells == 0) {
                        Cell poAvgCell = poAvgRow.createCell(startCol);
                        poAvgCell.setCellValue(avg);
                        poAvgCell.setCellStyle(style);
                    }
                    
                    startCol++;
                }
                
                // Average results
                if (countCells > 0) {
                    coHeaders.createCell(startCol).setCellValue("Avg");
                    
                    // Loop the lower rows
                    int i;
                    for (i = 0; i < students.length; i++) {
                        Student s = students[i];
                        double score = s.calculateOutcomeScore(po, false);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }
                    
                    i++;
                    double avg = po.calculateAverage(false) * 5;
                    Cell coAvgCell = coAvgRow.createCell(startCol);
                    coAvgCell.setCellValue(avg);
                    coAvgCell.setCellStyle(style);
                    
                    Cell poAvgCell = poAvgRow.createCell(startCol);
                    poAvgCell.setCellValue(avg);
                    poAvgCell.setCellStyle(style);
                    
                    startCol++;
                }
            }
        }
    }
    
    public static void successCriteria (Sheet sheet) {
        DataFormat format = sheet.getWorkbook().createDataFormat();
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setDataFormat(format.getFormat("0.00"));
        
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Related Program Outcomes");
        header.createCell(1).setCellValue("Weak [0.0 - 1.5]");
        header.createCell(2).setCellValue("Fair [1.5 - 2.5]");
        header.createCell(3).setCellValue("Good [2.5 - 3.5]");
        header.createCell(4).setCellValue("Very Good [3.5 - 4.5]");
        header.createCell(5).setCellValue("Excellent [4.5 - 5.0]");
        
        int currentRow = 1;
        for (ProgramOutcome po : ProgramOutcome.getAll()) {
            if (po.getRelevantQuestions(false).length > 0) {
                Row row = sheet.createRow(currentRow);
                row.createCell(0).setCellValue(po.getName());
                
                double score = po.calculateAverage(false) * 5;
                int targetCol = 1;
                if (score >= 1.5 && score < 2.5) {
                    targetCol = 2;
                } else if (score >= 2.5 && score < 3.5) {
                    targetCol = 3;
                } else if (score >= 3.5 && score < 4.5) {
                    targetCol = 4;
                } else if (score > 4.5) {
                    targetCol = 5;
                }
                
                Cell c = row.createCell(targetCol);
                c.setCellValue(score);
                c.setCellStyle(style);
                
                currentRow++;
            }
        }
    }
}
