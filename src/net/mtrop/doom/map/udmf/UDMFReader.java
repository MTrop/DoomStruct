package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.blackrook.commons.linkedlist.Stack;
import com.blackrook.lang.Lexer;
import com.blackrook.lang.LexerKernel;
import com.blackrook.lang.Parser;
import com.blackrook.lang.ParserException;

/**
 * Reads UDMF data.
 * @author Matthew Tropiano
 */
public final class UDMFReader
{
	private UDMFReader() {}
	
	/**
	 * Reads UDMF-formatted data into a UDMFTable from an {@link InputStream}.
	 * This will read until the end of the stream is reached.
	 * Does not close the InputStream at the end of the read.
	 * @param in the InputStream to read from.
	 * @throws UDMFParseException if a parsing error occurs.
	 */
	public static UDMFTable readData(InputStream in) throws IOException
	{
		return readData(new InputStreamReader(in, "UTF8"));
	}
	
	/**
	 * Reads UDMF-formatted data into a UDMFTable from a {@link Reader}.
	 * This will read until the end of the stream is reached.
	 * Does not close the InputStream at the end of the read.
	 * @param reader the reader to read from.
	 * @throws UDMFParseException if a parsing error occurs.
	 */
	public static UDMFTable readData(Reader reader) throws IOException
	{
		ULexer lexer = new ULexer(reader);
		UParser parser = new UParser(lexer);
		return parser.getTable();
	}
	
	/**
	 * Parser for UDMF data.
	 * This is NOT a thread safe object - if read is called by more
	 * than one thread at once, undefined behavior may occur.
	 * @author Matthew Tropiano
	 */
	private static class UParser extends Parser
	{
		/** Struct stack. */
		private Stack<UDMFObject> structStack;

		private String currentStructType;
		private String currentId;
		
		private Object currentValue;
		
		private UDMFTable table;
		
		/**
		 * Creates a new instance of a UDMF Reader.
		 */
		public UParser(ULexer lexer)
		{
			super(lexer);
			read();
		}
		
		/**
		 * Reads in UDMF text and returns a UDMFTable representing the structure.
		 * @throws ParserException if a parsing error occurs.
		 */
		public void read()
		{
			structStack = new Stack<UDMFObject>();
			table = new UDMFTable();
			structStack.push(table.getGlobalFields());
			
			nextToken();
			
			while (currentToken() != null && StructureList());

			String[] errors = getErrorMessages();
			if (errors.length > 0)
			{
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < errors.length; i++)
				{
					sb.append(errors[i]);
					if (i < errors.length-1)
						sb.append('\n');
				}
				throw new UDMFParseException(sb.toString());
			}
			
		}

		public UDMFTable getTable()
		{
			return table;
		}

		private boolean StructureList()
		{
			if (currentType(ULexer.TYPE_IDENTIFIER))
			{
				currentId = currentToken().getLexeme();
				nextToken();
				
				if (currentType(ULexer.TYPE_EQUALS))
				{
					nextToken();
					
					if (!Value())
					{
						currentId = null;
						return false;
					}
					
					if (!matchTypeStrict(ULexer.TYPE_SEMICOLON))
						return false;
					
					structStack.peek().put(currentId, currentValue);
					currentId = null;
					currentValue = null;
					
					return true;
				}
				else if (currentType(ULexer.TYPE_LBRACE))
				{
					currentStructType = currentId;
					nextToken();
					
					UDMFObject struct = new UDMFObject();
					structStack.push(struct);
					if (!FieldExpressionList())
					{
						structStack.pop();
						return false;
					}

					if (!matchTypeStrict(ULexer.TYPE_RBRACE))
						return false;

					structStack.pop();
					table.addStruct(currentStructType, struct);
					return true;
				}
				else
				{
					addErrorMessage("Expected field expression or start of structure.");
					return false;
				}
			}
			else if (currentType(ULexer.TYPE_END_OF_STREAM))
				return true;

			addErrorMessage("Expected global value or structure.");
			return false;
			
		}
		
		private boolean doField()
		{
			currentId = currentToken().getLexeme();
			nextToken();
			
			if (!matchTypeStrict(ULexer.TYPE_EQUALS))
				return false;
			
			if (!Value())
			{
				currentId = null;
				return false;
			}
			
			if (!matchTypeStrict(ULexer.TYPE_SEMICOLON))
				return false;
			
			structStack.peek().put(currentId, currentValue);
			currentId = null;
			currentValue = null;
			
			return true;
		}

		/*
		 * FieldExpressionList := IDENTIFIER = Value ; FieldExpressionList | [e]
		 */
		private boolean FieldExpressionList()
		{
			if (currentType(ULexer.TYPE_IDENTIFIER))
			{
				if (!doField())
					return false;
				return FieldExpressionList();
			}
			
			return true; 
		}

		/*
		 * Number := STRING | TRUE | FALSE | IntegerValue | FloatValue
		 */
		private boolean Value()
		{
			if (currentType(ULexer.TYPE_STRING))
			{
				currentValue = currentToken().getLexeme();
				nextToken();
				return true;
			}
			else if (currentType(ULexer.TYPE_TRUE))
			{
				currentValue = true;
				nextToken();
				return true;
			}
			else if (currentType(ULexer.TYPE_FALSE))
			{
				currentValue = false;
				nextToken();
				return true;
			}
			else if (IntegerValue())
				return true;
			else if (FloatValue())
				return true;
			
			addErrorMessage("Expected valid value.");
			return false; 
		}
		
		/*
		 * IntegerValue := PLUS INTEGER | MINUS INTEGER | INTEGER 
		 */
		private boolean IntegerValue()
		{
			if (matchType(ULexer.TYPE_MINUS))
			{
				if (currentType(ULexer.TYPE_NUMBER))
				{
					try {
						currentValue = -Integer.parseInt(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			else if (matchType(ULexer.TYPE_PLUS))
			{
				if (currentType(ULexer.TYPE_NUMBER))
				{
					try {
						currentValue = Integer.parseInt(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			else if (currentType(ULexer.TYPE_NUMBER))
			{
				{
					try {
						currentValue = Integer.parseInt(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			
			return false;
		}
		
		/*
		 * FloatValue := PLUS FLOAT | MINUS FLOAT | FLOAT 
		 */
		private boolean FloatValue()
		{
			if (matchType(ULexer.TYPE_MINUS))
			{
				if (currentType(ULexer.TYPE_NUMBER))
				{
					try {
						currentValue = -Float.parseFloat(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			else if (matchType(ULexer.TYPE_PLUS))
			{
				if (currentType(ULexer.TYPE_NUMBER))
				{
					try {
						currentValue = Float.parseFloat(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			else if (currentType(ULexer.TYPE_NUMBER))
			{
				{
					try {
						currentValue = Float.parseFloat(currentToken().getLexeme());
					} catch (NumberFormatException e) {
						return false;
					}
					nextToken();
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		protected String getTypeErrorText(int tokenType)
		{
			switch (tokenType)
			{
				case ULexer.TYPE_LBRACE:
					return "'{'";
				case ULexer.TYPE_RBRACE:
					return "'}'";
				case ULexer.TYPE_EQUALS:
					return "'='";
				case ULexer.TYPE_SEMICOLON:
					return "';'";
				case ULexer.TYPE_PLUS:
					return "'+'";
				case ULexer.TYPE_MINUS:
					return "'-'";
			}
			return null;
		}
}

	/**
	 * Lexer for the UDMFParser.
	 * @author Matthew Tropiano
	 */
	private static class ULexer extends Lexer
	{

		public static final int TYPE_COMMENT =		0;
		public static final int TYPE_TRUE = 		1;
		public static final int TYPE_FALSE = 		2;
		public static final int TYPE_EQUALS = 		3;
		public static final int TYPE_LBRACE = 		4;
		public static final int TYPE_RBRACE = 		5;
		public static final int TYPE_SEMICOLON = 	6;
		public static final int TYPE_PLUS = 		7;
		public static final int TYPE_MINUS = 		8;
		
		private static final LexerKernel KERNEL = new LexerKernel()
		{{
			addCommentStartDelimiter("/*", TYPE_COMMENT);
			addCommentLineDelimiter("//", TYPE_COMMENT);
			addCommentEndDelimiter("*/", TYPE_COMMENT);
			
			addCaseInsensitiveKeyword("true", TYPE_TRUE);
			addCaseInsensitiveKeyword("false", TYPE_FALSE);

			addStringDelimiter('"', '"');
			
			addDelimiter(";", TYPE_SEMICOLON);
			addDelimiter("=", TYPE_EQUALS);
			addDelimiter("{", TYPE_LBRACE);
			addDelimiter("}", TYPE_RBRACE);
			addDelimiter("+", TYPE_PLUS);
			addDelimiter("-", TYPE_MINUS);
		}};
		
		public ULexer(Reader reader)
		{
			super(KERNEL, "UDMFLexer", reader);
		}

	}

	
}
