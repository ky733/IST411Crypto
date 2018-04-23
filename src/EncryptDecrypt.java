/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author whb108
 */
public class EncryptDecrypt 
{
    public String encrypt(String strInMessage, int intInKey)
    {
        System.out.println("Key = " + intInKey);
       StringBuilder sbTemp = new StringBuilder(strInMessage);
       return  sbTemp.reverse().toString();
    } // encrypt
    
     public String decrypt(String strInMessage)
    {
       StringBuilder sbTemp = new StringBuilder(strInMessage);
       return  sbTemp.reverse().toString();
    } // deecrypt
    
     
     public static void main(String[] args) 
     {
         EncryptDecrypt myCrypt = new EncryptDecrypt();
         String strPainText = "Test Message #1";
         System.out.println("strPainText = " + strPainText);
         
         String strEncrypted = myCrypt.encrypt(strPainText,5);
         System.out.println("strEncrypted = " + strEncrypted);
         
         String strDecrypted = myCrypt.decrypt(strEncrypted);
         System.out.println("strDecrypted = " + strDecrypted);
         
         
        
    } // main
     
     
} // EncryptDecrypt
