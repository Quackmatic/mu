package pw.usn.mu.tokenizer;

import java.util.regex.Pattern;

import pw.usn.mu.parser.IdentifierNode;

/**
 * Represents the type of symbol represented by a {@link SymbolToken}.
 */
public enum SymbolTokenType {
	/**
	 * Represents an opening parenthesis ({@code (}).
	 */
	PAREN_OPEN("("),
	/**
	 * Represents a closing parenthesis ({@code )}).
	 */
	PAREN_CLOSE(")"),
	/**
	 * Represents an opening square bracket ({@code [}), for sequence literals or types.
	 */
	SEQUENCE_OPEN("["),
	/**
	 * Represents a closing square bracket ({@code ]}), for sequence literals or types.
	 */
	SEQUENCE_CLOSE("]"),
	/**
	 * Represents a namespace qualifier (with the symbol {@link
	 * Identifier#QUALIFIER_SYMBOL}), used for specifying the parent module
	 * that contains an identifier.
	 */
	NAMESPACE_QUALIFIER(String.valueOf(IdentifierNode.QUALIFIER_SYMBOL)),
	/**
	 * Represents the starting sigil for a function ({@code \}),
	 * to be followed by an identifier name for an argument.
	 */
	FUNCTION_DECLARE("\\"),
	/**
	 * Represents the starting sigil for a module ({@code @}),
	 * to be followed by a list of definitions.
	 */
	MODULE_DECLARE("@"),
	/**
	 * Represents the comma symbol ({@code ,}).
	 */
	COMMA(","),
	/**
	 * Represents the back-arrow ({@code <-}) used to create a binding.
	 */
	BIND("<-"),
	/**
	 * Represents the forward-arrow ({@code ->}) used to declare the beginning of
	 * a function body, or represent a function in a type expression.
	 */
	FUNCTION_BEGIN("->"),
	/**
	 * Represents the cons operator ({@code ::}).
	 */
	CONS("::"),
	/**
	 * Represents the colon symbol ({@code :}).
	 */
	COLON(":"),
	/**
	 * Represents the start of a switch block ({@code ?}).
	 */
	SWITCH_DECLARE("?"),
	/**
	 * Represents the semicolon ({@code ;}) used to separate certain expressions such
	 * as bindings and switch branches.
	 */
	SEPARATOR(";");
	
	private String display;
	
	/**
	 * Create a new SymbolTokenType with the specified display string and token
	 * level.
	 * @param display The display string of the given symbol token type.
	 */
	private SymbolTokenType(String display) {
		this.display = display;
	}
	
	/**
	 * Gets a pattern for this symbol token type during tokenization.
	 * @return A pattern that matches this symbol token type in mu source code.
	 */
	public Pattern getPattern() { 
		return Pattern.compile(Pattern.quote(display));
	}
	
	/**
	 * Gets the display string for this token type.
	 * @return A string containing this symbol token type, as it would appear in
	 * a mu source code file.
	 */
	public String getDisplayString() {
		return display;
	}
}
