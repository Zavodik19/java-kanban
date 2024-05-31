package task;

import model.SubTask;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubTaskTest {

    @Test
    public void testSubTaskCannotBeItsOwnEpic() {
        SubTask subTask = new SubTask("SubTask 1", "Description 1", TaskStatus.NEW, 1);
        subTask.setId(1);

        subTask.setEpicId(1);

        assertNotEquals(1, subTask.getEpicId());
    }
}