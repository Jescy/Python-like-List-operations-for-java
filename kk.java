import java.io.*;
import java.util.*;
import java.util.Scanner;
public class kk
{
	public static void main(String args[])
	{

		Scanner s=new Scanner(System.in);

		ArrayList<Integer> k1=[1,2,2,3];	
		System.out.println(""+
				"ArrayList<Integer> k1=[1,2,2,3];\n"+
				"System.out.println(k1);\n"	
		);
		s.nextLine();
		System.out.println(k1);
		s.nextLine();
	
		System.out.println("List<Integer> k2=k1;\n"+
							"List k3=[k1,k2,2];\n"+
							"System.out.println(k3);\n");
	
		s.nextLine();
		List<Integer>k2=k1;
		List k3=[k1,k2,2];
		System.out.println(k3);
		s.nextLine();

		List k=[1,[1,2],3,[1,2,3,[4,5,"abc"]],new Object()];
		System.out.println("List k=[1,[1,2],3,[1,2,3,[4,5,\"abc\"]],new Object(\"object\")];\nSystem.out.println(k);\n");
		s.nextLine();
		System.out.println(k);

		s.nextLine();
		System.out.println("System.out.println(k[3]);");
		s.nextLine();
		System.out.println(k[3]);

		s.nextLine();
		System.out.println("System.out.println(k[1:5:2]);");
		s.nextLine();
		System.out.println(k[0:3:2]);

	}
	
	
}
