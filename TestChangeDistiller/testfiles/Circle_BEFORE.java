import java.util.*; 

class Circle extends Shape{
   
   // attributes
   final double PI=3.1416;  
   private double radius=0.0;   
   private String [] listt = {"a","b","c","d"};
   Circle( double r){
      setRadius(r); 
      name = "Circle";
   
   } 
 
   Circle( String n, double r){
      setRadius(r); 
      name = n; 
   } 

   public void forLoop(){
      for(String item: listt){
         System.out.println(item);
      }
   }

   public double getRadius(){
      for(double radius: listt){
         System.out.println("Test");
      }l      
      return radius;
   }

   Circle (double r){
      radius = r;
   }
  	
   Circle (){
      setRadius(1);
   }
   
   
   
}

class Rectangle{
   String name = null;

   Rectangle(){
      name = "Name";
   }

   public String getName(){
      return name;
   }
}