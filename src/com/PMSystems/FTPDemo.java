package com.PMSystems;

import java.io.*;
import java.net.*;

public class FTPDemo {
    /**
             * Download a file from a FTP server. A FTP URL is generated with the following syntax:
         * <code>ftp://user:password@host:port/filePath;type=i</code>.
             *
             * @param ftpServer FTP server address (incl. optional port ':portNumber').
             * @param user Optional user name to login.
             * @param pwd Optional password for <i>user</i>.
             * @param fileName Name of file to download (with optional preceeding relative path, e.g. one/two/three.txt).
             * @param destination Destination file to save.
         * @throws MalformedURLException, IOException on error.
         */
            public void download(String ftpServer, String user, String pwd, String fileName/*,File destination*/) throws MalformedURLException, IOException {

            if (ftpServer != null && fileName != null/* && destination != null*/) {
                StringBuffer sb = new StringBuffer("ftp://");
                if (user != null && pwd != null) { //need authentication?
                    sb.append(user);
                    sb.append(':');
                    sb.append(pwd);
                    sb.append('@');
                }//else: anonymous access
                sb.append(ftpServer);
                sb.append('/');
                sb.append(fileName);
                //sb.append(";type=i"); //a=ASCII mode, i=image (binary) mode, d= file directory listing

                System.out.println("\n-> FTP URL: "+sb);

                //BufferedInputStream bis = null;
                //BufferedOutputStream bos = null;
                BufferedReader rd =null;
                PrintWriter pr = null;
                try {
                    URL url = new URL(sb.toString());
                    URLConnection urlc = url.openConnection();

                    //bis = new BufferedInputStream(urlc.getInputStream());
                    //bos = new BufferedOutputStream(new FileOutputStream(destination.getName()));

                    /*
                    int i;
                    while ((i = bis.read()) != -1) { //read next byte until end of stream
                        bos.write(i);

                    }//next byte
*/
                    rd = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                    //pr = new PrintWriter(new FileOutputStream(destination.getPath()));
                    pr = new PrintWriter(new FileOutputStream("E:\\downloaded.csv"));

                    String str = rd.readLine();
                    while(str!=null){
                        System.out.println("[" + str + "]");
                        pr.println(str);
                        //pr.write(str);
                        pr.flush();
                        str = rd.readLine();
                    }


                }catch(IOException io){
                    System.out.println("\nIOEXCEPTION: "+io.getMessage());
                    io.printStackTrace();
                }catch(Exception e){
                    System.out.println("\nEXCEPTION: "+e.getMessage());
                    e.printStackTrace();
                }finally {
                    //if (bis != null) try { bis.close(); } catch (IOException ioe) { /* ignore*/ }
                    //if (bos != null) try { bos.close(); } catch (IOException ioe) { /* ignore*/ }
                    if (rd != null) try { rd.close(); } catch (IOException ioe) { /* ignore*/ }
                    if (pr != null) try { pr.close(); } catch (Exception ioe) { /* ignore*/ }
                }

            }//else: input unavailable
        }//download()

        public void upload() {
            try {
                URL url = new URL("ftp://demo:test@bilal/BLR.txt");
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                conn.connect();
                OutputStream out = conn.getOutputStream();

                FileInputStream fis = new FileInputStream(
                    "c:\\Vauxhalll\\FTP\\testin\\BLR.txt");
                int b;
                while ( (b = fis.read()) != -1) {
                    out.write(b);
                }
                out.close();
                fis.close();

            }
            catch (IOException ie) {
                System.out.println(ie);
            }
        }//upload
        public static void main(String[] args) {
            try {
                File file = new File("E:\\ftpdata.txt");
                file.createNewFile();
//String ftpServer, String user, String pwd, String fileName, File destination

//                new FTPDemo().download("192.168.10.108", "demo", "test", "BCBS.csv");
                new FTPDemo().download("ummehabiba.com", "ummeh", "ummeh", "bilal/sub01.csv");
               // new FTPDemo().upload();
            }
            catch (Exception ex) {
                System.out.println("Exceptioni: "+ex.getMessage());
                ex.printStackTrace();
            }
        }
}//class

