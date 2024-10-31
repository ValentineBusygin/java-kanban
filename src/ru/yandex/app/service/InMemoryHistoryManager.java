package ru.yandex.app.service;

import ru.yandex.app.model.Node;
import ru.yandex.app.model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    PracticumLinkedList<Task> taskHistory = new PracticumLinkedList<>();
    Map<Integer, Node<Task>> taskHash = new HashMap<>();

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

    static class PracticumLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public Node<T> linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(element, null, oldTail);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }

            size++;

            return newNode;
        }

        public List<T> getElements() {
            List<T> elementsList = new ArrayList<>();

            Node<T> link = head;
            while (link != null) {
                elementsList.add(link.data);
                link = link.next;
            }

            return elementsList;
        }

        public void removeNode(Node<T> node) {
            if (node != null) {
                Node<T> prev = node.prev;
                Node<T> next = node.next;

                //Сразу же уменьшаем размер на 1 - в худшем случае дальше в функции значение будет установлено в 0
                size--;

                //Предыдущий элемент не пустой
                if (prev != null) {
                    //Следующий элемент не пустой
                    if (next != null) {
                        prev.next = node.next;
                    //Следующий эелемент пустой
                    } else {
                        prev.next = null;
                        tail = prev;
                    }
                //Предыдущий элемент пустой
                } else {
                    //Следующий элемент не пустой
                    if (next != null) {
                        next.prev = null;
                        head = next;
                    //Следующий эелемент пустой
                    } else {
                        //Первый элемент пустой, последний элемент пустой - список полностью пуст
                        head = null;
                        tail = null;
                        size = 0;
                    }
                }
            }
        }
    }
}