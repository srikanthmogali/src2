/**
 * Created by srikanthmogali on 11/3/14.
 */
/**
 * Created by srikanthmogali on 10/8/14.
 */
import com.dropbox.core.*;
import net.contentobjects.jnotify.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.io.FileReader;


public class ListenFiless
{
    static DbxClient client=null;
    public static void main(String [] args) throws IOException, DbxException {

        final String APP_KEY = "si6rbzj8w7urk15";

        final String APP_SECRET = "fsb3oihvh2usaoy";

        DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

        DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());

        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

        String authorizeUrl = webAuth.start();

        System.out.println("1. Go to: " + authorizeUrl);

        System.out.println("2. Click \"Allow\" (you might have to log in first)");
        String a=null;
        System.out.println("3. Copy the authorization code.");


        BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home")+"/AccessCode.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            a = sb.toString();
        } finally {
            br.close();
        }

        String accessToken =a.trim() ;  //used for signing in
        //accessToken=accessToken.replace(accessToken.substring(accessToken.length() - 1), "");
        //System.out.println(accessToken.charAt(0)+" hi");
        //System.out.println(accessToken);//code inserted

        client = new DbxClient(config, accessToken);

        System.out.println("Linked account: " + client.getAccountInfo().displayName);

        String path = FolderCreation.ROOTPATH+"/Temp";
        int changeFilemask =  JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;

        boolean watchSubtree = true;
        int watchID = JNotify.addWatch(path, changeFilemask, watchSubtree, new JNotifyListener(){

            @Override
            public void fileCreated(int arg0, String arg1, String arg2) {

                System.out.println( "created"+arg1+arg2);
            }

            @Override
            public void fileDeleted(int arg0, String arg1, String arg2) {
                // TODO Auto-generated method stub
                System.out.println( "deleted"+arg1+arg2);
            }

            @Override
            public void fileModified(int arg0, String arg1, String arg2) {
                // TODO Auto-generated method stub
                System.out.println( arg1+"   "+arg2);
                try {
                    upload(arg1, arg2);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fileRenamed(int arg0, String arg1, String arg2,
                                    String arg3) {
                // TODO Auto-generated method stub
                System.out.println("renamed");
            }});
        try
        {
            Thread.sleep(1000000);//this is for how much time the fileListener runs until it modifies or exits
        }
        catch (InterruptedException e1)
        {
        }

    }

    static void upload(String arg1, String arg2) throws IOException, DbxException {
        System.out.println(arg1);
        System.out.println(arg2);
        File inputFile = new File(arg1+"/"+arg2);
        FileInputStream inputStream = new FileInputStream(inputFile);
        try {
            System.out.println("hi");
            DbxEntry.File uploadedFile = client.uploadFile("/"+arg2,
                    DbxWriteMode.force(), inputFile.length(), inputStream);
            System.out.println("Uploaded: " + uploadedFile.toString());

        } finally {
            inputStream.close();
        }

    }
}