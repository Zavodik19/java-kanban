package test.model;


import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TaskTest {

    @Test
    public void testTaskEquality() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Description 1", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2);
    }

    @Test
    public void testTaskInequality() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.NEW);

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2);
    }
}