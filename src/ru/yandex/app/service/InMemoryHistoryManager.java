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
            Node<Task> node = taskHash.get(task.getTaskID());
            if (node != null) {
                taskHistory.removeNode(node);
            }
            node = taskHistory.linkLast(task);

            taskHash.put(task.getTaskID(), node);
        }
    }

    @Override
    public void remove(int id) {
        Node<Task> node = taskHash.get(id);
        taskHistory.removeNode(node);
        taskHash.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return taskHistory.getElements();
    }

    private static class PracticumLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public Node<T> linkLast(T element) {

            final Node<T> newNode = new Node<>(element, null, tail);
            if (tail == null) {
                head = newNode;
            } else {
                tail.setNext(newNode);
            }
            tail = newNode;

            size++;

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

                size--;

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