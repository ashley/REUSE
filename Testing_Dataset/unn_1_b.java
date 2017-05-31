import java.util.*;

public class unn_1_b{
	
	public static int value;
	
	public static void main(String[] args){	
		if (isTerminated()) {
			setValue(null);
	   	}
	}
	public static Boolean isTerminated(){
		return (value==0);
	}
	public static void setValue(int n){
		value = n;
	}
	
}

