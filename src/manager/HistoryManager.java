package manager;

import model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);  // Добавление задачи в историю

    void remove(int id);  // Удаление задачи из просмотров

    List<Task> getHistory();  // Получение списка просмотров

}
