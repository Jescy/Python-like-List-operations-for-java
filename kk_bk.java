import java.util.*;
import java.io.*;
public class kk
{

	public static void main(String args[])
	{

		List<Integer> kkk=[1,2,3,4,5,6,7,8,9,0,12,193,15,17,181,13];
		System.out.println(qsort(kkk));
		kkk[0]=kkk[10];
		System.out.println(kkk);


		List<Integer> k=[1,2,3,4,5,6,7,8,9,0];
		List<Integer> c=[x for (int x:k)];
		List<Integer> kk=k+c+c;
		List<Integer> k1=k+k;
		List aaa=[0];
		System.out.println(aaa*100);
		System.out.println(100*aaa);

		List<Integer> b=[x for(int x:k) if(x<3)];
		
		System.out.println(b);
		System.out.println(c[3:]);

		File cwd = new File(".");
		List<File> dirs = [file for(File file : cwd.listFiles()) if file.isDirectory()];
		
		System.out.println(dirs);
		System.out.println(dirs[1::2]);
		System.out.println(kk+dirs);
		double a;
		a=
		{a=10;}
		+
		{
			for(int i=0;i<10;i++)
				a=1;
			a=1;
		};
		System.out.println(a);

	}
	
		public static List<Integer> qsort(List<Integer> lista){
		if(lista.isEmpty())		return [];
		else					
			return qsort([x for(int x:lista[1:]) if x < lista[0]])+[lista[0]]+qsort([x for(int x:lista[1:]) if x >= lista[0]]);

		}
	static List get(){
		return [1,2.02,3,4,5,6,8,7];
	}
}
