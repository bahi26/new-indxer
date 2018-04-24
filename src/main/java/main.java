import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.mongodb.*;
import com.mongodb.client.model.DBCollectionUpdateOptions;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;


import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Filters;

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


    static void update_Data(DBCollection words_collection,DBCollection pages_collection)
    {
        Map<String,ArrayList<DBObject>> innerObjects= new HashMap<String, ArrayList<DBObject>>() ;
        int index=0;
        Wordhashing.intialize();
        try{
            BufferedReader urls = new BufferedReader(new FileReader("C:\\Users\\BAHI\\IdeaProjects\\indexer\\input\\ForIndexer.txt"));
            BufferedReader links = new BufferedReader(new FileReader("C:\\Users\\BAHI\\IdeaProjects\\indexer\\input\\links.txt"));

            String line;
            List<DBObject> pages=new ArrayList();

            Map<String,List> words_list=new HashMap();
            List<DBObject>page_urls=new ArrayList<DBObject>();
            while ((line = urls.readLine()) != null)
            {
                String [] splitArray=line.trim().split(" ");

                pages.add( new BasicDBObject().append("url",splitArray[0]).append("index",index).append("video",splitArray[1]));
                //append("links",Integer.parseInt(links.readLine()));
                File file = new File("C:\\Users\\BAHI\\IdeaProjects\\indexer\\input\\"+splitArray[1]+".txt");
                String data = new Scanner(file).useDelimiter("\\A").next();

                ArrayList<String> words=(ArrayList<String>) Wordhashing.escapeHtml(data);
                System.gc();
                int words_size=words.size();

                for(int i=0;i<words.size();i++)
                {
                    String [] word_data=words.get(i).split(" ");
                    DBObject innerObject=new BasicDBObject("url",splitArray[0]).append("origin",word_data[1]).append("position",word_data[2]).append("rank",word_data[3]).append("size",words_size);
                    if(innerObjects.get(word_data[0])==null)innerObject.put(word_data[0],new ArrayList<DBObject>());
                     innerObjects.get(word_data[0]).add(innerObject);

                }

                //remove url words
                page_urls.add(new BasicDBObject("url",splitArray[0]));
                if(page_urls.size()>500)
                {
                    DBObject updateQuery = new BasicDBObject("$pull",new BasicDBObject("list",page_urls));
                    page_urls.clear();
                }
            }

            pages_collection.remove(new BasicDBObject("$or",pages));
            pages_collection.insert(pages);

            for(Map.Entry<String,ArrayList<DBObject>>entry:innerObjects.entrySet())
            {
                entry.getKey();
                DBObject findQuery = new BasicDBObject("word",entry.getKey());
                DBCollectionUpdateOptions options= new DBCollectionUpdateOptions().upsert(true);
                DBObject updateQuery = new BasicDBObject("$push",new BasicDBObject("list",entry.getValue()));
                words_collection.update(findQuery,updateQuery,options);
            }

        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public static void main(String[] args) {

        try {
            int action=0;
            MongoClient mongoClient = new MongoClient();

            DB database = mongoClient.getDB("indexer2");
            DBCollection pages=database.getCollection("pages");
            DBCollection words=database.getCollection("words");

            if(action==0)
            {
                long time=System.currentTimeMillis();
                update_Data(words,pages);
              //  System.out.println(System.currentTimeMillis()-time);
            }
            else
                {
                indexer Indexer = new indexer(pages.find().toArray(),words);

                Scanner scan = new Scanner(System.in);
                System.out.println("write query");

                while (true) {
                    String query = scan.nextLine();
                    Set links = Indexer.search(query);
                    Iterator it = links.iterator();
                    while (it.hasNext()) System.out.println(it.next());
                    System.out.println("write query");
                }
            }

        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

}
