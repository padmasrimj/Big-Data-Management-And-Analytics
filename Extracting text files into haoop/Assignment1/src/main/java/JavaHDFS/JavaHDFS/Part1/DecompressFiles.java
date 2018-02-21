package JavaHDFS.JavaHDFS.Part1;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.Progressable;

public class DecompressFiles {
	public static void main(String[] args) throws Exception{
		
		String destination = args[0];
		
		Map<String, String> urls = new HashMap<String, String>();
		urls.put("20417","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/20417.txt.bz2");
		urls.put("5000-8","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/5000-8.txt.bz2");
		urls.put("132","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/132.txt.bz2");
		urls.put("1661-8","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/1661-8.txt.bz2");
		urls.put("972","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/972.txt.bz2");
		urls.put("19699","http://www.utdallas.edu/~axn112530/cs6350/lab2/input/19699.txt.bz2");
		
		
		
		InputStream inputStream = null;
		InputStream inputStream1 =null;
		OutputStream outputStream = null;
		OutputStream outputStream1 = null;
		FileSystem fs = null;
		 
		try{
			for(String url : urls.keySet())
		    {
		    	URL link = new URL(urls.get(url));
				inputStream = new BufferedInputStream(link.openStream());
				
				Configuration conf = new Configuration();
			    conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
			    conf.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));
			    
			    String urlString = destination + url + ".txt.bz2";
			    
			    
			    
			    fs = FileSystem.get(URI.create(urlString), conf);
			    outputStream = fs.create(new Path(urlString), new Progressable() {
			      public void progress() {  
			        System.out.print("*");
			      }
			    });
			    IOUtils.copyBytes(inputStream, outputStream, 4096, true);
			    
			    
			    
			    Path inputPath = new Path(urlString);
			    CompressionCodecFactory factory = new CompressionCodecFactory(conf);
			    CompressionCodec codec = factory.getCodec(inputPath);
			    if (codec == null) {
			      System.err.println("No codec found for " + urlString);
			      System.exit(1);
			    }			    
			      String outputUri =
			      CompressionCodecFactory.removeSuffix(urlString, codec.getDefaultExtension());    
			      inputStream1 = codec.createInputStream(fs.open(inputPath));
			      outputStream1 = fs.create(new Path(outputUri));
			      IOUtils.copyBytes(inputStream1, outputStream1, conf);
			      
			      
			      fs.delete(inputPath);
		    }
		}finally{
			IOUtils.closeStream(inputStream);
		    IOUtils.closeStream(outputStream);
		    IOUtils.closeStream(inputStream1);
		    IOUtils.closeStream(outputStream1);
			
		}
		
	}

}
