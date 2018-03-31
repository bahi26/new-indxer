
import com.mongodb.*;

import java.util.*;

public class indexer {
    private Map <String,PriorityQueue<word>> data;
    private Map <String,Integer> urls;
    private DBCollection words_collection;
    public indexer(List<DBObject> pages,DBCollection words_collection)
    {
        this.data=new HashMap();
        this.urls=new HashMap();
        this.words_collection=words_collection;
        //Integer.parseInt(page.get("video").toString())
        for(DBObject page:pages) this.urls.put(page.get("url").toString(),0);

    }

    public void add_word(String w,String page,int position,int rank,String origin)
    {
        PriorityQueue<word> new_row;
        if(this.data.get(w)!=null)
            new_row=this.data.get(w);
        else
            new_row=new PriorityQueue();

        new_row.add(new word(page,position,rank,origin));
        this.data.put(w, new_row);
    }

    public Map<String, PriorityQueue<word>> getData() {
        return data;
    }


    public Set retrieve(String query)
    {

        String[] splitArray = query.trim().replaceAll(" +", " ").split(" ");
        Set urls=new HashSet();
        for (String single_word : splitArray)
        {
            Queue<word> queue = data.get(single_word);
            if(queue!=null)
                for(word w:queue)
                    urls.add(w.page);
        }
        return urls;
    }
    public Set search( String query)
    {
        query=query.trim().replaceAll(" +", " ");
        List<DBObject> list=new ArrayList();
        String[] splitArray=query.split(" ");

        for (String single_word : splitArray)
            if (data.get(single_word) == null) list.add(new BasicDBObject("word", single_word));

        if(list.size()>0)
        {
            DBCursor cursor = this.words_collection.find(new BasicDBObject("$or", list));
            List<DBObject> words = cursor.toArray();

            for (DBObject o : words)
            {
                this.add_word(o.get("word").toString(), o.get("url").toString(), Integer.parseInt(o.get("position").toString()), Integer.parseInt(o.get("rank").toString()),o.get("word").toString());
            }
        }
        return this.retrieve(query);

    }



}
