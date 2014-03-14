package com.sun.tools.javac.tree;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlock;
import com.sun.tools.javac.tree.JCTree.JCBlockExp;
import com.sun.tools.javac.tree.JCTree.JCListAccess;
import com.sun.tools.javac.tree.JCTree.JCListComp;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.xml.internal.messaging.saaj.soap.impl.TreeException;

public class MyBlocks {
	
	private final  ParserFactory parserFactory;
	private final  TreeMaker treeMaker;
	private final Symtab syms;
	public MyBlocks(Context context)
	{
		parserFactory=ParserFactory.instance(context);
		treeMaker=TreeMaker.instance(context);
		syms=Symtab.instance(context);
	}
	/**
     *get block code of __list_access from name of them. 
     */
    private String getListAccessCode(JCListAccess tree)
    {
    	String list = tree.indexed.toString();
    	String beg = tree.term1.toString();
    	String end = tree.term2.toString();
    	String step = tree.term3.toString();
    	String code_list_access
    	= "{"
    		+"		java.util.List _access_list = " + list + ";"
    		+"		int _access_beg = " + beg + ";"
    		+"      int _access_end = " + end + ";"
    		+"      int _access_step = " + step + ";"
    		+ "		int _access_len = _access_list.size();                                           	"
    		+ "		java.util.List _access_tmpList = new java.util.ArrayList();              	"
    		+ "                                                                         "
    		+ "		if (_access_step == Integer.MAX_VALUE)                                   	"
    		+ "			_access_step = 1;                                                    	"
    		+ "		if (_access_step > 0) {                                                  	"
    		+ "			_access_beg += (_access_beg < 0) ? _access_len : 0;                                  	"
    		+ "			_access_end += (_access_end < 0) ? _access_len : 0;                                     "
    		+ "                                                                      	"
    		+ "			if (_access_beg == Integer.MAX_VALUE)                                	"
    		+ "				_access_beg = 0;                                                 	"
    		+ "			if (_access_end == Integer.MAX_VALUE)                                	"
    		+ "				_access_end = _access_len;                                               	"
    		+ "			for (int i = _access_beg; i < _access_end; i += _access_step) {                      	"
    		+ "				_access_tmpList.add(_access_list.get(i));                                	"
    		+ "			}                                                            	"
    		+ "		} else {                                                         	"
    		+ "			_access_beg += (_access_beg < 0) ? _access_len : 0;                                  	"
    		+ "			_access_end += (_access_end < 0) ? _access_len : 0;                                  	"
    		+ "                                                                      	"
    		+ "			if (_access_beg == Integer.MAX_VALUE)                                	"
    		+ "				_access_beg = _access_len - 1;                                           	"
    		+ "			if (_access_end == Integer.MAX_VALUE)                                	"
    		+ "				_access_end = -1;                                                	"
    		+ "                                                                         "
    		+ "			for (int i = _access_beg; i > _access_end; i += _access_step) {                         "
    		+ "				_access_tmpList.add(_access_list.get(i));                                   "
    		+ "			}                                                               "
    		+ "		}                                                                   "
    		+ "		_access_tmpList=_access_tmpList;                                                     "
    		+ "	} end ";
    	return code_list_access;
    }


    private JCBlockExp parseBlockExp(String code)
    {
    	JavacParser parser=(JavacParser) parserFactory.newParser(code.subSequence(0, code.length()-1), false, false, false);
    	JCBlock block=parser.block();
    	JCBlockExp blockExp=treeMaker.BlockExp(block);
    	return blockExp;
    }
    public JCBlockExp getListAccessBlock(JCListAccess tree)
    {
    	String code=getListAccessCode(tree);
    	JCBlockExp blockExp=parseBlockExp(code);
    	return blockExp;
    }
    
    private String getListCompCode(JCListComp tree)
    {
    	String typ=tree.decl.type.toString();
    	String varName=tree.decl.name.toString();
    	String expr=tree.expr.toString();
    	String listExpr=tree.listExpr.toString();
    	String ifExpr="";
    	if(tree.ifExpr!=null)
    		ifExpr=tree.ifExpr.toString();

    	String code="{"
    		+"java.util.List _list_comp_list="+listExpr+";"
    		+"java.util.List _list_comp_tmpList = new ArrayList();"
    		+"for(int i=0;i<_list_comp_list.size();i++){";
    	code +=
    		typ+" "+varName+ "=("+typ+")_list_comp_list.get(i);";
    	
    	if(ifExpr!="")
    		code+=
    			"	if("+ifExpr+")";
    	code+=
    		"		_list_comp_tmpList.add("+expr+");"
    		+"}"
    		+"_list_comp_tmpList=_list_comp_tmpList;"
    		+"} end";
    	return code;
    }
    public JCBlockExp getListCompBlock(JCListComp tree)
    {
    	String code=getListCompCode(tree);
    	JCBlockExp blockExp=parseBlockExp(code);
    	return blockExp;
    }

    private String getListAddCode(JCBinary tree)
    {
    	String code="{"
    		+"java.util.List _list_add_tmp=new ArrayList();" 
    		+"_list_add_tmp.addAll("+tree.lhs.toString()+");"
    		+"_list_add_tmp.addAll("+tree.rhs.toString()+");"
    		+"_list_add_tmp=_list_add_tmp;"
    		+"} end";
    	return code;
    }
    
    public JCBlockExp getListAddBlock(JCBinary tree)
    {
    	String code=getListAddCode(tree);
    	JCBlockExp blockExp=parseBlockExp(code);
    	return blockExp;
    }
    private String getListMulCode(JCBinary tree)
    {
    	JCTree listTree,intTree;
    	if(tree.lhs.type.tsym==syms.listType.tsym){
    		listTree=tree.lhs;
    		intTree=tree.rhs;
    	}else
    	{
    		listTree=tree.rhs;
    		intTree=tree.lhs;
    	}
    	String code="{"
    		+"java.util.List __list="+listTree.toString()+";"
    		+"java.util.List __tmp=new ArrayList(__list);"
    		+"for(int i=0;i<"+intTree.toString()+"-1;i++)"
    		+"   __tmp.addAll(__list);"
    		+"__tmp=__tmp;"
    		+"} end";
    	return code;
    }
    public JCBlockExp getListMulBlock(JCBinary tree)
    {
    	String code=getListMulCode(tree);
    	JCBlockExp blockExp=parseBlockExp(code);
    	return blockExp;
    }
}
