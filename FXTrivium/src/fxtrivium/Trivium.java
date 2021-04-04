/*
 * Copyright (C) 2021 jairo
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Scanner;
/**
 *
 * @author jairo
 */
public class Trivium {
    
    int[] keyStream = new int[120];
    int[] inp = new int[10];
    int[] enc = new int[10];
    int[] sr = new int [288];
    int[] keystream = new int [1000];
    String binarykeystream="";
    String cipher;
    int t1;
    int t2;
    int t3;
    int newt1;
    int newt2;
    int newt3;
    int z;
    int[] zi = new int [1000];    
 
    public Trivium(String key, String iv, String plain) {
        String iv_result = this.stringToBinary(iv);
        String key_result = this.stringToBinary(key);
        String plain_result = this.stringToBinary(plain);
        
        for (int k = 0; k < key_result.length(); k++) {
            sr[k] = Byte.parseByte(String.valueOf(key_result.charAt(k)));
        }
        if (key_result.length() < 80) {
            for (int k = key_result.length(); k < 80; k++) {
                sr[k] = 0;
            }
        }
        for (int k = 80; k <= 92; k++) {
            sr[k] = 0;
        }
        for (int k = 93; k < iv_result.length(); k++) {
            sr[k] = Byte.parseByte(String.valueOf(iv_result.charAt(k - 93)));
        }
        if (iv_result.length() < 80) {
            for (int k = iv_result.length(); k < 80; k++) {
                sr[k + 93] = 0;
            }
        }
        for (int k = 173; k <= 176; k++) {
            sr[k] = 0;
        }
        for (int k = 177; k <= 284; k++) {
            sr[k] = 0;
        }
        for (int k = 285; k <= 287; k++) {
            sr[k] = 1;
        }
        System.out.print("Texto Plano = ");
        for (int i = 0; i < plain_result.length(); i++) {
            System.out.print(Byte.parseByte(String.valueOf(plain_result.charAt(i))));
        }
        System.out.println("");
        System.out.println("Registrar el contenido antes de cifrarlo = ");
        for (int i = 0; i < 288; i++) {
            System.out.print(sr[i]);
        }
        System.out.println("");
        keluar(plain_result);
    }
        
    public void keluar(String input){
        System.out.print("Keystream = ");
        for (int i = 0; i < 1152 + input.length(); i++) {
            t1 = sr[65] ^ sr[92];
            t2 = sr[161] ^ sr[176];
            t3 = sr[242] ^ sr[287];
            if (i > 1151) {
                z = t1 ^ t2 ^ t3;
                System.out.print(z);
                keystream[i - 1152] = z;
            }
            t1 = t1 ^ (sr[90] & sr[91]) ^ sr[170];
            t2 = t2 ^ (sr[174] & sr[175]) ^ sr[263];
            t3 = t3 ^ (sr[285] & sr[286]) ^ sr[68];
            for (int j = 287; j > 0; j--) {
                sr[j] = sr[j - 1];
            }
            sr[0] = t3;
            sr[93] = t1;
            sr[177] = t2;

        }
        System.out.println("");
        encrypt(input);
    }
     
    public String encrypt(String input) {
        String result = input;
        String result_string = "";
        Byte[] result_array = new Byte[result.length()];

        Byte[] result_xor_array = new Byte[result.length()];

        for (int i = 0; i < result_array.length; i++) {
            result_array[i] = Byte.parseByte(String.valueOf(result.charAt(i)));
            result_xor_array[i] = (byte) (this.keystream[i] ^ result_array[i]);
            result_string += result_xor_array[i];
            binarykeystream += keystream[i];

        }

        System.out.println("Texto Cifrado = " + result_string);
        decrypt(binarykeystream, result_string);
        cipher = result_string;
        return result_string;
    }
 
    public String decrypt(String keystream, String cipher) {
        
        Byte[] a_array = new Byte[cipher.length()];
        Byte[] b_array = new Byte[keystream.length()];

        String plain_binary = "";
        String plain = "";
        Byte[] hasil = new Byte[cipher.length()];

        for (int i = 0; i < a_array.length; i++) {
            a_array[i] = Byte.parseByte(String.valueOf(cipher.charAt(i)));
        }

        for (int i = 0; i < b_array.length; i++) {
            b_array[i] = Byte.parseByte(String.valueOf(keystream.charAt(i)));
        }

        for (int i = 0; i < cipher.length(); i++) {
            hasil[i] = (byte) (b_array[i] ^ a_array[i]);
            plain_binary += hasil[i];
        }
        
        System.out.println("Binario Plano = " + plain_binary);
        for (int i = 0; i < plain_binary.length(); i += 8) {
            int k = Integer.parseInt(plain_binary.substring(i, i + 8), 2);
            plain += (char) k;
        }
        
        System.out.println("Texto Original = "+plain);
        return plain;
    }
 
    public static String stringToBinary(String string) {
        String result = "";
        String tmpStr;
        int tmpInt;
        char[] messChar = string.toCharArray();
        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);

            tmpInt = tmpStr.length();

            if (tmpInt != 8) {
                tmpInt = 8 - tmpInt;
                if (tmpInt == 8) {
                    result += tmpStr;
                } else if (tmpInt > 0) {
                    for (int j = 0; j < tmpInt; j++) {
                        result += "0";

                    }
                } else {
                    System.out.println("Los argumentos de los bits son muy pequeños...");
                }
            }
            result += tmpStr;
        }
        result += "";

        return result;
    }
    
    public String hexToBinary(String hex) {
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for (int i = 0; i < hex.length(); i++) {
            iHex = Integer.parseInt("" + hex.charAt(i), 16);
            binFragment = Integer.toBinaryString(iHex);

            while (binFragment.length() < 4) {
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }
    
//    public static void main(String[] args) {
//        //Llave, Vector Inicializacion, Texto en plano
//        Trivium tr = new Trivium("perro","12345","hola mundo");
//        
//        System.out.println("DESCIFRANDO...");
//        //Stream de la llave, mensaje cifrado
//        tr.decrypt("10001101000111001100001111011111011000101100000101010000001010101000100011111111", "11100101011100111010111110111110010000101010110000100101010001001110110010010000");
//    } 
}

