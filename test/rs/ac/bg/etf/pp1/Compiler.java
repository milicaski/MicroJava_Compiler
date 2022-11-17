package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.Symbol;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	public static void tsdump() {
		Tab.dump(new MyDumpSymbolTableVisitor());
	}

	private static void fixTab() {
		Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool", new Struct(Struct.Bool)));
		Obj chr = Tab.find("chr");
		chr.setFpPos(3);
		Obj ord = Tab.find("ord");
		ord.setFpPos(3);
		Obj len = Tab.find("len");
		len.setFpPos(3);
	}

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}

	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger(Compiler.class);

		if (args.length < 2) {
			log.error("Not enough arguments supplied! Usage: MJParser <source-file> <obj-file> ");
			return;
		}
		
		File sourceCode = new File(args[0]);
		if (!sourceCode.exists()) {
			log.error("Source file [" + sourceCode.getAbsolutePath() + "] not found!");
			return;
		}
			
		log.info("Compiling source file: " + sourceCode.getAbsolutePath());
		
		Reader br = null;
		try {
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);

			MJParser p = new MJParser(lexer);
			Symbol s = null;
			Program prog = null;
			try {
				s = p.parse(); // pocetak parsiranja
				prog = (Program) (s.value);
			} catch (Exception e) {
				s = null;
				prog = null;
			}

			if (prog != null) {

				Tab.init();
				fixTab();
				// ispis sintaksnog stabla
				log.info(prog.toString(""));
				log.info("===================================");

				// ispis prepoznatih programskih konstrukcija
				SemanticAnalyzer v = new SemanticAnalyzer();
				prog.traverseBottomUp(v);

				log.info("===================================");

				tsdump();

				if (!p.errorDetected && v.passed()) {
					File objFile = new File(args[1]);
		        	log.info("Generating bytecode file: " + objFile.getAbsolutePath());
		        	if (objFile.exists())
		        		objFile.delete();

					Code.dataSize = v.nVars;
					CodeGenerator codeGenerator = new CodeGenerator();
					prog.traverseBottomUp(codeGenerator);

					Code.mainPc = codeGenerator.getMainPc();
					Code.write(new FileOutputStream(objFile));
					log.info("Parsiranje uspesno zavrseno!");
				} else {
					log.error("Parsiranje NIJE uspesno zavrseno!");
				}
			} else {
				log.error("Parsiranje NIJE uspesno zavrseno!");
			}

		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e1) {
					log.error(e1.getMessage(), e1);
				}
		}

	}

}
