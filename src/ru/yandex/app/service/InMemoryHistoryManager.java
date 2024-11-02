package ru.yandex.app.service;

import ru.yandex.app.model.Node;
import ru.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final PracticumLinkedList<Task> taskHistory = new PracticumLinkedList<>();
    private final Map<Integer, Node<Task>> taskHash = new HashMap<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            taskHistory.removeNode(taskHash.get(task.getTaskID()));

            Node<Task> node = taskHistory.linkLast(task);

            taskHash.put(task.getTaskID(), node);
        }
    }

    @Override
    public void remove(int id) {
        taskHistory.removeNode(taskHash.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getElements();
    }

    private static class PracticumLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;

        public Node<T> linkLast(T element) {

            final Node<T> newNode = new Node<>(element, null, tail);
            if (tail == null) {
                head = newNode;
            } else {
                tail.setNext(newNode);
            }
            tail = newNode;

            return newNode;
        }

        public List<T> getElements() {
            List<T> elementsList = new LinkedList<>();

            Node<T> link = head;
            while (link != null) {
                elementsList.add(link.getData());
                link = link.getNext();
            }

            return elementsList;
        }

        public void removeNode(Node<T> node) {
            if (node != null) {
                Node<T> prev = node.getPrev();
                Node<T> next = node.getNext();

                if (prev != null) {
                    prev.setNext(next);
                } else {
                    head = next;
                }

                if (next != null) {
                    next.setPrev(prev);
                } else {
                    tail = prev;
                }
            }
        }
    }
}