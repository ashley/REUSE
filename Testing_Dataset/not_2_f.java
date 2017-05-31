import java.util.*;

public class not_2_b{
	
	public static int value;
	
	public static void main(String[] args){	
		if (isTerminated()) {
			foo();
	   	}
	}
	public static Boolean isTerminated(){
		return (value==0);
	}
	public static void setValue(int n){
		value = n;
	}

	public static int getValue(){
		return value;
	}
	
}

