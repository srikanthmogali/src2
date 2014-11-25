import com.dropbox.core.*;
import com.dropbox.core.util.IOUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.security.InvalidKeyException;
import java.util.*;

public class DropboxSecuredSwing {
    static DbxClient client;
    public static void main(String[] args) throws IOException, DbxException, InvalidKeyException {
        createrootpathFolders();

        System.out.println(System.getProperty("user.home"));
        final String APP_KEY = "si6rbzj8w7urk15";
        final String APP_SECRET = "fsb3oihvh2usaoy";
        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());
        DbxWebAuthNoRedirect authWeb = new DbxWebAuthNoRedirect(config, appInfo);
        String authorizeUrl = authWeb.start();
        System.out.println("1. Go to: " + authorizeUrl);
        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        System.out.println("3. Copy the authorization code.");_F2AvJRFpvEAAAAAAAABsA7fD2vbj0oh6SSC2NSqa1s




        try {
            Desktop desktop = Desktop.getDesktop();
            if (Desktop.isDesktopSupported()
                    && desktop.isSupported(Desktop.Action.BROWSE)) {
                java.net.URI uri = new java.net.URI(authorizeUrl);
                desktop.browse(uri);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (java.net.URISyntaxException ex) {
            ex.printStackTrace();
        }
        String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        DbxAuthFinish authFinish = authWeb.finish(code);
        System.out.println(authFinish.accessToken);
String content=null;
        try {

              content = authFinish.accessToken;
            System.out.println("content="+content);
            File file = new File(System.getProperty("user.home")+"/Desktop/AccessCode.txt");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }


        String accessToken =content
               // "_F2AvJRFpvEAAAAAAAABUPyb_9qT9EcFSI3sJWTOu6A8vWQrWiR06Vj-2Z9rahfk"
               ;  //used for signing in
        System.out.println(content.length());
        System.out.println(accessToken);//code inserted


        client = new DbxClient(config, accessToken);
        System.out.println("Linked account: " + client.getAccountInfo().displayName);


        DbxEntry.WithChildren listing = client.getMetadataWithChildren("/");
        DbxEntry.WithChildren[] listingArray=new DbxEntry.WithChildren[100];
        StringBuffer z=new StringBuffer("/");
        FolderCreation.viewFiles(client, z);
        new DropboxSecuredSwing().fileDialog1();
    }



    public static void delete(File file)
            throws java.io.IOException{
        if(file.isDirectory()){
            //directory is empty, then delete it
            if(file.list().length==0){
                file.delete();
                System.out.println("Directory is deleted : "
                        + file.getAbsolutePath());
            }else{
                //list all the directory contents
                String files[] = file.list();
                for (String temp : files) {
                    //construct the file structure
                    File fileDelete = new File(file, temp);
                    //recursive delete
                    delete(fileDelete);
                }
                //check the directory again, if empty then delete it
                if(file.list().length==0){
                    file.delete();
                    System.out.println("Directory is deleted : "
                            + file.getAbsolutePath());
                }
            }
        }else{
            //if file, then delete it
            file.delete();
            System.out.println("File is deleted : " + file.getAbsolutePath());
        }
        new File(FolderCreation.ROOTPATH+"/Temp").mkdirs();
    }
    public void fileDialog1()   {
        JFrame dropboxFrame = new JFrame("Select Encrypted Dropbox Files ");
        dropboxFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = dropboxFrame.getContentPane();
        final JLabel directoryLabel = new JLabel(" ");

        directoryLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        JButton directoryButton=new JButton("clean Files ");
        contentPane.add(directoryButton, BorderLayout.NORTH);

        final JLabel filenameLabel = new JLabel(" Select Encrypted Files of ID");
        filenameLabel.setFont(new Font("Arial", Font.BOLD | Font.PLAIN, 22));
        contentPane.add(filenameLabel, BorderLayout.SOUTH);

        JFileChooser chooserFile = new JFileChooser(FolderCreation.ROOTPATH);
        chooserFile.setControlButtonsAreShown(false);
        contentPane.add(chooserFile, BorderLayout.CENTER);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser theFileChooser = (JFileChooser) actionEvent
                        .getSource();
                String command = actionEvent.getActionCommand();
                if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                    File fileSelected = theFileChooser.getSelectedFile();
                    System.out.println(fileSelected);
                    String file1=(String)fileSelected.getAbsolutePath().split(FolderCreation.ROOTPATH)[1];
                    System.out.println("file1"+file1);
                    String file2=FolderCreation.ROOTPATH+"/Temp"+file1;
                    System.out.println(file2);
                    String file3= (String) FolderCreation.pathSplitter(file2).get(0);//this is the decrypted file path
                    System.out.println(file3);
                    File file4=new File(file3);
                    file4.mkdirs();
                    File decFile=new File(file2);
                    try {
                        IOUtil.decrypt(new File(String.valueOf(fileSelected)) ,decFile );
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    try {
                        Desktop.getDesktop().open(decFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
                    directoryLabel.setText(" ");
                    filenameLabel.setText(" ");
                }
            }
        };
        ActionListener actionListener1 = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("hi");
                File index=new File(FolderCreation.ROOTPATH+"/Temp");
                try {
                    delete(index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        chooserFile.addActionListener(actionListener);
        directoryButton.addActionListener(actionListener1);
        dropboxFrame.pack();
        dropboxFrame.setVisible(true);
    }
    private static void createrootpathFolders(){
        File rootFolders= new File(FolderCreation.ROOTPATH);
        rootFolders.mkdirs();
        rootFolders=new File(FolderCreation.ROOTPATH+"/Temp");
        rootFolders.mkdirs();
    }
}
//286825044 id of user