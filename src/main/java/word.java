public class word implements Comparable<word>  {
    public int postition,rank;
    public String page,origin;

    public word(String page,int postition,int rank,String origin)
    {
        this.page=page;
        this.postition=postition;
        this.rank=rank;
        this.origin=origin;
    }


    public int compareTo(word word2) {
        if(word2.rank==this.rank)
        {
            if(this.postition<word2.postition)
                return 1;
            else
                return -1;
        }
        else
        if(word2.rank<this.rank)
            return 1;
        else
            return -1;
    }


}
