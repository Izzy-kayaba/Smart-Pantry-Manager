package za.ac.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private final long id;
    private final String name;
    private final String steps;
    private final List<RecipeRequirement> requirements;

    public Recipe(long id, String name, String steps) {
        this.id = id;
        this.name = name;
        this.steps = steps;
        this.requirements = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSteps() {
        return steps;
    }

    public List<RecipeRequirement> getRequirements() {
        return requirements;
    }

    public void addRequirement(RecipeRequirement requirement) {
        requirements.add(requirement);
    }
}
