package JavaHDFS.JavaHDFS.Part2;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class DecompressCorpus {
	
	public static boolean downloadBook(String location, String directory)
            throws IOException {
        	URL url = new URL(location);
       	    URLConnection myURLConnection = url.openConnection();
          
            myURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            myURLConnection.connect();
            InputStream inputStream = new BufferedInputStream(myURLConnection.getInputStream());
            Configuration config = new Configuration();
            config.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/core-site.xml"));
            config.addResource(new Path("/usr/local/hadoop-2.4.1/etc/hadoop/hdfs-site.xml"));

            FileSystem fs = FileSystem.get(URI.create(directory), config);
            if(!fs.exists(new Path(directory))){
            	OutputStream outStream = fs.create(new Path(directory));
                        	IOUtils.copyBytes(inputStream, outStream, 4096, true);
            	
            	return true;
            }
            else{
            	System.out.println("File Already Exists!");
            	return false;
            }
    }	

	public static void main(String[] args) {
	
		String downloadLocation=args[0];
	
		String url="https://corpus.byu.edu/wikitext-samples/text.zip";
	 String destination=downloadLocation+"/";
		String loc=downloadLocation+"/text";
		boolean flag;
		try {
			flag = downloadBook(url,loc);
			if (flag)
			{
				try {
					ZipExtractor.unzip(loc,destination);
				   } 
				catch (Exception e)
				{
					e.printStackTrace();
				 }
				
			}
			else
			{
				System.out.println("Decompression Failed!");
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	
	}

	}

