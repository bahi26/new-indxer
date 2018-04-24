public class Page {
    public String page;
    public int img;
    public double rank;

    public Page(String page,double rank,int img)
    {
        this.page=page;
        this.rank=rank;
        this.img=img;
    }
    public int compareTo(Page page2)
    {
        if(page2.rank<this.rank)
            return 1;
        else
            return -1;
    }
}
