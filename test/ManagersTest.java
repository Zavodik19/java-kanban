import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class ManagersTest {
    @Test
    public void getDefaultNotNull() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Экземпляр InMemoryTaskManager не создан");
    }

    @Test
    public void getDefaultHistoryNotNull() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Экземпляр InMemoryHistoryManager не создан");
    }
}

