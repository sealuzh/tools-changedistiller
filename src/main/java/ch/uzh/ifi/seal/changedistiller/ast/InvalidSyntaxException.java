package ch.uzh.ifi.seal.changedistiller.ast;

/**
 * Thrown if a file has syntax errors. In such a case, ChangeDistiller will not
 * be able to detect any changes.
 * 
 * @author linzhp
 * @author wuersch
 * 
 */
public class InvalidSyntaxException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String fileName;

	public InvalidSyntaxException(String fileName, String message) {
		super(message);
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
}
