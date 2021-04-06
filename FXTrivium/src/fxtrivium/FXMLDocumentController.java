/*
 * Copyright (C) 2016 srey
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package fxtrivium;

//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import jtrivium.cipher.JTrivium;
import jtrivium.utils.FileEncrypt;

/**
 *
 * @author srey
 */
public class FXMLDocumentController implements Initializable {
    private final byte MAX_LENGTH= 10;
    
    private Label label;
    @FXML
    private AnchorPane apn_mainContainer;
    @FXML
    private TitledPane tpn_configContainer;
    @FXML
    private AnchorPane apn_configContainer;
    @FXML
    private GridPane gpn_configContainer;
    @FXML
    private Label lbl_key;
    @FXML
    private Label lbl_vi;
    @FXML
    private Label lbl_text;
    @FXML
    private Label lbl_inputFile;
    @FXML
    private TextField txt_key;
    @FXML
    private TextField txt_vi;
    @FXML
    private TextArea txta_text;
    @FXML
    private CheckBox ckb_text;
    @FXML
    private TextField txt_inputFile;
    @FXML
    private Button btn_inputFile;
    @FXML
    private TextArea txta_output;
    @FXML    
    private ButtonBar bpn_buttonContainer;
    @FXML
    private Button btn_clear;
    @FXML
    private Button btn_encript;
    @FXML
    private Button btn_desencript;
    @FXML
    private Button btn_save;
    @FXML
    private Label lbl_output;
    @FXML
    private Pane pn_radioButtonConatiner;
    @FXML
    private RadioButton rb_hex;
    @FXML
    private RadioButton rb_base64;
    @FXML
    private ToggleGroup tgrb_output;
    @FXML
    private RadioButton rb_binary;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
//        this.btn_clear.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));
//        this.btn_encript.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));
//        this.btn_desencript.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.LOCK));
//        this.btn_inputFile.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.FILE));
//        this.btn_save.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));

        
        this.ckb_text.selectedProperty().addListener((observ, oldValue, newValue) -> {
            this.txt_inputFile.setDisable(newValue);
            this.btn_inputFile.setDisable(newValue);
            
            this.txta_text.setDisable(!newValue);
        });
    }      
    
    // section of private methods
    
    /**
     * Este metodo se encarga de limpiar todos los controles de la pantalla, segun su parametro.
     * 
     * @param all 
     *                  Indica si se desea limpiart todos los controles de la pantalla.
     */
    private void clear(boolean all) {
        if (all) {
            this.txt_inputFile.clear();
            this.txt_key.clear();
            this.txt_vi.clear();
            this.txta_text.clear();
            
            this.ckb_text.setSelected(true);
        }
        
        this.txta_output.clear();
    }
    
    /**
     * Este metodo se encarga de validar los parametros ingresados por el usuario.
     * 
     * Si uno de los parametros es invalido muestra un mensaje al usario y le pone el {@code foco} al control
     * que no cumpla con las condiciones.
     * 
     * @return 
     *      Retorna un {@code true} en caso que los parametros se ingresaran de forma satisfactoria, en caso contrario retorna un
     *      {@code false}.
     */
    private boolean validateParams() {
        if (this.txt_key.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar la contraceña");
            
            this.txt_key.requestFocus();
            return false;
        } else if (this.txt_key.getText().length() > 10) {
            DialogUtil.showWarning("Parametro incorrecto", 
                    "La cantidad de caracteres para la clave debe ser mayor o igual a 10");
            
            this.txt_key.requestFocus();
            return false;
        }
        
        if (this.txt_vi.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar el vector de incialización");
            
            this.txt_vi.requestFocus();
            return false;
        } else if (this.txt_vi.getText().length() > 10) {
            DialogUtil.showWarning("Parametro incorrecto", 
                    "La cantidad de caracteres para el vector de inicialización debe ser mayor o igual a 10");
            
            this.txt_vi.requestFocus();
            return false;
        }
        
        if (this.ckb_text.isSelected() && this.txta_text.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar el texto a cifrar");
            
            this.txta_text.requestFocus();
            return false;
        } else if (!this.ckb_text.isSelected() && this.txt_inputFile.getText().isEmpty()) {
            DialogUtil.showWarning("Faltan Parametros", 
                    "Debe de ingresar la ruta que contiene el archivo de texto que desea cifrar");
            
            this.txt_inputFile.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Este metodo se encarga de codificar un arreglo de bytes en texto hexadecimal.
     * 
     * @param buf
     *                  Arreglo que contiene los datos a codificar.
     * 
     * @param sb
     *                  Instancia a la cual se le concatenaran los datos codificados en hexadecimal.
     * 
     * @return 
     *          Retorna una instancia que implementa {@link Appendable}, la cual contendra todo el texto codificado
     *          en hexadecimal.
     */
    private Appendable hexEncode(byte buf[], Appendable sb)    {   
        final Formatter formatter = new Formatter(sb);   

        for (int i = 0; i < buf.length; i++) {   
            try {
                int low = buf[i] & 0xF;
                int high = (buf[i] >> 8) & 0xF;

                sb.append(Character.forDigit(high, 16)).
                        append(Character.forDigit(low, 16)).
                        append(" ");
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    
        return sb;   
    }
    
    public static String unHex(String arg) {        

        String str = "";
        for(int i=0;i<arg.length();i+=2)
        {
            String s = arg.substring(i, (i + 2));
            int decimal = Integer.parseInt(s, 16);
            str = str + (char) decimal;
        }       
        return str;
    }
    
    /**
     * Este metodo se encarga de codificar los datos contendios en un arreglo de bytes a su representacion
     * binaria.
     * 
     * @param buffred
     *                  Arreglo que contiene los datos a codificar.
     * 
     * @param sb
     *                  Instancia a la cual se le concatenaran los datos codificados en binario.
     * 
     * @return 
     *          Retorna una instancia que implementa {@link Appendable}, la cual contendra todo el texto codificado
     *          en binaria.
     */
    private Appendable binaryEncode(byte[] buffred, Appendable sb) {
        
        try {
            for (byte b : buffred)  {
                int val = b;
                
                for (int i = 0; i < 8; i++) {
                    sb.append((val & 128) == 0 ? "0" : "1");
                    val <<= 1;
                }
                    
                sb.append(" ");
            }
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sb;
    }
    
    /**
     * Este metodo es el encargado de cifrar el texto con el algoritmo Trivium y 
     * codificados sengun las opciones brindadas por el usuario.
     * 
     * @param text
     *                  Instancia que contiene el texto que se desea cifrar.
     * 
     * @param cipher
     *                  Instancia que contiene la implementacion del algoritmo Trivium.
     * 
     * @return 
     *      Retorna una instancia de tipo {@code String} con los datos encriptados
     */
    private String encrypt(String text, JTrivium cipher) {
        StringBuilder result= new StringBuilder();
        
        try (DataInputStream dataInput= new DataInputStream(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)))) {
            byte[] bufferd= new byte[dataInput.available()];
            int readBytes= 0;
            
            do {
                readBytes= dataInput.read(bufferd);
                
                for (int i=0; i < readBytes; i++)
                    bufferd[i] ^= cipher.getKeyByte();
                
                if (readBytes > 0) {
                    if (this.rb_hex.isSelected())
                        this.hexEncode(bufferd, result);
                    
                    else  if (this.rb_base64.isSelected())
                        result.append(Base64.getMimeEncoder().encodeToString(bufferd));
                        //result.append(new String(bufferd, StandardCharsets.UTF_8));
                        //result.append(bufferd.toString());
                    
                    else
                        this.binaryEncode(bufferd, result);
                }
                
            } while (readBytes > 0) ;
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            
            DialogUtil.showException("Error al cerrar Stream", ex);
        }
        
        return result.toString();
    }
    
    private String desencrypt(String text, JTrivium cipher) {
        StringBuilder result= new StringBuilder();

        try (DataInputStream dataInput = new DataInputStream(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)))) {
            byte[] bufferd = new byte[dataInput.available()];
            byte[] hasil = new byte[text.length()];
            //Base64.getMimeDecoder().decode(bufferd)
            int readBytes= 0;
            
            do {
                readBytes= dataInput.read(bufferd);

                if (readBytes > 0) {

                    if (this.rb_hex.isSelected()){
  
                        result.append("En Proceso....");                    
                    } 
                    else  if (this.rb_base64.isSelected()){
                        hasil = Base64.getMimeDecoder().decode(bufferd);
                        for (int i=0; i < hasil.length; i++)
                            hasil[i] ^= cipher.getKeyByte();     
                        
                        result.append(new String(hasil, StandardCharsets.UTF_8));                    
                    }

                    else
                        
                        result.append("En Proceso...."); 
                }                

            } while (readBytes > 0) ;
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            
            DialogUtil.showException("Error al cerrar Stream", ex);
        }
        
        return result.toString();
    }
    
    @FXML
    private void handleBtn_clear(ActionEvent evt) {
        this.clear(true);
    }

    @FXML
    private void handleBtn_descipher(ActionEvent evt) {
        if (this.validateParams()) {
            this.clear(false);
            
            DialogUtil.showInfoMessage("Inicia el proceso de desencriptar el texto", "Desencriptar");
            
            StringBuilder sb= new StringBuilder();
            
            byte[] key= null;
            byte[] iv= null;
            
            // primero se codifica en hexadecimal la clave y se optiene un arreglo de bytes.
            key= this.hexEncode(this.txt_key.getText().getBytes(), sb).toString().getBytes();
            
            sb.delete(0, sb.length());
            
            // se codifica en hexadecimal el vector de inicalizacion y se optien un arreglo de bytes.
            iv= this.hexEncode(this.txt_vi.getText().getBytes(), sb).toString().getBytes();
            
            // se incializa la intancia que implementa el algoritmo Trivium
            JTrivium cipher= new JTrivium(key, iv, false);
            
            // se pregunta si el usuario puso el texto plano en un campo de texto o TextArea
            if (this.ckb_text.isSelected()){
                this.txta_output.setText(this.desencrypt(this.txta_text.getText(), cipher));
            } 
               
        }
    }
    
    @FXML
    private void handleBtn_cipher(ActionEvent evt) {
        if (this.validateParams()) {
            this.clear(false);
            
            DialogUtil.showInfoMessage("Inicia el proceso de encriptar el texto", "Encriptar");
            
            StringBuilder sb= new StringBuilder();
            
            byte[] key= null;
            byte[] iv= null;
            
            // primero se codifica en hexadecimal la clave y se optiene un arreglo de bytes.
            key= this.hexEncode(this.txt_key.getText().getBytes(), sb).toString().getBytes();
            
            sb.delete(0, sb.length());
            
            // se codifica en hexadecimal el vector de inicalizacion y se optien un arreglo de bytes.
            iv= this.hexEncode(this.txt_vi.getText().getBytes(), sb).toString().getBytes();
            
            // se incializa la intancia que implementa el algoritmo Trivium
            JTrivium cipher= new JTrivium(key, iv, false);
            
            // se pregunta si el usuario puso el texto plano en un campo de texto o TextArea
            if (this.ckb_text.isSelected()) 
                this.txta_output.setText(this.encrypt(this.txta_text.getText(), cipher));
            
            // en caso que el usuario carge un archivo que contenga el texto plano a cifrar.
            else {
                try {
                    // se crea un archivo temporal sobre el que operara posteriormente.
                    Path tmpFile= Files.createTempFile("trivium", null);
                    Path inputFile= Paths.get(this.txt_inputFile.getText());
                    
                    if (inputFile != null && Files.exists(inputFile)) {
                        // se instancia una clase que se encarga de encriptar usando el algoritmo Trivium em un archivo.
                        try (FileEncrypt fileEncryp= new FileEncrypt(inputFile.toString(), tmpFile.toString(), cipher, 512)) {
                            if (this.rb_base64.isSelected())
                                fileEncryp.encrypt(FileEncrypt.TypeEncode.BASE64);
                            
                            else if (this.rb_hex.isSelected())
                                fileEncryp.encrypt(FileEncrypt.TypeEncode.HEX);
                            
                            else 
                                fileEncryp.encrypt(FileEncrypt.TypeEncode.BINARY);
                            
                            // finalmente se leen todas las lienas del archivo temporal y se muestra al usuario
                            // el texto cifrado.
                            Files.readAllLines(tmpFile, StandardCharsets.UTF_8).
                                    forEach(s -> this.txta_output.appendText(s + System.getProperty("line.separator")));
                        }
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    
                    DialogUtil.showException("Error al intentar cifrar el texto", ex);
                }
            }
        }
    }
    
    @FXML
    private void handleBtn_inpuFile(ActionEvent evt) {
        FileChooser fc= new FileChooser();
        
        fc.setTitle("Abrir archivo de Texto");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Text (*.txt)", "*.txt"));
        
        File selected= fc.showOpenDialog(this.lbl_inputFile.getScene().getWindow());
        
        if (selected != null && selected.isFile())
            this.txt_inputFile.setText(selected.toPath().toString());
    }
    
    @FXML
    private void handleBtn_save(ActionEvent evt) {
        FileChooser fc= new FileChooser();
        
        fc.setTitle("Guardar Texto Cifrado");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos de Text (*.txt)", "*.txt"));
                
        File selected= fc.showSaveDialog(this.lbl_inputFile.getScene().getWindow());
        
        if (selected != null) {
            
            try (FileWriter fw= new FileWriter(selected)) {
                fw.write(this.txta_output.getText());
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    
                    DialogUtil.showException("Error al guardar el texto cifrado", ex);
            }
        }
    }

}
