/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://github.com/sultanskyman/StudentPerformanceAnalysis/blob/master/LICENSE.md
 */
package com.cemgokmen.studentperformanceanalysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author funstein
 */
public class ProcessPanel extends javax.swing.JPanel implements PropertyChangeListener {
    private Task task;
    private final MainUI mainUI;
    
    class Task extends SwingWorker<Void, String> {
        private final File[] files;
        
        public Task(File[] files) {
            this.files = files;
        }

        @Override
        public Void doInBackground() {
            String output = "";
            setProgress(0);
            
            double valueOfEachFile = 1.0 / files.length;

            publish("Starting StudentPerformanceAnalysis!");

            for (int key = 0; key < files.length; key++) {
                File file = files[key];
                try {
                    // TODO: Reset all classes!
                    publish("\nProcessing " + file.getName() + " [" + (key+1) + "/" + files.length + "].\n");
                    Workbook wb = WorkbookFactory.create(file);
                    
                    // Process Program Outcomes
                    Sheet programOutcomeSheet = wb.getSheetAt(0);
                    ProgramOutcome.processExcelSheet(programOutcomeSheet);
                    setProgress((int) Math.round((key + 0.25) * valueOfEachFile * 100));
                    
                    // Process Course Outcomes
                    Sheet courseOutcomeSheet = wb.getSheetAt(1);
                    CourseOutcome.processExcelSheet(courseOutcomeSheet);
                    setProgress((int) Math.round((key + 0.50) * valueOfEachFile * 100));
                    
                    Sheet gradeSheet = wb.getSheetAt(2);
                    
                    // Process Evaluations
                    Evaluation.processExcelSheet(gradeSheet);
                    setProgress((int) Math.round((key + 0.75) * valueOfEachFile * 100));
                    
                    // Process Students
                    Student.processExcelSheet(gradeSheet);
                    setProgress((int) Math.round((key + 1.0) * valueOfEachFile * 100));
                    
                    // Let's output
                    for (Outcome outcome : Outcome.getAll()) {            
                        publish(outcome.toString());
                    }
                    
                    publish("Successfully processed " + file.getName() + " [" + (key+1) + "/" + files.length + "].\n");
                } catch (Exception ex) {
                    publish("Could not process file " + file.getName() + ".");
                    publish(ex.getMessage());
                    for (StackTraceElement e : ex.getStackTrace()) {
                        publish(e.toString());
                    }
                }
                setProgress((int) Math.round((key + 1.0) * valueOfEachFile * 100));
            }
            
            return null;
        }
        
        @Override
        protected void process(List<String> outputs) {
            for (String s : outputs) {
                textArea.append(s + "\n");
            }
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {            
            continueButton.setEnabled(true);
        }
    }
    
    /**
     * Creates new form InitialPanel
     * @param ui
     */
    public ProcessPanel(MainUI ui) {
        this.mainUI = ui;
        initComponents();
        progressBar.setValue(0);
    }
    
    public void processFiles(File[] files) {
        //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (task == null) {
            task = new Task(files);
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        fileChooser = new javax.swing.JFileChooser();
        jLabel1 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        saveButton = new javax.swing.JButton();
        continueButton = new javax.swing.JButton();

        fileChooser.setApproveButtonText("Process");
        fileChooser.setApproveButtonToolTipText("");
        fileChooser.setDialogTitle("Choose files to process");
        fileChooser.setMultiSelectionEnabled(true);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Running analysis...");

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        scrollPane.setViewportView(textArea);

        saveButton.setText("Save Log");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        continueButton.setText("Continue ->");
        continueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continueButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(139, 139, 139)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(continueButton, javax.swing.GroupLayout.DEFAULT_SIZE, 322, Short.MAX_VALUE))
                .addGap(139, 139, 139))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(5, 5, 5)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveButton)
                .addGap(5, 5, 5)
                .addComponent(continueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        /*FileWriter fw = null;
        try {
            File newTextFile = new File("log.txt");
            newTextFile.createNewFile();
            
            fw = new FileWriter(newTextFile);
            fw.write(textArea.getText());
        } catch (IOException ex) {
            Logger.getLogger(ProcessPanel.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(ProcessPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Results");
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
        
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("0.00"));
        
        XSSFCellStyle cancelledStyle = wb.createCellStyle();
        cancelledStyle.cloneStyleFrom(style);
        cancelledStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cancelledStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        
        Student[] students = Student.getAll();
        System.out.println(students.length);
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
                double score = student.calculateOutcomeScore(o) * 100;
                Cell c = row.createCell(k+3);
                c.setCellType(Cell.CELL_TYPE_NUMERIC);
                c.setCellValue(score);
                c.setCellStyle(style);
                
                if (o.getRelevantQuestions().length > 0) {
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
        j++;
        Row averageRow = sheet.createRow(j);
        averageRow.createCell(1).setCellValue("Course averages:");
        Cell averageCell = averageRow.createCell(2);
        
        for (int i = 0; i < allOutcomes.length; i++) {
            Cell cell = averageRow.createCell(i + 3);
            double score = allOutcomes[i].calculateAverage() * 100;
            cell.setCellValue(score);
            cell.setCellStyle(style);
        }
        averageCell.setCellValue(sumAllScores / studentCount);
        averageCell.setCellStyle(style);
            
        try {
            File file = new File("workbook.xlsx");
            file.createNewFile();
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                wb.write(fileOut);
                fileOut.close();
            }
        } catch (Exception ex) {
            
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_continueButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continueButton;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
