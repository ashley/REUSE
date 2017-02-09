import java.util.*;

class Average {
   
   public static void main (String [] argv) {
   
      Scanner sc = new Scanner(System.in);
      int counter = 1;
      int total = 0;
      
      while (counter <= 3) {
         
         System.out.println("enter a number: ");
         int number = sc.nextInt();
         total += number;
         counter += 1;
      }
      
      int average = total/(counter*1);
   
   }
   
}
