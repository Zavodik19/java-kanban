import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // Создаем задачи
        Task task1 = new Task("Сделать уборку", "Сделать уборку за 2 часа", TaskStatus.NEW);
        Task task2 = new Task("Сходить в магазин", "Купить еду в магазине", TaskStatus.NEW);

        // Добавляем задачи в manager.TaskManager
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        // Создаем эпики
        Epic epic1 = new Epic("Построить дом", "Построить дом по новой технологии");
        Epic epic2 = new Epic("Написать список целей", "Написать список целей на текущий год");

        // Добавляем эпики в manager.TaskManager
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        // Создаем подзадачи
        SubTask subtask1 = new SubTask("Заложить фундамент", "Заложить фундамент под будущий дом", TaskStatus.NEW, epic1.getId());
        SubTask subtask2 = new SubTask("Покрасить стены", "Обработать спец материалом стены и покрасить их до зимы", TaskStatus.NEW, epic1.getId());

        // Добавляем подзадачи в manager.TaskManager
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);

        // Выводим все задачи, эпики и подзадачи
        System.out.println("Все задачи: " + taskManager.getAllTask());
        System.out.println("Все эпики: " + taskManager.getAllEpics());
        System.out.println("Все подзадачи: " + taskManager.getAllSubTasks());

        // Удаляем эпик
        System.out.println("Удаляем эпик");
        taskManager.deleteEpic(epic2.getId());

        // Выводим списки задач
        System.out.println("\nОбновленные задачи: " + taskManager.getAllTask());
        System.out.println("Обновленные эпики: " + taskManager.getAllEpics());
        System.out.println("Обновленные подзадачи: " + taskManager.getAllSubTasks());
    }
}


