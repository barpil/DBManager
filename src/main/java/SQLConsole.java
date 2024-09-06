import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SQLConsole extends JDialog {
    SQLConsole(Frame frame, String nazwaOkna, boolean modal) {
        super(frame, nazwaOkna, modal);
        this.setSize(new Dimension(400, 400));
        this.setResizable(true);

        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty() {
        JPanel panelGlowny = new JPanel();
        panelGlowny.setLayout(new BorderLayout());

        // Panel tekstowy z JTextArea
        JTextArea textArea = new JTextArea(new String("Insert SQL command"));
        textArea.addMouseListener(new MouseAdapter() {
            boolean czyJuzKlikniete = false;
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if(!czyJuzKlikniete){
                    textArea.setText("");
                    czyJuzKlikniete=true;
                }
            }
        });
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        panelGlowny.add(scrollPane, BorderLayout.CENTER);

        JPanel panelPrzyciskow = new JPanel();
        panelPrzyciskow.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton runButton = new JButton("Run");
        runButton.setPreferredSize(new Dimension(60, 20));
        runButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        runButton.addActionListener(e -> {
            runCommand(textArea.getText());
            BazaDanych.getBazaDanych().getSqlThreadQueue().rozpocznijWykonywanie();
        });
        InputMap inputMap = runButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = runButton.getActionMap();
        KeyStroke keyStroke = KeyStroke.getKeyStroke("control R");
        inputMap.put(keyStroke, "runButton");

        actionMap.put("runButton", new AbstractAction() {
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

        panelGlowny.add(panelPrzyciskow, BorderLayout.SOUTH);

        this.add(panelGlowny);
    }

    private void runCommand(String textCommand) {
        BazaDanych.getBazaDanych().customSQLCommand(this, textCommand);
    }

    public static void poinformujOBledzie(){
        JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), "A SQL error occured.\nPlease check the syntax and correctness of the command", "SQL error", JOptionPane.ERROR_MESSAGE);
    }

}
