import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class TabelaPodgladuBazyDanych extends TabelaSQL {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    TabelaPodgladuBazyDanych(){
       super(false);
       this.repaint();
    }

    @Override
    protected void updateData() {
        daneTabeli=BazaDanych.getBazaDanych().getData();
    }

}
