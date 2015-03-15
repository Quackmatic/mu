package pw.usn.mu.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pw.usn.mu.tokenizer.IdentifierToken;
import pw.usn.mu.tokenizer.Location;
import pw.usn.mu.tokenizer.SymbolTokenType;

/**
 * Represents an (optionally qualified) identifier in mu source code.
 */
public class IdentifierNode extends Node {
	/**
	 * The symbol used to qualify identifiers with module names.
	 */
	public static final String QUALIFIER_SYMBOL = ".";
	private String[] modules;
	private String name;
	
	/**
	 * @param location The location of the AST node in a parsed input source.
	 */
	private IdentifierNode(Location location) {
		super(location);
	}
	
	/**
	 * Initializes a new IdentifierNode with the specified identifier components.
	 * @param location The location of the AST node in a parsed input source.
	 * @param names The components (normally separated by {@link
	 * IdentifierNode#QUALIFIER_SYMBOL}) comprising this identifier.
	 */
	public IdentifierNode(Location location, String... names) {
		this(location);
		if(names.length == 0) {
			throw new IllegalArgumentException("Must specify at least one component of identifier.");
		} else {
			modules = new String[names.length - 1];
			System.arraycopy(names, 0, modules, 0, modules.length);
			name = names[modules.length];
		}
	}
	
	/**
	 * Initializes a new IdentifierNode with the specified identifier string.
	 * @param location The location of the AST node in a parsed input source.
	 * @param identifier The identifier string, as it appears in the source
	 * code, representing this identifier.
	 */
	public IdentifierNode(Location location, String identifier) {
		this(location, identifier.split(Pattern.quote(QUALIFIER_SYMBOL)));
	}
	
	/**
	 * Creates a copy of this identifier with the first qualifying namespace
	 * name removed.
	 * @return A copy of this identifier with the head stripped.
	 */
	public IdentifierNode tail() {
		if(isUnqualified()) {
			IdentifierNode tailIdentifier = new IdentifierNode(getLocation());
			tailIdentifier.name = name;
			tailIdentifier.modules = new String[modules.length - 1];
			System.arraycopy(modules, 1, tailIdentifier.modules, 0, tailIdentifier.modules.length);
			return tailIdentifier;
		} else {
			throw new IllegalStateException("Cannot get the tail of an unqualified identifier.");
		}
	}
	
	/**
	 * Gets the first component of this identifier.
	 * @return If this identifier is unqualified, the name of the identifier is returned.
	 * Otherwise, the first module is returned.
	 */
	public String head() {
		if(isUnqualified()) {
			return name;
		} else {
			return modules[0];
		}
	}
	
	/**
	 * Creates a new identifier where {@code qualifyingIdentifier} is prepended to the
	 * components of this identifier. For example, qualifying an identifier {@code some.stuff}
	 * with the identifier {@code parent.module} would return an identifier
	 * {@code parent.module.some.stuff}.
	 * @param qualifyingIdentifier The identifier to qualify with.
	 * @return THe resulting qualified identifier.
	 */
	public IdentifierNode qualify(IdentifierNode qualifyingIdentifier) {
		IdentifierNode newIdentifier = new IdentifierNode(getLocation());
		newIdentifier.modules = new String[qualifyingIdentifier.modules.length + 1 + modules.length];
		
		System.arraycopy(qualifyingIdentifier.modules, 0, newIdentifier.modules, 0, qualifyingIdentifier.modules.length);
		newIdentifier.modules[qualifyingIdentifier.modules.length] = qualifyingIdentifier.name;
		System.arraycopy(modules, 0, newIdentifier.modules, qualifyingIdentifier.modules.length + 1, modules.length);
		
		newIdentifier.name = name;
		
		return newIdentifier;
	}
	
	/**
	 * Gets the modules qualifying this identifier.
	 * @return An array of the nested module names qualifying this identifier.
	 * For example, given an identifier {@code mu:test:func}, this would return
	 * an array {@code ["mu", "test"]}.
	 */
	public String[] getModules() {
		return modules;
	}
	
	/**
	 * Determines whether this identifier is unqualified (ie. no namespace
	 * qualifiers).
	 * @return Whether this identifier is unqualified.
	 */
	public boolean isUnqualified() {
		return modules.length == 0;
	}
	
	/**
	 * Gets the name of this identifier.
	 * @return The name of this identifier.
	 * For example, given an identifier {@code mu:test:func}, this would return
	 * a string {@code "func"}.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Parses an identifier from the given parser state.
	 * @param parser The parser enumerator to use.
	 * @return An identifier, as parsed from the current input.
	 */
	public static IdentifierNode parse(Parser parser) {
		List<String> identifierParts = new ArrayList<String>();
		Location identifierLocation = null;
		do {
			Location identifierPartLocation = parser.expect(token -> token instanceof IdentifierToken, "Identifier expected.");
			if(identifierLocation == null) {
				/* The reported location of the identifier is the location of the first
				 * token of the identifier.
				 */
				identifierLocation = identifierPartLocation;
			}
			identifierParts.add(((IdentifierToken)parser.current()).getIdentifier());
		} while(parser.accept(token -> token.isSymbolToken(SymbolTokenType.NAMESPACE_QUALIFIER)));
		String[] partsArray = new String[identifierParts.size()];
		identifierParts.toArray(partsArray);
		return new IdentifierNode(identifierLocation, partsArray);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(String module : modules) {
			builder.append(module);
			builder.append(QUALIFIER_SYMBOL);
		}
		builder.append(name);
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		int hashCode = 0x633c0b24, prevHashCode = 0x6902a10c;
		for(int i = 0; i < modules.length; i++) {
			int currentHashCode = modules[i].hashCode();
			hashCode ^= currentHashCode ^ (prevHashCode << ((i + currentHashCode % 8) % 3));
			prevHashCode = currentHashCode;
		}
		int currentHashCode = name.hashCode();
		hashCode ^= currentHashCode + modules.length ^ (prevHashCode);
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof IdentifierNode) {
			IdentifierNode identifier = (IdentifierNode)obj;
			if(!identifier.name.equals(this.name)) {
				return false;
			}
			if(identifier.modules.length != this.modules.length) {
				return false;
			}
			for(int i = 0; i < this.modules.length; i++) {
				if(!this.modules[i].equals(identifier.modules[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
