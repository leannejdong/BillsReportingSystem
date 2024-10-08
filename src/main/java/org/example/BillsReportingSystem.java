/**
 * --------------------------------------------------------
 * Class: Bills Reporting System
 *
 * @author Mark O'Reilly
 * 
 * Developed: 2016-2017
 *
 * Purpose: Sample Java application for displaying the hypothetical class
 *                 test results for the students of Bill's Geography class.
 *
 * Demonstrating the implementation of: 
 *  - Use of a multi-dimension array 
 *  - the Javax Swing library 
 *  - Action, Key and Window Listeners 
 *  - Text file data management 
 *  - Implementation of Java packages 
 *  - Use of WindowAdapter
 *  - Reading and Writing to a Random Access Files
 *  - Selected modularisation (coupling and cohesion) and OOP concepts
 *
 * ----------------------------------------------------------
 */


package billsreporting;


import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class BillsReporting extends JFrame implements ActionListener, KeyListener
{
    private int totalX = 12;
    private int totalY = 23;
    int xPos = 0;
    int yPos = 0;

    private JTextField[][] fields;
    //private JTextField[][] fields = new JTextField[totalX][totalY]; 
    String[] sortArray;
    //String[] sortArray = new String[(totalY-3)];
    
    private JButton btnClear, btnSave, btnExit, btnSaveTable, btnSort, btnRAF, btnFind;
    private String dataFileName = "BillsReportingSystem.csv";
    private String tableFileName = "BillsReportingTable.csv";
    private String rafFileName = "BillsReportingRAF.csv";

    private String[] headingsAtTheTop = {"Qns", "Q1", "Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8", "Q9", "Q10", "Q11", "Q12", "Q13", "Q14", "Q15", "Q16", "Q17", "Q18", "Q19", "Q20", "Result"};
    private JTextField txtFind;
    SpringLayout springLayout;
    
    
    public static void main(String[] args)
    {
        BillsReporting billsReportingSystem = new BillsReporting();
        billsReportingSystem.run();
    }
    
    private void run()
    {
        getScreenDimensions(dataFileName);
        fields = new JTextField[totalX][totalY];
        sortArray = new String[(totalY-3)];
        
        setBounds(10, 10, xPos, yPos);
        setTitle("Bill's Reporting System");
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        
        displayGUI();
        readDataFile(dataFileName);
        calculateStudentResults();
        calculateStudentAverage();   
        calculateQuestionModes();

        setResizable(true);
        setVisible(true);
    }

    
    //<editor-fold defaultstate="collapsed" desc="Display GUI">    

    private void getScreenDimensions(String fileName)
    {
        try
        {
            int count = 0;
            String line;
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            while ((line = br.readLine()) != null)
            {
                String temp[] = line.split(",");                    
                count++;
                totalX = temp.length + 1;
            }
            totalY = count + 2;
            xPos = totalX * 65 + 50;
            if(xPos < 825) { xPos = 825; }
            yPos = totalY * 22 +120;

            br.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());            
        }
    }
    
    private void displayGUI()
    {
        springLayout = new SpringLayout();
        setLayout(springLayout);
        
        displayTextFields(springLayout);
        displayButtons(springLayout);
        setupTable(springLayout);
    }

	
/*	//Version 1...
    private void displayTextFields(SpringLayout layout)
    {
        for (int y = 0; y < totalY; y++)
        {
            for (int x = 0; x < totalX; x++)     
            {
                xPos = x * 65 + 20;
                yPos = y * 22 + 20;           
				JTextField fields[x][y] = new JTextField(5);
				add(fields[x][y]);  
				fields[x][y].addKeyListener(fields[x][y]);
				layout.putConstraint(SpringLayout.WEST, fields[x][y], xPos, SpringLayout.WEST, this);
				layout.putConstraint(SpringLayout.NORTH, fields[x][y], yPos, SpringLayout.NORTH, this);
            }
        }
    }
*/
	
    private void displayTextFields(SpringLayout layout)
    {
        for (int y = 0; y < totalY; y++)
        {
            for (int x = 0; x < totalX; x++)     
            {
                xPos = x * 65 + 20;
                yPos = y * 22 + 20;           
                fields[x][y] = LibraryComponents.LocateAJTextField(this, this, layout, 5, xPos, yPos);
            }
        }
    }

    private void displayButtons(SpringLayout layout)
    {
        int yPos = totalY * 22 + 40;
        int xTension = 0;
        if (totalX > 12) { xTension = ((totalX - 12) * 65); }
                
        btnClear = LibraryComponents.LocateAJButton(this, this, layout, "Clear", 20, yPos, 80, 25);
        btnSaveTable = LibraryComponents.LocateAJButton(this, this, layout, "Save Table", 100, yPos, 120, 25);
        btnSave = LibraryComponents.LocateAJButton(this, this, layout, "Save", 220, yPos, 80, 25);
        btnSort = LibraryComponents.LocateAJButton(this, this, layout, "Sort", 300, yPos, 80, 25);
        btnFind = LibraryComponents.LocateAJButton(this, this, layout, "Find", 555 + xTension, yPos, 80, 25);
        btnRAF = LibraryComponents.LocateAJButton(this, this, layout, "RAF", 635 + xTension, yPos, 80, 25);
        btnExit = LibraryComponents.LocateAJButton(this, this, layout, "Exit", 715 + xTension, yPos, 80, 25);
        txtFind = LibraryComponents.LocateAJTextField(this, this, layout, 13, 405 + xTension, yPos+4);
        txtFind.addActionListener(this);  
    }  
    
    private void setupTable(SpringLayout layout)
    {
        for (int y = 0; y < totalY; y++)
        {
            for (int x = 0; x < totalX; x++)
            {
                setFieldProperties(x, y, true, 255, 255, 255);
            }
        }
        for (int y = 0; y < totalY; y++)
        {
            setFieldProperties(0, y, false, 220, 220, 255);
            setFieldProperties(totalX-1, y, false, 220, 255, 220);
        }
        for (int x = 0; x < totalX; x++)
        {
            fields[x][0].setText(headingsAtTheTop[x]);
            setFieldProperties(x, 0, false, 220, 220, 255);
            setFieldProperties(x, 1, true, 220, 255, 220);
            setFieldProperties(x, totalY-1, false, 220, 255, 220);
        }
        fields[totalX-1][0].setText("Results");
        fields[0][totalY - 1].setText("Mode");
    } 

    public void setFieldProperties(int x, int y, boolean editable, int r, int g, int b)
    {
        fields[x][y].setEditable(editable);
        fields[x][y].setBackground(new Color(r, g, b));
    }
    

    //</editor-fold>

                
    //<editor-fold defaultstate="collapsed" desc="Action and Key Listeners">    
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        setupTable(springLayout);

        if (e.getSource() == btnClear)
        {
            LibraryComponents.clearJTextFieldArray(fields,2,1,totalX,totalY);
        }
        if (e.getSource() == btnSaveTable)
        {
            saveTableToFile(tableFileName);
        }
        if (e.getSource() == btnSave)
        {
            writeDataFile(dataFileName);
        }
        if (e.getSource() == btnSort)
        {
            sortStudentRecords();
        }
        if (e.getSource() == btnFind  || e.getSource() == txtFind)
        {
            findStudentRecord();
        }
        if (e.getSource() == btnRAF)
        {
            writeRandomAccessFile(rafFileName);
            int requiredEntry = Integer.parseInt(LibraryComponents.checkInteger(txtFind.getText()));
            readRandomAccessFile(rafFileName,requiredEntry);
        }        
        if (e.getSource() == btnExit)
        {
            System.exit(0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e)  {  }
    @Override
    public void keyPressed(KeyEvent e)  {  }
    @Override
    public void keyReleased(KeyEvent e)
    {
        calculateStudentResults();
        calculateStudentAverage();
        calculateQuestionModes();
    }
    
    //</editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="Manage Screen Data">    
  
    
//    MOVED TO:  LIBRARY COMPONENTS...
//
//    public void ClearData(JTextField[][] JTxtFld, int maxX, int maxY)
//    {
//        for (int y = 2; y < maxY; y++)
//        {
//            for (int x = 1; x < maxX; x++)
//            {
//                JTxtFld[x][y].setText("");
//            }
//        }
//    }

    public void calculateStudentResults()
    {
        int total = 0;
        for (int y = 2; y < totalY - 1; y++)
        {
            for (int x = 1; x < totalX - 1; x++)
            {
                if(fields[x][1].getText().equals(fields[x][y].getText())) total++;
            }
            fields[totalX-1][y].setText("" + total);
            total = 0;
        }
    }
       
    public void calculateStudentAverage()
    {
        int total = 0;
        for (int y = 2; y < totalY - 1; y++)
        {
           total = total + Integer.parseInt(fields[totalX-1][y].getText());
        }
        fields[totalX-1][totalY-1].setText("" + String.format("%.2f",((double)total / (double)(totalY-3))));
    }

    
    public void calculateQuestionModes()
    {
        int[] qnCount = new int[4];
        for (int x = 1; x < totalX - 1; x++) 
        {
            for(int i = 0; i < 4; i++) { qnCount[i] = 0; }
            for (int y = 2; y < totalY - 1; y++)
            {
                if(fields[x][y].getText().equals("A")) { qnCount[0]++; } 
                if(fields[x][y].getText().equals("B")) { qnCount[1]++; } 
                if(fields[x][y].getText().equals("C")) { qnCount[2]++; } 
                if(fields[x][y].getText().equals("D")) { qnCount[3]++; } 
            }
            int index = LibraryComponents.getLargestIndex(qnCount);
            
            if(index == 0) { fields[x][totalY-1].setText("A"); }   
            if(index == 1) { fields[x][totalY-1].setText("B"); }   
            if(index == 2) { fields[x][totalY-1].setText("C"); }   
            if(index == 3) { fields[x][totalY-1].setText("D"); }   
        }        
    }

//    MOVED TO:  LIBRARY COMPONENTS...
//
//    public int getLargestIndex(int arr[])
//    {
//        int largestIndex = -1;
//        int largestValue = -1;
//        for (int i = 0; i<arr.length; i++)
//        {
//            if(arr[i] > largestValue)
//            {
//                largestValue = arr[i];
//                largestIndex = i;
//            }
//        }        
//        return largestIndex;       
//    }
//
//    public int getLargestValue(int arr[])
//    {
//        int largestValue = -1;
//        for (int i = 0; i<arr.length; i++)
//        {
//            if(largestValue > arr[i])
//            {
//                largestValue = arr[i];
//            }
//        }        
//        return largestValue;       
//    }
    
/*
    public String checkInteger(String strValue)
    {
        try 
        {
            Integer.parseInt(strValue);
            return strValue;
        }
        catch (Exception e) 
        {
            return "0";
        }
    }

*/
	
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="Find and Sort Code">    
    
    public void findStudentRecord()
    {
        boolean found = false;
        int y = 2;
        String strFind = txtFind.getText();
        
        while(y < totalY-1 && found == false)
        {
           if(fields[0][y].getText().equalsIgnoreCase(strFind))
           {
               found = true;
           }
           y++;
        }
        if (found)
        {
            for (int x = 0; x < totalX; x++)
            {
                fields[x][y-1].setBackground(new Color(255,217,200));
            }
            txtFind.setText(txtFind.getText() + " ...Found.");
        }
        else
        {
            txtFind.setText(txtFind.getText() + " ...Not Found.");
        }
    }

    public void sortStudentRecords()
    {
        copyToSortTable();
        sortTheSortTable();
        displaySortedTable();
    }

    public void copyToSortTable()
    {
        for (int y = 2; y < totalY - 1; y++)
        {
            sortArray[y-2] = "";
            for (int x = 0; x < totalX-1; x++)
            {
                sortArray[y-2] = sortArray[y-2] + fields[x][y].getText() + ",";
            }
            sortArray[y-2] = sortArray[y-2] + fields[totalX - 1][y].getText();
        }      
    }
 
    public void sortTheSortTable()
    {
        Arrays.sort(sortArray);
    }

    public void displaySortedTable()
    {
        for (int y = 2; y < totalY - 1; y++)
        {
            String temp[] = sortArray[y-2].split(",");                    
            for (int x = 0; x < totalX; x++)
            {
                fields[x][y].setText(temp[x]);
            }
        }
    }
           
    //</editor-fold>    

    
    //<editor-fold defaultstate="collapsed" desc="File Management">    

    private void readDataFile(String fileName)
    {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            for (int y = 1; y < totalY - 1; y++)
            {
                String temp[] = br.readLine().split(",");                    
                for (int x = 0; x < totalX - 1; x++)
                {
                    fields[x][y].setText(temp[x]);
                }
            }
            br.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());            
        }
    }

    public void writeDataFile(String fileName)
    {
        try
        {
            //BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
            BufferedWriter outFile = new BufferedWriter(new FileWriter("BillsReportingSystem_NEW.csv"));
            for (int y = 1; y < totalY - 1; y++)
            {
                for (int x = 0; x < totalX - 2; x++)
                {
                    outFile.write(fields[x][y].getText() + ",");
                }
                outFile.write(fields[totalX - 2][y].getText());
                outFile.newLine();
            }
            outFile.close();
            System.out.println("Bills Reporting System data has been saved.");
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void saveTableToFile(String fileName)
    {
        try
        {
            BufferedWriter outFile = new BufferedWriter(new FileWriter(fileName));
            for (int y = 0; y < totalY; y++)
            {
                for (int x = 0; x < totalX - 1; x++)
                {
                    outFile.write(fields[x][y].getText() + "," );               
                }
                outFile.write(fields[totalX - 1][y].getText());
                outFile.newLine();
            }
            outFile.close();
            System.out.println("Bills Reporting System TABLE has been saved.");
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
    }

    //</editor-fold>    

    
    //<editor-fold defaultstate="collapsed" desc="Random Access File">    

    public void writeRandomAccessFile(String fileName)
    {
        try
        {
            String str;
            RandomAccessFile rafFile = new RandomAccessFile(fileName, "rw");
            for (int y = 1; y < totalY - 1; y++)
            {
                str = "";
                for (int x = 0; x < totalX - 1; x++)
                {
                    str = str + fields[x][y].getText();
                }
                rafFile.writeUTF(str);
            }
            rafFile.close();
            System.out.println("Bills Reporting System RAF data has been saved.");
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
    }
    

    private void readRandomAccessFile(String fileName, int index)
    {
        try
        {
            String str = "";
            RandomAccessFile rafFile = new RandomAccessFile(fileName, "rw");
            for(int i = 0; i<index; i++)
            {
                str = rafFile.readUTF();
            }
            System.out.println(str);
            rafFile.close();
        }
        catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());            
        }
    }
    
    
    //</editor-fold>       
    
}

