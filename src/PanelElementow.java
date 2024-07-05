package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelElementow extends JScrollPane{
    private static PanelElementow panelElementow;
    private JTable jTable;
    DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private PanelElementow() {
        this.setPreferredSize(new Dimension(350, 500));
        this.setBackground(Color.GREEN);
        utworzElementy();
    }

    private void utworzElementy() {
        model.setDataVector(BazaDanych.getBazaDanych().getData(), new String[]{"id", "nazwa"});
        jTable = new JTable(model);
        this.setViewportView(jTable);
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    }

    public void updateModel() {
        model.setDataVector(BazaDanych.getBazaDanych().getData(), new String[]{"id","nazwa"} );
        jTable.setModel(model);
    }

    public static PanelElementow getPanelElementow() {
        if(panelElementow==null){
            panelElementow= new PanelElementow();
        }
        return panelElementow;
    }

}
