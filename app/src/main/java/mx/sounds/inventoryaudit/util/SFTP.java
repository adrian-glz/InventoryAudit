/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.sounds.inventoryaudit.util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *
 * @author jmendoza
 */
public class SFTP {
    static String result_text = "";

    public static void DownloadSFTP(String pUser, String pPass,
                    String pHost, int pPort, String pOutputStream, String cRemotePath, Boolean lShowResult)
                    throws Exception {
        JSch sftp = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        result_text = "";
        cRemotePath = cRemotePath.trim();
        if (!"/".equals(cRemotePath.substring(cRemotePath.length()-1))) {
            cRemotePath = cRemotePath + "/";
        }
        
        try {
                session = sftp.getSession(pUser, pHost, pPort);
                session.setPassword(pPass);
                Properties prop = new Properties();
                prop.put("StrictHostKeyChecking", "no");
                session.setConfig(prop);
                session.connect();

                channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
                File f = new File(pOutputStream);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(
                                pOutputStream));
                channelSftp.get(cRemotePath +  f.getName(), os);
                os.close();
                //Thread.sleep(2000);
                if (f.exists()) {
                    result_text = result_text + "Bajada de archivo Completada: " + f.getName();
                } else {
                    result_text = result_text + "Bajada de archivo Fallada: " + f.getName(); 
                }
                if (lShowResult) {
                    //JOptionPane.showMessageDialog(frame, result_text, "Informacion", JOptionPane.INFORMATION_MESSAGE);
                }
        } catch (Exception e) {
            if (lShowResult) {
                //JOptionPane.showMessageDialog(frame, "No fue posible subir archivo, Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            throw new Exception(e);
        } finally {
            if (channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        }// end try
    }// end DownloadSFTP    

    public static int UploadSFTP(String pUser, String pPass,
                    String pHost, int pPort, String pInputStream, String cRemotePath, Boolean lShowResult)
                    throws Exception {
    	int nRet=0;
        //createRemoteDirSFTP(pUser, pPass, pHost, pPort, cRemotePath, false);
        JSch sftp = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        String cFileName = "";
        result_text = "";
        cRemotePath = cRemotePath.trim();
        if (!"/".equals(cRemotePath.substring(cRemotePath.length()-1))) {
            cRemotePath = cRemotePath + "/";
        }
        try {
            session = sftp.getSession(pUser, pHost, pPort);
            session.setPassword(pPass);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(cRemotePath);
            File f = new File(pInputStream);
            if (f.exists()) {
                cFileName = f.getName();
                FileInputStream ioStream=new FileInputStream(f);
                channelSftp.put(ioStream, cFileName);
                ioStream.close();
                Thread.sleep(1000);
                //if(!channelSftp.stat(cRemotePath + "/" + cFileName)){
                if(channelSftp.stat(cRemotePath + cFileName).getSize() > 0){
                    result_text = result_text + "Subida de archivo Completada: " + cFileName;
                    nRet=1;
                } else {
                    result_text = result_text + "Subida de archivo Fallada: "  + cFileName; 
                    nRet=0;
                }
            } else {
                result_text = result_text + "Archivo no existente en disco local: " + pInputStream; 
                nRet=0;
            }
            if (lShowResult) {
                //JOptionPane.showMessageDialog(frame, result_text, "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
        	nRet=0;
            if (lShowResult) {
                //JOptionPane.showMessageDialog(frame, "No fue posible subir archivo, Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            throw new Exception(e);
        } finally {
            if (channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        }// end try
        return nRet;
    }// end UploadSFTP    
    

    public static void createRemoteDirSFTP(String pUser, String pPass,
                    String pHost, int pPort, String cRemotePath, Boolean lShowResult)
                    throws Exception {
        JSch sftp = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        result_text = "";
        cRemotePath = cRemotePath.trim();
        if (!"/".equals(cRemotePath.substring(cRemotePath.length()-1))) {
            cRemotePath = cRemotePath + "/";
        }
        try {
            session = sftp.getSession(pUser, pHost, pPort);
            session.setPassword(pPass);
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.mkdir(cRemotePath);
            channelSftp.cd(cRemotePath);
            result_text = "Directorio remoto creado satisfactoriamente !!!";
            if (lShowResult) {
                //JOptionPane.showMessageDialog(frame, result_text, "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            if (lShowResult) {
                //JOptionPane.showMessageDialog(frame, "No fue posible crear directorio remoto, Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            //throw new Exception(e);
        } finally {
            if (channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        }// end try
    }// end createRemoteDirSFTP    
    
        /*
    try {
            // El ejemplo es de una ejecucion Cliente Windows y Servidor Linux aunque esto no es relevante
            DescargarPorSFTP("miusuario", "clavesupersecreta", "192.168.1.100", 22, "C:\\devtroce.jpg", "/media/devtroce.jpg");
            System.out.println("descarga satisfactoria...");
    } catch (Exception e) {
            e.printStackTrace();
    }        
        */
    
    
}
