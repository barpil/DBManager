import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class Menu extends JMenuBar {
    private static Menu menu;
    private final JMenu menuGlowne;
    private Menu(){
        menuGlowne = new JMenu("Menu");
        this.add(menuGlowne);

        dodajComponenty();
    }

    private void dodajComponenty() {
        menuGlowne.removeAll();
        JMenuItem wczytajBazeDanychMI = new JMenuItem("Load database");
        wczytajBazeDanychMI.addActionListener(e -> OknoWczytywaniaBazyDanych.showOknoWczytywania());
        menuGlowne.add(wczytajBazeDanychMI);

        if(BazaDanych.getBazaDanych()!=null){
            dodajOpcjeBazyDanych();
        }

    }



    public void dodajOpcjeBazyDanych() {
        JMenu wybierzTabeleMenu = new JMenu("Select table");
        try {
            List<String> listaTabel = BazaDanych.getBazaDanych().getNazwyTabel();
            for(String nazwaTabeli: listaTabel){
                JMenuItem nazwaTabeliMI = new JMenuItem(nazwaTabeli);
                nazwaTabeliMI.addActionListener(e -> {
                    try {
                        BazaDanych.getBazaDanych().setNazwaTabeli(nazwaTabeli);
                        BazaDanych.getBazaDanych().zaktualizujBaze();
                        PanelElementow.zaladujTabele();
                        zaktualizujMenu();
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                wybierzTabeleMenu.add(nazwaTabeliMI);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        menuGlowne.add(wybierzTabeleMenu);
        JMenuItem edytujTabeleMI = new JMenuItem("Edit table");
        edytujTabeleMI.addActionListener(e -> edytujTabele());
        JMenuItem sqlConsoleMI = new JMenuItem("SQL console");
        sqlConsoleMI.addActionListener(e -> otworzKonsoleSQL());
        menuGlowne.add(edytujTabeleMI);
        JMenu sortujWyniki = new JMenu("Sort...");
        menuGlowne.add(sortujWyniki);
        menuGlowne.add(sqlConsoleMI);
        JMenu sortujASC = new JMenu("ASC");
        JMenu sortujDESC = new JMenu("DESC");
        sortujWyniki.add(sortujASC);
        sortujWyniki.add(sortujDESC);
        InformacjeOTabeli informacjeOTabeli = BazaDanych.getBazaDanych().getInformacjeOTabeli();
        System.out.println(informacjeOTabeli);
        List<String> listaKolumn = informacjeOTabeli.getInformacjaOKolumnie(InformacjeOTabeli.InformacjeKolumny.NAZWA_KOLUMNY);
        for(String nazwaKolumny: listaKolumn){
            JMenuItem przyciskKolumnyASC = new JMenuItem(nazwaKolumny);
            przyciskKolumnyASC.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().sortujDane(nazwaKolumny,"ASC");
                    PanelElementow.zaladujTabele();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            JMenuItem przyciskKolumnyDESC = new JMenuItem(nazwaKolumny);
            przyciskKolumnyDESC.addActionListener(e -> {
                try {
                    BazaDanych.getBazaDanych().sortujDane(nazwaKolumny,"DESC");
                    PanelElementow.zaladujTabele();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            sortujASC.add(przyciskKolumnyASC);
            sortujDESC.add(przyciskKolumnyDESC);

        }
    }

    public void zaktualizujOpcjeWyboruTabel(){
        JMenu menuGlowne = (JMenu) this.getMenu(0);
        //(JMenu) menuGlowne.getItem(1).
    }


    private void otworzKonsoleSQL() {
        SQLConsole sqlConsole = new SQLConsole(OknoGlowne.getOknoGlowne(), "SQL console", false);
    }

    private void edytujTabele() {
        OknoEdycjiTabeli oknoEdytowaniaTabeli = new OknoEdycjiTabeli(OknoGlowne.getOknoGlowne(), "Edytuj tabelę", true);
        try {
            BazaDanych.getBazaDanych().zaktualizujBaze();
            PanelElementow.zaladujTabele();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Menu getMenu() {
        if(menu==null){
            menu=new Menu();
        }
        return menu;
    }

    public void zaktualizujMenu(){
        menuGlowne.removeAll();
        dodajComponenty();
        OknoGlowne.getOknoGlowne().revalidate();
    }

    public JMenu getMenuGlowne() {
        return menuGlowne;
    }
}
