package WordCount.WordCount;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		List<String> stopWordsList = new ArrayList<String>();

		public TokenizerMapper() {
			try {
				FileReader fr = new FileReader("common-english-words-with-contractions.txt");
				BufferedReader br = new BufferedReader(fr);
				String line = "";

				while ((line = br.readLine()) != null) {
					StringTokenizer tokenizer = new StringTokenizer(line, ",");
					while (tokenizer.hasMoreTokens()) {
						stopWordsList.add(tokenizer.nextToken());
					}
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {
				StringTokenizer itr = new StringTokenizer(value.toString());
				while (itr.hasMoreTokens()) {
					String newToken = itr.nextToken().toString().trim();
					newToken = newToken.replaceAll("[^a-zA-Z]+", "");
					newToken = newToken.toLowerCase();
					if (newToken.length() >= 5 && !stopWordsList.contains(newToken)) {
						word.set(newToken);
						context.write(word, one);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapred.job.tracker", "hdfs://cshadoop1:61120");
		conf.set("yarn.resourcemanager.address", "cshadoop1.utdallas.edu:8032");
		conf.set("mapreduce.framework.name", "yarn");
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}