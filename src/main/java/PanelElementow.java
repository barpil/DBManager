import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelElementow extends JScrollPane{
    private static PanelElementow panelElementow;
    private static Tabela tabela;
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private PanelElementow() {
        this.setPreferredSize(new Dimension(350, 500));
        this.setBackground(Color.GREEN);
        utworzElementy();
        if(BazaDanych.getBazaDanych()!=null){
            zaladujTabele();
            panelElementow.setViewportView(tabela);
            panelElementow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        }



    }

    private void utworzElementy() {

    }

    public static void zaladujTabele(){
        tabela = new Tabela();
        panelElementow.setViewportView(tabela);
        panelElementow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

    }


    public static PanelElementow getPanelElementow() {
        if(panelElementow==null){
            panelElementow= new PanelElementow();
        }
        return panelElementow;
    }

}
