package Managers;

import Tasks.SimpleTask;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private final CustomLinkedList history = new CustomLinkedList();
    @Override
    public void addTask(SimpleTask task) {
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

    class CustomLinkedList{

        private HashMap<Integer, Node> historyMap = new HashMap<>();
        class Node {
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

        private void linkLast(SimpleTask task){
            final Node oldTail = tail;
            final Node newTail = new Node(oldTail, task, null);

            historyMap.put(task.getId(), newTail); //Не совсем уверен, что именно здесь должно вставляться в мапу

            tail = newTail;

            if(oldTail == null){
                head = newTail;
            }else{
                oldTail.next = newTail;
            }

            size++;
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
            linkLast(task);
        }

        private void removeNodeById(int id){
            if(historyMap.containsKey(id)) {
                removeNode(historyMap.get(id));
            }
        }
    }
}
