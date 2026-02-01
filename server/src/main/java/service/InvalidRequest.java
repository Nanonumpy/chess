package service;

public class InvalidRequest extends Exception {
    public InvalidRequest(String message) {
        super(message);
    }
    public InvalidRequest(String message, Throwable ex) {super(message, ex);}
}
