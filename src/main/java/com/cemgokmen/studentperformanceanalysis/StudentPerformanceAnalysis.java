/*
 * StudentPerformanceAnalysis 1.0
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
 */
package com.cemgokmen.studentperformanceanalysis;

import javax.swing.JPanel;

public class StudentPerformanceAnalysis  {
    public static void main( String[] args ) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainUI frame = new MainUI();
                JPanel panel = new InitialPanel(frame);
                frame.setPanel(panel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
