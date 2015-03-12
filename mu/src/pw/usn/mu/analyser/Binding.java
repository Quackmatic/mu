package pw.usn.mu.analyser;

import java.util.stream.Stream;

import pw.usn.mu.analyser.closure.ClosureContext;
import pw.usn.mu.parser.BindingNode;
import pw.usn.mu.parser.IdentifierNode;
import pw.usn.mu.tokenizer.Location;

/**
 * Represents the binding of a value in a mu program.
 */
public class Binding extends Expression {
	private Value value;
	private Expression expression, body;
	
	/**
	 * Initializes a new Binding.
	 * @param location The original location, in a source, of the code that represents
	 * this expression.
	 * @param value The bound-to value.
	 * @param expression The expression bound in the binding.
	 * @param body The body of the new binding.
	 */
	public Binding(Location location, Value value, Expression expression, Expression body) {
		super(location);
		this.value = value;
		this.expression = expression;
		this.body = body;
	}
	
	/**
	 * Gets the value to which an expression was bound.
	 * @return The bound-to value.
	 */
	public Value getValue() {
		return value;
	}
	
	/**
	 * Gets the expression bound in this binding.
	 * @return The expression bound.
	 */
	public Expression getExpression() {
		return expression;
	}
	
	/**
	 * Gets the body of this binding.
	 * @return The body of this binding.
	 */
	public Expression getBody() {
		return body;
	}
	
	@Override
	public void liftClosures(ClosureContext context) {
		ClosureContext bindingContext = new ClosureContext(context) {
			@Override
			public void liftReference(Reference reference) {
				if(reference.isNonLocalReference()) {
					/* Do nothing - the reference refers to a value that is not
					 * declared in a local scope, so we cannot close over it.
					 */
				} if(reference.refersTo(getValue())) {
					/* Do nothing - we know this reference refers to a value
					 * in local scope (ie. a value bound in this binding),
					 * so we don't need to do anything else.
					 */
				} else {
					getEnclosingScope().liftReference(reference);
				}
			}
			
			@Override
			public Stream<Value> getLocalValues() {
				return Stream.concat(
						Stream.of(getValue()),
						getEnclosingScope().getLocalValues());
			}
		};
		expression.liftClosures(bindingContext);
		body.liftClosures(bindingContext);
	}

	/**
	 * Analyses the given {@code node} in the given context and creates an equivalent
	 * {@link Binding}.
	 * @param context The context in which {@code node} resides.
	 * @param node The AST node to analyse.
	 * @return An {@link Binding} representing the same program structure as {@code
	 * node} but with all identifiers resolved into references to values.
	 */
	public static Binding analyse(ResolutionContext context, BindingNode node) {
		Value value = new Value(node.getName());
		
		ResolutionContext bindingContext = new ResolutionContext(context) {
			@Override
			public Reference resolve(IdentifierNode identifier) {
				if(identifier.isUnqualified() &&
						identifier.getName().equals(node.getName())) {
					return value.newReference(identifier.getLocation());
				} else {
					return super.resolve(identifier);
				}
			}
		};
		
		return new Binding(node.getLocation(),
				value,
				Expression.analyse(bindingContext, node.getValue()),
				Expression.analyse(bindingContext, node.getContent()));
	}
}
