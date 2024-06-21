package exception;

import java.io.IOException;

public class TaskManagerLoadException extends RuntimeException {
    public TaskManagerLoadException(String s, IOException e) {
    }
}
