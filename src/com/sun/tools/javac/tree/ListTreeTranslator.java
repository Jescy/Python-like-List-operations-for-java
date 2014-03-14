package com.sun.tools.javac.tree;

import com.sun.tools.classfile.Type.MethodType;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.code.Type.ClassType;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.parser.JavacParser;
import com.sun.tools.javac.parser.ParserFactory;
import com.sun.tools.javac.tree.JCTree.JCArrayAccess;
import com.sun.tools.javac.tree.JCTree.JCAssign;
import com.sun.tools.javac.tree.JCTree.JCBinary;
import com.sun.tools.javac.tree.JCTree.JCBlockExp;
import com.sun.tools.javac.tree.JCTree.JCConditional;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import com.sun.tools.javac.tree.JCTree.JCFieldAccess;
import com.sun.tools.javac.tree.JCTree.JCIdent;
import com.sun.tools.javac.tree.JCTree.JCListAccess;
import com.sun.tools.javac.tree.JCTree.JCListComp;
import com.sun.tools.javac.tree.JCTree.JCLiteral;
import com.sun.tools.javac.tree.JCTree.JCMethodDecl;
import com.sun.tools.javac.tree.JCTree.JCMethodInvocation;
import com.sun.tools.javac.tree.JCTree.JCNewClass;
import com.sun.tools.javac.tree.JCTree.JCNewList;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

public class ListTreeTranslator extends TreeTranslator {
	private final Names names;
	private final TreeMaker treeMaker;
	private final Log log;
	final Symtab syms;
	private boolean isLeft = false;
	private boolean isSet = false;
	private final MyBlocks myBlocks;
	private final Types types;
	public ListTreeTranslator(Context context) {
		names = Names.instance(context);
		treeMaker = TreeMaker.instance(context);
		log = Log.instance(context);
		myBlocks=new MyBlocks(context);
		syms= Symtab.instance(context);
		types=Types.instance(context);
	}

	
	@Override
	public void visitAssign(JCAssign tree) { // isLeft is used to different
		// list[i]=a(true) or
		// a=list[i](false)
		boolean tmpIsLeft = isLeft; // save isLeft states, to solve nested
		// problem such as list[list[1]=a]=a;
		isLeft = true; // set isLeft=true
		boolean tmpIsSet = isSet; // save isSet states
		isSet = false;
		tree.lhs = translate(tree.lhs); // translate left tree of assign

		isLeft = false;
		tree.rhs = translate(tree.rhs); // translate right tree of assign

		if (isSet) {
			JCArrayAccess arrayAccess = (JCArrayAccess) tree.lhs;
			JCFieldAccess list_set = treeMaker.Select(arrayAccess.indexed,
					names.fromString("set"));
			List<JCExpression> args = List.of(arrayAccess.index, tree.rhs);
			JCMethodInvocation list_set_invoc = treeMaker.Apply(null, list_set,
					args);
			list_set_invoc.setType(tree.rhs.type);
			result = list_set_invoc;
			
		} else
			result = tree;

		isLeft = tmpIsLeft; // restore states
		isSet = tmpIsSet;
	}

	@Override
	public void visitIndexed(JCArrayAccess tree) {
		tree.indexed = translate(tree.indexed);
		tree.index = translate(tree.index);

		JCIdent selected = (JCIdent) tree.indexed;
		
		if (types.isSubtypeUnchecked(types.erasure(selected.type), syms.listType)) // if list
		{
			// translate k[expr]==> k[expr+((expr<0)?k.size():0)]
			tree.index = treeMaker.TypeCast(treeMaker.Ident(names
					.fromString("Integer")), tree.index);
			JCLiteral false_part = treeMaker.Literal(4, 0); // 0
			JCFieldAccess list_size_meth = treeMaker.Select(tree.indexed, names
					.fromString("size")); // list.size
			JCMethodInvocation true_part = treeMaker.Apply(null,
					list_size_meth, List.<JCExpression> nil());// list.size()

			JCBinary cond_expr_0 = treeMaker.Binary(64, treeMaker
					.Parens(tree.index), treeMaker.Literal(0));// (expr)<0
			JCConditional conditional = treeMaker.Conditional(cond_expr_0,
					true_part, false_part); // (expr)<0?list.size():0

			JCBinary indexed_binary = treeMaker.Binary(71, treeMaker
					.Parens(tree.index), treeMaker.Parens(conditional)); // (expr)+((expr)<0?list.size():0)

			if (!isLeft) // list.get(2)
			{
				List<JCExpression> args = List
						.of((JCExpression) indexed_binary);
				Name get = names.fromString("get");
				JCFieldAccess list_get = treeMaker.Select(selected, get);
				JCMethodInvocation list_get_k = treeMaker.Apply(null, list_get,
						args);
				
				//type
				Type elemType=syms.objectType;
				if(selected.type.getTypeArguments().head!=null)
					elemType=selected.type.getTypeArguments().head;
				list_get_k.setType(elemType);
				
				result = list_get_k;
				
			} else // list.set
			{
				isSet = true;
				tree.index = indexed_binary;
				result = tree;
			}

		} else if (types.isArray(selected.type)) {// if array
			result = tree;
		} else {
			log.error(tree.pos(), "array.req.but.found", selected);
		}
	}

	@Override
	public void visitIndexedL(JCListAccess tree) {
		tree.indexed = translate(tree.indexed);
		tree.term1 = translate(tree.term1);
		tree.term2 = translate(tree.term2);
		tree.term3 = translate(tree.term3);
		
		result=myBlocks.getListAccessBlock(tree);
		result.setType(tree.type);
	}

	@Override
	public void visitNewList(JCNewList tree) {
		tree.elemtype = translate(tree.elemtype);
		tree.elems = translate(tree.elems);
		// result = tree;

		// methord: Arrays.asList()
		JCIdent arrays = treeMaker.Ident(names.fromString("Arrays"));
		Name asList = names.fromString("asList");
		JCFieldAccess arrays_asList = treeMaker.Select(arrays, asList);

		JCMethodInvocation arrays_asListInvocation = treeMaker.Apply(null,
				arrays_asList, tree.elems);

		// NEW: new ArrayList()
		List<JCExpression> args_new = List
				.of((JCExpression) arrays_asListInvocation);

		JCIdent class_new = treeMaker.Ident(names.fromString("ArrayList"));

		JCNewClass newClass = treeMaker.NewClass(null, null, class_new,
				args_new, null);

		Env<AttrContext> aEnv=null;

		Type elemType=syms.objectType;

		if(tree.type.getTypeArguments().head!=null)
			elemType=tree.type.getTypeArguments().head;
		newClass.setType(new ClassType(Type.noType, List.of(types.boxedTypeOrType(elemType)), syms.listType.tsym));
		result = newClass;
	}

	@Override
	public void visitBinary(JCBinary tree) {
		tree.lhs = translate(tree.lhs);
		tree.rhs = translate(tree.rhs);

		try{
			Type eleft=types.erasure(tree.lhs.type);
	        Type eright=types.erasure(tree.rhs.type);

	        if(types.isSubtypeUnchecked(eleft, syms.listType) 
	        		&& types.isSubtypeUnchecked(eright, syms.listType) 
	        		&& tree.getOpcode()==JCTree.PLUS)
	        {
	        	result = myBlocks.getListAddBlock(tree);
				List<Type> lType=tree.lhs.type.getTypeArguments();
				List<Type> rType=tree.rhs.type.getTypeArguments();
				
				Type aType=syms.objectType;
				if(lType.head!=null && rType.head!=null)
					aType=types.lub(types.erasure(lType.head),types.erasure(rType.head));
				result.setType( new ClassType(Type.noType, List.of(types.boxedTypeOrType(aType)), syms.listType.tsym));
				
	        }else if(types.isSameType(eleft, syms.intType) 
	        			&& types.isSubtypeUnchecked(eright, syms.listType) 
	        			&& tree.getOpcode() == JCTree.MUL
	        		)
	        {
	        	result=myBlocks.getListMulBlock(tree);
	        	result.setType(tree.rhs.type);
	        }else if(types.isSameType(eright, syms.intType)
			&& types.isSubtypeUnchecked(eleft, syms.listType)
			&& tree.getOpcode() == JCTree.MUL)
	        {
	        	result=myBlocks.getListMulBlock(tree);
	        	result.setType(tree.lhs.type);
	        }
	        else {
			result = tree;
		}
		}
		catch (Exception e) {
			result=tree;
		}
	}


	public void visitListComp(JCListComp tree)
	{
		tree.decl=translate(tree.decl);
		tree.expr=translate(tree.expr);
		tree.ifExpr=translate(tree.ifExpr);
		
		if(types.isArray(tree.listExpr.type))
		{
			JCIdent arrays = treeMaker.Ident(names.fromString("Arrays"));
			Name asList = names.fromString("asList");
			JCFieldAccess arrays_asList = treeMaker.Select(arrays, asList);

			JCMethodInvocation arrays_asListInvocation = treeMaker.Apply(null,
					arrays_asList, List.of(tree.listExpr));
			tree.listExpr=translate(arrays_asListInvocation);
			
		}else
			tree.listExpr=translate(tree.listExpr);
		result = myBlocks.getListCompBlock(tree);
		result.setType( new ClassType(Type.noType, List.of(types.boxedTypeOrType(tree.expr.type)), syms.listType.tsym));
	}
}
