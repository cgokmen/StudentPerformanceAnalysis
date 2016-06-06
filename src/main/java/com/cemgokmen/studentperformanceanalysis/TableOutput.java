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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author funstein
 */
public class TableOutput {
    public static int calculateWidthFromCharacterCount(int charCount) {
        return (int) ((charCount*7+5)/7.0*256);
    }

    /**
     *
     * @param sheet
     */
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
                double score = student.calculateOutcomeScore(o, false, false) * 100;
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
            double score = allOutcomes[i].calculateAverage(false, false) * 100;
            cell.setCellValue(score);
            cell.setCellStyle(style);
        }
        averageCell.setCellValue(sumAllScores / studentCount);
        averageCell.setCellStyle(style);
    }

    /**
     *
     * @param sheet
     */
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
                        double score = s.calculateOutcomeScore(co, true, false);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }

                    double avg = co.calculateAverage(true, false) * 5;
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
                        double score = s.calculateOutcomeScore(po, true, false);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }

                    double avg = po.calculateAverage(true, false) * 5;
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
                        double score = s.calculateOutcomeScore(po, false, false);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }

                    i++;
                    double avg = po.calculateAverage(false, false) * 5;
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

    /**
     *
     * @param sheet
     */
    public static void quantizedResults (Sheet sheet) {
        Row poHeaders = sheet.createRow(0);
        Row qHeaders = sheet.createRow(1);

        DataFormat format = sheet.getWorkbook().createDataFormat();
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setDataFormat(format.getFormat("0"));

        CellStyle avgStyle = sheet.getWorkbook().createCellStyle();
        avgStyle.setDataFormat(format.getFormat("0.00"));

        CellStyle textStyle = sheet.getWorkbook().createCellStyle();
        textStyle.setWrapText(true);

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

        // Some help text
        Cell poInfo = poHeaders.createCell(0);
        poInfo.setCellValue("Program Outcomes");
        poInfo.setCellStyle(textStyle);

        Cell qInfo = qHeaders.createCell(0);
        qInfo.setCellValue("Measurement Tools");
        qInfo.setCellStyle(textStyle);

        Row coAvgRow = sheet.createRow(j + 2);
        Cell txtCell1 = coAvgRow.createCell(0);
        txtCell1.setCellValue("Average of measurement tools:");
        txtCell1.setCellStyle(textStyle);

        Row poAvgRow = sheet.createRow(j + 3);
        Cell txtCell2 = poAvgRow.createCell(0);
        txtCell2.setCellValue("Program Outcome Average:");
        txtCell2.setCellStyle(textStyle);

        Row criteriaRow = sheet.createRow(j + 4);
        Cell txtCell3 = criteriaRow.createCell(0);
        txtCell3.setCellValue("(5=excellent, 1=weak)");
        txtCell3.setCellStyle(textStyle);

        int startCol = 1;
        ProgramOutcome[] programOutcomes = ProgramOutcome.getAll();
        for (ProgramOutcome po : programOutcomes) {
            int countCells = 0;

            Question[] relatedQuestions = po.getRelevantQuestions(false);
            countCells += relatedQuestions.length;

            if (countCells > 1) {
                countCells++; // Average column
            }

            if (countCells > 0) {
                Cell poHeader = poHeaders.createCell(startCol);
                poHeader.setCellValue(po.getName());
                poHeader.setCellStyle(headerStyle);

                if (countCells > 1)
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, startCol, startCol + countCells - 1));

                // First questions
                for (Question q : relatedQuestions) {
                    // Insert header
                    qHeaders.createCell(startCol).setCellValue(q.getFullName());

                    // Loop the lower rows
                    for (int i = 0; i < students.length; i++) {
                        Student s = students[i];
                        double score = s.getQuestionPercentage(q, true);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(style);
                    }

                    double avg = q.calculateAverage(true) * 5;
                    Cell avgCell = coAvgRow.createCell(startCol);
                    avgCell.setCellValue(avg);
                    avgCell.setCellStyle(avgStyle);

                    if (countCells == 0) {
                        Cell poAvgCell = poAvgRow.createCell(startCol);
                        poAvgCell.setCellValue(avg);
                        poAvgCell.setCellStyle(avgStyle);
                    }

                    startCol++;
                }

                // Average results
                if (countCells > 0) {
                    qHeaders.createCell(startCol).setCellValue("Avg");

                    // Loop the lower rows
                    int i;
                    for (i = 0; i < students.length; i++) {
                        Student s = students[i];
                        double score = s.calculateOutcomeScore(po, false, true);
                        Cell c = sheet.getRow(i+2).createCell(startCol);
                        c.setCellValue(score * 5);
                        c.setCellStyle(avgStyle);
                    }

                    i++;
                    double avg = po.calculateAverage(false, true) * 5;
                    Cell coAvgCell = coAvgRow.createCell(startCol);
                    coAvgCell.setCellValue(avg);
                    coAvgCell.setCellStyle(avgStyle);

                    Cell poAvgCell = poAvgRow.createCell(startCol);
                    poAvgCell.setCellValue(avg);
                    poAvgCell.setCellStyle(avgStyle);

                    startCol++;
                }
            }
        }
    }

    /**
     *
     * @param sheet
     */
    public static void coEvaluation (Sheet sheet) {
        Row infoHeaders = sheet.createRow(0);
        Workbook wb = sheet.getWorkbook();

        XSSFFont defaultFont= (XSSFFont) sheet.getWorkbook().createFont();
        defaultFont.setFontHeightInPoints((short)10);
        defaultFont.setFontName("Calibri");
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setBold(false);
        defaultFont.setItalic(false);

        XSSFFont font= (XSSFFont) sheet.getWorkbook().createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);

        DataFormat format = sheet.getWorkbook().createDataFormat();

        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        headerStyle.setAlignment(CellStyle.ALIGN_CENTER);
        headerStyle.setWrapText(true);
        headerStyle.setFont(font);
        headerStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        headerStyle.setBorderTop(CellStyle.BORDER_THICK);
        headerStyle.setBorderBottom(CellStyle.BORDER_THICK);
        headerStyle.setBorderLeft(CellStyle.BORDER_THICK);
        headerStyle.setBorderRight(CellStyle.BORDER_THICK);

        CellStyle poStyle = sheet.getWorkbook().createCellStyle();
        poStyle.setAlignment(CellStyle.ALIGN_CENTER);
        poStyle.setFont(font);
        poStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        CellStyle coStyle = sheet.getWorkbook().createCellStyle();
        coStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        coStyle.setWrapText(true);
        coStyle.setDataFormat(format.getFormat("0.00"));

        CellStyle questionStyle = sheet.getWorkbook().createCellStyle();
        questionStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        questionStyle.setBorderTop(CellStyle.BORDER_THIN);
        questionStyle.setBorderBottom(CellStyle.BORDER_THIN);

        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setFont(font);
        style.setDataFormat(format.getFormat("0.00"));

        Cell c = null;

        c = infoHeaders.createCell(0);
        c.setCellStyle(headerStyle);
        c.setCellValue("Related Program Outcomes (A-K)");

        c = infoHeaders.createCell(1);
        c.setCellStyle(headerStyle);
        c.setCellValue("Course Learning Outcomes");

        c = infoHeaders.createCell(2);
        c.setCellStyle(headerStyle);
        c.setCellValue("Measurement Tools");

        c = infoHeaders.createCell(3);
        c.setCellStyle(headerStyle);
        c.setCellValue("Measured Average Learning Outcome Result");

        c = infoHeaders.createCell(4);
        c.setCellStyle(headerStyle);
        c.setCellValue("Learning Outcome Survey Result");

        // Calculate number of questions
        int currentRow = 1;
        for (ProgramOutcome po : ProgramOutcome.getAll()) {
            Row poRow = null;
            CourseOutcome[] courseOutcomes = po.getRelatedOutcomes();
            for (CourseOutcome co : courseOutcomes) {
                Row coRow = null;
                Question[] questions = co.getRelevantQuestions(false);
                for (Question q : questions) {
                    Row questionRow = sheet.createRow(currentRow);
                    if (poRow == null) poRow = questionRow;
                    if (coRow == null) coRow = questionRow;

                    Cell questionCell = questionRow.createCell(2);
                    questionCell.setCellValue(q.getFullName());
                    questionCell.setCellStyle(questionStyle);

                    currentRow++;
                }

                if (coRow != null) {
                    Cell outcomeCell = coRow.createCell(1);
                    outcomeCell.setCellStyle(style);
                    outcomeCell.setCellValue(co.getName() + ": " + co.getExplanation());

                    Cell resultCell = coRow.createCell(3);
                    double result = co.calculateAverage(true, true);
                    resultCell.setCellStyle(style);
                    resultCell.setCellValue(result * 5);

                    if (questions.length > 1) {
                        sheet.addMergedRegion(new CellRangeAddress(coRow.getRowNum(), currentRow - 1, 1, 1));
                        sheet.addMergedRegion(new CellRangeAddress(coRow.getRowNum(), currentRow - 1, 3, 3));
                        sheet.addMergedRegion(new CellRangeAddress(coRow.getRowNum(), currentRow - 1, 4, 4));
                    }

                    // Draw the borders
                    for (int i = 1; i < 5; i++) {
                        CellRangeAddress cellRangeAddress1 = new CellRangeAddress(coRow.getRowNum(), currentRow - 1, i, i);
                        RegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, cellRangeAddress1, sheet, wb);
                        RegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, cellRangeAddress1, sheet, wb);
                        RegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, cellRangeAddress1, sheet, wb);
                        RegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, cellRangeAddress1, sheet, wb);
                    }
                }
            }

            if (poRow != null) {
                Cell poCell = poRow.createCell(0);
                poCell.setCellValue(po.getName());
                poCell.setCellStyle(poStyle);

                if (currentRow - poRow.getRowNum() > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(poRow.getRowNum(), currentRow - 1, 0, 0));
                }

                CellRangeAddress cellRangeAddress2 = new CellRangeAddress(poRow.getRowNum(), currentRow - 1, 0, 4);
                RegionUtil.setBorderTop(CellStyle.BORDER_THICK, cellRangeAddress2, sheet, wb);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THICK, cellRangeAddress2, sheet, wb);
                RegionUtil.setBorderRight(CellStyle.BORDER_THICK, cellRangeAddress2, sheet, wb);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THICK, cellRangeAddress2, sheet, wb);
            }
        }

        sheet.setColumnWidth(0, calculateWidthFromCharacterCount(10));
        sheet.setColumnWidth(1, calculateWidthFromCharacterCount(15));
        sheet.setColumnWidth(3, calculateWidthFromCharacterCount(10));
        sheet.setColumnWidth(4, calculateWidthFromCharacterCount(10));
        sheet.autoSizeColumn(2);
    }


    /**
     *
     * @param sheet
     */
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

                double score = po.calculateAverage(false, true) * 5;
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
