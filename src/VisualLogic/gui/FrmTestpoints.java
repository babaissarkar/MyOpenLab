/*
MyOpenLab by Carmelo Salafia www.myopenlab.de
Copyright (C) 2004  Carmelo Salafia cswi@gmx.de

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

import VisualLogic.Basis;
import VisualLogic.Element;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author  Carmelo
 */
public class FrmTestpoints extends javax.swing.JFrame
{
    private DefaultTableModel model = new DefaultTableModel();
    private int[] colCount=new int[100];
    private Basis basis;
    private boolean gesperrt=false;
    /** Creates new form frmTestpoints */
    public FrmTestpoints(Basis basis)
    {
        initComponents();
        jTable1.setModel(model);
        
        setVisible(false);
        
        this.basis=basis;
        
        
        //setIconImage(basis.getFrameMain().iconImage);
        
        javax.swing.ImageIcon icon =new javax.swing.ImageIcon(getClass().getResource("/Assets/Bilder/testPointWhite.png"));
        setIconImage(icon.getImage());
        
        
    }
    
    // Returns the Columns Index
    // else -1
    private int columnExist(String columnTitle)
    {
        for (int i=0;i<model.getColumnCount();i++)
        {
            if (model.getColumnName(i).equals(columnTitle))
            {
                return i;
            }
        }
        return -1;
    }
    
    private void appendColumn(String columnTitle)
    {
        model.addColumn(columnTitle);
    }
    
    private void addValueToColumn(int columnIndex, String value)
    {
        // Row Aufbauen!
        String[] row=new String[model.getColumnCount()];
        for (int i=0;i<model.getColumnCount();i++)
        {
            if (i==columnIndex)
            {
                row[i]=value;
            }
            else
            {
                int rowCount=model.getRowCount();
                if (rowCount==0)
                {
                    row[i]="";
                }
                else
                {
                    row[i]=""+model.getValueAt(rowCount-1,i);
                }
                
            }
        }
        model.addRow(row);
        jLabel2.setText(""+model.getRowCount());
        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                jScrollPane1.getViewport().scrollRectToVisible( jTable1.getCellRect(jTable1.getRowCount(), 0, true ) );
            }
        });
        
    }
    
    private int internalC=0;
    private int refreshC=0;
    
    public void process()
    {
        /*if (tpNodes==null) return;
        if (tpNodes.length==0) return;
        if (dontRefresh) return;
        if (internalC>=abtastFreq)
        {
            internalC=0;
            next();
        }
        
        if (refreshC>=refreshFreq)
        {
            refreshC=0;
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    myGraph1.graph.init();
                    myGraph1.updateUI();
                }
            });
            
        }
        refreshC++;
        internalC++;*/
    }
    
    
    public void clear()
    {
        model = new DefaultTableModel();
        
        for (int i=0;i<colCount.length;i++)
        {
            colCount[i]=0;
        }
        
        jTable1.setModel(model);
    }
    
    public void init()
    {
        clear();
        Element[] tpNodes = basis.getCircuitBasis().getAllTestpointElements();
        
        String columnTitle;
        for (int i=0;i<tpNodes.length;i++)
        {
            columnTitle=tpNodes[i].jGetCaption();
            
            int colIndex=columnExist(columnTitle);
            if (colIndex==-1)
            {
                appendColumn(columnTitle);
            }
        }
    }
    
    public void addValue(String columnTitle, String value)
    {
        if (gesperrt==false)
        {
            int colIndex=columnExist(columnTitle);
            if (colIndex>-1)
            {
                addValueToColumn(colIndex,value);
            }
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("VisualLogic/NOI18N/FrmTestpoints"); // NOI18N
        setTitle(bundle.getString("Testpoint_Window")); // NOI18N
        setAlwaysOnTop(true);
        setLocationByPlatform(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText(bundle.getString("Reset")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jButton1)
        );

        jLabel1.setText(bundle.getString("Row_count_=_")); // NOI18N

        jLabel2.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(272, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jLabel2))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        setSize(new java.awt.Dimension(374, 316));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
      gesperrt=true;
      while(model.getRowCount()>0)
      {
        model.removeRow(0);
      }
      gesperrt=false;
    }//GEN-LAST:event_jButton1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    
}
