package ch.uzh.ifi.seal.changedistiller.ast;

public class CompilationError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public String fileName;

	public CompilationError(String fileName, String message) {
		super(message);
		this.fileName = fileName;
	}
}
