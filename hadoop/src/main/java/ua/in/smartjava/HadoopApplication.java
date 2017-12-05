package ua.in.smartjava;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.hadoop.fs.FsShell;

import java.io.IOException;

import ua.in.smartjava.mapreducers.words.AllWordReducer;
import ua.in.smartjava.mapreducers.words.LettersMapper;
import ua.in.smartjava.mapreducers.words.WordMapper;
import ua.in.smartjava.mapreducers.words.WordReducer;

@SpringBootApplication
@ImportAutoConfiguration(classes = HadoopConfig.class)
public class HadoopApplication implements CommandLineRunner {

    @Autowired
    private FsShell shell;

    @Override
    public void run(String[] args) throws IOException {
        shell.lsr("/user").forEach(
                it -> System.out.println(it.getPath())
        );
//pg1661.txt

        RunningJob watsonsJob = createJob(jobWatsonWordsCount(), "watson search", "doyle.txt");
        RunningJob allWordsJob = createJob(jobAllWordsCount(), "all words usage search", "doyle.txt");
        RunningJob lettersJob = createJob(jobAllLetterCount(), "all letters usage search", "doyle.txt");
        watsonsJob.waitForCompletion();
        allWordsJob.waitForCompletion();
        lettersJob.waitForCompletion();
    }

    public static JobConf jobWatsonWordsCount() {
        JobConf jobConfig = new JobConf();
        jobConfig.setJarByClass(HadoopApplication.class);

        jobConfig.setJobName("All words search");
        jobConfig.setMapperClass(WordMapper.class);
        jobConfig.setReducerClass(WordReducer.class);
        jobConfig.setOutputKeyClass(Text.class);
        jobConfig.setOutputValueClass(IntWritable.class);
        return jobConfig;
    }

    public static JobConf jobAllWordsCount() {
        JobConf jobConfig = new JobConf();
        jobConfig.setJarByClass(HadoopApplication.class);

        jobConfig.setJobName("Watsons search");
        jobConfig.setMapperClass(WordMapper.class);
        jobConfig.setReducerClass(AllWordReducer.class);
        jobConfig.setOutputKeyClass(Text.class);
        jobConfig.setOutputValueClass(IntWritable.class);
        return jobConfig;
    }

    public static JobConf jobAllLetterCount() {
        JobConf jobConfig = new JobConf();
        jobConfig.setJarByClass(HadoopApplication.class);

        jobConfig.setJobName("letters search");
        jobConfig.setMapperClass(LettersMapper.class);
        jobConfig.setReducerClass(AllWordReducer.class);
        jobConfig.setOutputKeyClass(Text.class);
        jobConfig.setOutputValueClass(IntWritable.class);
        return jobConfig;
    }

    public static RunningJob createJob(JobConf jobConfig, String jobName, String fileName) throws IOException {
        Job job = new Job(jobConfig);

        FileInputFormat.addInputPath(jobConfig, new Path("hdfs://cloudera-1:8020/user/root/" + fileName));
        FileOutputFormat.setOutputPath(jobConfig, new Path("/tmp/results/" + jobName));

        return job.getJobClient().submitJob(jobConfig);
    }

    public static void main(String[] args) {
        SpringApplication.run(HadoopApplication.class, args);
    }
}
