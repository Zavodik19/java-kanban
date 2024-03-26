import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

public class TaskManagerApplication {
    public static void main(String[] args) {

        TaskManager inMemoryTaskManager = Managers.getDefault();

        // Создаем задачи
        Task task1 = new Task("Сделать уборку", "Сделать уборку за 2 часа", TaskStatus.NEW);
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине", TaskStatus.NEW);

        // Добавляем задачи в manager.TaskManager
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);

        // Создаем эпики
        Epic epic1 = new Epic("Построить дом", "Построить дом по новой технологии");
        Epic epic2 = new Epic("Написать список целей", "Написать список целей на текущий год");

        // Добавляем эпики в manager.TaskManager
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);

        // Создаем подзадачи
        SubTask subtask1 = new SubTask("Заложить фундамент", "Заложить фундамент под будущий дом",
                TaskStatus.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Покрасить стены", "Обработать спец материалом стены и покрасить их до зимы",
                TaskStatus.NEW, epic1.getId());

        // Добавляем подзадачи в manager.TaskManager
        inMemoryTaskManager.addSubTask(subtask1);
        inMemoryTaskManager.addSubTask(subtask2);

        // Выводим все задачи, эпики и подзадачи
        System.out.println("Все задачи: " + inMemoryTaskManager.getAllTask());
        System.out.println("Все эпики: " + inMemoryTaskManager.getAllEpics());
        System.out.println("Все подзадачи: " + inMemoryTaskManager.getAllSubTasks());

        // Удаляем эпик
        System.out.println("Удаляем эпик");
        inMemoryTaskManager.deleteEpic(epic2.getId());

        // Выводим списки задач
        System.out.println("\nОбновленные задачи: " + inMemoryTaskManager.getAllTask());
        System.out.println("Обновленные эпики: " + inMemoryTaskManager.getAllEpics());
        System.out.println("Обновленные подзадачи: " + inMemoryTaskManager.getAllSubTasks());
    }
}


