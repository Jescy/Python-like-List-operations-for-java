import java.io.*;
import java.util.*;

public class kk{
public static ArrayList<ArrayList<Integer>> Perm(List<Integer> elems){
    if(elems.size() <=1)
        	return new ArrayList(Arrays.asList(elems));
    else{
		ArrayList<ArrayList<Integer>> lists=new ArrayList<ArrayList<Integer>>();
		for(List b:Perm(elems[1:]))
		{
			for(int ii=0;ii<elems.size();ii++)
			{
				ArrayList<Integer> tmpArrayList=new ArrayList<Integer>(b[:ii]+[elems[0]]+b[ii:]);
				lists.add(tmpArrayList);
				
			}
		}
	return lists;	
	}
}
public static void main(String args[])
{
	List<Integer> k=[1,2,3,4];
	for(List a:Perm(k))
	{
		System.out.println(a);
	}
}
}

