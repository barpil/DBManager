import com.mysql.cj.x.protobuf.Mysqlx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ConfigFileOperator {
    private static final String CONFIG_FILE_PATH="config";
    private static final String[] MANDATORY_CONFIG_PROPERTIES={
            "autologin.enabled",
            "autologin.serverName",
            "autologin.port",
            "autologin.databaseName",
            "autologin.username",
            "autologin.userPassword"};

    private final static Properties properties = new Properties();
    private static boolean czyZaladowane = false;
    private static final Logger log = LoggerFactory.getLogger(ConfigFileOperator.class);

    static {
        loadProperties(CONFIG_FILE_PATH);
        if(!isConfigFileValid(properties)){
            OknoGlowne.getOknoGlowne().dispose();
            System.exit(1);
        }
    }


    private static void loadProperties(String configFilePath){
        if(!czyZaladowane){

            try(FileReader configFileReader = new FileReader(configFilePath);) {
                czyZaladowane=true;
                properties.load(configFileReader);
            } catch (IOException e) {
                String sciezkaDoConfigFile=ConfigFileOperator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(),"Unable to load the configuration file at path: "+sciezkaDoConfigFile.substring(0,sciezkaDoConfigFile.lastIndexOf('/'))+". Please ensure that the file exists and is accessible.", "Config file error", JOptionPane.ERROR_MESSAGE);
                log.error("Config file error, Unable to load the configuration file");
                OknoGlowne.getOknoGlowne().dispose();
                System.exit(1);
            }


        }
    }

    private static boolean isConfigFileValid(Properties properties){

        Set<String> propertiesNames = properties.stringPropertyNames();
        List<String> unprovidedProperties = new LinkedList<>();
        for(String propertyName: MANDATORY_CONFIG_PROPERTIES){
            if(!propertiesNames.contains(propertyName)){
                unprovidedProperties.add(propertyName);
            }
        }
        if(!unprovidedProperties.isEmpty()){
            String message="Not all required properties are present in the configuration file.\nPlease ensure that the configuration file contains all necessary\nproperties for the application to function correctly.\nMissing properties:\n";
            String missingProperties="";
            for(int i=0; i< unprovidedProperties.size();i++){
                missingProperties+="\n"+unprovidedProperties.get(i);
            }
            JOptionPane.showMessageDialog(OknoGlowne.getOknoGlowne(), message+missingProperties, "Config file error", JOptionPane.ERROR_MESSAGE);
            log.error("Missing config file properties: {}", missingProperties);
            return false;
        }
        return true;
    }

    private static boolean getBooleanPropertyValue(String s){
        switch (s.toLowerCase()){
            case "true":
                return true;
            case "false":
                return false;
            default:
                log.error("Invalid configfile boolean property value: \"{}\". Property set to false",s);
                return false;
        }
    }

    public static boolean isAutologinEnabled(){
        return getBooleanPropertyValue(properties.getProperty("autologin.enabled"));
    }

    public static void autoLoginAlreadyPerformed(){
        properties.setProperty("autologin.enabled", "false");
    }

    public record AutologinProperties(String serverName, String port, String databaseName, String username, String password){}

    public static AutologinProperties getAutologinProperties(){
        if(isAutologinEnabled()){
            return new AutologinProperties(properties.getProperty("autologin.serverName"), properties.getProperty("autologin.port"), properties.getProperty("autologin.databaseName"),properties.getProperty("autologin.username"),properties.getProperty("autologin.userPassword"));
        }
        return null;
    }
}
