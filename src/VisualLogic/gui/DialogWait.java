/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de
Copyright (C) 2017  Javier Velsquez javiervelasquez125@gmail.com
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package VisualLogic.gui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

/**
 *
 * @author  Salafia
 */
public class DialogWait extends javax.swing.JFrame 
{
    private final Image image = null;
    
    private final int counter = 0;
    private final int maximum = 100;
    public static boolean stop = true;
    
    /** Creates new form DialogWait */
    public DialogWait()
    {
        initComponents();
        
        this.setAlwaysOnTop(true); // To avoid Myopenlab Freezes if user click main Frame while this window is loading
        this.setAutoRequestFocus(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, (dim.height/2-this.getSize().height/2));
        //Double gapWdbl = dim.width*0.05;
        //int gapW=gapWdbl.intValue();
        //Double gapHdbl = dim.height*0.1;
        //int gapH=gapHdbl.intValue();
        //this.setLocation(dim.width-this.getSize().width - gapW, (dim.height-this.getSize().height)-gapH);
        
        //this.pack();
        //this.setLocationRelativeTo(null);
    }
    
    public void setProgress()
    {       
        //  jProgressBar1.setValue(counter++);
     }
    
    public String textX;
    public void setElementName(String text)
    { 
       /* if (!text.trim().equalsIgnoreCase(""))
        this.textX=text;
       
        label2.setText(textX);*/
     }
    
    public void setMaximum(int max)
    {
      /*maximum=max;
        jProgressBar1.setMaximum(maximum);*/
    }
    
    
  /*  public void pa(Graphics g)
    {
        super.paint(g);
        
       if (image!=null) g.drawImage(image,0,0,null);
                
    }*/
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        label1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        label2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(73, 88, 159));
        setMaximumSize(new java.awt.Dimension(500, 100));
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(73, 88, 159));
        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setMinimumSize(new java.awt.Dimension(400, 100));
        jPanel1.setPreferredSize(new java.awt.Dimension(400, 100));

        label1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label1.setForeground(new java.awt.Color(255, 255, 255));
        label1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Bilder/ajax-loader.gif"))); // NOI18N
        label1.setText("Please wait");
        label1.setAlignmentX(0.5F);

        jProgressBar1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jProgressBar1.setForeground(new java.awt.Color(253, 153, 0));
        jProgressBar1.setPreferredSize(new java.awt.Dimension(380, 23));
        jProgressBar1.setStringPainted(true);

        label2.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 372, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(label2, javax.swing.GroupLayout.DEFAULT_SIZE, 16, Short.MAX_VALUE)
                        .addGap(41, 41, 41))))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitForm(java.awt.event.WindowEvent evt)                          
    {
        System.exit(0);
    }                         
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JProgressBar jProgressBar1;
    public static javax.swing.JLabel label1;
    public javax.swing.JLabel label2;
    // End of variables declaration//GEN-END:variables
    
}