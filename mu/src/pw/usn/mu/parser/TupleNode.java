package pw.usn.mu.parser;

import java.util.ArrayList;
import java.util.List;

import pw.usn.mu.tokenizer.SymbolTokenType;

/**
 * Represents an unnamed tuple of values.
 */
public class TupleNode extends Node {
	private Node[] values;
	
	/**
	 * Initializes a new untagged Tuple with the given {@code values}.
	 * @param values The values contained within this tuple.
	 */
	public TupleNode(Node... values) {
		if(values.length > 1) {
			this.values = values;
		} else {
			throw new IllegalArgumentException("Tuple must have at least two values.");
		}
	}
	
	/**
	 * Gets the length of the tuple.
	 * @return The length (number of values) in this tuple.
	 */
	public int getLength() {
		return values.length;
	}
	
	/**
	 * Gets the {@code index}-th value in the tuple, where {@code index}={@code 0} is
	 * the first item in the tuple.
	 * @param index The position in the tuple of the value to get.
	 * @return The specified value in the suple.
	 */
	public Node getValues(int index) {
		return values[index];
	}
	
	/**
	 * Parses a tuple from the given parser state.
	 * @param parser The parser enumerator to use.
	 * @return An expression, as parsed from the current input. This method must
	 * return an {@link Node} rather than a concrete {@link TupleNode} as
	 * the tuple may have a length of one, in which case the value returned is
	 * the first (and only) value in the tuple.
	 */
	public static Node parse(Parser parser) {
		List<Node> expressions = new ArrayList<Node>(1);
		do {
			expressions.add(Node.parseBound(parser));
		} while(parser.accept(token -> token.isSymbolToken(SymbolTokenType.COMMA)));
		if(expressions.size() == 1) {
			return expressions.get(0);
		} else {
			Node[] expressionsArray = new Node[expressions.size()];
			expressions.toArray(expressionsArray);
			return new TupleNode(expressionsArray);
		}
	}
}
