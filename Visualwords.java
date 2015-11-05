

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bruce
 */
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import static net.semanticmetadata.lire.utils.ImageUtils.cropImage;
import static net.semanticmetadata.lire.utils.ImageUtils.scaleImage;
import net.semanticmetadata.lire.utils.LuceneUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
public class Visualwords {
    private static String dir="images";
    private static String index="./images_index";
    int numClusters=2000;
    public static void setUp(String dirName, String indexPath){
        dir = dirName;
        index=indexPath;
    }
    
    public static void indexFiles(ArrayList<String> images)throws IOException{
        ChainedDocumentBuilder documentBuilder = new ChainedDocumentBuilder();
        documentBuilder.addBuilder(new SurfDocumentBuilder());
        documentBuilder.addBuilder(DocumentBuilderFactory.getColorLayoutBuilder());
        IndexWriter iw = LuceneUtils.createIndexWriter(index, true);//, LuceneUtils.AnalyzerType.WhitespaceAnalyzer);
  
        for(String identifier: images){
            try{
                BufferedImage img = ImageIO.read(new File(identifier));
                BufferedImage processedImage = cropImage(scaleImage(img,200,300),20, 20, 160, 260);
                Document doc = documentBuilder.createDocument(processedImage, identifier);
                iw.addDocument(doc);
               
            }catch(IOException e){
                System.out.println(e);
            }
        }
        iw.close();
        //IndexReader ir = DirectoryReader.open(FSDirectory.open(new File(index)));
        //BOVWBuilder sfh = new BOVWBuilder(ir, new SurfFeature(), 1000, 500);
        //sfh.index();
        
        System.out.println("Finished indexing");
    }
    public static void main(String[] args)throws IOException{
        boolean passed = false;
        if (args.length == 2) {
            File f = new File(args[0]);
            System.out.println("Indexing images in " + args[0]);
            if (f.exists() && f.isDirectory()){
                    passed = true;
            }
        }
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"Visualwords <directory> <index directory>\" to index files of a directory.");
            System.exit(1);
        }
        setUp(args[0], args[1]);
        ArrayList<String>images = new ArrayList();
        File directory = new File(args[0]);           
        String[] extensions = new String[] {"jpg", "jpeg"};
        List<File> files = (List<File>)org.apache.commons.io.FileUtils.listFiles(directory, extensions, true);
        for(File file: files){
            images.add(file.getCanonicalPath());
        }
       
        indexFiles(images);    
                
    }
}
