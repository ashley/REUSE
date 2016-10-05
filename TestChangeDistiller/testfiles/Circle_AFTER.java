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
   //comment
   Circle( String n, double r){
      setRadius(r); 
      name = n; 
   } 

   public double getRadius(){
      return radius;
   }

   Circle (double r){
      radius = r;
   }

   Circle (){
      setRadius(1);
   } //Added comment to existing code in After filee

   //Added a comment to After file
   
   
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