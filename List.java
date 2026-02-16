
public class List {

    private Node first;
    private int size;

    public List() {
        first = null;
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public CharData getFirst() {
        return (first != null) ? first.data : null;
    }

    public void addFirst(char chr) {
        Node newNode = new Node(new CharData(chr), first);
        first = newNode;
        size++;
    }

    public void addLast(char chr) {
        Node newNode = new Node(new CharData(chr));
        if (first == null) {
            first = newNode;
        } else {
            Node current = first;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

public void update(char chr) {
    Node current = first;
    while (current != null) {
        if (current.data.chr == chr) {
            current.data.count++;
            return;
        }
        current = current.next;
    }
    addFirst(chr); 
}
    public int indexOf(char chr) {
        Node current = first;
        int index = 0;
        while (current != null) {
            if (current.data.chr == chr) return index;
            current = current.next;
            index++;
        }
        return -1;
    }

    public boolean remove(char chr) {
        if (first == null) return false;
        if (first.data.chr == chr) {
            first = first.next;
            size--;
            return true;
        }
        Node prev = first;
        Node current = first.next;
        while (current != null) {
            if (current.data.chr == chr) {
                prev.next = current.next;
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public CharData get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    public ListIterator listIterator(int index) {
        Node current = first;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return new ListIterator(current);
    }

    public String toString() {
        if (size == 0) return "()";
        StringBuilder sb = new StringBuilder("(");
        Node current = first;
        while (current != null) {
            sb.append(current.data.toString());
            if (current.next != null) sb.append(" ");
            current = current.next;
        }
        sb.append(")");
        return sb.toString();
    }
}