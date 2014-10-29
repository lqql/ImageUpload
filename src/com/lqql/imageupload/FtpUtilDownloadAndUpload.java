package com.lqql.imageupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import android.util.Log;

public class FtpUtilDownloadAndUpload {
	/*
	 * �����ļ��ϴ��ļ�
	 */
	private String url;
    private int port;
    private String username;
    private String password;
    public FtpUtilDownloadAndUpload(String url, int port, String username, String password) {
        this.url = url;
        this.port = port;
        this.username = username;
        this.password = password;
        }
    public void seturl(String Url){
    	url=Url;
    }
    public void setusername(String userName){
    	username=userName;
    }
    public void setpassword(String Password){
    	password=Password;
    }
    
	//�����ļ��ķ���
	public boolean downFile(
			String url,//Ftp������hostname
			int port,//Ftp�������˿�
			String username,//Ftp��¼���˺�
			String password,//ftp�ĵ�¼����
			String remotePath,//Ҫ���ص��ļ��ķ����������·��
			String fileName,//Ҫ���ص��ļ��� 
			String locaPath//��Ҫ���浽�ñ���·��
			){
		
		boolean success = false;
		
		FTPClient ftp = new FTPClient();
		int reply;
		Log.i("test", "�ļ�������");
		try {
			ftp.setDefaultPort(port);
			ftp.configure(getFtpConfig());
			ftp.connect(url);
			//�������Ĭ�϶˿ڣ�����ʹ��ftp.connect(url)�ķ�ʽֱ������FTP������
			ftp.login(username, password);//���е�¼
			ftp.setControlEncoding("gbk");//���ñ��뼯ΪGBK֧������
			
			reply = ftp.getReplyCode();
			if(!FTPReply.isPositiveCompletion(reply)){
				 //���Ӳ��ɹ�ֱ�ӷ���
				ftp.disconnect();
				return success;
			}
			ftp.enterLocalPassiveMode();//��Ϊ����ģʽ
			Log.i("test", "��Ҫ�����ļ���");
			ftp.changeWorkingDirectory(remotePath);//ת�Ƶ�FTP��������Ŀ¼
			Log.i("test", "��ִ����");
			FTPFile[] fs = ftp.listFiles();//��ø�Ŀ¼�µ������ļ�
			Log.i("test", "�ļ��ĸ���=="+fs.length);
			for(FTPFile ff:fs){
				Log.i("test", "����ִ�� ��ff.getName() == "+ ff.getName());
				
				
				if(ff.getName().equals(fileName)){
					Log.i("test", "123456�ҵ��ˡ����Ĳ���.txt�ļ���");
					File localFile = new File(locaPath+ "���Ĳ��Զη�.txt");//���ñ��浽���ص�·��
					Log.i("test", "�����ļ��Ĵ����" + locaPath + "���Ĳ��Զη�.txt");
					OutputStream os = new FileOutputStream(localFile);
					String loadName = "/"+fileName;
					ftp.retrieveFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), os);//��ȡ�ļ�  ��������ʽ�浽����
					
					os.close();
				}
				
			}
			ftp.logout();//�˳���¼
			success = true;//��ʾ�ɹ�
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(ftp.isConnected()){
				//������ӻ�����
				
				try {
					ftp.disconnect();//�ر�����
				} catch (IOException e) {
					e.printStackTrace();
				}//
				
			}
		}
		return success;
	}
	
	
	
	
	public boolean uploadFiles(
            String url,//��������hostname
            int port,//ftp�Ķ˿ں�
            String username,//ftp���û��˻�
            String password,//ftp�û�����
            String path,//ftp����������·��            
            List<String> files//��Ҫ�ϴ����ļ��б�
            ){
        
        boolean success = false;
        
        FTPClient ftp = new FTPClient();//ftp�ͻ���ʵ��
        
        int reply;//�û��Ƿ��½�ɹ���״̬��
        Log.i("test", "");
    
        
        
        try {
            //ftp.setDefaultPort(port);
            ftp.setControlEncoding("GBK");
           // ftp.configure(getFtpConfig());
            ftp.connect(url, 21);
            //�������Ĭ�϶˿ڣ�����ʹ��ftp.connect(url)�ķ�ʽֱ������FTP������
            ftp.login(username, password);//���е�¼
           // ftp.setControlEncoding("gbk");//���ñ���ΪGBK֧������
            
            
            reply = ftp.getReplyCode();
            if(!FTPReply.isPositiveCompletion(reply)){
                 //���Ӳ��ɹ�ֱ�ӷ���
                ftp.disconnect();
                return success;
            }
            ftp.enterLocalPassiveMode();//���ñ���ģʽ
            ftp.changeWorkingDirectory(path);//ת�Ƶ�FTP��������Ŀ¼
            
            for (String filePath : files) {
                File file =new File(filePath);  
                String fileName = file.getName();  
                FileInputStream input = new FileInputStream(file); 
                ftp.storeFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), input);//��
                input.close();
            }
            ftp.logout();//�˳���¼
            success = true;//��״̬����Ϊ�ɹ�
            
            Log.i("test", "success == " + success);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(ftp.isConnected()){
                //����������������ӵ���ر�����
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        return success;
    }
	
	public boolean uploadFiles(
            String path,
            List<String> files
            ){
        
        boolean success = false;
        
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        
        int reply;
        
        try {
             
            Log.i("test", "ftp login");
            
            ftp.connect(url, port); 

            ftp.login(username, password);
            
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE); 
            
            reply = ftp.getReplyCode();
            
            
            if(!FTPReply.isPositiveCompletion(reply)){
                ftp.disconnect();
                return success;
            }
            
            ftp.changeWorkingDirectory(path);
            ftp.enterLocalPassiveMode();
            for (String filePath : files) {
                File file =new File(filePath);  
                String fileName = file.getName();  
                FileInputStream input = new FileInputStream(file); 
                ftp.storeFile(new String(fileName.getBytes("GBK"),"ISO-8859-1"), input);//��
                input.close();
            }
            
            ftp.logout();
            success = true;
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(ftp.isConnected()){
                try {
                    ftp.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        return success;
    }
	
	
	
    
    /**

     * ����FTP�ͷ��˵�����--һ����Բ�����

     * @return

     */

   private static FTPClientConfig getFtpConfig(){

        FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);

        ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);

       return ftpConfig;

    }

	

}
