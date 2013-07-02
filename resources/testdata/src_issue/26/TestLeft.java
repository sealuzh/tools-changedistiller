public class Test {
	public void someMethod(char c) {
		switch (Character.getType(c)) {
		case Character.DECIMAL_DIGIT_NUMBER:
		case Character.LOWERCASE_LETTER:
		case Character.UPPERCASE_LETTER:
			push(c);
			if (length == MAX_WORD_LEN)
				return flush();
			break;
		case Character.OTHER_LETTER:
			if (length > 0) {
				bufferIndex--;
				return flush();
			}
			push(c);
			return flush();
		default:
			if (length > 0)
				return flush();
			break;
		}
	}
}