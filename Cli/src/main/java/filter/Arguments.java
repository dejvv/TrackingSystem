package filter;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.*;

public class Arguments {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    String[] arguments;
    Options options;
    Map<String, List<String>> optionValues;
    public Boolean containsHelpFlag;
    public Arguments(String[] arguments) {
        if (arguments == null) {
            arguments = new String[0];
        }
        this.arguments = arguments;
        this.optionValues = new HashMap<>();
        this.createOptions();
        this.containsHelpFlag = false;
    }

    /**
     * Creates available options (flags that a user can use).
     */
    private void createOptions () {
        if (this.options == null) {
            this.options = new Options();
        }
        if (!this.options.getOptions().isEmpty()) {
            return;
        }
        Option filter = new Option("f", "filter", false, "Acts as filter. Values provided with this filter are compared to accountIds of events. If they match, event is shown, otherwise it's discarded.");
        filter.setArgs(Option.UNLIMITED_VALUES);
        this.options.addOption(filter);
        this.options.addOption(new Option("h", "help", false, "Displays this help menu."));
    }

    /**
     * Prepares text that is used as an help text (-h flag).
     */
    private void displayHelp() {
        String header = "Displays events from the PubSub service.\n\n";
        String footer = "\nPlease report issues at https://github.com/dejvv/trackingsystem";
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("TrackingCli", header, this.options, footer, true);
    }

    /**
     * Parse arguments and creates wrapper object with data about available options and their values.
     * @return Object with available options and their values.
     */
    private CommandLine parse () {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(this.options, this.arguments);
        } catch (ParseException e) {
            Arguments.LOGGER.error(e.getMessage());
        }
        return commandLine;
    }

    /**
     * Do some action based on the options.
     * @param commandLine Object with available options and their values.
     */
    private void executeCommand (CommandLine commandLine) {
        if (commandLine == null) {
            return;
        }
        if (commandLine.hasOption("-h")) {
            this.displayHelp();
            this.containsHelpFlag = true;
            return;
        }
        if (commandLine.hasOption("-f")) {
            System.out.println("-f options:"+Arrays.toString(commandLine.getOptionValues("f")));
            this.optionValues.put("f", Arrays.asList(commandLine.getOptionValues("f")));
        }
    }

    /**
     * Combines parse and executeCommand methods.
     */
    public void read () {
        this.executeCommand(this.parse());
    }

    /**
     * Finds all values that user wrote as input for given flag.
     * @param flag Flag for which values should be received.
     * @return Set of strings that represents user's values for the given flag. If flag is null or blank empty set is returned.
     */
    public Set<String> getFlagValues (String flag) {
        List<String> filterValues = null;
        if (!flag.isBlank()) {
            filterValues = this.optionValues.get(flag);
        }
        if (filterValues == null) {
            filterValues = new ArrayList<>();
        }
        return new HashSet<>(filterValues);
    }

    /**
     * Finds all available values for -f flag. This values represents account ids.
     * @return Set of strings that represents user's values for account ids.
     */
    public Set<String> getAccountIds() {
        return this.getFlagValues("f");
    }
}
