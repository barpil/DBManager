import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

public class OknoWczytywaniaBazyDanych extends JDialog {
    private static OknoWczytywaniaBazyDanych oknoWczytywaniaBazyDanych;
    private static String nazwaSerwera;
    private static String port;
    private static String nazwaBazy;
    private static String nazwaUzytkownika;
    JPanel panelWewnetrzny;
    private OknoWczytywaniaBazyDanych(){
        if(ConfigFileOperator.isAutologinEnabled()){
            ConfigFileOperator.AutologinProperties autologinProperties = ConfigFileOperator.getAutologinProperties();
            assert autologinProperties != null;
            loadDatabase(autologinProperties.serverName(), autologinProperties.port(),autologinProperties.databaseName(),autologinProperties.username(), autologinProperties.password());
            ConfigFileOperator.autoLoginAlreadyPerformed();


            return;
        }
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setSize(new Dimension(270,310));
        this.setLocationRelativeTo(OknoGlowne.getOknoGlowne());
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));


        dodajComponenty();
        this.setVisible(true);
    }

    private void dodajComponenty(){

        panelWewnetrzny= new JPanel();
        this.add(panelWewnetrzny);

        JLabel tytulOknaLB = new JLabel("Load Database",SwingConstants.CENTER);
        panelWewnetrzny.add(tytulOknaLB);

        Dimension liniaWprowadzaniaDimension = new Dimension(270,40);

        JPanel liniaIP = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,5));
        liniaIP.setPreferredSize(liniaWprowadzaniaDimension);
        liniaIP.setBackground(Color.blue);
        liniaIP.add(new Label("   Server name/IP:"));
        JTextField liniaIPTB = new JTextField();
        liniaIPTB.setPreferredSize(new Dimension(120,30));
        liniaIPTB.setText(nazwaSerwera);
        liniaIP.add(liniaIPTB);

        JPanel liniaPort = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,5));
        liniaPort.setPreferredSize(liniaWprowadzaniaDimension);
        liniaPort.setBackground(Color.green);
        liniaPort.add(new Label("                    Port:"));
        JTextField liniaPortTB = new JTextField();
        liniaPortTB.setPreferredSize(new Dimension(120,30));
        liniaPortTB.setText(port);
        liniaPort.add(liniaPortTB);

        JPanel liniaNazwaBazy = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,5));
        liniaNazwaBazy.setPreferredSize(liniaWprowadzaniaDimension);
        liniaNazwaBazy.setBackground(Color.red);
        liniaNazwaBazy.add(new Label("  Database name:"));
        JTextField liniaNazwaBazyTB = new JTextField();
        liniaNazwaBazyTB.setPreferredSize(new Dimension(120,30));
        liniaNazwaBazyTB.setText(nazwaBazy);
        liniaNazwaBazy.add(liniaNazwaBazyTB);

        JPanel pustaLinia = new JPanel();
        pustaLinia.setMinimumSize(new Dimension(270,20));

        JPanel liniaNazwyUzytkownika = new JPanel(new FlowLayout(FlowLayout.LEFT,11,5));
        liniaNazwyUzytkownika.setPreferredSize(liniaWprowadzaniaDimension);
        liniaNazwyUzytkownika.add(new Label("          Username:"));
        JTextField liniaNazwyUzytkownikaTB = new JTextField();
        liniaNazwyUzytkownikaTB.setPreferredSize(new Dimension(120,30));
        liniaNazwyUzytkownikaTB.setText(nazwaUzytkownika);
        liniaNazwyUzytkownika.add(liniaNazwyUzytkownikaTB);

        JPanel liniaHaslaUzytkownika = new JPanel(new FlowLayout(FlowLayout.LEFT,11,5));
        liniaHaslaUzytkownika.setPreferredSize(liniaWprowadzaniaDimension);
        liniaHaslaUzytkownika.add(new Label("           Password:"));
        JTextField liniaHaslaUzytkownikaTB = new JPasswordField();
        liniaHaslaUzytkownikaTB.setPreferredSize(new Dimension(120,30));
        liniaHaslaUzytkownika.add(liniaHaslaUzytkownikaTB);

        JPanel liniaPrzyciskow = new JPanel(new FlowLayout(FlowLayout.CENTER,30,10));
        Button loadButton = new Button("Load");
        loadButton.setPreferredSize(new Dimension(70,20));
        loadButton.addActionListener(e -> {
            loadDatabase(liniaIPTB.getText(), liniaPortTB.getText(),liniaNazwaBazyTB.getText(), liniaNazwyUzytkownikaTB.getText(), liniaHaslaUzytkownikaTB.getText());
            this.dispose();
        });
        liniaPrzyciskow.add(loadButton);
        Button declineButton = new Button("Decline");
        declineButton.setPreferredSize(new Dimension(70,20));
        declineButton.addActionListener(e -> {
            this.dispose();
        });
        liniaPrzyciskow.add(declineButton);

        this.add(liniaIP);
        this.add(liniaPort);
        this.add(liniaNazwaBazy);
        this.add(pustaLinia);
        this.add(liniaNazwyUzytkownika);
        this.add(liniaHaslaUzytkownika);
        this.add(liniaPrzyciskow);

    }
    private void loadDatabase(String serverName, String portNumber, String databaseName, String username, String password){
        try {
            BazaDanych.ustawBaze(serverName, portNumber, databaseName, username, password);
            //Przypisanie w tej kolejności bo jeżeli ktoś nie poda poprawnych danych logowania to nie chcemy ich zapamietywac
            nazwaSerwera = serverName;
            port = portNumber;
            nazwaBazy = databaseName;
            nazwaUzytkownika = username;

            Menu.getMenu().dodajOpcjeBazyDanych();
            BazaDanych.getBazaDanych().zaktualizujBaze();
            PanelElementow.zaladujTabele();


        }
        catch( SQLSyntaxErrorException ex){
            String message = "";
            if(ConfigFileOperator.isAutologinEnabled()){
                message+="Autologin Error: ";
            }
            JOptionPane.showMessageDialog(this,message+"An incorrect database name has been entered!", "Invalid database error", JOptionPane.ERROR_MESSAGE);
        }
        catch(CommunicationsException ex){
            String message = "";
            if(ConfigFileOperator.isAutologinEnabled()){
                message+="Autologin Error: ";
            }
            JOptionPane.showMessageDialog(this,message+"An unknown server name or port has been entered!", "Invalid server error", JOptionPane.ERROR_MESSAGE);
        }
        catch (SQLException ex) {
            String message = "";
            if(ConfigFileOperator.isAutologinEnabled()){
                message+="Autologin Error: ";
            }
            JOptionPane.showMessageDialog(this,message+"An incorrect login data has been provided!", "Invalid login error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void showOknoWczytywania(){
        oknoWczytywaniaBazyDanych= new OknoWczytywaniaBazyDanych();
    }

}
