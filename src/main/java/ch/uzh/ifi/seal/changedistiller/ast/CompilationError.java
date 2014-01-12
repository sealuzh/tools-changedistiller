package ch.uzh.ifi.seal.changedistiller.ast;

public class CompilationError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompilationError(String message) {
		super(message);
	}
}
