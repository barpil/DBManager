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
       super();
       PopUpMenuTabeli popUpMenuTabeli = new PopUpMenuTabeli(this);
    }

    @Override
    protected void updateData() {
        super.daneTabeli=BazaDanych.getBazaDanych().getData();
    }


    class PopUpMenuTabeli extends JPopupMenu{
        private final PopUpMenuTabeli thisPopUpMenu = this;
        PopUpMenuTabeli(TabelaPodgladuBazyDanych tabelaPodgladuBazyDanych){

            JMenuItem deleteRows = new JMenuItem("Delete rows");
            deleteRows.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().usunDane(tabelaPodgladuBazyDanych.getSelectedRows());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });


            this.add(deleteRows);

            tabelaPodgladuBazyDanych.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger() && tabelaPodgladuBazyDanych.getSelectedRows().length>0){
                        thisPopUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }


            });
        }
    }
}
