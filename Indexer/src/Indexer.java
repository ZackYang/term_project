
import java.io.File;
import java.io.IOException;
import net.semanticmetadata.lire.imageanalysis.features.global.CEDD;
import net.semanticmetadata.lire.imageanalysis.features.global.FCTH;
import net.semanticmetadata.lire.indexers.parallel.ParallelIndexer;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bruce
 */
public class Indexer {
  public static void main(String[] args) throws IOException {
        // Checking if arg[0] is there and if it is a directory.
        boolean passed = false;
        if (args.length > 0) {
            File f = new File(args[0]);
            System.out.println("Indexing images in " + args[0]);
            if (f.exists() && f.isDirectory()) passed = true;
        }
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"ParallelIndexing <directory>\" to index files of a directory.");
            System.exit(1);
        }

        // use ParallelIndexer to index all photos from args[0] into "index" ... use 6 threads (actually 7 with the I/O thread).
        ParallelIndexer indexer = new ParallelIndexer(6, "index", args[0]);
        // use this to add you preferred builders. For now we go for CEDD, FCTH and AutoColorCorrelogram
   
        //indexer.addExtractor(FCTH.class, SimpleExtractor.KeypointDetector.GaussRandom);
        indexer.addExtractor(CEDD.class);
        indexer.addExtractor(FCTH.class);
        indexer.run();
        System.out.println("Finished indexing.");
    }
}
    

