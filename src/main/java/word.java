public class word implements Comparable<word>  {
    public int postition,rank;
    public String page;

    public word(String page,int postition,int rank)
    {
        this.page=page;
        this.postition=postition;
        this.rank=rank;
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
