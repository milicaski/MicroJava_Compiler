package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rs.ac.bg.etf.pp1.CounterVisitor.VarPartCounter;
import rs.ac.bg.etf.pp1.ast.ArrayElem;
import rs.ac.bg.etf.pp1.ast.ArrayElemStart;
import rs.ac.bg.etf.pp1.ast.AssignDesOption;
import rs.ac.bg.etf.pp1.ast.Assignop;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassField;
import rs.ac.bg.etf.pp1.ast.ClassName;
import rs.ac.bg.etf.pp1.ast.CondTerm;
import rs.ac.bg.etf.pp1.ast.ConditionNoError;
import rs.ac.bg.etf.pp1.ast.ConstructorDecl;
import rs.ac.bg.etf.pp1.ast.ConstructorName;
import rs.ac.bg.etf.pp1.ast.DecDesOption;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.DesignatorNameIdent;
import rs.ac.bg.etf.pp1.ast.DesignatorNameSuper;
import rs.ac.bg.etf.pp1.ast.DesignatorNameThis;
import rs.ac.bg.etf.pp1.ast.DiffOp;
import rs.ac.bg.etf.pp1.ast.DivideOp;
import rs.ac.bg.etf.pp1.ast.DoWhileCondStart;
import rs.ac.bg.etf.pp1.ast.DoWhileStart;
import rs.ac.bg.etf.pp1.ast.ElseStart;
import rs.ac.bg.etf.pp1.ast.EqualOp;
import rs.ac.bg.etf.pp1.ast.Expr;
import rs.ac.bg.etf.pp1.ast.FactorAllocArray;
import rs.ac.bg.etf.pp1.ast.FactorAllocType;
import rs.ac.bg.etf.pp1.ast.FactorBoolConst;
import rs.ac.bg.etf.pp1.ast.FactorCharConst;
import rs.ac.bg.etf.pp1.ast.FactorFuncCall;
import rs.ac.bg.etf.pp1.ast.FactorNumConst;
import rs.ac.bg.etf.pp1.ast.FactorVar;
import rs.ac.bg.etf.pp1.ast.FuncCallDesOption;
import rs.ac.bg.etf.pp1.ast.GrtEqOp;
import rs.ac.bg.etf.pp1.ast.GrtOp;
import rs.ac.bg.etf.pp1.ast.IncDesOption;
import rs.ac.bg.etf.pp1.ast.LessEqOp;
import rs.ac.bg.etf.pp1.ast.LessOp;
import rs.ac.bg.etf.pp1.ast.Method;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MinusFactor;
import rs.ac.bg.etf.pp1.ast.MinusOp;
import rs.ac.bg.etf.pp1.ast.ModOp;
import rs.ac.bg.etf.pp1.ast.MoreExpr;
import rs.ac.bg.etf.pp1.ast.MoreFactor;
import rs.ac.bg.etf.pp1.ast.MoreTerm;
import rs.ac.bg.etf.pp1.ast.OrRelOp;
import rs.ac.bg.etf.pp1.ast.PlusOp;
import rs.ac.bg.etf.pp1.ast.RecordDecl;
import rs.ac.bg.etf.pp1.ast.RelopCondFactExpr;
import rs.ac.bg.etf.pp1.ast.SingStmtBreak;
import rs.ac.bg.etf.pp1.ast.SingStmtContinue;
import rs.ac.bg.etf.pp1.ast.SingStmtDoWhile;
import rs.ac.bg.etf.pp1.ast.SingStmtIf;
import rs.ac.bg.etf.pp1.ast.SingStmtIfElse;
import rs.ac.bg.etf.pp1.ast.SingStmtPrintNoNumConst;
import rs.ac.bg.etf.pp1.ast.SingStmtPrintNumConst;
import rs.ac.bg.etf.pp1.ast.SingStmtRead;
import rs.ac.bg.etf.pp1.ast.SingStmtReturn;
import rs.ac.bg.etf.pp1.ast.SingStmtReturnExpr;
import rs.ac.bg.etf.pp1.ast.SingleCondFactExpr;
import rs.ac.bg.etf.pp1.ast.SingleExpr;
import rs.ac.bg.etf.pp1.ast.StarOp;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.ThenStart;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.ac.bg.etf.pp1.ast.StartFunctionCall;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {
	private int mainPc;
	// private Obj currentDesignator;
	private Obj currentDesignatorUseForFileds;
	// private int currentIndexUseForField;

	private boolean hasFields = false;
	private boolean assignHasFields = false;
	private boolean incdecHasFields = false;
	private boolean isPrevArrayAccess = false;
	private byte arrayAccessOp;
	// private List<Byte> designatorCodeOp = new ArrayList<>();

	// private int indexToLoad;
	private boolean toLoadFirst = false;
	// private List<Byte> toLoadOp = new ArrayList<>();
	private Obj toLoadObj;
	private boolean isAllocArray = false;

	private List<Integer> doWhileStartPc = new ArrayList<>();
	private List<Integer> orAdrFixup = new ArrayList<>();// moze i jedan int samo jer se kod or to popravi
	private List<Integer> endOfStatementsAdrFixup = new ArrayList<>();// za kraj do while ili if ili ifelse, lista zato
																		// sto mogu da postoje ugnjezdeni
	private List<Integer> condTrueAdrFixup = new ArrayList<>();// lista adresa gde da se dopravi u Condition gde je
																// ispunjen uslov
	private List<Integer> endOfThenAdrFixup = new ArrayList<>();// na kraj thena ima jmp e pa tu vrednost da popravimo

	private List<List<Integer>> continueListAdrFixup = new ArrayList<>();
	private List<List<Integer>> breakListAdrFixup = new ArrayList<>();

	// private Struct currentParentClassStruct = null;// sluzi da bismo uzeli za
	// metode adresu od osnovne klase mozda
	List<Byte> methodTable = new ArrayList<>();// za tabele virt fja
	HashMap<Struct, Integer> TVFStartAdrMap = new HashMap<>();
	private boolean isClassMethodActive = false;
	private List<Boolean> isClassMethCall = new ArrayList<>();
	HashMap<Struct, Obj> classConstructorMap = new HashMap<>();
	private Struct currentClassStruct;

	void addWordToStaticData(int value, int address) {
		if (0 <= value && value <= 5) {
			methodTable.add(new Byte((byte) (Code.const_n + value)));
		} else if (value == -1) {
			methodTable.add(new Byte((byte) Code.const_m1));
		} else {
			methodTable.add(new Byte((byte) Code.const_));
			methodTable.add(new Byte((byte) ((value >> 16) >> 8)));
			methodTable.add(new Byte((byte) (value >> 16)));
			methodTable.add(new Byte((byte) (value >> 8)));
			methodTable.add(new Byte((byte) value));
		}

		methodTable.add(new Byte((byte) Code.putstatic));
		methodTable.add(new Byte((byte) (address >> 8)));
		methodTable.add(new Byte((byte) address));
	}

	void addNameTerminator() {
		addWordToStaticData(-1, Code.dataSize++);
	}

	void addTableTerminator() {
		addWordToStaticData(-2, Code.dataSize++);
	}

	void addFunctionAddress(int functionAddress) {
		addWordToStaticData(functionAddress, Code.dataSize++);
	}

	void addFunctionEntry(String name, int functionAddressInCodeBuffer) {
		for (int j = 0; j < name.length(); j++) {
			addWordToStaticData((int) (name.charAt(j)), Code.dataSize++);
		}
		addNameTerminator();
		addFunctionAddress(functionAddressInCodeBuffer);
	}

	public int getMainPc() {
		return mainPc;
	}

	public void visit(MethodTypeName methodTypeName) {
		if (methodTypeName.getName().equals("main")) {
			// ucitaj u code MethodTable
			mainPc = Code.pc;
			for (int i = 0; i < methodTable.size(); i++) {
				Code.put(methodTable.get(i));
			}
			methodTable.clear();
		}

		methodTypeName.obj.setAdr(Code.pc);

		SyntaxNode node = methodTypeName.getParent();

		// FormParsPartCounter fppc = new FormParsPartCounter();// ovu info imam valjda
		// i u obj za metod i to ce da olaksa
		// da bude nezavisno dal je metoda klase ili obicna met
		// node.traverseTopDown(fppc);

		VarPartCounter vpc = new VarPartCounter();
		node.traverseTopDown(vpc);

		Code.put(Code.enter);
		// Code.put(fppc.getCount());
		// Code.put(fppc.getCount() + vpc.getCount());
		Code.put(methodTypeName.obj.getLevel());
		Code.put(methodTypeName.obj.getLevel() + vpc.getCount());
	}

	public void visit(Method method) {
		if (method.getMethodTypeName().obj.getType() == Tab.noType) {
			Code.put(Code.exit);
			Code.put(Code.return_);
		} else {
			Code.put(Code.trap);
			Code.put(1);
		}
	}

	public void visit(ClassName className) {
		isClassMethodActive = true;
		currentClassStruct = className.obj.getType();
		// zapamti gde pocinje tabela virt fja u stat mem
		TVFStartAdrMap.put(className.obj.getType(), Code.dataSize);
	}

	private int getParentMethAdr(Struct parent, String methName) {
		int adr = 0;
		Obj[] members = parent.getMembers().toArray(new Obj[parent.getMembers().size()]);
		for (int i = parent.getNumberOfFields(); i < members.length; i++) {
			if (members[i].getName().equals(methName)) {
				adr = members[i].getAdr();
				break;
			}
		}
		return adr;
	}

	public void visit(ClassDecl classDecl) {
		// dopuni adrese nasledjenih metoda sa adresama iz roditeljske klase
		if (classDecl.getClassName().getExtendsType().struct != Tab.noType) {
			Obj[] members = classDecl.getClassName().obj.getType().getMembers()
					.toArray(new Obj[classDecl.getClassName().obj.getType().getMembers().size()]);
			for (int i = classDecl.getClassName().obj.getType().getNumberOfFields(); i < members.length; i++) {
				if (members[i].getFpPos() == 1) {// ako je prepisana od roditelja
					members[i].setAdr(
							getParentMethAdr(classDecl.getClassName().getExtendsType().struct, members[i].getName()));
				}
			}
		}

		// popuni listu koja sluzi za generisanje tab virt fja
		Obj[] members = classDecl.getClassName().obj.getType().getMembers()
				.toArray(new Obj[classDecl.getClassName().obj.getType().getMembers().size()]);
		for (int i = classDecl.getClassName().obj.getType().getNumberOfFields(); i < members.length; i++) {
			if (!members[i].getName().equals(classDecl.getClassName().obj.getName()))
				addFunctionEntry(members[i].getName(), members[i].getAdr());
		}
		addTableTerminator();
		// currentParentClassStruct = null;
		isClassMethodActive = false;
	}

	public void visit(RecordDecl recordDecl) {
		TVFStartAdrMap.put(recordDecl.getRecordName().obj.getType(), Code.dataSize);
		addTableTerminator();
	}

	public void visit(SingStmtReturnExpr returnExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(SingStmtReturn returnNoExpr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(SingStmtPrintNumConst singStmtPrintNumConst) {
		if (singStmtPrintNumConst.getExpr().struct == Tab.intType
				|| singStmtPrintNumConst.getExpr().struct == Tab.find("bool").getType()) {
			Code.loadConst(singStmtPrintNumConst.getNc());
			Code.put(Code.print);
		} else {
			Code.loadConst(singStmtPrintNumConst.getNc());
			Code.put(Code.bprint);
		}
	}

	public void visit(SingStmtPrintNoNumConst singStmtPrintNoNumConst) {
		if (singStmtPrintNoNumConst.getExpr().struct == Tab.intType
				|| singStmtPrintNoNumConst.getExpr().struct == Tab.find("bool").getType()) {
			Code.loadConst(5);
			Code.put(Code.print);
		} else {
			Code.loadConst(5);
			Code.put(Code.bprint);
		}
	}

	public void visit(FactorVar factorVar) {
		// Code.load(factorVar.getDesignator().obj);
		/*
		 * if (hasFields) { if (factorVar.getDesignator().obj.getType().getElemType() ==
		 * Tab.charType) { Code.put(Code.baload); } else { Code.put(Code.aload); } }
		 */
		if (toLoadFirst) {
			/*
			 * for (int i = indexToLoad; i < toLoadOp.size(); i++) {
			 * Code.put(toLoadOp.get(i)); }
			 */
			Code.load(toLoadObj);
			toLoadFirst = false;
		}
		if (isPrevArrayAccess) {
			Code.put(arrayAccessOp);
			// designatorCodeOp.add(arrayAccessOp);
		}
		/*
		 * if (!incdecHasFields) incdecHasFields = hasFields;
		 */
		hasFields = false;
		isPrevArrayAccess = false;
	}

	public void visit(FactorNumConst factorNumConst) {
		Obj o = new Obj(Obj.Con, "", Tab.intType);
		o.setAdr(factorNumConst.getNc());
		Code.load(o);
		// load(o, true);
	}

	public void visit(FactorCharConst factorCharConst) {
		Obj o = new Obj(Obj.Con, "", Tab.charType);
		o.setAdr(factorCharConst.getCc().charAt(1));
		Code.load(o);
		// load(o, true);
	}

	public void visit(FactorBoolConst factorBoolConst) {
		Obj o = new Obj(Obj.Con, "", Tab.find("bool").getType());
		o.setAdr(factorBoolConst.getBc().equals("true") ? 1 : 0);
		Code.load(o);
		// load(o, true);
	}

	public void visit(MinusFactor minusFactor) {
		Code.loadConst(-1);
		// put(Code.const_m1, true);
		Code.put(Code.mul);
		// put(Code.mul, true);
	}

	public void visit(MoreFactor moreFactor) {
		if (moreFactor.getMulop().getClass() == StarOp.class) {
			Code.put(Code.mul);
			// put(Code.mul, true);
		} else if (moreFactor.getMulop().getClass() == DivideOp.class) {
			Code.put(Code.div);
			// put(Code.div, true);
		} else if (moreFactor.getMulop().getClass() == ModOp.class) {
			Code.put(Code.rem);
			// put(Code.rem, true);
		}
	}

	public void visit(MoreTerm moreTerm) {
		if (moreTerm.getAddop().getClass() == PlusOp.class) {
			Code.put(Code.add);
			// put(Code.add, true);
		} else if (moreTerm.getAddop().getClass() == MinusOp.class) {
			Code.put(Code.sub);
			// put(Code.sub, true);
		}
	}

	public void visit(Designator designator) {
		incdecHasFields = hasFields;
		if (designator.getParent().getClass() != FactorVar.class
				&& designator.getParent().getClass() != DecDesOption.class
				&& designator.getParent().getClass() != IncDesOption.class)
			toLoadFirst = false;
	}

	public void visit(DesignatorNameIdent designatorNameIdent) {
		/*
		 * if (designatorName.getParent().getParent().getClass() == IncDesOption.class
		 * || designatorName.getParent().getParent().getClass() == DecDesOption.class) {
		 * designatorCodeOp.clear();// ako ti je roditelj od designator inc ili dec onda
		 * clear }
		 */
		currentDesignatorUseForFileds = designatorNameIdent.obj;

		int kind = currentDesignatorUseForFileds.getKind();
		if (kind == Obj.Var || kind == Obj.Con || kind == Obj.Fld) {
			if (kind == Obj.Fld) {
				Code.put(Code.load_n + 0);
				toLoadFirst = true;
				toLoadObj = currentDesignatorUseForFileds;
			} else {
				Code.load(currentDesignatorUseForFileds);
			}
			// System.out.println(currentDesignatorUseForFileds.getKind());

			// load(currentDesignatorUseForFileds, true);
		}
	}

	public void visit(DesignatorNameThis designatorNameThis) {
		currentDesignatorUseForFileds = designatorNameThis.obj;
		Code.put(Code.load_n + 0);
	}

	public void visit(DesignatorNameSuper designatorNameSuper) {
		currentDesignatorUseForFileds = designatorNameSuper.obj;
	}

	public void visit(AssignDesOption assignDesOption) {
		// kako cu da znam da nece arr =new int[5] da dodelu a ne npr arr[1] = 1;
		// svuda treba da se bakcem sa elem niza gde se koristi designator
		if (assignHasFields || (assignDesOption.getDesignator().getDesignatorFields().obj == null
				&& assignDesOption.getDesignator().obj.getKind() == Obj.Fld)) {// ako je designatorfields prazno a
																				// designator obj pokazuje nesto sto je
																				// field onda je assignhasfields true
																				// svakako
			if (assignDesOption.getDesignator().obj.getType().getKind() == Struct.Array && !isAllocArray) {
				if (assignDesOption.getDesignator().obj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			} else {
				Code.store(assignDesOption.getDesignator().obj);
			}

		} else {
			Code.store(assignDesOption.getDesignator().obj);
			Code.put(Code.pop);// skinemo designator koji je stavio designatorName
		}

		hasFields = false;// resetujemo za sledeci designator, svi kad iskoriste designator treba da
		isAllocArray = false; // resetuju
		assignHasFields = false;
		// designatorCodeOp.clear();
	}

	public void visit(IncDesOption incDesOption) {
		if (isPrevArrayAccess) {
			Code.put(Code.dup2);
			Code.put(arrayAccessOp);
		} else {
			if (toLoadFirst) {
				Code.put(Code.dup);
				/*
				 * for (int i = indexToLoad; i < toLoadOp.size(); i++) {
				 * Code.put(toLoadOp.get(i)); }
				 */
				Code.load(toLoadObj);
				toLoadFirst = false;
			}
		}
		isPrevArrayAccess = false;

		Code.loadConst(1);
		Code.put(Code.add);

		if (incdecHasFields /* || hasFields */) {
			if (incDesOption.getDesignator().obj.getType().getKind() == Struct.Array) {
				if (incDesOption.getDesignator().obj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			} else {
				Code.store(incDesOption.getDesignator().obj);
			}

		} else {
			Code.store(incDesOption.getDesignator().obj);
		}
		incdecHasFields = false;
		hasFields = false;
		// designatorCodeOp.clear();
	}

	public void visit(DecDesOption decDesOption) {
		if (isPrevArrayAccess) {
			Code.put(Code.dup2);
			Code.put(arrayAccessOp);
		} else {
			if (toLoadFirst) {
				Code.put(Code.dup);
				/*
				 * for (int i = indexToLoad; i < toLoadOp.size(); i++) {
				 * Code.put(toLoadOp.get(i)); }
				 */
				Code.load(toLoadObj);
				toLoadFirst = false;
			}
		}
		isPrevArrayAccess = false;

		Code.loadConst(1);
		Code.put(Code.sub);

		if (incdecHasFields /* || hasFields */) {
			if (decDesOption.getDesignator().obj.getType().getKind() == Struct.Array) {
				if (decDesOption.getDesignator().obj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			} else {
				Code.store(decDesOption.getDesignator().obj);
			}

		} else {
			Code.store(decDesOption.getDesignator().obj);
		}
		incdecHasFields = false;
		hasFields = false;
		// designatorCodeOp.clear();
	}

	public void visit(StartFunctionCall startFunctionCall) {
		if ((startFunctionCall.getDesignator().getDesignatorFields().obj != null || isClassMethodActive)
				&& startFunctionCall.getDesignator().obj.getFpPos() != 3) {

			if (startFunctionCall.getDesignator().getDesignatorFields().obj == null) {
				Code.put(Code.load_n + 0);
			}
			// na steku je this
			if (startFunctionCall.getDesignator().getDesignatorName().getClass() != DesignatorNameSuper.class) {
				Code.put(Code.dup);
				isClassMethCall.add(true);
			} else {
				isClassMethCall.add(false);
			}

		} else {
			isClassMethCall.add(false);
		}
		hasFields = false;
		// isClassMethodActive = false;
	}

	/*
	 * public void visit(Expr expr) { if (!isClassMethCall.isEmpty() &&
	 * isClassMethCall.get(isClassMethCall.size() - 1)) { Code.put(Code.dup_x1);
	 * Code.put(Code.pop); } }
	 */

	public void visit(MoreExpr moreExpr) {
		if (!isClassMethCall.isEmpty() && isClassMethCall.get(isClassMethCall.size() - 1)) {
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
		}
	}

	public void visit(SingleExpr singleExpr) {
		if (!isClassMethCall.isEmpty() && isClassMethCall.get(isClassMethCall.size() - 1)) {
			Code.put(Code.dup_x1);
			Code.put(Code.pop);
		}
	}

	public void visit(FactorFuncCall factorFuncCall) {
		Obj o = factorFuncCall.getStartFunctionCall().getDesignator().obj;
		if (o == Tab.chrObj) {
			// Code.put(Code.dup);
		} else if (o == Tab.ordObj) {
			// Code.put(Code.dup);
		} else if (o == Tab.lenObj) {
			Code.put(Code.arraylength);
		} else {
			if (isClassMethCall.get(isClassMethCall.size() - 1)) {
				Code.put(Code.getfield);
				Code.put2(0);
				Code.put(Code.invokevirtual);
				for (int i = 0; i < o.getName().length(); i++) {
					Code.put4(o.getName().charAt(i));
				}
				Code.put4(-1);
			} else {
				int dest_adr = o.getAdr() - Code.pc;
				Code.put(Code.call);
				Code.put2(dest_adr);
			}
		}
		isClassMethCall.remove(isClassMethCall.size() - 1);
	}

	public void visit(FuncCallDesOption funcCallDesOption) {
		Obj o = funcCallDesOption.getStartFunctionCall().getDesignator().obj;
		if (o == Tab.chrObj) {
			// Code.put(Code.dup);
		} else if (o == Tab.ordObj) {
			// Code.put(Code.dup);
		} else if (o == Tab.lenObj) {
			Code.put(Code.arraylength);
		} else {
			if (isClassMethCall.get(isClassMethCall.size() - 1)) {
				Code.put(Code.getfield);
				Code.put2(0);
				Code.put(Code.invokevirtual);
				for (int i = 0; i < o.getName().length(); i++) {
					Code.put4(o.getName().charAt(i));
				}
				Code.put4(-1);
			} else {
				int dest_adr = o.getAdr() - Code.pc;
				Code.put(Code.call);
				Code.put2(dest_adr);
			}
		}
		isClassMethCall.remove(isClassMethCall.size() - 1);

		if (o.getType() != Tab.noType)
			Code.put(Code.pop);
	}

	public void visit(FactorAllocArray factorAllocArray) {
		Code.put(Code.newarray);
		if (factorAllocArray.getType().struct == Tab.charType)
			Code.put(0);
		else
			Code.put(1);
		isAllocArray = true;
	}

	public void visit(ConstructorName constructorName) {
		constructorName.obj.setAdr(Code.pc);

		SyntaxNode node = constructorName.getParent();

		VarPartCounter vpc = new VarPartCounter();
		node.traverseTopDown(vpc);

		Code.put(Code.enter);
		Code.put(constructorName.obj.getLevel());
		Code.put(constructorName.obj.getLevel() + vpc.getCount());
		classConstructorMap.put(currentClassStruct, constructorName.obj);
	}

	public void visit(ConstructorDecl constructorDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(SingStmtRead singStmtRead) {
		if (hasFields || (singStmtRead.getDesignator().getDesignatorFields().obj == null
				&& singStmtRead.getDesignator().obj.getKind() == Obj.Fld)) {
			if (singStmtRead.getDesignator().obj.getType().getKind() == Struct.Array) {
				if (singStmtRead.getDesignator().obj.getType().getElemType() == Tab.charType) {
					Code.put(Code.bread);
					Code.put(Code.bastore);
				} else {
					Code.put(Code.read);
					Code.put(Code.astore);
				}
			} else {
				if (singStmtRead.getDesignator().obj.getType() == Tab.charType) {
					Code.put(Code.bread);
					Code.store(singStmtRead.getDesignator().obj);
				} else {
					Code.put(Code.read);
					Code.store(singStmtRead.getDesignator().obj);
				}
			}

		} else {
			if (singStmtRead.getDesignator().obj.getType() == Tab.charType) {
				Code.put(Code.bread);
				Code.store(singStmtRead.getDesignator().obj);
				Code.put(Code.pop);
			} else {
				Code.put(Code.read);
				Code.store(singStmtRead.getDesignator().obj);
				Code.put(Code.pop);
			}
		}

		hasFields = false;// resetujemo za sledeci designator, svi kad iskoriste designator treba da
		isPrevArrayAccess = false; // resetuju
	}

	public void visit(ArrayElemStart arrayElemStart) {
		if (toLoadFirst) {
			/*
			 * for (int i = indexToLoad; i < toLoadOp.size(); i++) {
			 * Code.put(toLoadOp.get(i)); }
			 */
			Code.load(toLoadObj);
			toLoadFirst = false;
		}
	}

	public void visit(ArrayElem arrayElem) {
		if (arrayElem.obj.getType().getElemType() == Tab.charType) {
			arrayAccessOp = Code.baload;
		} else {
			arrayAccessOp = Code.aload;
		}
		isPrevArrayAccess = true;
		hasFields = true;// da znaju svi koji koriste designator da li ima polja ili pristupa nizu
	}

	public void visit(ClassField classField) {
		// System.out.println(classField.obj.getName());
		if (toLoadFirst) {
			/*
			 * for (int i = indexToLoad; i < toLoadOp.size(); i++) {
			 * Code.put(toLoadOp.get(i)); }
			 */
			Code.load(toLoadObj);
			toLoadFirst = false;
		}
		if (isPrevArrayAccess) {
			Code.put(arrayAccessOp);
			// designatorCodeOp.add(arrayAccessOp);
		}
		// indexToLoad = toLoadOp.size();
		toLoadFirst = true;// kazemo sledecem u lancu da li prvo treba da ucita
		// load(classField.obj);
		toLoadObj = classField.obj;
		isPrevArrayAccess = false;
		hasFields = true;
	}

	public void visit(Assignop assignop) {
		isPrevArrayAccess = false;// resetujemo za desnu stranu
		assignHasFields = hasFields;
	}

	public void visit(FactorAllocType factorAllocType) {
		Code.put(Code.new_);
		int s = factorAllocType.struct.getNumberOfFields();
		Code.put2(s * 4);
		Code.put(Code.dup);
		int c = TVFStartAdrMap.get(factorAllocType.struct);
		Code.loadConst(c);
		Code.put(Code.putfield);
		Code.put2(0);
		if (classConstructorMap.containsKey(factorAllocType.struct)) {
			Code.put(Code.dup);
			int dest_adr = classConstructorMap.get(factorAllocType.struct).getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(dest_adr);
		}
	}

	public void visit(RelopCondFactExpr relopCondFactExpr) {
		int op = 0;
		if (relopCondFactExpr.getRelop().getClass() == EqualOp.class) {
			op = Code.eq;
		} else if (relopCondFactExpr.getRelop().getClass() == DiffOp.class) {
			op = Code.ne;
		} else if (relopCondFactExpr.getRelop().getClass() == LessOp.class) {
			op = Code.lt;
		} else if (relopCondFactExpr.getRelop().getClass() == LessEqOp.class) {
			op = Code.le;
		} else if (relopCondFactExpr.getRelop().getClass() == GrtOp.class) {
			op = Code.gt;
		} else if (relopCondFactExpr.getRelop().getClass() == GrtEqOp.class) {
			op = Code.ge;
		}
		if (relopCondFactExpr.getParent().getParent().getClass() == CondTerm.class && relopCondFactExpr.getParent()
				.getParent().getParent().getParent().getClass() != ConditionNoError.class) {
			Code.put(Code.jcc + op);
			Code.put2(0);
			condTrueAdrFixup.add(Code.pc - 2);
		} else {
			Code.putFalseJump(op, 0);
			if (relopCondFactExpr.getParent().getParent().getParent().getParent()
					.getClass() == ConditionNoError.class) {
				endOfStatementsAdrFixup.add(Code.pc - 2);
			} else {
				orAdrFixup.add(Code.pc - 2);
			}
		}
	}

	public void visit(SingleCondFactExpr singleCondFactExpr) {
		Code.loadConst(1);
		if (singleCondFactExpr.getParent().getParent().getClass() == CondTerm.class && singleCondFactExpr.getParent()
				.getParent().getParent().getParent().getClass() != ConditionNoError.class) {
			Code.put(Code.jcc + Code.eq);
			Code.put2(0);
			condTrueAdrFixup.add(Code.pc - 2);
		} else {
			Code.putFalseJump(Code.eq, 0);
			if (singleCondFactExpr.getParent().getParent().getParent().getParent()
					.getClass() == ConditionNoError.class) {
				endOfStatementsAdrFixup.add(Code.pc - 2);
			} else {
				orAdrFixup.add(Code.pc - 2);
			}
		}
	}

	public void visit(DoWhileStart doWhileStart) {
		doWhileStartPc.add(Code.pc);
		continueListAdrFixup.add(new ArrayList<Integer>());
		breakListAdrFixup.add(new ArrayList<Integer>());
	}

	public void visit(SingStmtDoWhile singStmtDoWhile) {
		for (int i = 0; i < condTrueAdrFixup.size(); i++) {
			Code.fixup(condTrueAdrFixup.get(i));
		}
		condTrueAdrFixup.clear();
		Code.putJump(doWhileStartPc.get(doWhileStartPc.size() - 1));
		doWhileStartPc.remove(doWhileStartPc.size() - 1);
		Code.fixup(endOfStatementsAdrFixup.get(endOfStatementsAdrFixup.size() - 1));
		endOfStatementsAdrFixup.remove(endOfStatementsAdrFixup.size() - 1);

		for (int i = 0; i < orAdrFixup.size(); i++) {
			Code.fixup(orAdrFixup.get(i));
		}
		orAdrFixup.clear();

		List<Integer> currentDoWhileBreakAdrFixup = breakListAdrFixup.get(breakListAdrFixup.size() - 1);
		for (int i = 0; i < currentDoWhileBreakAdrFixup.size(); i++) {
			Code.fixup(currentDoWhileBreakAdrFixup.get(i));
		}
		breakListAdrFixup.remove(breakListAdrFixup.size() - 1);

	}

	public void visit(DoWhileCondStart doWhileCondStart) {
		List<Integer> currentDoWhileContinueAdrFixup = continueListAdrFixup.get(continueListAdrFixup.size() - 1);
		for (int i = 0; i < currentDoWhileContinueAdrFixup.size(); i++) {
			Code.fixup(currentDoWhileContinueAdrFixup.get(i));
		}
		continueListAdrFixup.remove(continueListAdrFixup.size() - 1);
	}

	public void visit(OrRelOp orRelOp) {
		for (int i = 0; i < orAdrFixup.size(); i++) {
			Code.fixup(orAdrFixup.get(i));
		}
		orAdrFixup.clear();
	}

	public void visit(ThenStart thenStart) {
		for (int i = 0; i < condTrueAdrFixup.size(); i++) {
			Code.fixup(condTrueAdrFixup.get(i));
		}
		condTrueAdrFixup.clear();
	}

	public void visit(ElseStart elseStart) {
		Code.putJump(0);
		endOfThenAdrFixup.add(Code.pc - 2);
		Code.fixup(endOfStatementsAdrFixup.get(endOfStatementsAdrFixup.size() - 1));
		endOfStatementsAdrFixup.remove(endOfStatementsAdrFixup.size() - 1);

		for (int i = 0; i < orAdrFixup.size(); i++) {
			Code.fixup(orAdrFixup.get(i));
		}
		orAdrFixup.clear();
	}

	public void visit(SingStmtIf singStmtIf) {
		Code.fixup(endOfStatementsAdrFixup.get(endOfStatementsAdrFixup.size() - 1));
		endOfStatementsAdrFixup.remove(endOfStatementsAdrFixup.size() - 1);

		for (int i = 0; i < orAdrFixup.size(); i++) {
			Code.fixup(orAdrFixup.get(i));
		}
		orAdrFixup.clear();
	}

	public void visit(SingStmtIfElse singStmtIfElse) {
		Code.fixup(endOfThenAdrFixup.get(endOfThenAdrFixup.size() - 1));
		endOfThenAdrFixup.remove(endOfThenAdrFixup.size() - 1);
	}

	public void visit(SingStmtBreak singStmtBreak) {
		Code.putJump(0);
		breakListAdrFixup.get(breakListAdrFixup.size() - 1).add(Code.pc - 2);
	}

	public void visit(SingStmtContinue singStmtContinue) {
		Code.putJump(0);
		continueListAdrFixup.get(continueListAdrFixup.size() - 1).add(Code.pc - 2);
	}
}
