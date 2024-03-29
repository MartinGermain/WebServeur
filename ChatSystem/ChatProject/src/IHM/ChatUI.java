/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package IHM;

import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author martingermain
 */
public class ChatUI extends javax.swing.JFrame {

    /**
     * Creates new form ChatUI
     */
    public ChatUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Fond = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jTextFieldSend = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaChat = new javax.swing.JTextArea();
        jLabelHeader = new javax.swing.JLabel();
        jButtonLogOut = new javax.swing.JButton();
        jButtonSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Fond.setBackground(new java.awt.Color(255, 255, 255));
        Fond.setPreferredSize(new java.awt.Dimension(700, 500));

        jSeparator1.setForeground(new java.awt.Color(70, 180, 180));

        jTextFieldSend.setFont(new java.awt.Font("PingFang TC", 0, 16)); // NOI18N
        jTextFieldSend.setForeground(new java.awt.Color(70, 180, 180));
        jTextFieldSend.setBorder(null);
        jTextFieldSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldSendActionPerformed(evt);
            }
        });

        jScrollPane1.setBorder(null);
        jScrollPane1.setForeground(new java.awt.Color(70, 180, 180));
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setFont(new java.awt.Font("PingFang TC", 0, 16)); // NOI18N
        jScrollPane1.setHorizontalScrollBar(null);

        jTextAreaChat.setEditable(false);
        jTextAreaChat.setColumns(20);
        jTextAreaChat.setForeground(new java.awt.Color(70, 180, 180));
        jTextAreaChat.setRows(5);
        jTextAreaChat.setCaretColor(new java.awt.Color(70, 180, 180));
        jScrollPane1.setViewportView(jTextAreaChat);

        jLabelHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Header.png"))); // NOI18N

        jButtonLogOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/logout.png"))); // NOI18N
        jButtonLogOut.setBorder(null);
        jButtonLogOut.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonLogOutMouseClicked(evt);
            }
        });

        jButtonSend.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Valider.png"))); // NOI18N
        jButtonSend.setBorder(null);
        jButtonSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSendMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout FondLayout = new javax.swing.GroupLayout(Fond);
        Fond.setLayout(FondLayout);
        FondLayout.setHorizontalGroup(
            FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(FondLayout.createSequentialGroup()
                .addComponent(jLabelHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(jButtonLogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
            .addGroup(FondLayout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                    .addGroup(FondLayout.createSequentialGroup()
                        .addGroup(FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSeparator1)
                            .addComponent(jTextFieldSend, javax.swing.GroupLayout.DEFAULT_SIZE, 511, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        FondLayout.setVerticalGroup(
            FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, FondLayout.createSequentialGroup()
                .addGroup(FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelHeader)
                    .addGroup(FondLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButtonLogOut, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(FondLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jTextFieldSend, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(418, 418, 418))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Fond, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Fond, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSendActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSendActionPerformed

    private void jButtonLogOutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLogOutMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLogOutMouseClicked

    private void jButtonSendMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSendMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSendMouseClicked
    
    /**
     * @return JTextField where the Client can write a message
     */
    public JTextField getMsg() {
        return jTextFieldSend;
    }
    
     /**
     * @return JTextArea where the all the messages are displayed
     */
    public JTextArea getArea() {
        return jTextAreaChat;
    }
    
    /**
     * @return JButton Exit Button
     */ 
    public JButton getExitbutton() {
        return jButtonLogOut;
    }
    
    /**
     * @return JButton Send Button
     */
     public JButton getbuttonSend() {
        return jButtonSend;
    }
     
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChatUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ChatUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Fond;
    private javax.swing.JButton jButtonLogOut;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JLabel jLabelHeader;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaChat;
    private javax.swing.JTextField jTextFieldSend;
    // End of variables declaration//GEN-END:variables
}
