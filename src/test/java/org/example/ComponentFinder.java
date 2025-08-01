package org.example;

import java.awt.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ComponentFinder {

    /**
     * Finds a component by name and asserts it is of the given type.
     *
     * @param root The root container to search.
     * @param name The name of the component (set with setName()).
     * @param type The expected type of the component.
     * @param <T>  The type to cast to and return.
     * @return The component cast to the given type.
     */
    public static <T> T findComponentByNameAsType(Container root, String name, Class<T> type) {
        Optional<Component> found = findComponentByName(root, name);
        assertThat(found)
                .as("Component with name '%s'", name)
                .isPresent();
        Component comp = found.get();
        assertThat(comp)
                .as("Component '%s' should be of type %s", name, type.getSimpleName())
                .isInstanceOf(type);
        return type.cast(comp);
    }

    /**
     * Recursively searches for a component with a given name.
     */
    public static Optional<Component> findComponentByName(Container root, String name) {
        for (Component comp : root.getComponents()) {
            if (name.equals(comp.getName())) {
                return Optional.of(comp);
            }
            if (comp instanceof Container) {
                Optional<Component> result = findComponentByName((Container) comp, name);
                if (result.isPresent()) return result;
            }
        }
        return Optional.empty();
    }
}
