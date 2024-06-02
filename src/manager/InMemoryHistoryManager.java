package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {
    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.item = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private final HashMap<Integer, Node> taskHistory = new HashMap<>();
    private Node first;
    private Node last;


    @Override
    public void add(Task task) {
        Node node = taskHistory.get(task.getId());
        if (node != null) {
            removeNode(node);
        }
        taskHistory.put(task.getId(), linkLast(task));
    }


    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();
        Node current = first;
        while (current != null) {
            list.add(current.item);
            current = current.next;
        }
        return list;
    }

    @Override  // Удаление из связанного списка
    public void remove(int id) {
        Node node = taskHistory.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private Node linkLast(Task task) {
        final Node lastNode = last;
        final Node newNode = new Node(lastNode, task, null);
        last = newNode;
        if (lastNode == null) {
            first = newNode;
        } else {
            lastNode.next = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        final Node nextNode = node.next;
        final Node prevNode = node.prev;

        if (prevNode == null) { // Если удаляемый узел является первым
            if (nextNode != null) { // Проверка, что nextNode не null
                first = nextNode;
                nextNode.prev = null;
            } else { // Если список содержит только один узел
                first = null;
                last = null;
            }
        } else if (nextNode == null) { // Если удаляемый узел является последним
            last = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
        taskHistory.remove(node.item.getId());
    }
}

