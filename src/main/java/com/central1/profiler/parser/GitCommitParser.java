package com.central1.profiler.parser;

import java.io.*;
import java.util.Locale;


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


    public String getCommitId()
    {
        File[] files = gitDirectory.listFiles();
        File head = null;
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
            fis = new FileInputStream(head);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);

            String line;
            while ( (line = br.readLine()) != null)
            {
                if ( line.toLowerCase(Locale.ENGLISH).contains("ref:") )
                {
                    String path = head.getAbsolutePath() + line.substring(5);
                    String headRef = line.substring(5);
                    File ref = new File(path);
                    if (ref.exists())
                    {
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
                        fis = new FileInputStream(new File(path));
                        isr = new InputStreamReader(fis);
                        br = new BufferedReader(isr);

                        while ((line = br.readLine()) != null  && line.contains("refs/heads/") )
                        {
                            if ( line.contains(headRef))
                            {

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


    public String resolveHead()
    {
        return "";
    }

    public static void main(String[] args)
    {

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
