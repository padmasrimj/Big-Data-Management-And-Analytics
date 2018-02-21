package JavaHDFS.JavaHDFS.Part2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
 
public class ZipExtractor {

	private static final int BUFFER_SIZE = 4096;
    
	public static void unzip(String zipLocation, String destination) throws IOException {
	    String uri = zipLocation;
	    Configuration config = new Configuration();
	    config.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
        config.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));
	    FileSystem fs = FileSystem.get(URI.create(uri), config);
	    
	    Path inputPath = new Path(uri);
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	     ZipInputStream zipIn = new ZipInputStream((fs.open(inputPath)));
	      ZipEntry entry = zipIn.getNextEntry();
	        while (entry != null) {
	            String filePath = destination + File.separator + entry.getName();
	            if (!entry.isDirectory()) 
	            {
	            	out = fs.create(new Path(destination+"/"+entry.getName()));
	            	byte[] bytesIn = new byte[BUFFER_SIZE];
	                int read = 0;
	            	while (( read = zipIn.read(bytesIn)) != -1)
	            	{
	                    out.write(bytesIn, 0, read);
	                }
	            }
	            else 
	            {
	                File dir = new File(filePath);
	                dir.mkdir();
	            }
	            zipIn.closeEntry();
	            entry = zipIn.getNextEntry();
	        }
	        fs.delete(inputPath);
		    zipIn.close();  
	        out.close();
	      
	      } 
	    finally
	    {
	      IOUtils.closeStream(in);
	      IOUtils.closeStream(out);
	    }
	  }    
	    
        
    
    	
}