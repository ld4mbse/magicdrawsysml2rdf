package edu.gatech.mbsec.adapter.magicdraw;

/**
 * Defines the arguments of this application.
 * @author rherrera
 */
public enum Args {
    /**
     * Help requested.
     */
    help("prints this file", false),
    /**
     * The mandatory input mdzip file.
     */
    mdzip("mandatory mdzip input file", true, "file"),
    /**
     * The meta information to be added.
     */
    meta("meta information to be added", false, ""),
    /**
     * The meta information to be added.
     */
    nsprefix("allows to define custom prefixes", false, ""),
    /**
     * The RDF format.
     */
    format("output format (Turtle by default)", false, "format"),
    /**
     * The output target.
     */
    target("output target (console by default)", false, "file|url"),
    /**
     * Vocabulary generation.
     */
    vocab("generates a vocabulary", false);
    /**
     * Argument's description.
     */
    private final String description;
    /**
     * Argument's requireness.
     */
    private final boolean required;
    /**
     * Argument's argument name.
     */
    private final String argumentName;
    /**
     * Constructs an instance specifying the {@code description}, the
     * {@code required} and the single {@code argumentName} arguments.
     * @param description the argument's description.
     * @param required whether this argument is required.
     * @param multipleArgs the single, this argument's, name.
     */
    private Args(String description, boolean required, String argumentName) {
        this.argumentName = argumentName;
        this.description = description;
        this.required = required;
    }
    /**
     * Constructs an instance specifying the argument's options.
     * @param description the argument's description.
     */
    private Args(String description, boolean required) {
        this(description, required, null);
    }
    /**
     * Gets the description of this argument.
     * @return the description of this argument.
     */
    public String getDescription() {
        return description;
    }
    /**
     * Gets the requireness of this argument.
     * @return {@code true} if this argument is required, {@code false}
     * otherwise.
     */
    public boolean isRequired() {
        return required;
    }
    /**
     * Gets the argument's argument name if any.
     * @return {@code null} if this argument does not have argument; its name
     * otherwise.
     */
    public String getArgumentName() {
        return argumentName;
    }
    /**
     * Determines whether this argument has a meta-argument.
     * @return {@code true} if this argument has a meta-argument; {@code false}
     * otherwise.
     */
    public boolean hasArgument() {
        return argumentName != null && !argumentName.isEmpty();
    }
    /**
     * Determines whether this argument has multiple meta-argument.
     * @return {@code true} if this argument is a multivalued argument;
     * {@code false} otherwise.
     */
    public boolean isMultiple() {
        return argumentName != null && argumentName.isEmpty();
    }
}