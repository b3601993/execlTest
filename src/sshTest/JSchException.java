package sshTest;

public class JSchException extends Exception {

	
	private Throwable cause = null;

	public Throwable getCause() {
		return cause;
	}

	public JSchException() {
		super();
	}

	public JSchException(String message, Throwable cause) {
		super(message, cause);
	}

	public JSchException(String message) {
		super(message);
	}

	public JSchException(Throwable cause) {
		super(cause);
	}

	
	

}
