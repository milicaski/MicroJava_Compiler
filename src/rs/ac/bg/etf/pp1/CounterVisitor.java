package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.FormParsPartArray;
import rs.ac.bg.etf.pp1.ast.FormParsPartNoArray;
import rs.ac.bg.etf.pp1.ast.VarPartArray;
import rs.ac.bg.etf.pp1.ast.VarPartNoArray;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

public class CounterVisitor extends VisitorAdaptor {

	protected int count;
	
	public int getCount(){
		return count;
	}
	
	public static class FormParsPartCounter extends CounterVisitor{
	
		public void visit(FormParsPartArray varPartArray){
			count++;
		}
		
		public void visit(FormParsPartNoArray varPartNoArray){
			count++;
		}
	}
	
	public static class VarPartCounter extends CounterVisitor{
		
		public void visit(VarPartArray varPartArray){
			count++;
		}
		
		public void visit(VarPartNoArray varPartNoArray){
			count++;
		}
	}
}
