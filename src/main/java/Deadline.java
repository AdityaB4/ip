import java.time.LocalDate;

public class Deadline extends Task {
    private LocalDate by;
    Deadline(String text, TaskStatus status, LocalDate by) {
        super(text, status);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D] " + super.toString() + " (by: " + by  + " )";
    }
}
