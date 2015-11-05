/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testapplication;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.imageanalysis.GenericDoubleLireFeature;
import net.semanticmetadata.lire.imageanalysis.SurfFeature;
import net.semanticmetadata.lire.imageanalysis.bovw.BOVWBuilder;
import net.semanticmetadata.lire.impl.ChainedDocumentBuilder;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;
import net.semanticmetadata.lire.impl.SurfDocumentBuilder;
import net.semanticmetadata.lire.indexing.parallel.ParallelIndexer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;

/**
 *
 * @author bruce
 */
public class TestApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String queryImage = "cheetah.jpeg";
        String datasetPath = "/home/bruce/NetBeansProjects/946Indexer/images";
        String indexPath = "/home/bruce/NetBeansProjects/Visualwords/index";

        ParallelIndexer parallelIndexer = new ParallelIndexer(16, indexPath, datasetPath, true) {
            @Override
            public void addBuilders(ChainedDocumentBuilder builder) {
                builder.addBuilder(new SurfDocumentBuilder());
            }
        };
        parallelIndexer.run();

        System.out.println("** SURF BoVW");
        BOVWBuilder bovwBuilderSURF = new BOVWBuilder(DirectoryReader.open(FSDirectory.open(new File(indexPath))), new SurfFeature(), 4000, 128);
        bovwBuilderSURF.index();
        
        IndexReader reader = DirectoryReader.open(new RAMDirectory(FSDirectory.open(new File(indexPath)), IOContext.READONCE));

        GenericFastImageSearcher gfisSURF = new GenericFastImageSearcher(10, GenericDoubleLireFeature.class, DocumentBuilder.FIELD_NAME_SURF
                + DocumentBuilder.FIELD_NAME_BOVW_VECTOR, true, reader);
       
        SurfDocumentBuilder SurfBuilder = new SurfDocumentBuilder();
      
        Document docSURF = bovwBuilderSURF.getVisualWords(SurfBuilder.createDocument(ImageIO.read(new File(queryImage)), queryImage));
       
        ImageSearchHits hitsSURF = gfisSURF.search(docSURF, reader);
  
        System.out.println("SURF results:");
        for(int i= 0; i < hitsSURF.length(); i++){
            String fileName = hitsSURF.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
            System.out.println(i + ": " + hitsSURF.score(i) + " ~ " + fileName);
        }
       
    }
    
}
