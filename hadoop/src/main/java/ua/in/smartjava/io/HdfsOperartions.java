package ua.in.smartjava.io;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.stream.Stream;

public class HdfsOperartions {

    private static final String[] stories = {
            "agrange.txt", "b-p_plan.txt", "bascombe.txt", "beryl.txt", "blanced.txt", "blkpeter.txt",
            "bluecar.txt", "cardbox.txt", "caseide.txt", "charles.txt", "copper.txt", "creeping.txt",
            "crookman.txt", "danceman.txt", "devilsf.txt", "doyle-adventures-380.txt", "doyle-case-381.txt",
            "doyle-his-382.txt", "doyle-hound-383.txt", "doyle-lost-385.txt", "doyle-memoirs-386.txt",
            "doyle-poison-387.txt", "doyle-return-388.txt", "doyle-sign-389.txt", "doyle-study-390.txt",
            "doyle-through-391.txt", "doyle-valley-392.txt", "dyingdec.txt", "finalpro.txt", "glorias.txt",
            "hislabow.txt", "illustr.txt", "ladyfran.txt", "lostw11.txt", "navaltr.txt", "noblebat.txt",
            "norwood.txt", "poisn10.txt", "poisn11.txt", "priory.txt", "redhead.txt", "resident.txt",
            "retired.txt", "rholm10.txt", "rholm11b.txt", "second.txt", "stockbrk.txt", "study10.txt",
            "sussex-v.txt"
    };

    private static final int BUFFER_SIZE = 4096;

    public HdfsOperartions() throws IOException {
    }

    public static void main(String[] args) throws Exception {
        String fileName = "hdfs://cloudera-1:8020/user/root/doyle.txt";
        Configuration conf = new Configuration();
        URI uri = URI.create(fileName);

//        readWithInputStream(uri, conf);
//        copyLocalToHdfs("/tmp/wifi-12-04-2017__10:56:52.log", "hdfs://cloudera-1:8020/user/root/pg1661.txt");
        loadConanDoyleStoriesToHdfs();
//        getStatus();
//        copyWebToHdfs(new URL("http://www.textfiles.com/etext/AUTHORS/DOYLE/agrange.txt"),
// "hdfs://cloudera-1:8020/user/root/doyle");
//        readWithFSDataInputStream(uri, conf);

    }

    private static void loadConanDoyleStoriesToHdfs() {
        Stream.of(stories).map(story -> "http://www.textfiles.com/etext/AUTHORS/DOYLE/" + story)
                .peek(System.out::println)
//                .skip(1)
                .forEach(url -> copyWebToHdfs(map(url), "hdfs://cloudera-1:8020/user/root/doyle.txt"));
    }

    private static URL map(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void readWithInputStream(URI uri, Configuration conf) throws IOException, InterruptedException {
        FileSystem fs = FileSystem.get(uri, conf, "hdfs");
        try (InputStream inputStream = fs.open(new Path(uri))) {
            IOUtils.copyBytes(inputStream, System.out, BUFFER_SIZE, false);
        } finally {
            fs.close();
        }
    }

    private static void readWithFSDataInputStream(URI uri, Configuration conf) throws IOException,
            InterruptedException {
        FileSystem fs = FileSystem.get(uri, conf, "hdfs");
        try (FSDataInputStream fsDIS = fs.open(new Path(uri))) {

            IOUtils.copyBytes(fsDIS, System.out, BUFFER_SIZE, false);
            fsDIS.seek(0);
            IOUtils.copyBytes(fsDIS, System.out, BUFFER_SIZE, false);

        } finally {
            fs.close();
        }
    }

    private static void copyLocalToHdfs(String localSrc, String dst) throws Exception {
        InputStream in = new BufferedInputStream(new FileInputStream(localSrc));

        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dst), conf, "hdfs");
        OutputStream out = fs.append(new Path(dst), BUFFER_SIZE, () -> System.out.print("."));

        IOUtils.copyBytes(in, out, 4096, false);
    }

    private static void copyWebToHdfs(URL webUri, String dst) {
        HttpURLConnection urlConnection = null;
        Configuration conf = new Configuration();
        try (FileSystem fs = FileSystem.get(URI.create(dst), conf, "hdfs")) {
            urlConnection = (HttpURLConnection) webUri.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            if (!fs.exists(new Path(dst))) {
                createNewFile(dst);
            }
            OutputStream out = fs.append(new Path(dst), BUFFER_SIZE);

            IOUtils.copyBytes(in, out, 4096, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createNewFile(String dst) {
        Configuration conf = new Configuration();
        try (FileSystem fs = FileSystem.get(URI.create(dst), conf, "hdfs")) {
            fs.create(new Path(dst));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getStatus() {
        String fileName = "hdfs://cloudera-1:8020/user/root/pg1661.txt";
        Configuration conf = new Configuration();
        URI uri = URI.create(fileName);
        try (FileSystem fs = FileSystem.get(uri, conf, "hdfs");) {
            Path file = new Path("hdfs://cloudera-1:8020/user/root/doyle");
            FileStatus stat = fs.getFileStatus(file);
            /*
            stat.getLen();
            stat.getModificationTime();
            stat.getReplication();
            stat.getBlockSize();
            stat.getOwner();
            stat.getGroup();
            stat.getPermission();
            stat.getAccessTime();
            */
            System.err.println(stat);
            System.err.println(stat.getBlockSize() / 1024 / 1024 + " MB");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
