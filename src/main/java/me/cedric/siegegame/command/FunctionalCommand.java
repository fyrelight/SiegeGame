package me.cedric.siegegame.command;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class FunctionalCommand implements BasicCommand {
    private final Map<String, FunctionalCommand> ARGUMENTS = new HashMap<>();
    private final Map<Integer, Collection<String>> COMPLETIONS = new HashMap<>();

    @Override
    public final void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length > 0) {
            FunctionalCommand argument = ARGUMENTS.get(args[0]);
            if (argument != null) {
                argument.execute(commandSourceStack, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        commandLogic(commandSourceStack, args);
    }

    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length == 0) return Collections.emptyList();

        if (args.length > 1) {
            FunctionalCommand argument = ARGUMENTS.get(args[0]);
            if (argument != null) {
                return argument.suggest(commandSourceStack, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        int index = args.length - 1;
        final String toComplete = args[index];

        Collection<String> possible = COMPLETIONS.get(index);
        if (possible == null) return Collections.emptyList();

        return possible.stream().filter(s -> s.startsWith(toComplete) && !s.equals(toComplete)).toList();
    }

    protected void registerArgument(String argument, FunctionalCommand command, String... aliases) {
        registerCompletions(1, List.of(argument));
        registerCompletions(1, Arrays.asList(aliases));

        ARGUMENTS.put(argument, command);
        for (String s : aliases) {
            ARGUMENTS.put(s, command);
        }

    }

    protected void registerCompletions(int index, Collection<String> strings) {
        if (index < 1) throw new IllegalArgumentException("Index must be at least 1 to register completion");
        index = index - 1;
        Collection<String> completions = COMPLETIONS.computeIfAbsent(index, k -> new ArrayList<>());
        completions.addAll(strings);
    }

    public abstract void commandLogic(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args);
}
