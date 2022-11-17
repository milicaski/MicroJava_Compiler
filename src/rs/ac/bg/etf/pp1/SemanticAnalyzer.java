package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.AssignDesOption;
import rs.ac.bg.etf.pp1.ast.BoolConstAssign;
import rs.ac.bg.etf.pp1.ast.CharConstAssign;
import rs.ac.bg.etf.pp1.ast.ClassDecl;
import rs.ac.bg.etf.pp1.ast.ClassField;
import rs.ac.bg.etf.pp1.ast.ClassName;
import rs.ac.bg.etf.pp1.ast.ConditionError;
import rs.ac.bg.etf.pp1.ast.ConstDeclError;
import rs.ac.bg.etf.pp1.ast.ConstDeclNoError;
import rs.ac.bg.etf.pp1.ast.ConstPart;
import rs.ac.bg.etf.pp1.ast.ConstType;
import rs.ac.bg.etf.pp1.ast.ConstructorDecl;
import rs.ac.bg.etf.pp1.ast.ConstructorName;
import rs.ac.bg.etf.pp1.ast.DecDesOption;
import rs.ac.bg.etf.pp1.ast.Designator;
import rs.ac.bg.etf.pp1.ast.DesignatorFieldsExist;
import rs.ac.bg.etf.pp1.ast.DesignatorNameIdent;
import rs.ac.bg.etf.pp1.ast.DesignatorNameSuper;
import rs.ac.bg.etf.pp1.ast.DesignatorNameThis;
import rs.ac.bg.etf.pp1.ast.DiffOp;
import rs.ac.bg.etf.pp1.ast.DoWhileStart;
import rs.ac.bg.etf.pp1.ast.EqualOp;
import rs.ac.bg.etf.pp1.ast.ErrorAssignDes;
import rs.ac.bg.etf.pp1.ast.ErrorConstList;
import rs.ac.bg.etf.pp1.ast.ErrorEndOfVariablesDecl;
import rs.ac.bg.etf.pp1.ast.ErrorExtends;
import rs.ac.bg.etf.pp1.ast.ErrorFPL;
import rs.ac.bg.etf.pp1.ast.ErrorFormParsList;
import rs.ac.bg.etf.pp1.ast.ErrorVarList;
import rs.ac.bg.etf.pp1.ast.Expr;
import rs.ac.bg.etf.pp1.ast.ExtendsT;
import rs.ac.bg.etf.pp1.ast.FactorAllocArray;
import rs.ac.bg.etf.pp1.ast.FactorAllocType;
import rs.ac.bg.etf.pp1.ast.FactorBoolConst;
import rs.ac.bg.etf.pp1.ast.FactorCharConst;
import rs.ac.bg.etf.pp1.ast.FactorFuncCall;
import rs.ac.bg.etf.pp1.ast.FactorNumConst;
import rs.ac.bg.etf.pp1.ast.FactorParentheses;
import rs.ac.bg.etf.pp1.ast.FactorVar;
import rs.ac.bg.etf.pp1.ast.FormParsPartArray;
import rs.ac.bg.etf.pp1.ast.FormParsPartNoArray;
import rs.ac.bg.etf.pp1.ast.FuncCallDesOption;
import rs.ac.bg.etf.pp1.ast.IncDesOption;
import rs.ac.bg.etf.pp1.ast.Method;
import rs.ac.bg.etf.pp1.ast.MethodTypeName;
import rs.ac.bg.etf.pp1.ast.MinusFactor;
import rs.ac.bg.etf.pp1.ast.MoreExpr;
import rs.ac.bg.etf.pp1.ast.MoreFactor;
import rs.ac.bg.etf.pp1.ast.MoreTerm;
import rs.ac.bg.etf.pp1.ast.NoDesignatorFields;
import rs.ac.bg.etf.pp1.ast.NoExtendsT;
import rs.ac.bg.etf.pp1.ast.NormalEndOfVariablesDecl;
import rs.ac.bg.etf.pp1.ast.NumConstAssign;
import rs.ac.bg.etf.pp1.ast.ProgName;
import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.ast.RecordDecl;
import rs.ac.bg.etf.pp1.ast.RecordName;
import rs.ac.bg.etf.pp1.ast.RelopCondFactExpr;
import rs.ac.bg.etf.pp1.ast.SingStmtBreak;
import rs.ac.bg.etf.pp1.ast.SingStmtContinue;
import rs.ac.bg.etf.pp1.ast.SingStmtDoWhile;
import rs.ac.bg.etf.pp1.ast.SingStmtPrintNoNumConst;
import rs.ac.bg.etf.pp1.ast.SingStmtPrintNumConst;
import rs.ac.bg.etf.pp1.ast.SingStmtRead;
import rs.ac.bg.etf.pp1.ast.SingStmtReturn;
import rs.ac.bg.etf.pp1.ast.SingStmtReturnExpr;
import rs.ac.bg.etf.pp1.ast.SingleCondFactExpr;
import rs.ac.bg.etf.pp1.ast.SingleExpr;
import rs.ac.bg.etf.pp1.ast.SingleFactor;
import rs.ac.bg.etf.pp1.ast.SingleTerm;
import rs.ac.bg.etf.pp1.ast.StartFunctionCall;
import rs.ac.bg.etf.pp1.ast.StatementListStart;
import rs.ac.bg.etf.pp1.ast.Type;
import rs.ac.bg.etf.pp1.ast.VarDeclError;
import rs.ac.bg.etf.pp1.ast.VarDeclType;
import rs.ac.bg.etf.pp1.ast.VarPartArray;
import rs.ac.bg.etf.pp1.ast.VarPartNoArray;
import rs.ac.bg.etf.pp1.ast.SyntaxNode;
import rs.ac.bg.etf.pp1.ast.Term;
import rs.ac.bg.etf.pp1.ast.ArrayElem;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class SemanticAnalyzer extends VisitorAdaptor {
	boolean errorDetected = false;
	int nVars;
	Obj currentMethod = null;
	boolean returnFound = false;
	boolean mainFound = false;
	Integer numConst;
	String charConst;
	Boolean boolConst;
	// int currentScopeNVars = 0;
	int formParamsCount = 0;

	enum AssignmentType {
		noType, intType, charType, boolType
	};

	AssignmentType assigmentType = AssignmentType.noType;

	Struct constDeclaredType = null;

	Struct varDeclaredType = null;

	List<Obj> currentMethodDesignator = new ArrayList<>();
	List<Integer> currentMethodCallActPars = new ArrayList<>();

	Logger log = Logger.getLogger(getClass());
	// private Obj currentDesignator;
	private List<Obj> currentDesignator = new ArrayList<>();
	private boolean hasFields = false;

	private boolean isClass = false;
	private boolean isPrevArrayElem = false;
	private boolean arrayAlloc = false;

	private int isDoWhileActive = 0;
	private boolean isArrayAccess = false;
	private Obj currentClass;
	private List<Boolean> isClassFuncCall = new ArrayList<>();// mora lista jer mozemo da zovemo metodu klase ugnjezdeno
																// f.m(f.m)

	private String extendClass;

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(Obj o, SyntaxNode sn, String spec) {
		StringBuilder msg;
		if (o != Tab.noObj) {
			MyDumpSymbolTableVisitor v = new MyDumpSymbolTableVisitor();
			v.visitObjNode(o);
			msg = new StringBuilder("Pretraga na " + sn.getLine() + "(" + o.getName() + "), " + spec + " ---> ");
			msg.append(v.getOutput());
		} else {
			msg = new StringBuilder("Pretraga na " + sn.getLine() + " " + spec);
		}
		// int line = (info == null) ? 0 : info.getLine();
		// if (line != 0)
		// msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

	public boolean passed() {
		return !errorDetected;
	}

	public void visit(ErrorAssignDes errorAssignDes) {
		report_error("Uspesan oporavak od greske pri dodeli", errorAssignDes);
	}

	public void visit(ErrorVarList errorVarList) {
		report_error("Uspesan oporavak od greske do ',' pri definiciji promenljivih", errorVarList);
	}

	public void visit(VarDeclError varDeclError) {
		report_error("Uspesan oporavak od greske do ';' pri definiciji promenljivih", varDeclError);
	}

	public void visit(ErrorConstList errorConstList) {
		report_error("Uspesan oporavak od greske do ',' pri definiciji globalnih konstanti", errorConstList);
	}

	public void visit(ConstDeclError constDeclError) {
		report_error("Uspesan oporavak od greske do ';' pri definiciji globalnih konstanti", constDeclError);
	}

	public void visit(ErrorFormParsList errorFormParsList) {
		report_error("Uspesan oporavak od greske do ',' pri deklaraciji formalnih parametara funkcije",
				errorFormParsList);
	}

	public void visit(ErrorFPL errorFPL) {
		report_error("Uspesan oporavak od greske do ')' pri deklaraciji formalnih parametara funkcije", errorFPL);
	}

	public void visit(ConditionError conditionError) {
		report_error("Uspesan oporavak od greske do ')' pri logickom izrazu u if konstrukciji", conditionError);
	}

	public void visit(ErrorExtends errorExtends) {
		report_error("Uspesan oporavak od greske do '{' pri izvodjenju klase", errorExtends);
		errorExtends.struct = Tab.noType;
	}

	public void visit(ErrorEndOfVariablesDecl errorEndOfVariablesDecl) {
		report_error("Uspesan oporavak od greske do '{' pri deklaraciji polja unutrasnje klase",
				errorEndOfVariablesDecl);
	}

	public void visit(ProgName ProgName) {
		ProgName.obj = Tab.insert(Obj.Prog, ProgName.getName(), Tab.noType);
		Tab.openScope();
		/// currentScopeNVars = 0;
	}

	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(program.getProgName().obj);
		Tab.closeScope();
		if (!mainFound) {
			report_error("Nije pronadjena main metoda", null);
		}
	}

	public void visit(Method method) {
		if (!returnFound && currentMethod.getType() != Tab.noType) {
			report_error("Semanticka greska na liniji " + method.getLine() + ": funkcija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		}
		if (currentMethod.getName().equals("main") && formParamsCount > 0) {
			report_error("Semanticka greska na liniji " + method.getLine() + ": funkcija " + currentMethod.getName()
					+ " ne sme da ima formalne parametre!", null);
		}

		// Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		// currentMethod.setLevel(formParamsCount);// I hope :)
		if (isClass) {
			Tab.chainLocalSymbols(currentClass.getType());
		}
		returnFound = false;
		currentMethod = null;
	}

	public void visit(NormalEndOfVariablesDecl normalEndOfVariablesDecl) {
		Tab.chainLocalSymbols(currentClass.getType());
	}

	public void visit(StatementListStart statementListStart) {// dodato zbog poziva iste metode
		Tab.chainLocalSymbols(currentMethod);
		currentMethod.setLevel(formParamsCount);// I hope :)
	}

	public void visit(MethodTypeName methodTypeName) {
		boolean ok = true;
		Obj resultObj = null;
		if (Tab.currentScope.getLocals() != null) {
			resultObj = Tab.currentScope.getLocals().searchKey(methodTypeName.getName());
		}
		if (resultObj != null) {
			report_error("Greska vec postoji metoda sa nazivom: " + methodTypeName.getName() + " !", methodTypeName);
			methodTypeName.obj = Tab.noObj;
			ok = false;
			// currentMethod = methodTypeName.obj;
		} // else {
		if (isClass && methodTypeName.getName().equals(currentClass.getName())) {
			report_error("Greska metoda ne sme da se zove kao naziv klase: " + methodTypeName.getName() + " !",
					methodTypeName);
			methodTypeName.obj = Tab.noObj;
			ok = false;
			// currentMethod = methodTypeName.obj;
		} // else {

		if (isClass && currentClass.getType().getElemType() != null) {// provera da li se poklapaju tipovi povratne
																		// vrednosti
			Obj[] parentMembers = currentClass.getType().getElemType().getMembers()
					.toArray(new Obj[currentClass.getType().getElemType().getMembers().size()]);
			for (int i = currentClass.getType().getElemType().getNumberOfFields(); i < parentMembers.length; i++) {
				if (parentMembers[i].getName().equals(methodTypeName.getName())) {
					if (!parentMembers[i].getType().equals(methodTypeName.getType().struct)
							&& !assignable(methodTypeName.getType().struct, parentMembers[i].getType())) {
						report_error(
								"Greska metoda " + methodTypeName.getName()
										+ " izvedene klase mora da ima kompatibilan tip povratne vrednosti!",
								methodTypeName);
						methodTypeName.obj = Tab.noObj;
						ok = false;
					}
				}
			}
		}

		methodTypeName.obj = Tab.insert(Obj.Meth, methodTypeName.getName(), methodTypeName.getType().struct);
		currentMethod = methodTypeName.obj;
		if (methodTypeName.getName().equals("main")) {
			if (mainFound) {// dodatna provera
				report_error("Semanticka greska na liniji " + methodTypeName.getLine() + ": main metoda vec postoji!",
						null);
			}
			if (isClass) {// dodatna provera
				report_error(
						"Semanticka greska na liniji " + methodTypeName.getLine() + ": main metoda mora biti globalna!",
						null);
			}
			mainFound = true;
			if (methodTypeName.getType().struct != Tab.noType) {
				report_error("Semanticka greska na liniji " + methodTypeName.getLine() + ": povratna vrednost funkcije "
						+ methodTypeName.getName() + " mora biti void!", null);
			}
		}
		Tab.openScope();
		formParamsCount = 0;
		if (isClass) {
			Tab.insert(Obj.Var, "this", currentClass.getType());
			formParamsCount++;
		}
		// currentScopeNVars = 0;
		// }
		// }
	}

	public void visit(ConstructorName constructorName) {
		if (!currentClass.getName().equals(constructorName.getId())) {
			report_error("Konstruktor mora imati isti naziv kao i klasa u kojoj je definisan ", constructorName);
			constructorName.obj = Tab.noObj;
			currentMethod = constructorName.obj;
		}
		constructorName.obj = Tab.insert(Obj.Meth, constructorName.getId(), Tab.noType);
		currentMethod = constructorName.obj;
		Tab.openScope();
		Tab.insert(Obj.Var, "this", currentClass.getType());
		constructorName.obj.setLevel(1);
		constructorName.obj.setFpPos(2);

	}

	public void visit(ConstructorDecl constructorDecl) {
		Tab.chainLocalSymbols(constructorDecl.getConstructorName().obj);
		Tab.closeScope();
		currentMethod = null;
	}

	public void visit(Type type) {
		if (type.getTypeName().equals("void")) {
			type.struct = Tab.noType;
		} else {
			Obj typeObj = Tab.find(type.getTypeName());
			if (typeObj == Tab.noObj) {
				report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
				type.struct = Tab.noType;
			} else {
				if (typeObj.getKind() == Obj.Type) {
					type.struct = typeObj.getType();
				} else {
					if (typeObj.getKind() == Obj.Meth && isClass) {// proveri jel nije konstrktor
						Obj o = null;
						if (type.getParent().getClass() == MethodTypeName.class) {
							o = Tab.currentScope().getOuter().findSymbol(type.getTypeName());
						} else {
							o = Tab.currentScope().getOuter().getOuter().findSymbol(type.getTypeName());
						}
						if (o == null) {
							report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
							type.struct = Tab.noType;
						} else {
							type.struct = o.getType();
						}
					} else {
						report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
						type.struct = Tab.noType;
					}
				}
			}
		}
	}

	public void visit(NumConstAssign numConstAssign) {
		numConst = numConstAssign.getNum();
		assigmentType = AssignmentType.intType;
	}

	public void visit(CharConstAssign charConstAssign) {
		charConst = charConstAssign.getCh();
		assigmentType = AssignmentType.charType;
	}

	public void visit(BoolConstAssign boolConstAssign) {
		boolean b;
		if (boolConstAssign.getB().equals("true")) {
			b = true;
		} else {
			b = false;
		}
		boolConst = b;
		assigmentType = AssignmentType.boolType;
	}

	public void visit(ConstPart constPart) {
		if (Tab.find(constPart.getConstName()) != Tab.noObj) {
			report_error("Greska visestruka definicija simbola " + constPart.getConstName(), constPart);
		} else {
			if (constDeclaredType == null) {
				report_error("Greska nema ga tip ko zna sto ne bi trebalo nikad da se desi ", null);
			} else if (constDeclaredType == Tab.intType) {
				if (assigmentType != AssignmentType.intType) {
					report_error("Ne podudara se tip konstante i vrednost koja joj se dodeljuje na liniji "
							+ constPart.getLine(), null);
				} else {
					Obj constObj = Tab.insert(Obj.Con, constPart.getConstName(), constDeclaredType);
					constObj.setAdr(numConst);
				}
			} else if (constDeclaredType == Tab.charType) {
				if (assigmentType != AssignmentType.charType) {
					report_error("Ne podudara se tip konstante i vrednost koja joj se dodeljuje na liniji "
							+ constPart.getLine(), null);
				} else {
					Obj constObj = Tab.insert(Obj.Con, constPart.getConstName(), constDeclaredType);
					constObj.setAdr(charConst.charAt(1));
				}
			} else if (constDeclaredType == Tab.find("bool").getType()) {
				if (assigmentType != AssignmentType.boolType) {
					report_error("Ne podudara se tip konstante i vrednost koja joj se dodeljuje na liniji "
							+ constPart.getLine(), null);
				} else {
					Obj constObj = Tab.insert(Obj.Con, constPart.getConstName(), constDeclaredType);
					constObj.setAdr(boolConst == true ? 1 : 0);
				}
			} else {
				report_error("Nije moguce da konstantne promenljive budu ovog tipa na liniji " + constPart.getLine(),
						null);
			}
		}
	}

	public void visit(ConstType constType) {
		constDeclaredType = constType.getType().struct;
	}

	public void visit(ConstDeclNoError constDeclNoError) {
		constDeclaredType = null;
	}

	public void visit(VarDeclType varDeclType) {
		varDeclaredType = varDeclType.getType().struct;
	}

	public void visit(VarPartArray varPartArray) {
		Obj resultObj = null;
		if (Tab.currentScope.getLocals() != null) {
			resultObj = Tab.currentScope.getLocals().searchKey(varPartArray.getVarNameA());
		}
		if (/* Tab.find(varPartArray.getVarNameA()) != Tab.noObj && */ resultObj != null) {// prvi uslov nemam pojma sto
																							// sam bila dodala mislim da
																							// ne treba
			report_error("Greska visestruka definicija simbola " + varPartArray.getVarNameA(), varPartArray);
		} else {
			if (isClass && currentMethod == null) {
				Tab.insert(Obj.Fld, varPartArray.getVarNameA(), new Struct(Struct.Array, varDeclaredType));
			} else {
				Tab.insert(Obj.Var, varPartArray.getVarNameA(), new Struct(Struct.Array, varDeclaredType));
			}
		}
	}

	public void visit(VarPartNoArray varPartNoArray) {
		Obj resultObj = null;
		if (Tab.currentScope.getLocals() != null) {
			resultObj = Tab.currentScope.getLocals().searchKey(varPartNoArray.getVarName());
		}
		if (/* Tab.find(varPartNoArray.getVarName()) != Tab.noObj && */ resultObj != null) {// prvi uslov nemam pojma
																							// sto sam bila dodala
																							// mislim da ne treba
			report_error("Greska visestruka definicija simbola " + varPartNoArray.getVarName(), varPartNoArray);
		} else {
			if (isClass && currentMethod == null) {
				Tab.insert(Obj.Fld, varPartNoArray.getVarName(), varDeclaredType);
			} else {
				Tab.insert(Obj.Var, varPartNoArray.getVarName(), varDeclaredType);
			}
		}
	}

	public void visit(FormParsPartArray formParsPartArray) {
		Obj resultObj = null;
		if (Tab.currentScope.getLocals() != null) {
			resultObj = Tab.currentScope.getLocals().searchKey(formParsPartArray.getArrName());
		}
		if (Tab.find(formParsPartArray.getArrName()) != Tab.noObj && resultObj != null) {
			report_error("Greska visestruka deklaracija formalnog parametra " + formParsPartArray.getArrName(),
					formParsPartArray);
		} else {
			if (isClass && currentClass.getType().getElemType() != null) {// ako ima roditeljska klasa nadji metodu koja
				// se nadjacava i proveri tipove parametra
				Obj[] parentMembers = currentClass.getType().getElemType().getMembers()
						.toArray(new Obj[currentClass.getType().getElemType().getMembers().size()]);
				for (int i = currentClass.getType().getElemType().getNumberOfFields(); i < parentMembers.length; i++) {
					if (parentMembers[i].getName().equals(currentMethod.getName())) {// nasli smo metodu koju
						// nadjacavamo
						if (parentMembers[i].getLevel() <= formParamsCount) {
							report_error(
									"1.Broj formalnih parametara u izvedenoj metodi mora biti isti kao u roditeljskoj klasi u metodi "
											+ currentMethod.getName(),
									formParsPartArray);
							break;
						}
						Obj[] methPars = parentMembers[i].getLocalSymbols()
								.toArray(new Obj[parentMembers[i].getLocalSymbols().size()]);
						if (methPars[formParamsCount].getType().getKind() != Struct.Array
								|| (methPars[formParamsCount].getType().getKind() == Struct.Array
										&& !methPars[formParamsCount].getType().getElemType()
												.equals(formParsPartArray.getType().struct))) {
							report_error(
									"1.Tip formalnog parametara u izvedenoj metodi mora biti isti kao u roditeljskoj klasi u metodi "
											+ currentMethod.getName(),
									formParsPartArray);
							break;
						}
					}
				}
			}
			Obj varObj = Tab.insert(Obj.Var, formParsPartArray.getArrName(),
					new Struct(Struct.Array, formParsPartArray.getType().struct));
			varObj.setFpPos(formParamsCount);
		}
		formParamsCount++;
	}

	public void visit(FormParsPartNoArray formParsPartNoArray) {
		Obj resultObj = null;
		if (Tab.currentScope.getLocals() != null) {
			resultObj = Tab.currentScope.getLocals().searchKey(formParsPartNoArray.getParName());
		}
		if (Tab.find(formParsPartNoArray.getParName()) != Tab.noObj && resultObj != null) {
			report_error("Greska visestruka deklaracija formalnog parametra " + formParsPartNoArray.getParName(),
					formParsPartNoArray);
		} else {
			if (isClass && currentClass.getType().getElemType() != null) {// ako ima roditeljska klasa nadji metodu koja
																			// se nadjacava i proveri tipove parametra
				Obj[] parentMembers = currentClass.getType().getElemType().getMembers()
						.toArray(new Obj[currentClass.getType().getElemType().getMembers().size()]);
				for (int i = currentClass.getType().getElemType().getNumberOfFields(); i < parentMembers.length; i++) {
					if (parentMembers[i].getName().equals(currentMethod.getName())) {// nasli smo metodu koju
																						// nadjacavamo
						if (parentMembers[i].getLevel() <= formParamsCount) {
							report_error(
									"2.Broj formalnih parametara u izvedenoj metodi mora biti isti kao u roditeljskoj klasi u metodi "
											+ currentMethod.getName(),
									formParsPartNoArray);
							break;
						}
						Obj[] methPars = parentMembers[i].getLocalSymbols()
								.toArray(new Obj[parentMembers[i].getLocalSymbols().size()]);
						if (!methPars[formParamsCount].getType().equals(formParsPartNoArray.getType().struct)) {
							report_error(
									"2.Tip formalnog parametara u izvedenoj metodi mora biti isti kao u roditeljskoj klasi u metodi "
											+ currentMethod.getName(),
									formParsPartNoArray);
							break;
						}
					}
				}
			}
			Obj varObj = Tab.insert(Obj.Var, formParsPartNoArray.getParName(), formParsPartNoArray.getType().struct);
			varObj.setFpPos(formParamsCount);
		}
		formParamsCount++;
	}

	public void visit(SingStmtReturnExpr singStmtReturnExpr) {
		returnFound = true;
		Struct currMethType = currentMethod.getType();
		if (!currMethType.compatibleWith(singStmtReturnExpr.getExpr().struct)
				&& !assignable(singStmtReturnExpr.getExpr().struct, currMethType)) {
			report_error("Greska na liniji " + singStmtReturnExpr.getLine() + " : "
					+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
					+ currentMethod.getName(), null);
		}
	}

	public void visit(SingStmtReturn singStmtReturn) {
		Struct currMethType = currentMethod.getType();
		if (currMethType != Tab.noType) {
			report_error("Greska na liniji " + singStmtReturn.getLine() + " : "
					+ "metoda nije void, tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
					+ currentMethod.getName(), null);
		}
	}

	public void visit(Expr expr) {
		expr.struct = expr.getTermList().struct;
	}

	public void visit(MoreTerm moreTerm) {
		if (moreTerm.getTermList().struct != Tab.intType || moreTerm.getTerm().struct != Tab.intType) {
			report_error("Greska u liniji " + moreTerm.getLine() + ": svi izrazi moraju biti int", null);
		}
		moreTerm.struct = moreTerm.getTermList().struct;
	}

	public void visit(SingleTerm singleTerm) {
		singleTerm.struct = singleTerm.getTerm().struct;
	}

	public void visit(Term term) {
		term.struct = term.getFactorList().struct;
	}

	public void visit(MoreFactor moreFactor) {
		if (moreFactor.getFactorList().struct != Tab.intType || moreFactor.getFactor().struct != Tab.intType) {
			report_error("Greska u liniji " + moreFactor.getLine() + ": svi izrazi moraju biti int", null);
		}
		moreFactor.struct = moreFactor.getFactorList().struct;
	}

	public void visit(SingleFactor singleFactor) {
		singleFactor.struct = singleFactor.getFactor().struct;
	}

	public void visit(MinusFactor minusFactor) {
		if (minusFactor.getFactor().struct != Tab.intType) {
			report_error("Greska u liniji " + minusFactor.getLine() + ": tip izraza mora biti int", null);
		}
		minusFactor.struct = minusFactor.getFactor().struct;
	}

	public void visit(Designator designator) {
		if (designator.getDesignatorName().getClass() == DesignatorNameSuper.class
				&& designator.getDesignatorFields().obj != null) {
			report_error("Greska: neispravno napisan poziv SUPER, ne sme da ima pristupe poljima", designator);
		}
		if (designator.getDesignatorFields().obj != null) {
			hasFields = true;
			designator.obj = designator.getDesignatorFields().obj;
		} else {
			hasFields = false;
			designator.obj = designator.getDesignatorName().obj;
		}
		isArrayAccess = isPrevArrayElem;// koristimo u factorVar, inc, dec da znamo da li elemType ili Type da vratimo
		currentDesignator.remove(currentDesignator.size() - 1);
		isPrevArrayElem = false;
		/*
		 * if (designator.obj.getKind() == Obj.Meth &&
		 * designator.getDesignatorFields().obj == null &&
		 * designator.getParent().getClass() == StartFunctionCall.class) {
		 * report_info(designator.obj, designator); }
		 */
		if (designator.getDesignatorName().getClass() == DesignatorNameIdent.class
				&& (designator.obj.getKind() == Obj.Con || designator.obj.getKind() == Obj.Var)) {
			if (designator.obj.getLevel() == 0) {
				report_info(designator.obj, designator, "pristup globalnoj promenljivi");
			} else {
				report_info(designator.obj, designator, "pristup lokalnoj promenljivi");
				// if (designator.obj.getFpPos() > 0) {
				if (currentMethod != null && designator.obj.getAdr() < currentMethod.getLevel()) {
					report_info(designator.obj, designator, "pristup formalnom parametru funkcije");
				}
			}
		}
		if ((designator.obj.getKind() == Obj.Meth && !isClass && designator.getDesignatorFields().obj == null
				&& designator.getParent().getClass() == StartFunctionCall.class)) {
			report_info(designator.obj, designator, "poziv globalne funkcije");
		}
		if (designator.obj.getKind() == Obj.Fld) {
			report_info(designator.obj, designator, "pristup polju klase");
		}
		if (designator.getDesignatorName().getClass() == DesignatorNameSuper.class && designator.obj.getFpPos() == 2) {
			report_info(designator.obj, designator, "poziv konstruktora natklase");
		}
	}

	public void visit(DesignatorNameIdent designatorNameIdent) {
		Obj desObj = Tab.find(designatorNameIdent.getDesName());
		if (desObj == Tab.noObj) {
			report_error("Greska nije definisana promenljiva " + designatorNameIdent.getDesName(), designatorNameIdent);
		}
		if (desObj.getKind() == Obj.Type) {
			report_error("Greska : promenljiva " + designatorNameIdent.getDesName() + " predstavlja tip!",
					designatorNameIdent);
		}
		designatorNameIdent.obj = desObj;
		// currentDesignator = desObj;
		currentDesignator.add(desObj);
		// isPrevArrayElem = false;
	}

	public void visit(DesignatorNameThis designatorNameThis) {
		// sme samo u metodi klase
		// mora negde da nadjem Obj koji predstavlja this
		Obj desObj = null;
		Scope s = Tab.currentScope();
		if (s.getLocals() != null) {
			desObj = s.getLocals().searchKey("this");
		}
		if (desObj == null) {
			report_error("Greska promenljiva this se moze naci samo unutar metoda unutrasnjih klasa ",
					designatorNameThis);
			desObj = Tab.noObj;
		}
		designatorNameThis.obj = desObj;
		currentDesignator.add(desObj);
	}

	public void visit(DesignatorNameSuper designatorNameSuper) {
		if (!isClass || currentMethod == null) {
			report_error("Greska SUPER se moze naci samo unutar metoda unutrasnjih klasa ", designatorNameSuper);
			designatorNameSuper.obj = Tab.noObj;
			currentDesignator.add(Tab.noObj);
		} else {
			// pronadji roditejsku
			if (currentClass.getType().getElemType() == null) {
				report_error(
						"Greska SUPER se moze naci samo unutar metoda unutrasnjih klasa koje nasledjuju neku klasu ",
						designatorNameSuper);
				designatorNameSuper.obj = Tab.noObj;
				currentDesignator.add(Tab.noObj);
			} else {
				Obj[] parentMembers = currentClass.getType().getElemType().getMembers()
						.toArray(new Obj[currentClass.getType().getElemType().getMembers().size()]);
				if (currentMethod.getName().equals(currentClass.getName())) {// ako pozivamo konstruktor natklase
					boolean found = false;
					for (int i = currentClass.getType().getElemType()
							.getNumberOfFields(); i < parentMembers.length; i++) {
						if (parentMembers[i].getName().equals(extendClass)) {
							designatorNameSuper.obj = parentMembers[i];
							currentDesignator.add(parentMembers[i]);
							found = true;
							break;
						}
					}
					if (!found) {
						report_error("Greska SUPER se ne moze pozvati jer ne postoji konstruktor u natklasi!",
								designatorNameSuper);
						designatorNameSuper.obj = Tab.noObj;
						currentDesignator.add(designatorNameSuper.obj);
					}
				} else {
					boolean found = false;
					for (int i = currentClass.getType().getElemType()
							.getNumberOfFields(); i < parentMembers.length; i++) {
						if (parentMembers[i].getName().equals(currentMethod.getName())) {
							designatorNameSuper.obj = parentMembers[i];
							currentDesignator.add(parentMembers[i]);
							found = true;
							break;
						}
					}

					if (!found) {
						report_error("Greska SUPER se ne moze pozvati jer ne postoji istoimena metoda u natklasi!",
								designatorNameSuper);
						designatorNameSuper.obj = Tab.noObj;
						currentDesignator.add(designatorNameSuper.obj);
					}
				}

			}
		}
	}

	public void visit(FactorVar factorVar) {
		if (factorVar.getDesignator().obj.getType().getKind() == Struct.Array
				&& hasFields /* && currentMethodCallActPars.isEmpty() */) {
			if (isArrayAccess) {
				factorVar.struct = factorVar.getDesignator().obj.getType().getElemType();
			} else {
				factorVar.struct = factorVar.getDesignator().obj.getType();
			}
		} else {
			factorVar.struct = factorVar.getDesignator().obj.getType();
		}
	}

	public void visit(FactorNumConst factorNumConst) {
		factorNumConst.struct = Tab.intType;
	}

	public void visit(FactorCharConst factorCharConst) {
		factorCharConst.struct = Tab.charType;
	}

	public void visit(FactorBoolConst factorBoolConst) {
		factorBoolConst.struct = Tab.find("bool").getType();
	}

	public void visit(FactorParentheses factorParentheses) {
		factorParentheses.struct = factorParentheses.getExpr().struct;
	}

	public void visit(SingStmtPrintNumConst singStmtPrintNumConst) {
		Struct s = singStmtPrintNumConst.getExpr().struct;
		if (s != Tab.intType && s != Tab.charType && s != Tab.find("bool").getType()) {
			report_error(
					"Greska u liniji " + singStmtPrintNumConst.getLine() + ": tip izraza mora biti int, char ili bool",
					null);
		}
	}

	public void visit(SingStmtPrintNoNumConst singStmtPrintNoNumConst) {
		Struct s = singStmtPrintNoNumConst.getExpr().struct;
		if (s != Tab.intType && s != Tab.charType && s != Tab.find("bool").getType()) {
			report_error("Greska u liniji " + singStmtPrintNoNumConst.getLine()
					+ ": tip izraza mora biti int, char ili bool", null);
		}
	}

	public void visit(AssignDesOption assignDesOption) {
		Struct t = assignDesOption.getExpr().struct;
		if (assignDesOption.getDesignator().obj.getKind() != Obj.Var
				&& assignDesOption.getDesignator().obj.getKind() != Obj.Fld) {
			// mora i provera dodatna ne samo da je promeljiva nego i elem niza i polje
			// klase
			report_error(
					"Greska u liniji " + assignDesOption.getLine() + ": promenljiva sa leve strane mora biti lvalue",
					null);
		}
		if (assignDesOption.getDesignator().obj.getType().getKind() == Struct.Array
				&& assignDesOption.getDesignator().getDesignatorFields().obj != null) {
			if (arrayAlloc) {
				if (!t.assignableTo(assignDesOption.getDesignator().obj.getType())
						&& !assignable(t, assignDesOption.getDesignator().obj.getType()))
					report_error("Greska u liniji " + assignDesOption.getLine() + ": nekompatibilni tipovi za dodelu 1",
							null);
			} else {
				if (!t.assignableTo(assignDesOption.getDesignator().obj.getType().getElemType())
						&& !assignable(t, assignDesOption.getDesignator().obj.getType().getElemType()))
					report_error(
							"Greska u liniji " + assignDesOption.getLine() + ": nekompatibilni tipovi za dodelu 11",
							null);
			}

		} else {
			if (!t.assignableTo(assignDesOption.getDesignator().obj.getType())
					&& !assignable(t, assignDesOption.getDesignator().obj.getType())) {
				report_error("Greska u liniji " + assignDesOption.getLine() + ": nekompatibilni tipovi za dodelu 2",
						null);
			}
		}
		arrayAlloc = false;
	}

	public void visit(IncDesOption incDesOption) {
		if (incDesOption.getDesignator().obj.getType().getKind() == Struct.Array) {
			if (incDesOption.getDesignator().getDesignatorFields().obj != null) {
				if (isArrayAccess) {
					if (incDesOption.getDesignator().obj.getType().getElemType() != Tab.intType) {
						report_error("Greska u liniji " + incDesOption.getLine() + ": element niza mora biti tipa int",
								null);
					}
				} else {
					report_error("Greska u liniji " + incDesOption.getLine()
							+ ": nije moguce uvecavati promenljivu tipa niza", null);
				}

			} else {
				report_error(
						"Greska u liniji " + incDesOption.getLine() + ": nije moguce uvecavati promenljivu tipa niza",
						null);
			}
		} else {
			if (incDesOption.getDesignator().obj.getKind() != Obj.Var
					&& incDesOption.getDesignator().obj.getKind() != Obj.Fld) {
				report_error("Greska u liniji " + incDesOption.getLine() + ": promenljiva u ++ mora biti lvalue", null);
			}
			if (incDesOption.getDesignator().obj.getType() != Tab.intType) {
				report_error("Greska u liniji " + incDesOption.getLine() + ": promenljiva mora biti tipa int", null);
			}
		}
		/*
		 * if (incDesOption.getDesignator().obj.getType().getKind() == Struct.Array &&
		 * incDesOption.getDesignator().getDesignatorFields().obj != null) { if
		 * (incDesOption.getDesignator().obj.getType().getElemType() != Tab.intType) {
		 * report_error("Greska u liniji " + incDesOption.getLine() +
		 * ": element niza mora biti tipa int", null); } if (!isArrayAccess) {// da ne
		 * bismo mogli array++ report_error( "Greska u liniji " + incDesOption.getLine()
		 * + ": nije moguce uvecavati promenljivu tipa niza", null); } } else { if
		 * (incDesOption.getDesignator().obj.getKind() != Obj.Var &&
		 * incDesOption.getDesignator().obj.getKind() != Obj.Fld) {
		 * report_error("Greska u liniji " + incDesOption.getLine() +
		 * ": promenljiva u ++ mora biti lvalue", null); } if
		 * (incDesOption.getDesignator().obj.getType() != Tab.intType) {
		 * report_error("Greska u liniji " + incDesOption.getLine() +
		 * ": promenljiva mora biti tipa int", null); } }
		 */

	}

	public void visit(DecDesOption decDesOption) {
		if (decDesOption.getDesignator().obj.getType().getKind() == Struct.Array) {
			if (decDesOption.getDesignator().getDesignatorFields().obj != null) {
				if (isArrayAccess) {
					if (decDesOption.getDesignator().obj.getType().getElemType() != Tab.intType) {
						report_error("Greska u liniji " + decDesOption.getLine() + ": element niza mora biti tipa int",
								null);
					}
				} else {
					report_error("Greska u liniji " + decDesOption.getLine()
							+ ": nije moguce umanjivati promenljivu tipa niza", null);
				}

			} else {
				report_error(
						"Greska u liniji " + decDesOption.getLine() + ": nije moguce umanjivati promenljivu tipa niza",
						null);
			}
		} else {
			if (decDesOption.getDesignator().obj.getKind() != Obj.Var
					&& decDesOption.getDesignator().obj.getKind() != Obj.Fld) {
				report_error("Greska u liniji " + decDesOption.getLine() + ": promenljiva u -- mora biti lvalue", null);
			}
			if (decDesOption.getDesignator().obj.getType() != Tab.intType) {
				report_error("Greska u liniji " + decDesOption.getLine() + ": promenljiva mora biti tipa int", null);
			}
		}
		/*
		 * if (decDesOption.getDesignator().obj.getType().getKind() == Struct.Array &&
		 * decDesOption.getDesignator().getDesignatorFields().obj != null) { if
		 * (decDesOption.getDesignator().obj.getType().getElemType() != Tab.intType) {
		 * report_error("Greska u liniji " + decDesOption.getLine() +
		 * ": element niza mora biti tipa int", null); } if (!isArrayAccess) {// da ne
		 * bismo mogli p.array-- report_error( "Greska u liniji " +
		 * decDesOption.getLine() + ": nije moguce umanjivati promenljivu tipa niza",
		 * null); } } else { if (decDesOption.getDesignator().obj.getKind() != Obj.Var
		 * && decDesOption.getDesignator().obj.getKind() != Obj.Fld) {
		 * report_error("Greska u liniji " + decDesOption.getLine() +
		 * ": promenljiva u -- mora biti lvalue", null); } if
		 * (decDesOption.getDesignator().obj.getType() != Tab.intType) {
		 * report_error("Greska u liniji " + decDesOption.getLine() +
		 * ": promenljiva mora biti tipa int", null); } }
		 */
	}

	public void visit(FuncCallDesOption funcCallDesOption) {
		Obj currDes = currentMethodDesignator.get(currentMethodDesignator.size() - 1);
		if (currDes.getKind() != Obj.Meth)
			report_error("Greska u liniji " + funcCallDesOption.getLine() + ": Ocekivan metod", null);
		if (currDes.getLevel() != currentMethodCallActPars.get(currentMethodCallActPars.size() - 1)
				&& (funcCallDesOption.getStartFunctionCall().getDesignator().getDesignatorFields() != null
						&& currDes.getLevel() - 1 != currentMethodCallActPars
								.get(currentMethodCallActPars.size() - 1))) {
			report_error("Greska u liniji " + funcCallDesOption.getLine()
					+ ": broj formalnih i stvarnih parametara se ne poklapa 1", null);
		}
		currentMethodCallActPars.remove(currentMethodCallActPars.size() - 1);
		// isClassFuncCall = false;
		isClassFuncCall.remove(isClassFuncCall.size() - 1);
	}

	public void visit(FactorFuncCall factorFuncCall) {
		Obj currDes = currentMethodDesignator.get(currentMethodDesignator.size() - 1);
		if (currDes.getKind() != Obj.Meth)
			report_error("Greska u liniji " + factorFuncCall.getLine() + ": Ocekivan metod", null);
		if (currDes.getLevel() != currentMethodCallActPars.get(currentMethodCallActPars.size() - 1) && (factorFuncCall
				.getStartFunctionCall().getDesignator().getDesignatorFields() != null
				&& currDes.getLevel() - 1 != currentMethodCallActPars.get(currentMethodCallActPars.size() - 1))) {
			report_error("Greska u liniji " + factorFuncCall.getLine()
					+ ": broj formalnih i stvarnih parametara se ne poklapa 2", null);
		}
		if (currDes.getType() == Tab.noType)
			report_error("Greska u liniji " + factorFuncCall.getLine() + ": Procedura pozvana kao funkcija", null);

		currentMethodCallActPars.remove(currentMethodCallActPars.size() - 1);
		currentMethodDesignator.remove(currentMethodDesignator.size() - 1);

		factorFuncCall.struct = currDes.getType();
		// isClassFuncCall = false;
		isClassFuncCall.remove(isClassFuncCall.size() - 1);
	}

	public void visit(StartFunctionCall startFunctionCall) {
		if (startFunctionCall.getDesignator().obj.getFpPos() == 2
				&& startFunctionCall.getDesignator().getDesignatorName().getClass() != DesignatorNameSuper.class) {
			report_error("Greska: konstruktor se ne moze pozivati nakon konstrukcije objekta", startFunctionCall);
		}
		currentMethodDesignator.add(startFunctionCall.getDesignator().obj);
		currentMethodCallActPars.add(0);
		if ((hasFields || isClass) && startFunctionCall.getDesignator().obj.getFpPos() != 3) {// ako ima pristupa polju
																								// ili ako se nalazimo u
																								// klasi tj metodi klase
																								// a nije
			// poziv ord, chr, len
			// isClassFuncCall = true;
			isClassFuncCall.add(true);
		} else {
			// isClassFuncCall = false;
			isClassFuncCall.add(false);
		}
	}

	public void visit(MoreExpr moreExpr) {
		Obj currDes = currentMethodDesignator.get(currentMethodDesignator.size() - 1);
		int currFuncPar = currentMethodCallActPars.size() - 1;
		int currNumPar = currentMethodCallActPars.get(currFuncPar);
		if ((isClassFuncCall.size() > 0 && isClassFuncCall.get(isClassFuncCall.size() - 1)
				&& currDes.getLocalSymbols().size() <= currNumPar + 1)
				|| (isClassFuncCall.size() > 0 && !isClassFuncCall.get(isClassFuncCall.size() - 1)
						&& currDes.getLocalSymbols().size() <= currNumPar)
				|| (currDes.getLocalSymbols().size() <= currNumPar)) {
			report_error(
					"Greska u liniji " + moreExpr.getLine() + ": broj formalnih i stvarnih parametara se ne poklapa 3",
					null);
		}
		/*
		 * if (currDes.getLocalSymbols().size() <= currNumPar) { report_error(
		 * "Greska u liniji " + moreExpr.getLine() +
		 * ": broj formalnih i stvarnih parametara se ne poklapa 3", null); }
		 */ else {
			Obj[] locals = currDes.getLocalSymbols().toArray(new Obj[currDes.getLocalSymbols().size()]);
			if (isClassFuncCall.size() > 0) {
				if ((!isClassFuncCall.get(isClassFuncCall.size() - 1)
						&& !moreExpr.getExpr().struct.assignableTo(locals[currNumPar].getType())
						&& !assignable(moreExpr.getExpr().struct, locals[currNumPar].getType()))
						|| (isClassFuncCall.get(isClassFuncCall.size() - 1)
								&& !moreExpr.getExpr().struct.assignableTo(locals[currNumPar + 1].getType())
								&& !assignable(moreExpr.getExpr().struct, locals[currNumPar + 1].getType()))) {
					report_error("Greska u liniji " + moreExpr.getLine()
							+ ": tip formalnog i stvarnog parametra se ne poklapaju 2", null);
				}
			} else {
				if (!moreExpr.getExpr().struct.assignableTo(locals[currNumPar].getType())
						&& !assignable(moreExpr.getExpr().struct, locals[currNumPar].getType())) {
					report_error("Greska u liniji " + moreExpr.getLine()
							+ ": tip formalnog i stvarnog parametra se ne poklapaju 2", null);
				}
			}

		}
		currentMethodCallActPars.set(currFuncPar, currentMethodCallActPars.get(currFuncPar) + 1);

	}

	public void visit(SingleExpr singleExpr) {
		Obj currDes = currentMethodDesignator.get(currentMethodDesignator.size() - 1);
		int currFuncPar = currentMethodCallActPars.size() - 1;
		int currNumPar = currentMethodCallActPars.get(currFuncPar);
		if ((isClassFuncCall.size() > 0 && isClassFuncCall.get(isClassFuncCall.size() - 1)
				&& currDes.getLocalSymbols().size() <= currNumPar + 1)
				|| (isClassFuncCall.size() > 0 && !isClassFuncCall.get(isClassFuncCall.size() - 1)
						&& currDes.getLocalSymbols().size() <= currNumPar)
				|| (currDes.getLocalSymbols().size() <= currNumPar)) {
			report_error("Greska u liniji " + singleExpr.getLine()
					+ ": broj formalnih i stvarnih parametara se ne poklapa 4", null);
		}
		/*
		 * if (currDes.getLocalSymbols().size() <= currNumPar) {
		 * report_error("Greska u liniji " + singleExpr.getLine() +
		 * ": broj formalnih i stvarnih parametara se ne poklapa 4", null); }
		 */ else {
			Obj[] locals = currDes.getLocalSymbols().toArray(new Obj[currDes.getLocalSymbols().size()]);
			if (isClassFuncCall.size() > 0) {
				if ((!isClassFuncCall.get(isClassFuncCall.size() - 1)
						&& !singleExpr.getExpr().struct.assignableTo(locals[currNumPar].getType())
						&& !assignable(singleExpr.getExpr().struct, locals[currNumPar].getType()))
						|| (isClassFuncCall.get(isClassFuncCall.size() - 1)
								&& !singleExpr.getExpr().struct.assignableTo(locals[currNumPar + 1].getType())
								&& !assignable(singleExpr.getExpr().struct, locals[currNumPar + 1].getType()))) {
					report_error("Greska u liniji " + singleExpr.getLine()
							+ ": tip formalnog i stvarnog parametra se ne poklapaju 1", null);
				}
			} else {
				if (!singleExpr.getExpr().struct.assignableTo(locals[currNumPar].getType())
						&& !assignable(singleExpr.getExpr().struct, locals[currNumPar].getType())) {
					report_error("Greska u liniji " + singleExpr.getLine()
							+ ": tip formalnog i stvarnog parametra se ne poklapaju 1", null);
				}
			}

		}

		currentMethodCallActPars.set(currFuncPar, currentMethodCallActPars.get(currFuncPar) + 1);
	}

	public void visit(FactorAllocArray factorAllocArray) {
		if (factorAllocArray.getExpr().struct != Tab.intType) {
			report_error("Greska u liniji " + factorAllocArray.getLine() + ": tip u [] mora biti int", null);
		}
		factorAllocArray.struct = new Struct(Struct.Array, factorAllocArray.getType().struct);// valjda
		arrayAlloc = true;
	}

	public void visit(DesignatorFieldsExist designatorFieldsExist) {
		designatorFieldsExist.obj = designatorFieldsExist.getField().obj;
	}

	public void visit(NoDesignatorFields noDesignatorFields) {
		// noDesignatorFields.syntaxnode = null;
		noDesignatorFields.obj = null;
		isPrevArrayElem = false;
	}

	public void visit(ArrayElem arrayElem) {
		if (arrayElem.getExpr().struct != Tab.intType) {
			report_error("Greska u liniji " + arrayElem.getLine() + ": tip u [] mora biti int", null);
		}
		if (currentDesignator.get(currentDesignator.size() - 1).getType().getKind() != Struct.Array) {
			report_error("Greska u liniji " + arrayElem.getLine() + ": promenljiva mora biti niz", null);
		}
		arrayElem.obj = currentDesignator.get(currentDesignator.size() - 1);
		if (isPrevArrayElem) {
			report_error("Greska u liniji " + arrayElem.getLine() + ": nizovi moraju biti jednodimenzionalni ", null);
		}
		isPrevArrayElem = true;

		report_info(arrayElem.obj, arrayElem, "pristup elementu niza");
	}

	public void visit(ClassField classField) {
		// u currentDesignator se nalaze svi designatori koji postoje u trenutnom izrazu
		// Tip neterminala Designator mora biti unutrašnja klasa (ident mora biti ili
		// polje ili metoda objekta označenog neterminalom Designator)
		//
		if ((!isPrevArrayElem
				&& currentDesignator.get(currentDesignator.size() - 1).getType().getKind() != Struct.Class)
				|| (isPrevArrayElem
						&& currentDesignator.get(currentDesignator.size() - 1).getType().getKind() == Struct.Array
						&& currentDesignator.get(currentDesignator.size() - 1).getType().getElemType()
								.getKind() != Struct.Class)) {
			report_error("Greska u liniji " + classField.getLine() + ": promenljiva mora biti tipa klase", null);
		}

		boolean found = false;
		if (currentDesignator.get(currentDesignator.size() - 1).getName().equals("this")) {
			Obj desObj = Tab.find(classField.getId());
			if (desObj != Tab.noObj && desObj.getKind() == Obj.Var) {// mora ova provera jer ako se parametar zove isto
																		// kao polje da odemo
				// jedan scoope iznad da nadjemo polje
				desObj = Tab.currentScope().getOuter().findSymbol(classField.getId());
			}
			if (desObj == Tab.noObj) {
				found = false;
			} else {
				classField.obj = desObj;
				currentDesignator.set(currentDesignator.size() - 1, desObj);
				found = true;
			}
		} else {
			int numFieldsInClass;
			Obj fieldsInClass[] = null;
			if (isPrevArrayElem) {
				numFieldsInClass = currentDesignator.get(currentDesignator.size() - 1).getType().getElemType()
						.getMembers().size();
				fieldsInClass = currentDesignator.get(currentDesignator.size() - 1).getType().getElemType().getMembers()
						.toArray(new Obj[numFieldsInClass]);
			} else {
				numFieldsInClass = currentDesignator.get(currentDesignator.size() - 1).getType().getMembers().size();
				fieldsInClass = currentDesignator.get(currentDesignator.size() - 1).getType().getMembers()
						.toArray(new Obj[numFieldsInClass]);
			}

			for (int i = 0; i < numFieldsInClass; i++) {
				if (fieldsInClass[i].getName().equals(classField.getId())) {
					classField.obj = fieldsInClass[i];
					currentDesignator.set(currentDesignator.size() - 1, fieldsInClass[i]);
					found = true;
					break;
				}
			}
		}

		if (!found) {
			report_error(
					"Greska u liniji " + classField.getLine() + ": ne postoji polje sa nazivom - " + classField.getId(),
					null);
			classField.obj = Tab.noObj;
		}
		isPrevArrayElem = false;
	}

	public void visit(SingStmtRead singStmtRead) {
		if (singStmtRead.getDesignator().obj.getKind() != Obj.Var
				&& singStmtRead.getDesignator().obj.getKind() != Obj.Fld) {
			report_error("Greska u liniji " + singStmtRead.getLine() + ": promenljiva mora biti lvalue", null);
		}
		Struct t = singStmtRead.getDesignator().obj.getType();
		if ((!hasFields && (t != Tab.intType && t != Tab.charType && t != Tab.find("bool").getType()))
				|| (hasFields && t.getKind() == Struct.Array
						&& (t.getElemType() != Tab.intType && t.getElemType() != Tab.charType
								&& t.getElemType() != Tab.find("bool").getType()))
				|| (hasFields && !isArrayAccess && t.getKind() == Struct.Array)) {// da ne
																					// bismo
																					// mogli
																					// read(r.fr.array)
			report_error(
					"Greska u liniji " + singStmtRead.getLine() + ": promenljiva u read mora biti int char ili bool",
					null);
		}
	}

	public void visit(RecordName recordName) {
		if (Tab.find(recordName.getId()) != Tab.noObj) {
			report_error("Greska vec postoji simbol ili tip sa nazivom: " + recordName.getId(), recordName);
		} else {
			recordName.obj = Tab.insert(Obj.Type, recordName.getId(), new Struct(Struct.Class));
			recordName.obj.setFpPos(1);// ako je setovan znaci da je record
		}
		Tab.openScope();
		isClass = true;
		Tab.insert(Obj.Fld, "TVF" + recordName.getId(), new Struct(Struct.None));
	}

	public void visit(RecordDecl recordDecl) {
		Tab.chainLocalSymbols(recordDecl.getRecordName().obj.getType());
		Tab.closeScope();
		isClass = false;
		report_info(recordDecl.getRecordName().obj, recordDecl, "rad sa recordom");
	}

	public void visit(ClassName className) {
		if (Tab.find(className.getId()) != Tab.noObj) {
			report_error("Greska vec postoji simbol ili tip sa nazivom: " + className.getId(), className);
			className.obj = Tab.noObj;
		} else {
			if (className.getExtendsType().struct == Tab.noType) {
				className.obj = Tab.insert(Obj.Type, className.getId(), new Struct(Struct.Class));
			} else {
				className.obj = Tab.insert(Obj.Type, className.getId(), new Struct(Struct.Class));
				className.obj.getType().setElementType(className.getExtendsType().struct);
			}
			Tab.openScope();
			isClass = true;
			currentClass = className.obj;

			if (className.getExtendsType().struct != Tab.noType) {// prekopiraj nasledjena polja
				Obj[] members = className.getExtendsType().struct.getMembers()
						.toArray(new Obj[className.getExtendsType().struct.getNumberOfFields()]);

				for (int i = 0; i < className.getExtendsType().struct.getNumberOfFields(); i++) {
					if (members[i].getName().equals("TVF" + extendClass)) {
						Tab.insert(Obj.Fld, "TVF" + className.getId(), members[i].getType());
					} else {
						Tab.insert(Obj.Fld, members[i].getName(), members[i].getType());
					}
				}
			} else {// dodaj kao 0 polje tab virt fja
				Tab.insert(Obj.Fld, "TVF" + className.getId(), new Struct(Struct.None));
			}
		}
	}

	private boolean checkIfIsOverridden(Obj meth) {
		// nadjacana metoda ako se isto zove ima istu povratnu vrednost i isti broj i
		// tip parametra
		if (meth.getName().equals(extendClass)) {
			return true;
		}
		Obj o = Tab.currentScope().findSymbol(meth.getName());
		if (o == null)
			return false;
		/*
		 * if ((o.getType().equals(meth.getType()) || assignable(o.getType(),
		 * meth.getType())) && o.getLevel() == meth.getLevel()) { Obj[] localsD =
		 * o.getLocalSymbols().toArray(new Obj[o.getLocalSymbols().size()]); Obj[]
		 * localsB = meth.getLocalSymbols().toArray(new
		 * Obj[meth.getLocalSymbols().size()]); for (int i = 1; i < o.getLevel(); i++) {
		 * if (!localsB[i].getType().equals(localsD[i].getType())) { return false; } } }
		 * else { return false; }
		 */
		return true;
	}

	public void visit(ClassDecl classDecl) {
		Struct parentClass = classDecl.getClassName().getExtendsType().struct;
		if (parentClass != Tab.noType) {// ostatak metoda dodaj koje nisu nadjacane
			Obj[] members = parentClass.getMembers().toArray(new Obj[parentClass.getMembers().size()]);
			for (int i = parentClass.getNumberOfFields(); i < members.length; i++) {
				if (!checkIfIsOverridden(members[i])) {// proveri jel nije nadjacana ako nije dodaj

					Obj meth = Tab.insert(Obj.Meth, members[i].getName(), members[i].getType());
					meth.setLevel(members[i].getLevel());
					meth.setFpPos(1);// da bismo znali pri generisanju koda da se adr ove metode prepisuje od
										// roditelja
					// adresa se namesta u CodeGenerator
					if (!members[i].getLocalSymbols().isEmpty()) {
						Tab.openScope();
						Obj[] locals = members[i].getLocalSymbols()
								.toArray(new Obj[members[i].getLocalSymbols().size()]);
						for (int j = 0; j < locals.length; j++) {
							if (locals[j].getName().equals("this")) {
								Tab.insert(locals[j].getKind(), locals[j].getName(),
										classDecl.getClassName().obj.getType());// za this promenimo da je tipa izvedene
																				// klase
							} else {
								Tab.insert(locals[j].getKind(), locals[j].getName(), locals[j].getType());
							}
						}
						Tab.chainLocalSymbols(meth);
						Tab.closeScope();
					}
				}
			}
		}
		Tab.chainLocalSymbols(classDecl.getClassName().obj.getType());
		Tab.closeScope();
		isClass = false;
	}

	public void visit(ExtendsT extendsT) {
		Obj o = Tab.find(extendsT.getId());
		if (o == Tab.noObj || o.getKind() != Obj.Type || o.getType().getKind() != Struct.Class
				|| (o.getKind() == Obj.Type && o.getType().getKind() == Struct.Class && o.getFpPos() == 1)) {
			report_error(
					"Greska tip prilikom izvodjenja klase iz druge klase mora biti unutrasnja klasa glavnog programa.  "
							+ extendsT.getId() + " nije klasa",
					extendsT);
		}
		extendsT.struct = o.getType();
		extendClass = extendsT.getId();
	}

	public void visit(NoExtendsT noExtendsT) {
		noExtendsT.struct = Tab.noType;
	}

	public void visit(FactorAllocType factorAllocType) {
		if (factorAllocType.getType().struct.getKind() != Struct.Class) {
			report_error("Greska: " + factorAllocType.getType().getTypeName() + " mora da oznacava unutrasnju klasu!",
					factorAllocType);
		}
		factorAllocType.struct = factorAllocType.getType().struct;
		report_info(Tab.noObj, factorAllocType, "pravljenje objekta klase " + factorAllocType.getType().getTypeName());
	}

	public void visit(RelopCondFactExpr relopCondFactExpr) {
		if (!relopCondFactExpr.getExpr().struct.compatibleWith(relopCondFactExpr.getExpr1().struct)
				&& !assignable(relopCondFactExpr.getExpr().struct, relopCondFactExpr.getExpr1().struct)
				&& !assignable(relopCondFactExpr.getExpr1().struct, relopCondFactExpr.getExpr().struct)) {
			report_error("Greska: tipovi unutar uslova moraju biti kompatibilni!", relopCondFactExpr);
		}
		Struct expr1 = relopCondFactExpr.getExpr().struct;
		Struct expr2 = relopCondFactExpr.getExpr1().struct;
		if ((expr1.getKind() == Struct.Array || expr1.getKind() == Struct.Class || expr2.getKind() == Struct.Array
				|| expr2.getKind() == Struct.Class) && relopCondFactExpr.getRelop().getClass() != EqualOp.class
				&& relopCondFactExpr.getRelop().getClass() != DiffOp.class) {
			report_error("Greska: uz promenljive tipa niza ili klase dozvoljeni su samo relacioni operatori == i != !",
					relopCondFactExpr);
		}
	}

	public void visit(SingleCondFactExpr singleCondFactExpr) {
		if (singleCondFactExpr.getExpr().struct != Tab.find("bool").getType()) {
			report_error("Greska: uslovni izraz mora biti tipa bool!", singleCondFactExpr);
		}
	}

	public void visit(DoWhileStart doWhileStart) {
		isDoWhileActive++;
	}

	public void visit(SingStmtDoWhile singStmtDoWhile) {
		isDoWhileActive--;
	}

	public void visit(SingStmtBreak singStmtBreak) {
		if (isDoWhileActive == 0) {
			report_error("Greska: break se moze nalaziti samo unutar do-while petlje!", singStmtBreak);
		}
	}

	public void visit(SingStmtContinue singStmtContinue) {
		if (isDoWhileActive == 0) {
			report_error("Greska: continue se moze nalaziti samo unutar do-while petlje!", singStmtContinue);
		}
	}

	private boolean assignable(Struct src, Struct dest) {
		for (Struct s = src.getElemType(); s != null; s = s.getElemType()) {
			if (s.equals(dest))
				return true;
		}
		return false;
	}
}
