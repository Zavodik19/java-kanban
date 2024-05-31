import manager.InMemoryHistoryManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {

    @Test
    public void addAndGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertEquals(2, history.size());
    }

    @Test
    public void remove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();

        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
        assertEquals(1, history.size());
    }

    @Test
    public void removeNode() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3", TaskStatus.DONE);
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertTrue(history.contains(task1));
        assertFalse(history.contains(task2));
        assertTrue(history.contains(task3));
        assertEquals(2, history.size());
    }
}


//class InMemoryHistoryManagerTest {
//    private Task task;
//    private List<Task> listOfTasks = new ArrayList<>();
//    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
//
//    @Test
//    void testRemoveFirst() {
//        InMemoryTaskManager manager = new InMemoryTaskManager();
//        Task task1 = manager.addTask(new Task("task1", "Сходить в кино", NEW));
//        manager.addTask(task1);
//
//        Task task2 = manager.addTask(new Task("task2", "Помыть посуду", NEW));
//        manager.addTask(task2);
//
//        Task task3 = manager.addTask(new Task("task3", "Сделать зарядку", NEW));
//        manager.addTask(task3);
//
//        manager.deleteTask(task1.getId());
//        assertEquals(manager.getHistory(), List.of(task1, task3));
//    }
//
//    @Test
//    void testRemoveSecond() {
//        InMemoryHistoryManager manager = new InMemoryHistoryManager();
//        Task task1 = new Task("task1", "Сходить в кино", NEW);
//        manager.add(task1);
//        Task task2 = new Task("task2", "Сходить в кино", NEW);
//        manager.add(task2);
//        Task task3 = new Task("task3", "Сходить в кино", NEW);
//        manager.add(task3);
//
//        manager.remove(2);
//        assertEquals(manager.getHistory(), List.of(task1, task3));
//    }
//
//    @Test
//    void testRemoveLast() {
//        InMemoryHistoryManager manager = new InMemoryHistoryManager();
//        Task task1 = new Task("task1", "Сходить в кино", NEW);
//        manager.add(task1);
//        Task task2 = new Task("task2", "Сходить в кино", NEW);
//        manager.add(task2);
//        Task task3 = new Task("task3", "Сходить в кино", NEW);
//        manager.add(task3);
//
//        manager.remove(3);
//        assertEquals(manager.getHistory(), List.of(task1, task2));
//    }
//}
