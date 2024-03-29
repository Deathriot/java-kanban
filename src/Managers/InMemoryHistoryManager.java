package Managers;

import Tasks.SimpleTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager{

    private final CustomLinkedList history = new CustomLinkedList();
    @Override
    public void addTask(SimpleTask task) {

        if(task == null){
            return; // защита от null
        }

        history.addTask(task);
    }

    @Override
    public List<SimpleTask> getHistory() {
        return history.getTasks();
    }

    @Override
    public void remove(int id){
        history.removeNodeById(id);
    }

    private static class CustomLinkedList{
        private final Map<Integer, Node> historyMap = new HashMap<>();
       static class Node {
            public SimpleTask data;
            public Node next;
            public Node prev;

            public Node(Node prev, SimpleTask data, Node next) {
                this.data = data;
                this.next = next;
                this.prev = prev;
            }
        }

        private Node head;
        private Node tail;
        private int size = 0;

        private Node linkLast(SimpleTask task){
            final Node oldTail = tail;
            final Node newNode = new Node(oldTail, task, null);

            tail = newNode;

            if(oldTail == null){
                head = newNode;
            }else{
                oldTail.next = newNode;
            }

            size++;
            return newNode;
        }

        private List<SimpleTask> getTasks(){
            List<SimpleTask> tasks = new ArrayList<>();
            Node currentNode = head;

            while(currentNode != null){
                tasks.add(currentNode.data);
                currentNode = currentNode.next;
            }

            return tasks;
        }

        private void removeNode(Node node){

            if(size == 0){
                return;
            }

            if(size == 1){
                head = null;
                tail = null;
                size--;
                return;
            }

            Node prevNode = node.prev;
            Node nextNode = node.next;

            if(prevNode == null){
                head = nextNode;
                nextNode.prev = null;
                size--;
                return;
            }

            if(nextNode == null){
                tail = prevNode;
                prevNode.next = null;
                size--;
                return;
            }

            prevNode.next = nextNode;
            nextNode.prev = prevNode;
            size--;
        }

        private void addTask (SimpleTask task){
            if(historyMap.containsKey(task.getId())) {
                removeNode(historyMap.get(task.getId()));
            }
            Node newNode = linkLast(task);
            historyMap.put(task.getId(), newNode);
        }

        private void removeNodeById(int id){
            if(historyMap.containsKey(id)) {
                removeNode(historyMap.get(id));
            }
        }
    }
}
