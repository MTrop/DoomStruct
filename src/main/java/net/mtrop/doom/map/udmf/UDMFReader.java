/*******************************************************************************
 * Copyright (c) 2015-2016 Matt Tropiano
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package net.mtrop.doom.map.udmf;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

import net.mtrop.doom.struct.Lexer;
import net.mtrop.doom.struct.Lexer.Parser;

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
	 * @return a UDMFTable containing the structures.
	 * @throws UDMFParseException if a parsing error occurs.
	 * @throws IOException if the data can't be read.
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
	 * @return a UDMFTable containing the structures.
	 * @throws UDMFParseException if a parsing error occurs.
	 * @throws IOException if the data can't be read.
	 */
	public static UDMFTable readData(Reader reader) throws IOException
	{
		ULexer lexer = new ULexer(reader);
		UParser parser = new UParser(lexer);
		parser.read();
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
		private Deque<UDMFObject> structStack;

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
		}
		
		/**
		 * Reads in UDMF text and returns a UDMFTable representing the structure.
		 * @throws ParserException if a parsing error occurs.
		 */
		public void read()
		{
			table = new UDMFTable();
			structStack = new LinkedList<>();

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
			if (currentType(ULexerKernel.TYPE_IDENTIFIER))
			{
				currentId = currentToken().getLexeme();
				nextToken();
				
				if (currentType(ULexerKernel.TYPE_EQUALS))
				{
					nextToken();
					
					if (!Value())
					{
						currentId = null;
						return false;
					}
					
					if (!matchType(ULexerKernel.TYPE_SEMICOLON))
					{
						addErrorMessage("Expected \";\" to terminate field statement.");
						return false;
					}
					
					structStack.peek().set(currentId, currentValue);
					currentId = null;
					currentValue = null;
					
					return true;
				}
				else if (currentType(ULexerKernel.TYPE_LBRACE))
				{
					currentStructType = currentId;
					nextToken();
					
					UDMFObject object = new UDMFObject();
					structStack.push(object);
					if (!FieldExpressionList())
					{
						structStack.pop();
						return false;
					}

					if (!matchType(ULexerKernel.TYPE_RBRACE))
					{
						addErrorMessage("Expected \"}\" to terminate object.");
						return false;
					}

					structStack.pop();
					table.addObject(currentStructType, object);
					return true;
				}
				else
				{
					addErrorMessage("Expected field expression or start of structure.");
					return false;
				}
			}
			else if (currentType(ULexerKernel.TYPE_END_OF_STREAM))
				return true;

			addErrorMessage("Expected global value or structure.");
			return false;
			
		}
		
		private boolean doField()
		{
			currentId = currentToken().getLexeme();
			nextToken();
			
			if (!matchType(ULexerKernel.TYPE_EQUALS))
			{
				addErrorMessage("Expected \"=\" after field.");
				return false;
			}
			
			if (!Value())
			{
				currentId = null;
				return false;
			}
			
			if (!matchType(ULexerKernel.TYPE_SEMICOLON))
			{
				addErrorMessage("Expected \";\" to terminate field statement.");
				return false;
			}
			
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
			if (currentType(ULexerKernel.TYPE_IDENTIFIER))
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
			if (currentType(ULexerKernel.TYPE_STRING))
			{
				currentValue = currentToken().getLexeme();
				nextToken();
				return true;
			}
			else if (currentType(ULexerKernel.TYPE_TRUE))
			{
				currentValue = true;
				nextToken();
				return true;
			}
			else if (currentType(ULexerKernel.TYPE_FALSE))
			{
				currentValue = false;
				nextToken();
				return true;
			}
			else if (NumericValue())
				return true;

			addErrorMessage("Expected valid value.");
			return false; 
		}
		
		/*
		 * NumericValue := PLUS NUMBER | MINUS NUMBER | NUMBER 
		 */
		private boolean NumericValue()
		{
			if (matchType(ULexerKernel.TYPE_MINUS))
			{
				if (currentType(ULexerKernel.TYPE_NUMBER))
				{
					String lexeme = currentToken().getLexeme();
					if (lexeme.startsWith("0X") || lexeme.startsWith("0x"))
					{
						currentValue = Integer.parseInt(lexeme.substring(2), 16);
						nextToken();
						return true;
					}
					else if (lexeme.contains("."))
					{
						currentValue = Float.parseFloat(lexeme);
						nextToken();
						return true;
					}
					else
					{
						currentValue = Integer.parseInt(lexeme);
						nextToken();
						return true;
					}
				}
			}
			else if (matchType(ULexerKernel.TYPE_PLUS))
			{
				if (currentType(ULexerKernel.TYPE_NUMBER))
				{
					String lexeme = currentToken().getLexeme();
					if (lexeme.startsWith("0X") || lexeme.startsWith("0x"))
					{
						currentValue = Integer.parseInt(lexeme.substring(2), 16);
						nextToken();
						return true;
					}
					else if (lexeme.contains("."))
					{
						currentValue = Float.parseFloat(lexeme);
						nextToken();
						return true;
					}
					else
					{
						currentValue = Integer.parseInt(lexeme);
						nextToken();
						return true;
					}
				}
			}
			else if (currentType(ULexerKernel.TYPE_NUMBER))
			{
				String lexeme = currentToken().getLexeme();
				if (lexeme.startsWith("0X") || lexeme.startsWith("0x"))
				{
					currentValue = Integer.parseInt(lexeme.substring(2), 16);
					nextToken();
					return true;
				}
				else if (lexeme.contains("."))
				{
					currentValue = Float.parseFloat(lexeme);
					nextToken();
					return true;
				}
				else
				{
					currentValue = Integer.parseInt(lexeme);
					nextToken();
					return true;
				}
			}
			
			return false;
		}
		
	}

	// Lexer kernel.
	private static final ULexerKernel KERNEL = new ULexerKernel();

	
	/**
	 * Kernel for UDMF parser.
	 */
	private static class ULexerKernel extends Lexer.Kernel
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

		private ULexerKernel()
		{
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
		}
		
	}
	
	/**
	 * Lexer for the UDMFParser.
	 * @author Matthew Tropiano
	 */
	private static class ULexer extends Lexer
	{
		public ULexer(Reader reader)
		{
			super(KERNEL, "UDMFLexer", reader);
		}

	}

}
