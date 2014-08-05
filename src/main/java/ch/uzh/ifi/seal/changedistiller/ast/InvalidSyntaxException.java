package ch.uzh.ifi.seal.changedistiller.ast;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2014 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
