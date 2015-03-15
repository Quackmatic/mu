package pw.usn.mu.parser;

import pw.usn.mu.tokenizer.Location;

/**
 * Represents the application of a function with a given argument.
 */
public class ApplicationNode extends Node {
	private Node function;
	private Node argument;
	
	/**
	 * Initializes a new ApplicationNode, with the given function to apply
	 * and the value to apply with.
	 * @param location The location of the AST node in a parsed input source.
	 * @param function The function to apply in this application.
	 * @param argument The argument to pass to the function.
	 */
	public ApplicationNode(Location location, Node function, Node argument) {
		super(location);
		this.function = function;
		this.argument = argument;
	}
	
	/**
	 * Gets the function applied in this application.
	 * @return The function applied.
	 */
	public Node getFunction() {
		return function;
	}
	
	/**
	 * Gets the argument passed to the applied function in this application.
	 * @return The argument passed to the function being applied.
	 */
	public Node getArgument() {
		return argument;
	}
	
	/**
	 * Parses a variable number of applications from the given parser state.
	 * @param parser The parser enumerator to use.
	 * @return An expression, as parsed from the current input. This method must
	 * return an {@link Node} rather than a concrete {@link ApplicationNode} as
	 * there may be zero applications made, in which case there will be no
	 * application of anything.
	 */
	public static Node parse(Parser parser) { 
		Node left = Node.parseAtomic(parser);
		while(parser.test(token -> token.isAtomicToken())) {
			Node right = Node.parseAtomic(parser);
			left = new ApplicationNode(right.getLocation(), left, right);
		}
		return left;
	}
}
