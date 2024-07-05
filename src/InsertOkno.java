package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

public class InsertOkno extends JTextField {
    private final String DEFAULT_ACTION="addAction";
    private String lastAction=DEFAULT_ACTION;

    InsertOkno() {
        this.setMinimumSize(new Dimension(150, 50));
        this.setPreferredSize(new Dimension(150, 50));
        this.setMaximumSize(new Dimension(150, 50));
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.setFont(new Font("Arial", Font.PLAIN, 12));
        this.setForeground(Color.gray);
        this.setText("Podaj nazwe uzytkownika");
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (InsertOkno.this.getForeground() == Color.gray) {
                    InsertOkno.this.setText("");
                    InsertOkno.this.setForeground(Color.black);
                }

            }
        });
        this.addActionListener((e -> {
            switch (lastAction){
                case "addAction":
                    addAction();
                    break;
                case "delActionId":
                    delAction("id");
                    break;
                case "delActionNazwa":
                    delAction("nazwa");
                    break;
                default:


            }
        }));
    }

    public boolean addAction() {
        try {
            BazaDanych.getBazaDanych().dodajDane(ustalDostepneID(), this.getText());
        } catch (SQLException ex) {
            return false;
        }
        this.setText("");
        PanelElementow.getPanelElementow().updateModel();
        return true;
    }

    public boolean delAction(String kategoria) {

        try {
            switch (kategoria) {
                case "id":
                    BazaDanych.getBazaDanych().usunDane(Integer.parseInt(this.getText()));
                    break;
                case "nazwa":
                    BazaDanych.getBazaDanych().usunDane(this.getText());
                    break;
                default:
                    return false;

            }
        } catch (SQLException e) {
            return false;
        }
        this.setText("");
        PanelElementow.getPanelElementow().updateModel();
        return true;
    }

        private int ustalDostepneID(){
            Object[][] data = BazaDanych.getBazaDanych().getData();
            int n = 0;
            ArrayList<Integer> listaId= new ArrayList<>();
            for(Object[] id: data){
                listaId.add((int)id[0]);
            }
            listaId.sort(null);
            for (int liczba : listaId) {
                if (++n !=liczba) {
                    return n;
                }
            }
            return ++n;
        }

        public void setLastAction(String lastAction){this.lastAction=lastAction;}
    }
