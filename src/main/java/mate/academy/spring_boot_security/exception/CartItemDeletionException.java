package mate.academy.spring_boot_security.exception;

public class CartItemDeletionException extends RuntimeException {
    public CartItemDeletionException(String message) {
        super(message);
    }
}
