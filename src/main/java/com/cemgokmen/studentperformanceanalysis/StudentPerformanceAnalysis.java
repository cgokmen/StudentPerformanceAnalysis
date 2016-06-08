/*
 * StudentPerformanceAnalysis 2.2
 * http://gh.cemgokmen.com/studentperformanceanalysis
 *
 * Copyright 2016 Cem Gökmen
 * Released under the MIT license
 * https://bitbucket.org/sultanskyman/studentperformanceanalysis
 */
package com.cemgokmen.studentperformanceanalysis;

import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 *
 * @author funstein
 */
public class StudentPerformanceAnalysis {

    public static double quantize(double value) {
      double grade = value * 100;
      double result = value;

      if (grade < 19) {
        result = 0.2;
      } else if (grade < 39) {
        result = 0.4;
      } else if (grade < 59) {
        result = 0.6;
      } else if (grade < 79) {
        result = 0.8;
      } else {
        result = 1.0;
      }

      return result;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
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
