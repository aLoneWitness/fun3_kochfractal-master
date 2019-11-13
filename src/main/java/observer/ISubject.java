package observer;

public interface ISubject {
    void addListener(IListener listener);
    void notifyListeners(Object object);
}
