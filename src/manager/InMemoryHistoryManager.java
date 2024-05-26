package manager;

import model.Task;


import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        int id = task.getId();
        Node existingNode = history.get(id);

        if (existingNode != null) {
            removeNode(existingNode);
        }

        Node newNode = linkLast(task);
        history.put(id, newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = history.get(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            history.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = new ArrayList<>(history.size());
        Node current = first;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    private static class Node {
        public Task task;
        public Node prev;
        public Node next;

        Node(Node prev, Task task, Node next) {
            this.prev = prev;
            this.task = task;
            this.next = next;
        }
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(last, task, null);
        if (first == null) {
            first = newNode;
        } else if (last == null) {
            last = newNode;
            first.next = last;
            last.prev = first;
        } else {
            last.next = newNode;
            newNode.prev = last;
            last = newNode;
        }
        return newNode;
    }

    private void removeNode(Node node) {
        if (node == first) {
            first = node.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
        } else if (node == last) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}