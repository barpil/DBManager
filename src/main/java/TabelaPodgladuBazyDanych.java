import javax.swing.table.DefaultTableModel;

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
