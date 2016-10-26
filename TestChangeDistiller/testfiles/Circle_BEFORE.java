import java.util.*; 

class Circle extends Shape{
   
   // attributes
   final double PI=3.1416;  
   //private double radius=0.0;   
   private String [] listt = {"a","b","c","d"};
   Circle( double r){
      setRadius(r); 
      name = "Circle";
   
   } 

   public void forLoop(){
      for(String item: listt){
         System.out.println(item);
      }
   }

   public void secondForLoop(){
      for(String item: listt){
         System.out.println(item);
      }
   }

   public double getRadius(){
      System.out.println("Test");
      return radius;
   }

   Circle (double r){
      radius = r;
   }
  	
   Circle (){
      setRadius(1);
   }
   
}