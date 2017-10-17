import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by sanera on 02/12/2016.
 */
public class Trial {

    public static void main(String [] args){
        Set<Integer> newList=new HashSet<>();
        boolean statusFirst=newList.add(1);
        boolean statusFirst1=newList.add(1);

        System.out.println("done:");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
        Date today = new Date();
        String date = sdf.format(today);
        System.out.println(date);

        SimpleDateFormat day = new SimpleDateFormat("dd");
        System.out.println("day:"+day.format(today));

        SimpleDateFormat month = new SimpleDateFormat("MMM");
        System.out.println("month:"+month.format(today));

        SimpleDateFormat year = new SimpleDateFormat("YYYY");
        System.out.println("year:"+year.format(today));
      /*Date date =new Date(); // your date
       Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        System.out.println("year:"+ year+ " day:"+day +" month:"+month);


        System.out.println("month:"+cal.getDisplayName(month,1, Locale.ENGLISH));*/

    }

}
