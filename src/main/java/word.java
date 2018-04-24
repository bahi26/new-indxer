public class word implements Comparable<word>  {
    public int position,rank,page_size;
    public String page,origin;

    public word(String page,int position,int rank,String origin,int page_size)
    {

        this.page=page;
        this.position=position;
        this.rank=rank;
        this.origin=origin;
        this.page_size=page_size;
    }


    public int compareTo(word word2)
    {
        if(word2.page.equals(this.page))
            if(word2.position>this.position)
                return 1;
            else
                return -1;
        else
        {
            if(word2.page.compareTo(this.page)>0)
                return 1;
            else
                return -1;
        }
    }


}
