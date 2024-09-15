import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;



public class OknoEdycjiTabeli extends JDialog {
    TabelaEdycji tabelaPodgladu;
    OknoEdycjiTabeli(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);
        BazaDanych.getBazaDanych().zaktualizujBaze();
        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!tabelaPodgladu.dodawaneDane.isEmpty() || SQLThreadQueue.liczbaPozostalychWatkow()!=0) {
                    int potwierdzenieZamkniecia = JOptionPane.showConfirmDialog(
                            OknoEdycjiTabeli.this,
                            "Do you want to discard the changes?",
                            "Unsaved changes",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

                    if (potwierdzenieZamkniecia == JOptionPane.YES_OPTION) {
                        SQLThreadQueue.resetQueue();
                        OknoEdycjiTabeli.this.dispose();
                    }
                }else{
                    OknoEdycjiTabeli.this.dispose();
                }
            }
        });

        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BoxLayout(panelGlowny, BoxLayout.Y_AXIS));

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.setLayout(new BoxLayout(topButtonPanel, BoxLayout.X_AXIS));
        JLabel showNewOnlyLabel = new JLabel("New only");
        JCheckBox showNewOnlyCheckbox = new JCheckBox();
        ItemListener myItemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                tabelaPodgladu.setOnlyNewOption(true);
            } else {
                tabelaPodgladu.setOnlyNewOption(false);
            }
            tabelaPodgladu.updateModel();
        };
        showNewOnlyCheckbox.addItemListener(myItemListener);

        topButtonPanel.add(Box.createHorizontalGlue());
        topButtonPanel.add(showNewOnlyLabel);
        topButtonPanel.add(showNewOnlyCheckbox);

        panelGlowny.add(topButtonPanel);


        JScrollPane scrollPane = new JScrollPane();
        tabelaPodgladu = new TabelaEdycji();
        scrollPane.setViewportView(tabelaPodgladu);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panelGlowny.add(scrollPane);

        JPanel panelDodawaniaWierszy = new JPanel();
        panelDodawaniaWierszy.setLayout(new BoxLayout(panelDodawaniaWierszy, BoxLayout.X_AXIS));
        panelDodawaniaWierszy.setPreferredSize(new Dimension(400, 50));

        class TabelaDodawaniaWierszy extends TabelaSQL {

            TabelaDodawaniaWierszy() {
                super(true);
                this.getTableHeader().setReorderingAllowed(false);
                this.setRowSelectionAllowed(false);
                this.setFocusable(true);
            }

            @Override
            protected void updateData() {
                daneTabeli = new Object[1][nazwyKolumnTabeli.length];
                for (int i = 0; i < nazwyKolumnTabeli.length; i++) {
                    daneTabeli[0][i] = null;
                }
            }


            private void wyczyscWiersz() {
                updateModel();
            }

            class ZaznaczaczNieuzupelnionychPol extends DefaultTableCellRenderer
            {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
                {
                    Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setBackground(Color.red);
                    return c;
                }
            }

            private void dodajWiersz() {
                Row dodawanyWiersz = new Row(InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn());
                if (this.isEditing()) {
                    this.getCellEditor().stopCellEditing();
                }

                for (int i = 0; i < InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn(); i++) {
                    dodawanyWiersz.addPole(InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY), this.getModel().getValueAt(0, i));
                }

                List<Integer> listaNiewypelnionych = new LinkedList<>();
                for(int i=0;i<dodawanyWiersz.listaPol.size();i++){
                    if(InformacjeOBazie.getActiveTableInfo().getInformacjaOKolumnie(i, InformacjeOTabeli.InformacjeKolumny.IS_NULLABLE).equals("NO") && (dodawanyWiersz.getPole(i).getWartosc()==null || dodawanyWiersz.getPole(i).getWartosc().equals(""))){
                        listaNiewypelnionych.add(i);
                    }
                }
                if (listaNiewypelnionych.isEmpty()) {
                    tabelaPodgladu.dodajWiersz(dodawanyWiersz);
                }
                else{
                    String wiadomosc="Following fields must be filled:";
                    for(Integer i: listaNiewypelnionych){
                        wiadomosc+="\n"+dodawanyWiersz.getPole(i).getNazwaKolumny();
                        this.getColumnModel().getColumn(i).setCellRenderer(new ZaznaczaczNieuzupelnionychPol());
                    }
                    this.repaint();
                    JOptionPane.showMessageDialog(OknoEdycjiTabeli.this, wiadomosc, "Unfilled fields", JOptionPane.INFORMATION_MESSAGE);

                }



            }


        }


        JScrollPane dolnyScrollPane = new JScrollPane();
        dolnyScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        dolnyScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        TabelaDodawaniaWierszy tabelaDodawaniaWierszy = new TabelaDodawaniaWierszy();
        dolnyScrollPane.setViewportView(tabelaDodawaniaWierszy);
        dolnyScrollPane.setPreferredSize(new Dimension(400, 50));

        panelDodawaniaWierszy.add(dolnyScrollPane);


        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new BoxLayout(panelPrzyciskow, BoxLayout.Y_AXIS));

        Dimension wielkoscPrzycisku = new Dimension(40,25);

        JButton wyczyscWierszBtn = new JButton("Clear");
        wyczyscWierszBtn.setPreferredSize(wielkoscPrzycisku);
        wyczyscWierszBtn.setMinimumSize(wielkoscPrzycisku);
        wyczyscWierszBtn.setMaximumSize(wielkoscPrzycisku);
        wyczyscWierszBtn.setMargin(new Insets(1,1,1,1));
        wyczyscWierszBtn.addActionListener(e -> {
            tabelaDodawaniaWierszy.wyczyscWiersz();
        });

        JButton dodajWierszBtn = new JButton("+");
        dodajWierszBtn.setPreferredSize(wielkoscPrzycisku);
        dodajWierszBtn.setMinimumSize(wielkoscPrzycisku);
        dodajWierszBtn.setMaximumSize(wielkoscPrzycisku);
        dodajWierszBtn.setMargin(new Insets(1,1,1,1));
        dodajWierszBtn.addActionListener(e -> {
            tabelaDodawaniaWierszy.dodajWiersz();
            tabelaDodawaniaWierszy.wyczyscWiersz();
        });

        InputMap inputMap = tabelaDodawaniaWierszy.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = tabelaDodawaniaWierszy.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control ENTER");
        inputMap.put(keyStroke, "dodajWierszButton");

        actionMap.put("dodajWierszButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajWierszBtn.doClick();
            }
        });

        panelPrzyciskow.add(dodajWierszBtn);
        panelPrzyciskow.add(wyczyscWierszBtn);


        panelDodawaniaWierszy.add(panelPrzyciskow);

        panelGlowny.add(panelDodawaniaWierszy);

        JPanel panelPrzyciskowOkna = new JPanel();
        panelPrzyciskowOkna.setLayout(new BoxLayout(panelPrzyciskowOkna,BoxLayout.X_AXIS));
        panelPrzyciskowOkna.setPreferredSize(new Dimension(400,30));
        panelPrzyciskowOkna.add(Box.createHorizontalGlue());

        JButton applyButton = new JButton("Apply");



        applyButton.setPreferredSize(new Dimension(60,20));
        applyButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        applyButton.addActionListener(e -> {
            BazaDanych.getBazaDanych().dodajDane(tabelaPodgladu.getData());
            SQLThreadQueue.rozpocznijWykonywanie();
            while(SQLThreadQueue.liczbaPozostalychWatkow()!=0){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(SQLThreadQueue.getErrors().isEmpty()){
                this.dispose();
            } else{
                PanelSterowania.getPanelSterowania().getProgressBar().setVisible(false);
                String wiadomoscKomunikatu;
                JLabel komunikat;
                SQLException sqlException = (SQLException) SQLThreadQueue.getErrors().getFirst();
                switch (sqlException.getErrorCode()){
                    case 1062:
                        wiadomoscKomunikatu = "Primary key duplicate error.\nPlease remove duplicate data.<br> Error message:<br><font color='red'>"+sqlException.getMessage()+"</font>";
                        komunikat = new JLabel("<html><body style='width: 300px'>" + wiadomoscKomunikatu + "</body></html>");
                        JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), komunikat,"Duplicate data error",JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        wiadomoscKomunikatu = "An unexpected SQL error has occured.\nPlease check the data correctness or try again later.<br> Error message:<br><font color='red'>"+sqlException.getMessage()+"</font>";
                        komunikat = new JLabel("<html><body style='width: 300px'>" + wiadomoscKomunikatu + "</body></html>");
                        JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), komunikat,"Data insert error",JOptionPane.ERROR_MESSAGE);
                }
            }




        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60,20));
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cancelButton.addActionListener(e -> {
            SQLThreadQueue.resetQueue();
            this.dispose();
        });
        panelPrzyciskowOkna.add(applyButton);
        panelPrzyciskowOkna.add(Box.createRigidArea(new Dimension(10,0)));
        panelPrzyciskowOkna.add(cancelButton);
        panelPrzyciskowOkna.add(Box.createRigidArea(new Dimension(5,0)));

        panelGlowny.add(panelPrzyciskowOkna);

        this.add(panelGlowny);
    }

}



class TabelaEdycji extends TabelaSQL {
    static DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    List<Row> dodawaneDane;
    List<Row> daneBazy;
    private boolean onlyNewOption = false;

    TabelaEdycji(){
        super(false);
        this.getTableHeader().setReorderingAllowed(false);
        daneBazy=new LinkedList<>();
        daneBazy.addAll(BazaDanych.getBazaDanych().getDane());
        updateModel();
        PopUpMenuTabeliEdycji popUpMenuTabeli = new PopUpMenuTabeliEdycji(this);
    }

    @Override
    protected void updateData() {
        if(daneBazy==null){
            return;
        }
        if(dodawaneDane==null){
            dodawaneDane=new LinkedList<>();
        }
        Object[][] dane;
        if(onlyNewOption){
            dane = new Object[dodawaneDane.size()][InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn()];
            for(int j=0;j< dodawaneDane.size();j++){
                for(int k=0;k<InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();k++){
                    dane[j][k]=dodawaneDane.get(j).getPole(k).getWartosc();
                }
            }
        }
        else{
            dane = new Object[dodawaneDane.size()+daneBazy.size()][InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn()];
            for(int j=0;j<daneBazy.size();j++){
                for(int k=0;k<InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();k++){
                    dane[j][k]=daneBazy.get(j).getPole(k).getWartosc();
                }
            }
            for(int j=0;j< dodawaneDane.size();j++){
                for(int k=0;k<InformacjeOBazie.getActiveTableInfo().getLiczbaKolumn();k++){
                    dane[j+daneBazy.size()][k]=dodawaneDane.get(j).getPole(k).getWartosc();
                }
            }
        }

        super.daneTabeli=dane;
    }

    protected void setOnlyNewOption(boolean b){
        this.onlyNewOption=b;
    }


    public void dodajWiersz(Row wiersz){
        dodawaneDane.add(wiersz);
        super.updateModel();
    }

    public void usunWiersz(int[] numeryWierszy){
        for(int numerWiersza: numeryWierszy){
            System.out.println("Usuwany wiersz: "+numerWiersza);
            if(onlyNewOption){
                dodawaneDane.remove(numerWiersza);
            } else{
                if(numerWiersza<daneBazy.size()){
                    daneBazy.remove(numerWiersza);

                }else{
                    dodawaneDane.remove(daneTabeli.length-numerWiersza-1);
                }
            }

        }
        super.updateModel();


    }


public List<Row> getData(){
        return dodawaneDane;
}





    class PopUpMenuTabeliEdycji extends JPopupMenu{
        private final PopUpMenuTabeliEdycji thisPopUpMenu = this;
        PopUpMenuTabeliEdycji(TabelaEdycji tabela){

            JMenuItem deleteRows = new JMenuItem("Delete rows");
            deleteRows.addActionListener(e -> {

                int[] selectedRows = tabela.getSelectedRows();
                List<Integer> listaDoUsunieciaZBazy = new LinkedList<>();
                List<Integer> listaDoUsunieciaZNowych = new LinkedList<>();
                for(int row: selectedRows){
                    if(row<daneBazy.size() && !onlyNewOption){
                        listaDoUsunieciaZBazy.add(row);
                    }else{
                        listaDoUsunieciaZNowych.add(row);
                    }
                }
                int[] t = new int[listaDoUsunieciaZNowych.size()];

                for(int i=0;i<listaDoUsunieciaZNowych.size();i++){
                    t[i]=listaDoUsunieciaZNowych.get(i);
                }
                if (t.length!=0) {
                    usunWiersz(t);
                }
                t=new int[listaDoUsunieciaZBazy.size()];
                for(int i=0;i<listaDoUsunieciaZBazy.size();i++){
                    t[i]=listaDoUsunieciaZBazy.get(i);
                }


                System.out.println();
                if (t.length!=0) {
                    usunWiersz(t);

                    BazaDanych.getBazaDanych().usunDane(t);
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




