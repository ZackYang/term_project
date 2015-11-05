
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilder;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.imageanalysis.ColorLayout;
import net.semanticmetadata.lire.imageanalysis.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.Gabor;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;
import static net.semanticmetadata.lire.utils.ImageUtils.cropImage;
import static net.semanticmetadata.lire.utils.ImageUtils.scaleImage;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bohong Xu
 
 * Date: 4.11.15
 */
public class Searcher {
    public static void main(String[] args)throws IOException{
        BufferedImage processedImage= null;
        boolean passed = false;
        if (args.length == 2) {
            File f = new File(args[0]);
            if (f.exists()) {
                if(args[1].equals("-color") || args[1].equals("-texture") || args[1].equals("-combine") ||args[1].equals("-shape")){
                    try {
                        BufferedImage img = ImageIO.read(f);
                        processedImage = cropImage(scaleImage(img,200,300),20, 20, 160, 260);
                        passed = true;
                    }catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
        if (!passed) {
            System.out.println("No image or flag given as first argument.");
            System.out.println("Run \"Searcher <query image> -flag\" to search for <query image>.");
            System.exit(1);
        }
        IndexReader ir = null;
        ImageSearcher searcher = null;

        if(args[1].equals("-color")){
            ir = DirectoryReader.open(FSDirectory.open(new File("./color_index")));
//new File("/home/bruce/NetBeansProjects/946Indexer/color_index")));
            searcher = new GenericFastImageSearcher(10, ColorLayout.class);
        }
        else if(args[1].equals("-texture")){
            ir = DirectoryReader.open(FSDirectory.open(new File("./texture_index")));
                    //new File("/home/bruce/NetBeansProjects/946Indexer/texture_index")));
            searcher = new GenericFastImageSearcher(10, Gabor.class);
        }else if(args[1].equals("-shape")){
            ir = DirectoryReader.open(FSDirectory.open(new File("./shape_index")));
            //new File("/home/bruce/NetBeansProjects/946Indexer/shape_index")));
            searcher = new GenericFastImageSearcher(10, EdgeHistogram.class);
        }       
        else{
            ir = DirectoryReader.open(FSDirectory.open(new File("./combine_index")));
            
            //new File("/home/bruce/NetBeansProjects/946Indexer/combine_index")));
            searcher = new GenericFastImageSearcher(10, FCTH.class);
               
        }

        // searching with a image file ...
        ImageSearchHits hits = searcher.search(processedImage, ir);
        // searching with a Lucene document instance ...
//        ImageSearchHits hits = searcher.search(ir.document(0), ir);
        for (int i = 0; i < hits.length(); i++) {
            String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
            System.out.println(hits.score(i) + ": \t" + fileName);
        }
    
     
    
    }
}
