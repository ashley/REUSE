class CharStringDemo{

   public static void main(String argv[]){
   
      String word = "hello";
      char initial = word.charAt(0); 
      
      System.out.println("The first letter of " + word + " is " + initial);
   
      for(int i=0;i<word.length();i++)
         System.out.println(word.charAt(i));
       
   }//main


}//class
