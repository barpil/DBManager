import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelElementow extends JScrollPane{
    private static final Logger log = LoggerFactory.getLogger(PanelElementow.class);
    private static PanelElementow panelElementow;
    private static TabelaPodgladuBazyDanych tabelaPodgladuBazyDanych;
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private PanelElementow() {
        this.setPreferredSize(new Dimension(500, 500));
        if(BazaDanych.getBazaDanych()!=null){
            zaladujTabele();
            panelElementow.setViewportView(tabelaPodgladuBazyDanych);
            panelElementow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        }



    }


    public static void zaladujTabele(){
        tabelaPodgladuBazyDanych = new TabelaPodgladuBazyDanych();
        panelElementow.setViewportView(tabelaPodgladuBazyDanych);
        panelElementow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        log.debug("Data table has been loaded");

    }


    public static PanelElementow getPanelElementow() {
        if(panelElementow==null){
            panelElementow= new PanelElementow();
        }
        return panelElementow;
    }

}
