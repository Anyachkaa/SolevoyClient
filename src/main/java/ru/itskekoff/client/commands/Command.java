package ru.itskekoff.client.commands;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import ru.itskekoff.client.SolevoyClient;

@Getter
public abstract class Command {

    public final SolevoyClient client = SolevoyClient.getInstance();

    private final String prefix;
    private final String desc;
    private final String usage;

    public Command() {
        CommandInfo commandInfo = this.getClass().getDeclaredAnnotation(CommandInfo.class);
        Validate.notNull(commandInfo, "CONFUSED ANNOTATION EXCEPTION");
        this.prefix = commandInfo.prefix();
        this.desc = commandInfo.description();
        this.usage = commandInfo.usage();
    }

    public abstract void onCommand(final String[] args) throws Exception;
}