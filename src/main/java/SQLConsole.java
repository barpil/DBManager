import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class SQLConsole extends JDialog {
    private static final Logger log = LoggerFactory.getLogger(SQLConsole.class);
    private TabelaWyswietlaniaWynikow tabelaWyswietlaniaWynikow;
    SQLConsole(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        JPanel panelOkna = new JPanel();
        panelOkna.setLayout(new BoxLayout(panelOkna, BoxLayout.Y_AXIS));
        JPanel panelSterowania = new JPanel();
        panelSterowania.setLayout(new BoxLayout(panelSterowania, BoxLayout.X_AXIS));

        JPanel panelElementow = new JPanel();
        panelElementow.setLayout(new BoxLayout(panelElementow, BoxLayout.X_AXIS));

        JPanel panelWyswietlaniaWynikow = new JPanel();
        panelWyswietlaniaWynikow.setPreferredSize(new Dimension(0, 0));

        JScrollPane scrollPaneTabeli = new JScrollPane();

        tabelaWyswietlaniaWynikow = new TabelaWyswietlaniaWynikow(false);
        tabelaWyswietlaniaWynikow.setVisible(false);
        tabelaWyswietlaniaWynikow.setPreferredScrollableViewportSize(new Dimension(400, 400));
        tabelaWyswietlaniaWynikow.setFillsViewportHeight(true);
        tabelaWyswietlaniaWynikow.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        scrollPaneTabeli.setViewportView(tabelaWyswietlaniaWynikow);
        scrollPaneTabeli.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneTabeli.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panelWyswietlaniaWynikow.add(scrollPaneTabeli);

        JLabel showQueryResultLabel = new JLabel("Show result");
        JCheckBox showQueryResultCB = new JCheckBox();

        ItemListener myItemListener = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                panelWyswietlaniaWynikow.setPreferredSize(new Dimension(400, 400));
                tabelaWyswietlaniaWynikow.setVisible(true);
                tabelaWyswietlaniaWynikow.updateModel();
                tabelaWyswietlaniaWynikow.revalidate();
                tabelaWyswietlaniaWynikow.repaint();
                this.pack();
            } else {
                panelWyswietlaniaWynikow.setPreferredSize(new Dimension(0, 0));
                tabelaWyswietlaniaWynikow.setVisible(false);
                this.revalidate();
                tabelaWyswietlaniaWynikow.repaint();
                this.pack();
            }
        };
        showQueryResultCB.addItemListener(myItemListener);

        panelSterowania.add(Box.createHorizontalGlue());
        panelSterowania.add(showQueryResultLabel);
        panelSterowania.add(showQueryResultCB);

        JPanel panelPolecen = new JPanel();
        panelPolecen.setLayout(new BorderLayout());
        panelPolecen.setPreferredSize(new Dimension(400, 350));

        JTextArea textArea = new JTextArea("Insert SQL command");
        textArea.addMouseListener(new MouseAdapter() {
            boolean czyJuzKlikniete = false;

            @Override
            public void mousePressed(MouseEvent e) {
                if (!czyJuzKlikniete) {
                    textArea.setText("");
                    czyJuzKlikniete = true;
                }
            }
        });

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panelPolecen.add(scrollPane, BorderLayout.CENTER);

        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(60, 20));
        runButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        runButton.addActionListener(e -> {
            runCommand(textArea.getText());
            SQLThreadQueue.rozpocznijWykonywanie();
        });

        InputMap inputMap = textArea.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = textArea.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control ENTER");
        inputMap.put(keyStroke, "runCommandKey");

        actionMap.put("runCommandKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runButton.doClick();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(60, 20));
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cancelButton.addActionListener(e -> this.dispose());

        panelPrzyciskow.add(runButton);
        panelPrzyciskow.add(cancelButton);

        panelPolecen.add(panelPrzyciskow, BorderLayout.SOUTH);

        panelElementow.add(panelPolecen);

        panelWyswietlaniaWynikow.setBackground(Color.BLACK);
        panelElementow.add(panelWyswietlaniaWynikow);

        panelOkna.add(panelSterowania);
        panelOkna.add(panelElementow);

        this.add(panelOkna);
    }

    private void runCommand(String textCommand) {
        BazaDanych.getBazaDanych().customSQLCommand(this, textCommand);
    }

    public static void poinformujOBledzie(SQLException e){
        String wiadomoscKomunikatu = "A SQL error occured.\nPlease check the syntax and correctness of the command <br> Error message:<br><font color='red'>"+e.getMessage()+"</font>";
        JLabel komunikat = new JLabel("<html><body style='width: 300px'>" + wiadomoscKomunikatu + "</body></html>");
        JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), komunikat, "SQL error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isQuery(){
        return tabelaWyswietlaniaWynikow.isVisible();
    }

    public void passResult(ResultSet resultSet){
        tabelaWyswietlaniaWynikow.resultSet=resultSet;

        tabelaWyswietlaniaWynikow.updateModel();
    }

}

class TabelaWyswietlaniaWynikow extends TabelaSQL{
    private final Logger log = LoggerFactory.getLogger(TabelaWyswietlaniaWynikow.class);
    ResultSet resultSet=null;
    TabelaWyswietlaniaWynikow(boolean isCellEditable) {
        super(isCellEditable);
    }


    @Override
    protected void updateColumnNames(){
        if (resultSet!=null&& isVisible()) {
            int liczbaKolumn = 0;
            try {
                liczbaKolumn = resultSet.getMetaData().getColumnCount();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String[] nazwyKolumn = new String[liczbaKolumn];

            try {
                for(int i=0; i<resultSet.getMetaData().getColumnCount();i++){
                    nazwyKolumn[i]= resultSet.getMetaData().getColumnName(i+1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            nazwyKolumnTabeli = nazwyKolumn;
        }
    }

    @Override
    protected void updateData() {
        if(isVisible()&&resultSet!=null){
            daneTabeli= przygotujDaneTabeli();
        }
    }

    private Object[][] przygotujDaneTabeli() {
        Object[][] wynik = null;
        try {
            List<Row> listaDanych = new LinkedList<>();
            Row dodawanyRzad;
            int liczbaKolumn = resultSet.getMetaData().getColumnCount();

            while (resultSet.next()) {
                dodawanyRzad = new Row(liczbaKolumn);


                for (int i = 0; i < liczbaKolumn; i++) {
                    String nazwaKolumny = resultSet.getMetaData().getColumnName(i+1);
                    switch (resultSet.getMetaData().getColumnTypeName(i+1).toLowerCase()) {
                        case "int":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getInt(nazwaKolumny));
                            break;
                        case "varchar", "nvarchar":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getString(nazwaKolumny));
                            break;
                        case "date":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getDate(nazwaKolumny));
                            break;
                        case "decimal":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getBigDecimal(nazwaKolumny));
                            break;
                        case "tinyint":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getShort(nazwaKolumny));
                            break;
                        case "boolean":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getBoolean(nazwaKolumny));
                            break;
                        case "time":
                            dodawanyRzad.addPole(nazwaKolumny, resultSet.getTime(nazwaKolumny));
                            break;
                        default:
                            dodawanyRzad.addPole(nazwaKolumny + " (type error)", null);
                            log.error("Unknown data type encountered while trying to add data to table: {}", resultSet.getMetaData().getColumnTypeName(i+1).toLowerCase());
                            break;
                    }

                }
                listaDanych.add(dodawanyRzad);


            }

            wynik= new Object[listaDanych.size()][resultSet.getMetaData().getColumnCount()];
            for(int j=0;j< listaDanych.size();j++){
                for(int k=0;k<liczbaKolumn;k++){
                    wynik[j][k] = listaDanych.get(j).getPole(k).getWartosc();
                }
            }
        } catch (SQLException e) {
            log.error("Failed to prepare table data");
        }
        resultSet=null;
        return wynik;
    }

}
