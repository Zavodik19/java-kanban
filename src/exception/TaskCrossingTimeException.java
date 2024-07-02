package exception;

public class TaskCrossingTimeException extends RuntimeException {
    public TaskCrossingTimeException(String message) {
        super(message);
    }
}
