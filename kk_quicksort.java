import java.util.*;
import java.io.*;
class kk{
	public static void main(String args[])
	{
		List<Integer> k=[3,1,2,4,10,6,7,2,9,0,12,193,15,17,181,13];
		System.out.println(qsort(k));
	}
	
	public static List<Integer> qsort(List<Integer> lista){
		if(lista.isEmpty())		return [];
		else					
			return qsort([x for(int x:lista[1:]) if x < lista[0]])
					+[lista[0]]
					+qsort([x for(int x:lista[1:]) if x >= lista[0]]);
	
	}
}
