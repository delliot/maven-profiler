package com.central1.profiler.parser;

import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.zip.Inflater;


/**
 *
 */
public class GitCommitParser {

    private File gitDirectory;

    private final static String GIT_DIR = ".git";

    GitCommitParser( File dir ) throws IOException
    {

        try {
            this.gitDirectory = getGitDirRecursively( dir );
        } catch ( FileNotFoundException e ) {
            throw new IOException( e );
        }
    }

    public GitCommitParser() throws IOException
    {
            this(new File(System.getProperty("user.dir")));
    }

    private File getGitDirRecursively  ( File f ) throws FileNotFoundException
    {
        File[] files = f.listFiles();

        for ( File file : files )
        {
            if ( file.getName().toLowerCase().equals( GIT_DIR ) )
            {
                return file;
            }
        }

        if ( f.getParentFile() == null )
        {
            throw new FileNotFoundException( "No git directory was found!" );
        }

        return getGitDirRecursively( f.getParentFile() );
    }


    public String getHeadId()
    {
        File[] files = gitDirectory.listFiles();
        File head = null;

        Pattern p = Pattern.compile("[0-9a-fA-F]+");

        for ( File f : files )
        {
            if (f.getName().equals("HEAD"))
            {
                 head = f;
            }
        }


        FileInputStream fis = null;
        InputStreamReader isr;
        BufferedReader br;

        try {
            fis = new FileInputStream( head );
            isr = new InputStreamReader( fis );
            br = new BufferedReader( isr );


            String line;
            while ( ( line = br.readLine() ) != null )
            {
                if ( line.toLowerCase(Locale.ENGLISH).contains("ref:") )
                {
                    String path = gitDirectory.getAbsolutePath() + "/" + line.substring(5);
                    String headRef = line.substring(5);
                    System.out.println( path );
                    File ref = new File( path );
                    if ( ref.exists() )
                    {
                        br.close();
                        br = readerFromFile(new File(path));
                        line = br.readLine();
                        if ( line.matches("[0-9a-fA-F]+") )
                        {
                            return line.trim();
                        }
                    }
                    else
                    {
                        File packed = new File(gitDirectory.getAbsolutePath() + "/packed-refs");
                        if (packed.exists())
                        {
                            br.close();
                            br = readerFromFile(packed);
                            while ( ( line = br.readLine() ) != null )
                            {
                                if ( line.contains( headRef ) )
                                {
                                    return line.split(" " )[0];
                                }
                            }
                        }
                    }
                } else if ( line.matches("[0-9a-fA-F]+") )
                {
                    return line.trim();
                }

            }
        }
        catch ( IOException ioe )
        {

        }

        return "";
    }

    public Date getCommitDateTime(String file)
    {
        File commit = null;
        try {
            commit = openObjectFromId(file);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

        String deflatedString = "";
        try {
            Inflater i = new Inflater();
            FileInputStream fis = new FileInputStream(commit);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] inputBytes = new byte[512];
            byte[] outputBytes = new byte[1024];
            int off = 0;
            int plus;
            int len = 512;
            int zOff = 0;
            while ((fis.read(inputBytes, off, len)) != -1 )
            {
                i.setInput(inputBytes);
                while(!i.finished())
                {
                    int c = i.inflate(outputBytes);
                    baos.write(outputBytes, zOff, c);
                    zOff += c;
                }
            }
            System.out.println(baos.toString("UTF-8"));
            deflatedString =  baos.toString();
            String [] lines = deflatedString.split("\n");

            for ( String line : lines )
            {
                if (line.contains("author"))
                {
                    String [] parts = line.split(" ");

                    Date d = Date.from(Instant.ofEpochSecond(Long.parseLong(parts[4])));
                    return d;
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }


    public String resolveHead()
    {
        return "";
    }

    public static void main(String[] args)
    {

        GitCommitParser gitCommitParser = null;
        try {
            gitCommitParser = new GitCommitParser(new File("/Users/delliot/dev/maven"));
        } catch (IOException e) {
            e.printStackTrace();
        }



        Date d = gitCommitParser.getCommitDateTime(gitCommitParser.getHeadId());

        System.out.println("Parsed date: " + d.toString());

    }

    public File openObjectFromId(String id) throws FileNotFoundException
    {
        File gitDir = this.gitDirectory;

        File [] children = gitDir.listFiles();
        File objDir = null;
        File foundPrefix = null;

        for (File f : children)
        {
            if (f.getName().toLowerCase().equals("objects")) {
                objDir = f;
                break;
            }
        }

        File[] prefixes = objDir.listFiles();

        for (File pre : prefixes)
        {
            if (pre.getName().toLowerCase().equals(id.substring(0,2)))
            {
                foundPrefix = pre;
                break;
            }
        }
        if (foundPrefix.listFiles() != null)
        {
            for (File obj : foundPrefix.listFiles())
            {
                if ( obj.getName().toLowerCase().equals(id.substring(2)))
                {
                    return obj;
                }
            }
        }

        throw new FileNotFoundException("Object not found in .git directory!");
    }

    public static BufferedReader readerFromFile( File f )
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
        } catch (IOException ioe )
        {
            ioe.printStackTrace();
            return null;
        }

        InputStreamReader isr = new InputStreamReader(fis);
        return new BufferedReader(isr);
    }



}
