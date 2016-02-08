/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
 */
package com.cemgokmen.studentperformanceanalysis;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
            saveButton.setEnabled(false);
            continueButton.setEnabled(false);
            
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
                    
                    publish("Successfully processed " + file.getName() + " [" + (key+1) + "/" + files.length + "].");
                    
                    // Limit to one file for now -- TODO: Add support for more
                    if (files.length > 1) {
                        publish("Warning! The software currently supports only one file. Execution halted.");
                        break;
                    }
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
            saveButton.setEnabled(true);
            continueButton.setEnabled(true);
            setProgress(100);
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
    
    /**
     *
     * @param files
     */
    public void processFiles(File[] files) {
        //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (task == null) {
            task = new Task(files);
            task.addPropertyChangeListener(this);
            task.execute();
        }
    }
    
    @Override
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

        jLabel1 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        scrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        saveButton = new javax.swing.JButton();
        continueButton = new javax.swing.JButton();

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

        continueButton.setText("Save Results");
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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
        JFileChooser fileChooser = mainUI.getFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showSaveDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            FileWriter fw = null;
            try {
                File newTextFile = fileChooser.getSelectedFile();
                newTextFile.createNewFile();

                fw = new FileWriter(newTextFile);
                fw.write(textArea.getText());
                JOptionPane.showMessageDialog(mainUI, "Log saved successfully.", "Success!", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainUI, "Log could not be saved.", "Error!", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                }
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void continueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continueButtonActionPerformed
        JFileChooser fileChooser = mainUI.getFileChooser();
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showSaveDialog(mainUI) == JFileChooser.APPROVE_OPTION) {
            XSSFWorkbook wb = new XSSFWorkbook();

            // Sheet 1: Individual Outcomes
            Sheet sheet = wb.createSheet("Individual Results");
            TableOutput.individualOutcomes(sheet);

            // Sheet 2: Outcome Tree
            Sheet sheet2 = wb.createSheet("Tree Results");
            TableOutput.treeResults(sheet2);

            // Sheet 3: Success Criteria
            Sheet sheet3 = wb.createSheet("Success Criteria");
            TableOutput.successCriteria(sheet3);

            try {
                File file = fileChooser.getSelectedFile();
                file.createNewFile();
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    wb.write(fileOut);
                    fileOut.close();
                    JOptionPane.showMessageDialog(mainUI, "Results saved successfully.", "Success!", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainUI, "Results could not be saved.", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_continueButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton continueButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
