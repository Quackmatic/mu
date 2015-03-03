package pw.usn.mu.tokenizer;

import pw.usn.mu.Source;

/**
 * Represents a single syntax token in a Mu source file.
 */
public abstract class Token {
	private Location location;
	
	/**
	 * Initializes a new Token read from the given {@link Source} at the given location.
	 * @param location The location of the token.
	 * The start of each row is column 0.
	 */
	public Token(Location location) {
		this.location = location;
	}
	
	/**
	 * Gets the location of the token.
	 * @return The location of the token in a tokenized input source.
	 */
	public final Location getLocation() {
		return location;
	}

	/**
	 * Gets token-specific information used for formatting.
	 * @return A token-specific information string about some attribute of the token that was read.
	 * For example, an identifier token might represent the identifier that was read.
	 */
	public String getInformation() {
		return null;
	}
	
	@Override
	public String toString() {
		String information = getInformation();
		if(information == null) {
			return String.format("[%s %s]", location.toString(), getClass().getSimpleName());
		} else {
			return String.format("[%s %s=%s]", location.toString(), getClass().getSimpleName(), information);
		}
	}
	
	/**
	 * Determines if this Token marks the beginning of an atomic non-terminal in the
	 * parsing stage.
	 * @return Whether this token is the beginning of an atomic node.
	 */
	public boolean isAtomicToken() {
		return
				this instanceof LiteralIntToken ||
				this instanceof LiteralStringToken ||
				this instanceof IdentifierToken ||
				isSymbolToken(SymbolTokenType.PAREN_OPEN);
	}
	
	/**
	 * Determines if this Token is a {@link SymbolToken} of the specified type.
	 * @param type The type of symbol token to check for.
	 * @return Returns {@code true} if this token is an instance of {@link SymbolToken}, and
	 * {@link SymbolToken#getType()} equals {@code type}.
	 */
	public boolean isSymbolToken(SymbolTokenType type) {
		return this instanceof SymbolToken && ((SymbolToken)this).getType().equals(type);
	}
	
	/**
	 * Determines if this Token is a {@link OperatorToken} of the specified type.
	 * @param type The type of operator token to check for.
	 * @return Returns {@code true} if this token is an instance of {@link OperatorToken}, and
	 * {@link OperatorToken#getType()} equals {@code type}.
	 */
	public boolean isOperatorToken(OperatorTokenType type) {
		return this instanceof OperatorToken && ((OperatorToken)this).getType().equals(type);
	}
	
	/**
	 * Determines if this Token is a {@link OperatorToken} with the specified symbol.
	 * @param operator The operator to check for.
	 * @return Returns {@code true} if this token is an instance of {@link OperatorToken}, and
	 * {@link OperatorToken#getOperator()} equals {@code operator}.
	 */
	public boolean isOperatorToken(String operator) {
		return this instanceof OperatorToken && ((OperatorToken)this).getOperator().equals(operator);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	/**
	 * Return whether to discard this token from the token list.
	 * @return {@code true} to discard this token from the tokenized source; otherwise {@code false}.
	 */
	public boolean ignore() {
		return false;
	}
}
