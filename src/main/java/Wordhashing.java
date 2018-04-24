import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;


public class Wordhashing {
    static Map<String,Integer> map = new Hashtable();
    static int priority=-1;
    static  String part= "";

    public static void main(String args[]) throws FileNotFoundException, NoSuchFieldException {

        intialize();

    }

    public static ArrayList escapeHtml(String source) throws NoSuchFieldException, IllegalAccessException {
        //System.out.println(source);

        Document doc = Jsoup.parse(source);
        long index=0;
        ArrayList returned = new ArrayList<String>();
        String text="";


        //h1,h2,h3,h4,h5,h6,p,title,img,source
        org.jsoup.select.Elements  elements = doc.getAllElements();
        for (Element element : elements) {

            if(check_tag(element.tagName())){
                if (element.tagName().equalsIgnoreCase("textarea")) {
                    element.tag().getClass().getDeclaredField("preserveWhitespace").setAccessible(true);
                    element.tag().getClass().getDeclaredField("preserveWhitespace").set(element.tag(), true);
                }


                if(part.equals("im")){
                    text=element.attr("alt");
                }

                else text=element.text();

                if(text!= ""){


                    returned.addAll(words(text,priority,index));
                    index =returned.size();
                }

            }

        }


        // test array size
		   /*for(Object word : returned)System.out.println(word);
		   System.out.println("ARRAY SIZE : "+returned.size());*/
        //

        return returned;

    }





    public static boolean check_tag(String tag_name){
//h1,h2,h3,h4,h5,h6,p,title,img,header

        int n=(tag_name.length()<2)?1:2;
        part= tag_name.substring(0, n);


        boolean ans= (part.equals("h1") ||part.equals("h2")||part.equals("h3")||part.equals("h4")||part.equals("h5")||part.equals("h6")||part.charAt(0)==('p')||part.charAt(0)==('B') ||part.equals("st") ||part.equals("im")||part.equals("ti") );
        if(ans){
            if( part.charAt(0)=='B') priority=map.get( "B");
            else if(part.charAt(0)=='p') priority=map.get( "p");
            else
                priority=map.get(part);
        }
        else  priority =-1;
        return  ans;
    }

    public static ArrayList words(String line, int tag,long index)
    {


        ArrayList result = new ArrayList<String>();

        String[] words_list = line.split("[ \t\n,\\.\"!?$~()\\[\\]\\{\\}:;/\\\\<>+=%*]");


        int j=0;

        for(int i=0; i < words_list.length; i++)
        {
            if(words_list[i] != null && !words_list[i].equals(""))
            {
                String word = words_list[i].toLowerCase();


                String Text=Stemmer.stem(word);
                result.add(Text+' '+word+' '+Long.toString(index+j+1)+' '+tag);
                j++;
            }
        }

        return result;
    }

    public static void intialize(){
        map.put("im", 0);
        map.put("ti", 10);
        map.put("h1", 8);
        map.put("h2", 6);
        map.put("st", 6);
        map.put("B" , 6);
        map.put("p" , 5); map.put("h3" , 5); map.put("h4" , 5); map.put("h5" , 5); map.put("h6" , 5);




    }
}