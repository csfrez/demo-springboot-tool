package com.csfrez.tool.cache;

import java.util.HashMap;
import java.util.Map;

//手写双向链表
public class LRUNodeCache {
    class DNode {
        DNode prev;
        DNode next;
        int val;
        int key;
    }

    Map<Integer, DNode> map = new HashMap<>();
    DNode head, tail;
    int cap;

    public LRUNodeCache(int capacity) {
        head = new DNode();
        tail = new DNode();
        head.next = tail;
        tail.prev = head;
        cap = capacity;
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            DNode node = map.get(key);
            removeNode(node);
            addToHead(node);
            return node.val;
        } else {
            return -1;
        }
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            DNode node = map.get(key);
            node.val = value;
            removeNode(node);
            addToHead(node);
        } else {
            DNode newNode = new DNode();
            newNode.val = value;
            newNode.key = key;
            addToHead(newNode);
            map.put(key, newNode);
            if (map.size() > cap) {
                map.remove(tail.prev.key);
                removeNode(tail.prev);
            }
        }
    }

    public void removeNode(DNode node) {
        DNode prevNode = node.prev;
        DNode nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    public void addToHead(DNode node) {
        DNode firstNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = firstNode;
        firstNode.prev = node;
    }
}
