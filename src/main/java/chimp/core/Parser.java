package chimp.core;
import java.time.LocalDate;
import chimp.command.*;
import chimp.exception.CommandParseException;
import chimp.exception.InvalidCommandException;

public class Parser {
    public static Command parse(String input) throws InvalidCommandException, CommandParseException {
        int num;
        input = input.toLowerCase();
        String command = input.split(" ")[0];
        String arg = null;
        if (input.split(" ").length > 1) {
            // split at first space, and take everything on the right
            arg = input.substring(input.indexOf(' '), input.length());
        }

        switch (command) {
            case "list":
                return new ListCommand();
            case "mark":
                num = Integer.parseInt(arg);
                return new MarkCommand(num);
            case "unmark":
                num = Integer.parseInt(arg);
                return new UnmarkCommand(num);
            case "todo":
                if (arg == null || arg.equals("")) {
                    throw new CommandParseException("todo must have a desc");
                }
                return new TodoCommand(arg);
            case "event":
                String fromSubCommand = arg.split("/")[1];
                String from = fromSubCommand.substring(fromSubCommand.indexOf(' '));
                from = from.strip();
                if (from == null || from.equals("")) {
                    throw new CommandParseException("deadline needs by date/time!");
                }

                String toSubCommand = arg.split("/")[2];
                String to = toSubCommand.substring(toSubCommand.indexOf(' '));
                to = to.strip();
                if (to == null || to.equals("")) {
                    throw new CommandParseException("deadline needs by date/time!");
                }

                LocalDate fromDate;
                LocalDate toDate;
                try {
                    fromDate = LocalDate.parse(from);
                    toDate = LocalDate.parse(to);
                } catch (Exception e) {
                    throw new CommandParseException("Invalid date format provided to event");
                }

                String text = arg.split("/")[0].strip();
                return new EventCommand(text, fromDate, toDate);
            case "deadline":
                String bySubCommand = arg.split("/")[1];
                String by = bySubCommand.substring(3);
                by = by.strip();

                LocalDate byDate;
                try {
                    byDate = LocalDate.parse(by);
                } catch (Exception e) {
                    throw new CommandParseException("Invalid date format provided to deadline");
                }

                if (by == null || by.equals("")) {
                    throw new CommandParseException("deadline needs by date/time!");
                }

                // TODO: switch case scoping best practice?
                text = arg.split("/")[0].strip();
                return new DeadlineCommand(text, byDate);
            case "delete":
                num = Integer.parseInt(arg.strip());
                return new DeleteCommand(num);
            case "bye":
                return new ExitCommand();
            case "find":
                return new FindCommand(arg);
            default:
                throw new InvalidCommandException("command \"" + command + "\" is invalid");
        }
    }
}