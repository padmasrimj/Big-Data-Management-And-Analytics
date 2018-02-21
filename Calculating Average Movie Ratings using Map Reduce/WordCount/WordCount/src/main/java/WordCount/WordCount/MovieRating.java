package WordCount.WordCount;

import java.io.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieRating {

	public static class TokenizerMapper extends Mapper<Object, Text, Text, DoubleWritable> {

		private Text movieId = new Text();
		private DoubleWritable movieRating = new DoubleWritable();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			try {

				if (!value.toString().startsWith("userId")) {
					String[] stringTokens = value.toString().split(",");
					movieId.set(stringTokens[1]);
					Double rating = Double.parseDouble(stringTokens[2].toString().trim());
					movieRating.set(rating);
					context.write(movieId, movieRating);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class DoubleAverageReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();

		public void reduce(Text key, Iterable<DoubleWritable> values, Context context)
				throws IOException, InterruptedException {
			int count = 0;
			double sum = 0.0;

			for (DoubleWritable val : values) {
				sum += val.get();
				count += 1;
			}

			double averageRating = sum / count;
			result.set(Math.round(averageRating*100.0)/100.0);
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
		job.setCombinerClass(DoubleAverageReducer.class);
		job.setReducerClass(DoubleAverageReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}