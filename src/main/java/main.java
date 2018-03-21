
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

public class main {
    static void set_Data(String [] links)
    {
        try
        {
            Document doc;
            BufferedWriter  file;
            for (String link : links)
            {
                doc = (Document)Jsoup.connect(link).get();
                String name=link.replaceAll("[\\.$|,|;|'?*/:]", "");
                file = new BufferedWriter( new FileWriter("C:\\Users\\BAHI\\Documents\\NetBeansProjects\\indexer\\input\\"+name+".txt"));
                file.write(doc.toString());
                file.close();
            }

        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }

    }


    static void update_Data(DBCollection words_collection,DBCollection words_page)
    {
        try{
            BufferedReader urls = new BufferedReader(new FileReader("C:\\Users\\BAHI\\IdeaProjects\\indexer\\input\\urls.txt"));
            String line;


            List<DBObject> pages=new ArrayList();

            while ((line = urls.readLine()) != null)
            {

                List<DBObject> words_list=new ArrayList();
                String [] splitArray=line.trim().split(" ");
                words_page.remove(new BasicDBObject("url",splitArray[0]));

                pages.add( new BasicDBObject().append("url",splitArray[0]).append("video",splitArray[2]));

                File file = new File("C:\\Users\\BAHI\\IdeaProjects\\indexer\\input\\"+splitArray[0].replaceAll("[\\.$|,|;|'?*/:]", "")+".txt");
                String data = new Scanner(file).useDelimiter("\\A").next();
                file.delete();

                String phrases=Jsoup.parse(data).text().toLowerCase().replaceAll("'", "").trim().replaceAll(" +", " ");
                String[] words=phrases.split(" ");

                for(int i=0;i<words.length;i++)
                    words_list.add(new BasicDBObject().append("word",words[i]).append("position",i).append("rank",0).append("image",0).append("page",splitArray[0]));

                words_collection.insert(words_list);
            }
            words_collection.remove(new BasicDBObject("$or",pages));

            words_page.insert(pages);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public static void main(String[] args) {

        try {
            MongoClient mongoClient = new MongoClient();

            DB database = mongoClient.getDB("indexer");
            DBCollection pages=database.getCollection("pages");
            DBCollection words=database.getCollection("words");

            //update_Data(words,pages);

            indexer Indexer=new indexer(pages.find().toArray(),words);

            Scanner scan=new Scanner(System.in);
            System.out.println("write query");
            while(true)
            {
                String query=scan.next();
                Set links=Indexer.search(query);
                Iterator it=links.iterator();
                while(it.hasNext()) System.out.println(it.next());
                System.out.println("write query");
            }

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}