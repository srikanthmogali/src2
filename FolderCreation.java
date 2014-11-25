import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by srikanthmogali on 8/30/14.
 */
public class FolderCreation {

    static DbxEntry.WithChildren listing;
    private static FileOutputStream outputStream;
    static DbxEntry.File downloadedFile;
    static final String ROOTPATH= System.getProperty("user.home")+"/Desktop/Dropbox/Encrption";
    /**
     * A method for Splitting the File path into its folders and filename..into exactly two
     * @param j The path of the file
     */
    public static List pathSplitter(String j) {
        String[] p=String.valueOf(j).split("/");
        String g=p[p.length-1];
        String k=String.valueOf(j).replace(g, "");
        List<String> o=new ArrayList<String>();
        o.add(k);
        o.add(g);
        return o;
    }

    /**
     * A method for viewing the files in the dropbox path
     * @param z The dtopbox path of the file used StringBuffer
     * @param client the dropbox client object which is of type final., the client  finished authentication before calling this method
     */
    public static void viewFiles(DbxClient client,StringBuffer z) throws DbxException, IOException {
            listing=client.getMetadataWithChildren(String.valueOf(z));
            for (DbxEntry child : listing.children) {
            File fg=null;
            if(child.isFile()){
            fg=new File(ROOTPATH+child.path);
            System.out.println(fg);
            FileOutputStream outputStream= new FileOutputStream(ROOTPATH+child.path);
            DbxEntry.File downloadedFile = client.getFile(child.path, null,outputStream);
            }
            if (child.isFolder()) {
            createFolder(child.path);
            viewFiles(client, new StringBuffer(child.path));
            }
    }
}
    /**
     * A method for downloading all of dropbox files
     */
    public static void createFolder(String s) {
    File abcd= new File( ROOTPATH + s );
        abcd.mkdirs();
    }
}
