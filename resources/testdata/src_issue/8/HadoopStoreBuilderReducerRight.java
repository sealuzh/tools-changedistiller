// simplified example from voldemort.store.readonly.mr.HadoopStoreBuilderReducer, commit 17d5b4222334155d967b8b37e4f1fcb5a3a30fe5
public class Class {
	public void method() {
		if (previousElement == null && previousIterator.hasNext()) {
			previousElement = previousIterator.next();
		} else if (previousElement == null && !previousIterator.hasNext()) {
			
		} else {
			switch (ByteUtils.compare(previousElement.getFirst().array(),
					key.get())) {
			case 0:
				if (previousIterator.hasNext()) {
					previousElement = previousIterator.next();
				}
			case 1:
				if (previousIterator.hasNext()) {
					previousElement = previousIterator.next();
				} else {
					previousElement = null;
				}
			}
		}
	}
}
