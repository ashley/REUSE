import java.util.ArrayList;
import java.util.Collections;

class Sorts <T extends Comparable<T>>{
	
	public static <T extends Comparable<T>>  int bogoSort(ArrayList<T>al){
		int count = 0;
		while(!isSorted(al)){
			Collections.shuffle(al);
			count++;
		}
		return count;
	}
	
	public static <T extends Comparable<T>>  boolean isSorted(ArrayList<T>al){
		for(int i=0;i<al.size()-1;i++){
			if(al.get(i).compareTo(al.get(i+1))>-1){
				return false;
			}
		}
		return true;
	}
	
	public static <T extends Comparable<T>> void mergeSort(ArrayList<T> al){
		if(al.size() <=1){return;}
			int mid = al.size()/2;
			ArrayList<T> left = new ArrayList<T>();
			left.addAll(al.subList(0, mid));
			ArrayList<T> right = new ArrayList<T>();
			right.addAll(al.subList(mid,al.size()));
			
			mergeSort(left);
			mergeSort(right);
			al.clear();
			merge(left,right,al);
	}
	
	public static <T extends Comparable<T>> void merge(ArrayList<T>left, ArrayList<T> right, ArrayList<T> al){
		while(!left.isEmpty() && !right.isEmpty()){
			if(left.get(0).compareTo(right.get(0))<=0){
				al.add(left.remove(0));
			}
			else{
				al.add(right.remove(0));
			}
			if(!left.isEmpty()){
				al.addAll(left);
				left.clear();
			}
			if(!right.isEmpty()){
				al.addAll(right);
				right.clear();
			}
		}
	}
	
	public static <T extends Comparable<T>> int bubbleSort(ArrayList<T>al){
		int count = 0;
		int n = al.size();
		boolean sorted = false;
		while(n>0 && sorted == false){
			sorted = true;
			for(int i=0;i < n-1;i++){
				if(al.get(i).compareTo(al.get(i+1)) >= 1){
					swap(al,i,i+1);
					sorted = false;
				}
				count++;
			}
			n--;
		}
		return count;
	}
	
	public static <T extends Comparable<T>> void swap(ArrayList<T>al,int i, int j){
		T temp = al.get(i);
		al.set(i, al.get(j));
		al.set(j, temp);
	}
		

}