import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class TabelaEdycji extends TabelaSQL {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    List<Row> dodawaneDane;

    TabelaEdycji(){
        super(false);

        updateModel();
        PopUpMenuTabeliEdycji popUpMenuTabeli = new PopUpMenuTabeliEdycji(this);
    }

    @Override
    protected void updateData() {
        if(dodawaneDane==null){
            dodawaneDane=new LinkedList<>();
        }

        Object[][] dane = new Object[dodawaneDane.size()][InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn()];
        for(int j=0;j< dodawaneDane.size();j++){
            for(int k=0;k<InformacjeOTabeli.informacjeOTabeli.getLiczbaKolumn();k++){
                dane[j][k]=dodawaneDane.get(j).getPole(k).getWartosc();

            }

        }



        super.daneTabeli=dane;
    }



    public void dodajWiersz(Row wiersz){
        dodawaneDane.add(wiersz);
        super.updateModel();
    }




    class PopUpMenuTabeliEdycji extends JPopupMenu{
        private final PopUpMenuTabeliEdycji thisPopUpMenu = this;
        PopUpMenuTabeliEdycji(JTable tabela){

            JMenuItem deleteRows = new JMenuItem("Delete rows");
            deleteRows.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().usunDane(tabela.getSelectedRows());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });


            this.add(deleteRows);

            tabela.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.isPopupTrigger() && tabela.getSelectedRows().length>0){
                        thisPopUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }


            });
        }
    }
}
