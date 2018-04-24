
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

        for(DBObject page:pages) this.urls.put(page.get("url").toString(),0);

    }

    public void add_word(String w,String page,int position,int rank,String origin,int size)
    {
        PriorityQueue<word> new_row;
        if(this.data.get(w)!=null)
            new_row=this.data.get(w);
        else
            new_row=new PriorityQueue();

        new_row.add(new word(page,position,rank,origin,size));
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
                for(word w:queue) {
                    // urls.add(w.page);
                }
        }
        return urls;
    }
    public Set search( String query)
    {
        query=query.trim().replaceAll(" +", " ");
        List<String> collections=new ArrayList();
        String[] splitArray=query.split(" ");

        for (String single_word : splitArray)
             collections.add(single_word);

        for(int i=0;collections.size()>i;++i)
        {/*
            DBCursor cursor = database.getCollection(collections.get(i)).find();
            List<DBObject> words = cursor.toArray();

            for (DBObject o : words)
            {
                //this.add_word(collections.get(i), o.get("url").toString(), Integer.parseInt(o.get("position").toString()), Integer.parseInt(o.get("rank").toString()),o.get("origin").toString());
            }*/
        }
        return this.retrieve(query);

    }

    public Set ranker(String query,boolean phrase_sreach)
    {
        //String stem_query=fun(query);
        String[] splitArray = query.trim().replaceAll(" +", " ").split(" ");//--
        ArrayList<String> original_query= new ArrayList<String>();

        //convert queues into 2d array list
        ArrayList<ArrayList<word>> lists= new ArrayList();
        PriorityQueue<Page> Pages=new PriorityQueue();
        int index=-1;
        for (String single_word : splitArray)
            if(data.get(single_word).size()>0)
            {
                lists.add(new ArrayList(data.get(single_word)));
                original_query.add(splitArray[++index]);
            }

        //initialize idfs and tfs
        int listSize=lists.size();
        ArrayList<Double> idfs=new ArrayList<Double>();
        ArrayList<Integer> tfs=new ArrayList<Integer>();
        int sizes=0;
        for(int i=0;i<listSize;i++)
        {
            tfs.add(1);
            sizes+=lists.get(i).size();
        }

        for(int i=0;i<listSize;i++) idfs.add((1.0*(sizes-lists.get(i).size()))/sizes);

        while (lists.size()>0)
        {
            //initialize firs word rank
            int first_position,position=lists.get(0).get(0).position;
            first_position=position;
            String current_page=lists.get(0).get(0).page;
            double pre_rank=lists.get(0).get(0).rank*tfs.get(0)*idfs.get(0)*((lists.get(0).get(0).origin.equals(original_query.get(0)))? 2 : 1);
            if(tfs.get(0)>=(lists.get(0).get(0).page_size)/3)pre_rank*=-1;
            int image=(lists.get(0).get(0).rank==0)? 1:0;
            tfs.set(0,tfs.get(0));
            lists.get(0).remove(0);
            if(lists.get(0).size()==0)
            {
                lists.remove(0);
                tfs.remove(0);
                idfs.remove(0);
                original_query.remove(0);
            }
            //loop 3alehom
            for(int j=1;j<lists.size();++j)
            {
                for(int k=0;k<lists.get(j).size();++k)
                {
                    if(lists.get(j).get(k).page.equals(first_position))
                        if(lists.get(j).get(k).position>position)
                        {
                            word temp=lists.get(j).get(k);
                            int check=1;
                            if(tfs.get(j)>=(temp.page_size)/3)check=-1;

                            double word_rank=temp.rank*tfs.get(j)*idfs.get(j)*((temp.origin.equals(original_query.get(j)))? 2 : 1)*check;
                            Pages.add(new Page(current_page,(word_rank+pre_rank)/(position-temp.position),(temp.rank==0)? 1:0));
                            position=temp.position;
                            lists.get(j).remove(k);
                            if(lists.get(j).size()==0)
                            {
                                lists.remove(j);
                                tfs.remove(j);
                                idfs.remove(j);
                                original_query.remove(j);
                                --j;
                            }
                            break;
                        }
                    else break;
                }
                if(position==first_position) Pages.add(new Page(current_page,pre_rank,image));
            }

        }

        return null;
    }



}
