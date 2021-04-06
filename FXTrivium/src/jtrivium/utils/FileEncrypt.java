/*
 * Copyright (C) 2011 Daniel Ward dwa012@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jtrivium.utils;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jtrivium.cipher.JTrivium;

/**
 * 
 * @author daniel
 * 
 * @update Steven Rey Sequeira
 * @version 1.1
 */
public class FileEncrypt implements Closeable {
    
    /**
     * This enum type was added to get User flexible method store the encrypted text.
     */
    public enum TypeEncode {
        BINARY, BASE64, HEX, RAW;
    }
    
    private final int MAX_READ_BUFFER_SIZE = 2048;
    
    private JTrivium cipher;
    private DataInputStream reader;
    private DataOutputStream writer;
    
    private int bufferedSize;
    
    public FileEncrypt(String inputFilePath, String outputFilePath, JTrivium cipher, int bufferedSize) throws FileNotFoundException {
        this.cipher = cipher;
        
        this.reader = new DataInputStream(new FileInputStream(new File(inputFilePath)));
        this.writer = new DataOutputStream(new FileOutputStream(new File(outputFilePath)));
        this.bufferedSize= bufferedSize <= 0 ? MAX_READ_BUFFER_SIZE : bufferedSize;
        
    }

    public FileEncrypt(String inputFilePath, String outputFilePath, JTrivium cipher) throws FileNotFoundException {
        this(inputFilePath, outputFilePath, cipher, 0);
    }

    public void encrypt(TypeEncode encode) throws IOException {
        int readBytes = 0;
        int length= this.reader.available();
        
        StringBuilder appenable= new StringBuilder();
        
        byte[] buffer = new byte[length >= this.bufferedSize ? this.bufferedSize : length];
        
        do {
            readBytes = reader.read(buffer);
            
            for (int i = 0; i < readBytes; i++) 
                buffer[i] ^= cipher.getKeyByte();

            if (readBytes > 0) {
                switch (encode) {
                    case BASE64:
                        writer.write(Base64.getMimeEncoder().encode(buffer));
                        
                        break;
                        
                    case BINARY:
                        writer.write(this.binaryEncode(buffer, appenable).toString().getBytes());
                        appenable.delete(0, appenable.length());
                        
                        break;
                        
                    case HEX:
                        writer.write(this.hexEncode(buffer, appenable).toString().getBytes());
                        appenable.delete(0, appenable.length());
                        
                        break;
                        
                    case RAW:
                        writer.write(buffer, 0, readBytes);

                        break;
                }
                
                writer.flush();
            }
            
        } while (readBytes > 0);
    }
    
    public void encrypt() throws IOException {
        this.encrypt(TypeEncode.RAW);
    }
    
    @Override
    public void close() throws IOException{
        if (this.reader != null)
            this.reader.close();
        
        if (this.writer != null)
            this.writer.close();
    }
    
    // setcion of private methods
    
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
                Logger.getLogger(FileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    
        return sb;   
    }
    
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
            Logger.getLogger(FileEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return sb;
    }
}